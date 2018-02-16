package cc.etherspace

import org.amshove.kluent.`should be greater than`
import org.amshove.kluent.`should equal to`
import org.amshove.kluent.`should equal`
import org.junit.Before
import org.junit.Test
import org.web3j.crypto.Credentials
import org.web3j.crypto.ECKeyPair
import java.io.IOException
import java.math.BigInteger

class GreaterTest {
    private lateinit var greeter: Greeter

    @Before
    fun setUp() {
        val etherSpace = EtherSpace.build {
            provider = "https://rinkeby.infura.io/3teU4WimZ2pbdjPUDpPW"
            credentials = Credentials.create(ECKeyPair.create(
                    BigInteger("77398679111088585283982189543320298238063257726010371587476264149399587362827")))
        }
        greeter = etherSpace.create(SolAddress("0xa871c507184ecfaf947253e187826c1907e8dc7d"), Greeter::class.java)
    }

    @Test
    fun newGreeting() {
        val receipt = greeter.newGreeting("Hello World")
        receipt.blockHash.length.`should equal to`(66)
        receipt.transactionHash.length.`should equal to`(66)
        receipt.from.`should equal to`("0x39759a3c0ada2d61b6ca8eb6afc8243075307ed3")
        receipt.to.`should equal to`("0xa871c507184ecfaf947253e187826c1907e8dc7d")
        receipt.logs.size.`should be greater than`(0)
    }

    @Test(expected = IOException::class)
    fun newGreeting_options() {
        val transactionHash = greeter.newGreeting("Hello World",
                EtherSpace.Options(gas = BigInteger.valueOf(44_000_000_000L)))
        transactionHash.length.`should equal to`(66)
    }

    @Test
    fun greet() {
        val greet = greeter.greet()
        greet.`should equal to`("Hello World")
    }

    fun newPersonalGreeting() {
        val transactionHash = greeter.newPersonalGreeting("tempo", "Hello World")
        transactionHash.length.`should equal to`(66)
    }

    @Test
    fun personalGreet() {
        val pair = greeter.personalGreet()
        pair.first.`should equal`("tempo")
        pair.second.`should equal`("Hello World")
    }

    @Test
    fun multipleReturns() {
        val ret = greeter.multipleReturns()
        ret.first.`should equal to`("1")
        ret.second.`should equal to`("2")
        ret.third.`should equal to`("3")
        ret.fourth.`should equal to`("4")
        ret.fifth.`should equal to`("5")
        ret.sixth.`should equal to`("6")
        ret.seventh.`should equal to`("7")
    }

    @Test
    fun boolType() {
        var b = greeter.boolType(true)
        b.`should equal to`(true)

        b = greeter.boolType(false)
        b.`should equal to`(false)
    }

    @Test
    fun intType() {
        val i = greeter.intType(BigInteger.valueOf(10L))
        i.`should equal`(BigInteger.valueOf(10))
    }

    @Test
    fun uintType() {
        val i = greeter.uintType(UBigInteger(10L))
        i.`should equal`(UBigInteger(10L))
    }

    @Test
    fun int24Type() {
        val i = greeter.int24Type(SolInt24(10))
        i.`should equal`(SolInt24(10))
    }

    @Test
    fun addressType() {
        val address = SolAddress("0xa871c507184ecfaf947253e187826c1907e8dc7d")
        val a = greeter.addressType(address)
        a.`should equal`(address)
    }

    @Test
    fun uintsType() {
        val uints = listOf(UBigInteger(10L), UBigInteger(11L), UBigInteger(11L))
        val i = greeter.uintsType(uints)
        i.`should equal`(uints)
    }

    @Test
    fun byteType() {
        val i = greeter.byteType(10)
        i.`should equal to`(10)
    }

    @Test
    fun bytes15Type() {
        val bytes = SolBytes15("123456789012345".toByteArray(Charsets.ISO_8859_1))
        val i = greeter.bytes15Type(bytes)
        i.`should equal`(bytes)
    }

    @Test
    fun bytes32Type() {
        val bytes = SolBytes32("12345678901234567890123456789012".toByteArray(Charsets.ISO_8859_1))
        val i = greeter.bytes32Type(bytes)
        i.`should equal`(bytes)
    }

    @Test
    fun bytesType() {
        val bytes = "123456789012345678901234".toByteArray(Charsets.ISO_8859_1)
        val i = greeter.bytesType(bytes)
        i.`should equal`(bytes)
    }
}