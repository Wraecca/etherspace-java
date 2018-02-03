package cc.etherspace.calladapter

import cc.etherspace.Address
import cc.etherspace.EtherSpace
import kotlinx.coroutines.experimental.runBlocking
import org.amshove.kluent.`should equal to`
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
        greeter = etherSpace.create(Address("0x6183ef18b3961f695e255604facb0b8651d2b1ee"), CoroutineGreeter::class.java)
    }

    @Test
    fun newGreeting() {
        runBlocking {
            val transactionHash = greeter.newGreeting("Hello World").await()
            transactionHash.length.`should equal to`(66)
        }
    }

    @Test
    fun greet() {
        runBlocking {
            val greet = greeter.greet().await()
            greet.`should equal to`("Hello World")
        }
    }
}