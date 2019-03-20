package cc.etherspace.example

import cc.etherspace.*
import cc.etherspace.calladapter.CoroutineCallAdapter
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.runBlocking
import java.io.IOException
import java.math.BigInteger

interface CoroutineGreeter {
    @Throws(IOException::class)
    @Send
    fun newGreeting(greeting: String): Deferred<TransactionHash>

    @Throws(IOException::class)
    @Send
    fun newGreeting(greeting: String, options: Options): Deferred<TransactionHash>

    @Throws(IOException::class)
    @Call
    fun greet(): Deferred<String>
}

fun main(args: Array<String>) {
    runBlocking {
        println("Creating a new instance of Greeter")

        // Please fill in your private key or wallet file.
        val etherSpace = EtherSpace.build {
            provider = "https://rinkeby.infura.io/"
            credentials = WalletCredentials("YOUR_PRIVATE_KEY_OR_WALLET")
            callAdapters += CoroutineCallAdapter()
        }
        // The greeter smart contract has already been deployed to this address on rinkeby.
        val greeter = etherSpace.create("0x7c7fd86443a8a0b249080cfab29f231c31806527", CoroutineGreeter::class.java)

        println("Updating greeting to: Hello World")

        var hash = greeter.newGreeting("Hello World").await()
        hash.requestTransactionReceipt<Deferred<TransactionReceipt>>()

        println("Transaction returned with hash: ${hash.hash}")

        val greeting = greeter.greet().await()

        println("greeting is $greeting now")

        println("Updating greeting with higher gas")

        val options = Options(BigInteger.ZERO, BigInteger.valueOf(5_300_000), BigInteger.valueOf(24_000_000_000L))
        hash = greeter.newGreeting("Hello World", options).await()
        hash.requestTransactionReceipt<Deferred<TransactionReceipt>>()

        println("Transaction returned with hash: ${hash.hash}")
    }
}
