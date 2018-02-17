package cc.etherspace.calladapter

import cc.etherspace.Credentials
import cc.etherspace.EtherSpace
import cc.etherspace.SolAddress
import cc.etherspace.SolBytes32
import kotlinx.coroutines.experimental.runBlocking
import org.amshove.kluent.`should be greater than`
import org.amshove.kluent.`should equal to`
import org.amshove.kluent.`should equal`
import org.junit.Before
import org.junit.Test
import org.web3j.utils.Numeric

class CoroutineGreeterTest {
    private lateinit var greeter: CoroutineGreeter

    @Before
    fun setUp() {
        val etherSpace = EtherSpace.build {
            provider = "https://rinkeby.infura.io/3teU4WimZ2pbdjPUDpPW"
            credentials = Credentials("0xab1e199623aa5bb2c381c349b1734e31b5be08de0486ffab68e3af4853d9980b")
            callAdapters += CoroutineCallAdapter()
        }
        greeter = etherSpace.create(SolAddress("0xa871c507184ecfaf947253e187826c1907e8dc7d"),
                CoroutineGreeter::class.java)
    }

    @Test
    fun newGreeting() {
        runBlocking {
            val receipt = greeter.newGreeting("Hello World").await()
            receipt.blockHash.length.`should equal to`(66)
            receipt.transactionHash.length.`should equal to`(66)
            receipt.from.`should equal to`("0x39759a3c0ada2d61b6ca8eb6afc8243075307ed3")
            receipt.to.`should equal to`("0xa871c507184ecfaf947253e187826c1907e8dc7d")
            receipt.logs.size.`should be greater than`(0)

            val events = receipt.listEvents(CoroutineGreeter.Modified::class.java)
            events.size.`should equal to`(1)
            events[0].event.`should equal to`("Modified")
            events[0].returnValue.oldGreeting.`should equal to`("Hello World")
            events[0].returnValue.newGreeting.`should equal to`("Hello World")
            events[0].returnValue.oldGreetingIdx.`should equal`(SolBytes32(Numeric.hexStringToByteArray("0x592fa743889fc7f92ac2a37bb1f5ba1daf2a5c84741ca0e0061d243a2e6707ba")))
            events[0].returnValue.newGreetingIdx.`should equal`(SolBytes32(Numeric.hexStringToByteArray("0x592fa743889fc7f92ac2a37bb1f5ba1daf2a5c84741ca0e0061d243a2e6707ba")))
        }
    }

    @Test
    fun greet() {
        runBlocking {
            val greet = greeter.greet().await()
            greet.`should equal to`("Hello World")
        }
    }

    fun newPersonalGreeting() {
        runBlocking {
            val transactionHash = greeter.newPersonalGreeting("tempo", "Hello World").await()
            transactionHash.length.`should equal to`(66)
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