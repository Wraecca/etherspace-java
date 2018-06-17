package cc.etherspace

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be greater than`
import org.amshove.kluent.`should equal`
import org.junit.Before
import org.junit.Test
import org.web3j.utils.Numeric
import java.io.IOException
import java.math.BigInteger

class GreeterTest {
    private lateinit var greeter: Greeter

    @Before
    fun setUp() {
        val interceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message -> println(message) })
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build()

        val etherSpace = EtherSpace.build {
            client = okHttpClient
            provider = "https://rinkeby.infura.io/"
            credentials = Credentials(Tests.TEST_WALLET_KEY)
        }
        greeter = etherSpace.create(Tests.TEST_CONTRACT_ADDRESS, Greeter::class.java)
    }

    @Test
    fun newGreeting() {
        val receipt = greeter.newGreeting("Hello World")
        receipt.blockHash.length.`should be equal to`(66)
        receipt.transactionHash.length.`should be equal to`(66)
        receipt.from!!.`should be equal to`(Tests.TEST_WALLET_ADDRESS)
        receipt.to!!.`should be equal to`(Tests.TEST_CONTRACT_ADDRESS)
        receipt.logs.size.`should be greater than`(0)

        val events = receipt.listEvents(Greeter.Modified::class.java)
        events.size.`should be equal to`(1)
        events[0].event.`should be equal to`("Modified")
        events[0].returnValue.oldGreeting.`should be equal to`("Hello World")
        events[0].returnValue.newGreeting.`should be equal to`("Hello World")
        events[0].returnValue.oldGreetingIdx.`should equal`(SolBytes32(Numeric.hexStringToByteArray("0x592fa743889fc7f92ac2a37bb1f5ba1daf2a5c84741ca0e0061d243a2e6707ba")))
        events[0].returnValue.newGreetingIdx.`should equal`(SolBytes32(Numeric.hexStringToByteArray("0x592fa743889fc7f92ac2a37bb1f5ba1daf2a5c84741ca0e0061d243a2e6707ba")))
    }

    @Test
    fun newGreeting_functionName() {
        val receipt = greeter.newGreeting_functionName("Hello World")
        receipt.blockHash.length.`should be equal to`(66)
        receipt.transactionHash.length.`should be equal to`(66)
        receipt.from!!.`should be equal to`(Tests.TEST_WALLET_ADDRESS)
        receipt.to!!.`should be equal to`(Tests.TEST_CONTRACT_ADDRESS)
        receipt.logs.size.`should be greater than`(0)

        val events = receipt.listEvents(Greeter.Modified::class.java)
        events.size.`should be equal to`(1)
        events[0].event.`should be equal to`("Modified")
        events[0].returnValue.oldGreeting.`should be equal to`("Hello World")
        events[0].returnValue.newGreeting.`should be equal to`("Hello World")
        events[0].returnValue.oldGreetingIdx.`should equal`(SolBytes32(Numeric.hexStringToByteArray("0x592fa743889fc7f92ac2a37bb1f5ba1daf2a5c84741ca0e0061d243a2e6707ba")))
        events[0].returnValue.newGreetingIdx.`should equal`(SolBytes32(Numeric.hexStringToByteArray("0x592fa743889fc7f92ac2a37bb1f5ba1daf2a5c84741ca0e0061d243a2e6707ba")))
    }

    @Test(expected = IOException::class)
    fun newGreeting_options() {
        val transactionHash = greeter.newGreeting("Hello World",
                Options(gas = BigInteger.valueOf(44_000_000_000L)))
        transactionHash.length.`should be equal to`(66)
    }

    @Test
    fun newGreeting_transactionHash() {
        val transactionHash = greeter.newGreeting_transactionHash("Hello World")
        transactionHash.hash.length.`should be equal to`(66)
    }

    @Test
    fun greet() {
        val greet = greeter.greet()
        greet.`should be equal to`("Hello World")
    }

    @Test(expected = IllegalArgumentException::class)
    fun greet_wrongFunctionName() {
        val greet = greeter.greet_wrongFunctionName()
        greet.`should be equal to`("Hello World")
    }

    @Test
    fun greet_functionName() {
        val greet = greeter.greet_functionName()
        greet.`should be equal to`("Hello World")
    }

    fun newPersonalGreeting() {
        val transactionHash = greeter.newPersonalGreeting("tempo", "Hello World")
        transactionHash.length.`should be equal to`(66)
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
        ret.first.`should be equal to`("1")
        ret.second.`should be equal to`("2")
        ret.third.`should be equal to`("3")
        ret.fourth.`should be equal to`("4")
        ret.fifth.`should be equal to`("5")
        ret.sixth.`should be equal to`("6")
        ret.seventh.`should be equal to`("7")
    }

    @Test
    fun boolType() {
        var b = greeter.boolType(true)
        b.`should be equal to`(true)

        b = greeter.boolType(false)
        b.`should be equal to`(false)
    }

    @Test
    fun intType() {
        val i = greeter.intType(BigInteger.valueOf(10L))
        i.`should equal`(BigInteger.valueOf(10))
    }

    @Test
    fun uintType() {
        val i = greeter.uintType(SolUint256(10))
        i.`should equal`(SolUint256(10))
    }

    @Test
    fun int24Type() {
        val i = greeter.int24Type(SolInt24(10))
        i.`should equal`(SolInt24(10))
    }

    @Test
    fun addressType() {
        val address = SolAddress(Tests.TEST_CONTRACT_ADDRESS)
        val a = greeter.addressType(address)
        a.`should equal`(address)
    }

    @Test
    fun uintsType() {
        val uints = listOf(SolUint256(10), SolUint256(11), SolUint256(11))
        val i = greeter.uintsType(uints)
        i.`should equal`(uints)
    }

    @Test
    fun byteType() {
        val i = greeter.byteType(10)
        i.`should be equal to`(10)
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