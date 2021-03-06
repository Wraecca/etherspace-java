package cc.etherspace.example

import cc.etherspace.*
import java.io.IOException
import java.math.BigInteger

interface Greeter {
    @Throws(IOException::class)
    @Send
    fun newGreeting(greeting: String): TransactionHash

    @Throws(IOException::class)
    @Send
    fun newGreeting(greeting: String, options: Options): TransactionHash

    @Throws(IOException::class)
    @Call
    fun greet(): String
}

fun main(args: Array<String>) {
    println("Creating a new instance of Greeter")

    // Please fill in your private key or wallet file.
    val etherSpace = EtherSpace.build {
        provider = "https://rinkeby.infura.io/"
        credentials = WalletCredentials("YOUR_PRIVATE_KEY_OR_WALLET")
    }
    // The greeter smart contract has already been deployed to this address on rinkeby.
    val greeter = etherSpace.create("0x7c7fd86443a8a0b249080cfab29f231c31806527", Greeter::class.java)

    println("Updating greeting to: Hello World")

    var hash = greeter.newGreeting("Hello World")
    hash.requestTransactionReceipt<TransactionReceipt>()

    println("Transaction returned with hash: ${hash.hash}")

    val greeting = greeter.greet()

    println("greeting is $greeting now")

    println("Updating greeting with higher gas")

    val options = Options(BigInteger.ZERO, BigInteger.valueOf(5_300_000), BigInteger.valueOf(24_000_000_000L))
    hash = greeter.newGreeting("Hello World", options)
    hash.requestTransactionReceipt<TransactionReceipt>()

    println("Transaction returned with hash: ${hash.hash}")
}
