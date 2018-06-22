package cc.etherspace.calladapter

import cc.etherspace.*
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.runBlocking
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be greater than`
import org.amshove.kluent.`should equal`
import org.junit.Before
import org.junit.Test
import org.web3j.utils.Numeric
import java.io.IOException
import java.math.BigInteger

class CoroutineGreeterTest {
    private lateinit var greeter: CoroutineGreeter

    @Before
    fun setUp() {
        val etherSpace = EtherSpace.build {
            provider = "https://rinkeby.infura.io/"
            credentials = Credentials(Tests.TEST_WALLET_KEY)
            callAdapters += CoroutineCallAdapter()
        }
        greeter = etherSpace.create(Tests.TEST_CONTRACT_ADDRESS, CoroutineGreeter::class.java)
    }

    @Test
    fun newGreeting() {
        runBlocking {
            val receipt = greeter.newGreeting("Hello World").await()
            receipt.blockHash.length.`should be equal to`(66)
            receipt.transactionHash.length.`should be equal to`(66)
            receipt.from!!.`should be equal to`(Tests.TEST_WALLET_ADDRESS)
            receipt.to!!.`should be equal to`(Tests.TEST_CONTRACT_ADDRESS)
            receipt.logs.size.`should be greater than`(0)

            val events = receipt.listEvents(CoroutineGreeter.Modified::class.java)
            events.size.`should be equal to`(1)
            events[0].event.`should be equal to`("Modified")
            events[0].returnValue.oldGreeting.`should be equal to`("Hello World")
            events[0].returnValue.newGreeting.`should be equal to`("Hello World")
            events[0].returnValue.oldGreetingIdx.`should equal`(SolBytes32(Numeric.hexStringToByteArray("0x592fa743889fc7f92ac2a37bb1f5ba1daf2a5c84741ca0e0061d243a2e6707ba")))
            events[0].returnValue.newGreetingIdx.`should equal`(SolBytes32(Numeric.hexStringToByteArray("0x592fa743889fc7f92ac2a37bb1f5ba1daf2a5c84741ca0e0061d243a2e6707ba")))
        }
    }

    @Test
    fun newGreeting_transactionHash() {
        runBlocking {
            val hash = greeter.newGreeting_transactionHash("Hello World").await()
            hash.hash.length.`should be equal to`(66)
            val receipt = hash.requestTransactionReceipt<Deferred<TransactionReceipt>>().await()
            receipt.blockHash.length.`should be equal to`(66)
            receipt.transactionHash.length.`should be equal to`(66)
            receipt.from!!.`should be equal to`(Tests.TEST_WALLET_ADDRESS)
            receipt.to!!.`should be equal to`(Tests.TEST_CONTRACT_ADDRESS)
            receipt.logs.size.`should be greater than`(0)
        }
    }

    @Test(expected = IOException::class)
    fun newGreeting_options() {
        runBlocking {
            val receipt = greeter.newGreeting("Hello World", Options(gas = BigInteger.valueOf(44_000_000_000L))).await()
            receipt.transactionHash.length.`should be equal to`(66)
        }
    }

    @Test
    fun greet() {
        runBlocking {
            val greet = greeter.greet().await()
            greet.`should be equal to`("Hello World")
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun greet_wrongFunctionName() {
        runBlocking {
            val greet = greeter.greet_wrongFunctionName().await()
            greet.`should be equal to`("Hello World")
        }
    }

    fun newPersonalGreeting() {
        runBlocking {
            val transactionHash = greeter.newPersonalGreeting("tempo", "Hello World").await()
            transactionHash.length.`should be equal to`(66)
        }
    }

    @Test
    fun personalGreet() {
        runBlocking {
            val pair = greeter.personalGreet().await()
            pair.first.`should equal`("tempo")
            pair.second.`should equal`("Hello World")
        }
    }
}