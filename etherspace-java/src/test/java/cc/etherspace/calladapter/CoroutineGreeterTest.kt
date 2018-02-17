package cc.etherspace.calladapter

import cc.etherspace.SolAddress
import cc.etherspace.EtherSpace
import kotlinx.coroutines.experimental.runBlocking
import org.amshove.kluent.`should be greater than`
import org.amshove.kluent.`should equal to`
import org.amshove.kluent.`should equal`
import org.junit.Before
import org.junit.Test
import org.web3j.crypto.Credentials
import org.web3j.crypto.ECKeyPair
import java.math.BigInteger

class CoroutineGreeterTest {
    private lateinit var greeter: CoroutineGreeter

    @Before
    fun setUp() {
        val etherSpace = EtherSpace.build {
            provider = "https://rinkeby.infura.io/3teU4WimZ2pbdjPUDpPW"
            credentials = Credentials.create(ECKeyPair.create(
                    BigInteger("77398679111088585283982189543320298238063257726010371587476264149399587362827")))
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
        }
    }

    @Test
    fun greet() {
        runBlocking {
            val greet = greeter.greet().await()
            greet.`should equal to`("Hello World")
        }
    }

    @Test
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