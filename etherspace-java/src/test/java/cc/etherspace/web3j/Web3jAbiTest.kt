package cc.etherspace.web3j

import cc.etherspace.*
import com.google.common.reflect.TypeToken
import org.amshove.kluent.`should equal to`
import org.amshove.kluent.`should equal`
import org.junit.Before
import org.junit.Test
import java.math.BigInteger

class Web3jAbiTest {
    private lateinit var web3jAbi: Web3jAbi

    @Before
    fun setUp() {
        web3jAbi = Web3jAbi()
    }

    @Test
    fun encodeFunctionCall_newGreeting() {
        val enc = web3jAbi.encodeFunctionCall(listOf("hello world"), "newGreeting")
        enc.`should equal to`("0x4ac0d66e0000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000000b68656c6c6f20776f726c64000000000000000000000000000000000000000000")
    }

    @Test
    fun encodeFunctionCall_greet() {
        val enc = web3jAbi.encodeFunctionCall(emptyList(), "greet")
        enc.`should equal to`("0xcfae3217")
    }

    @Test
    fun encodeFunctionCall_newPersonalGreeting() {
        val enc = web3jAbi.encodeFunctionCall(listOf("馮彥永", "hello world"), "newPersonalGreeting")
        enc.`should equal to`("0x2897e7d1000000000000000000000000000000000000000000000000000000000000004000000000000000000000000000000000000000000000000000000000000000800000000000000000000000000000000000000000000000000000000000000009e9a6aee5bda5e6b0b80000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000b68656c6c6f20776f726c64000000000000000000000000000000000000000000")
    }

    @Test
    fun encodeFunctionCall_boolType() {
        val enc = web3jAbi.encodeFunctionCall(listOf(true), "boolType")
        enc.`should equal to`("0xfa778a390000000000000000000000000000000000000000000000000000000000000001")
    }

    @Test
    fun decodeParameters_bool() {
        val list = web3jAbi.decodeParameters(listOf(Boolean::class.java),
                "0000000000000000000000000000000000000000000000000000000000000001")
        list.`should equal`(listOf(true))
    }

    @Test
    fun encodeFunctionCall_intType() {
        val enc = web3jAbi.encodeFunctionCall(listOf(BigInteger.TEN), "intType")
        enc.`should equal to`("0xbf48b1eb000000000000000000000000000000000000000000000000000000000000000a")
    }

    @Test
    fun decodeParameters_int() {
        val list = web3jAbi.decodeParameters(listOf(BigInteger::class.java),
                "000000000000000000000000000000000000000000000000000000000000000a")
        list.`should equal`(listOf(BigInteger.valueOf(10)))
    }

    @Test
    fun encodeFunctionCall_negativeInt() {
        val enc = web3jAbi.encodeFunctionCall(listOf(BigInteger.valueOf(-10)), "intType")
        enc.`should equal to`("0xbf48b1ebfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff6")
    }

    @Test
    fun decodeParameters_negativeInt() {
        val list = web3jAbi.decodeParameters(listOf(BigInteger::class.java),
                "fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff6")
        list.`should equal`(listOf(BigInteger.valueOf(-10)))
    }

    @Test
    fun encodeFunctionCall_uintType() {
        val enc = web3jAbi.encodeFunctionCall(listOf(UBigInteger(10)), "uintType")
        enc.`should equal to`("0xf8c5f14f000000000000000000000000000000000000000000000000000000000000000a")
    }

    @Test
    fun decodeParameters_uint() {
        val list = web3jAbi.decodeParameters(listOf(UBigInteger::class.java),
                "000000000000000000000000000000000000000000000000000000000000000a")
        list.`should equal`(listOf(UBigInteger(10)))
    }

    @Test
    fun encodeFunctionCall_largeInt() {
        val enc = web3jAbi.encodeFunctionCall(listOf(UBigInteger(BigInteger("76508586243155862545955748917616667912411098067151927646783046335856770806840"))),
                "uintType")
        enc.`should equal to`("0xf8c5f14fa9265342bc3152636f86592c0f1ebf41106f6d6a79e131310a3a543db4cbf438")
    }

    @Test
    fun decodeParameters_largeInt() {
        val list = web3jAbi.decodeParameters(listOf(UBigInteger::class.java),
                "a9265342bc3152636f86592c0f1ebf41106f6d6a79e131310a3a543db4cbf438")
        list.`should equal`(listOf(UBigInteger(BigInteger("76508586243155862545955748917616667912411098067151927646783046335856770806840"))))
    }

    @Test
    fun encodeFunctionCall_int24Type() {
        val enc = web3jAbi.encodeFunctionCall(listOf(SolInt24(10)), "int24Type")
        enc.`should equal to`("0x338e98c6000000000000000000000000000000000000000000000000000000000000000a")
    }

    @Test
    fun decodeParameters_int24() {
        val list = web3jAbi.decodeParameters(listOf(SolInt24::class.java),
                "000000000000000000000000000000000000000000000000000000000000000a")
        list.`should equal`(listOf(SolInt24(10)))
    }

    @Test
    fun encodeFunctionCall_uint8Type() {
        val enc = web3jAbi.encodeFunctionCall(listOf(SolUint8(10)), "uint8Type")
        enc.`should equal to`("0xaf47a185000000000000000000000000000000000000000000000000000000000000000a")
    }

    @Test
    fun decodeParameters_uint8() {
        val list = web3jAbi.decodeParameters(listOf(SolUint8::class.java),
                "000000000000000000000000000000000000000000000000000000000000000a")
        list.`should equal`(listOf(SolUint8(10)))
    }

    @Test
    fun encodeFunctionCall_addressType() {
        val enc = web3jAbi.encodeFunctionCall(listOf(SolAddress("0xa871c507184ecfaf947253e187826c1907e8dc7d")),
                "addressType")
        enc.`should equal to`("0x99ddb29b000000000000000000000000a871c507184ecfaf947253e187826c1907e8dc7d")
    }

    @Test
    fun decodeParameters_address() {
        val list = web3jAbi.decodeParameters(listOf(SolAddress::class.java),
                "000000000000000000000000a871c507184ecfaf947253e187826c1907e8dc7d")
        list.`should equal`(listOf(SolAddress("0xa871c507184ecfaf947253e187826c1907e8dc7d")))
    }

    @Test
    fun encodeFunctionCall_uintsType() {
        val enc = web3jAbi.encodeFunctionCall(listOf(arrayOf(UBigInteger(1),
                UBigInteger(2),
                UBigInteger(3),
                UBigInteger(4))),
                "uintsType")
        enc.`should equal to`("0xf430746a0000000000000000000000000000000000000000000000000000000000000001000000000000000000000000000000000000000000000000000000000000000200000000000000000000000000000000000000000000000000000000000000030000000000000000000000000000000000000000000000000000000000000004")
    }

    @Test
    fun decodeParameters_uints() {
        val token = object : TypeToken<SolArray4<UBigInteger>>() {}
        val list = web3jAbi.decodeParameters(listOf(token.type),
                "0000000000000000000000000000000000000000000000000000000000000001000000000000000000000000000000000000000000000000000000000000000200000000000000000000000000000000000000000000000000000000000000030000000000000000000000000000000000000000000000000000000000000004")
        list.`should equal`(listOf(SolArray4(arrayOf(UBigInteger(1), UBigInteger(2), UBigInteger(3), UBigInteger(4)))))
    }

    @Test
    fun encodeFunctionCall_dynIntsType() {
        val enc = web3jAbi.encodeFunctionCall(listOf(listOf(BigInteger.valueOf(1), BigInteger.valueOf(2))),
                "dynIntsType")
        enc.`should equal to`("0xd778d7460000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000000200000000000000000000000000000000000000000000000000000000000000010000000000000000000000000000000000000000000000000000000000000002")
    }

    @Test
    fun decodeParameters_dynInts() {
        val token = object : TypeToken<List<BigInteger>>() {}
        val list = web3jAbi.decodeParameters(listOf(token.type),
                "0000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000000200000000000000000000000000000000000000000000000000000000000000010000000000000000000000000000000000000000000000000000000000000002")
        list.`should equal`(listOf(listOf(1.toBigInteger(), 2.toBigInteger())))
    }

    @Test
    fun encodeFunctionCall_byteType() {
        val enc = web3jAbi.encodeFunctionCall(listOf(1.toByte()), "byteType")
        enc.`should equal to`("0xf34bf9850100000000000000000000000000000000000000000000000000000000000000")
    }

    @Test
    fun decodeParameters_byte() {
        val list = web3jAbi.decodeParameters(listOf(Byte::class.java),
                "0100000000000000000000000000000000000000000000000000000000000000")
        list.`should equal`(listOf(1.toByte()))
    }

    @Test
    fun encodeFunctionCall_bytes15Type() {
        val enc = web3jAbi.encodeFunctionCall(listOf(SolBytes15("123456789012345".toByteArray(Charsets.US_ASCII))),
                "bytes15Type")
        enc.`should equal to`("0x8f31808f3132333435363738393031323334350000000000000000000000000000000000")
    }

    @Test
    fun decodeParameters_bytes15() {
        val list = web3jAbi.decodeParameters(listOf(SolBytes15::class.java),
                "3132333435363738393031323334350000000000000000000000000000000000")
        list.map { String((it as SolBytes15).value) }.`should equal`(listOf("123456789012345"))
    }

    @Test
    fun encodeFunctionCall_bytes32Type() {
        val enc = web3jAbi.encodeFunctionCall(listOf(SolBytes32("12345678901234567890123456789012".toByteArray(Charsets.US_ASCII))),
                "bytes32Type")
        enc.`should equal to`("0xb02ecf793132333435363738393031323334353637383930313233343536373839303132")
    }

    @Test
    fun decodeParameters_bytes32() {
        val list = web3jAbi.decodeParameters(listOf(SolBytes32::class.java),
                "3132333435363738393031323334353637383930313233343536373839303132")
        list.map { String((it as SolBytes32).value) }.`should equal`(listOf("12345678901234567890123456789012"))
    }

    @Test
    fun encodeFunctionCall_bytesType() {
        val enc = web3jAbi.encodeFunctionCall(listOf("123456789012345678901234".toByteArray(Charsets.US_ASCII)),
                "bytesType")
        enc.`should equal to`("0x4e76fb0d000000000000000000000000000000000000000000000000000000000000002000000000000000000000000000000000000000000000000000000000000000183132333435363738393031323334353637383930313233340000000000000000")
    }

    @Test
    fun decodeParameters_bytes() {
        val list = web3jAbi.decodeParameters(listOf(ByteArray::class.java),
                "000000000000000000000000000000000000000000000000000000000000002000000000000000000000000000000000000000000000000000000000000000183132333435363738393031323334353637383930313233340000000000000000")
        list.map { String(it as ByteArray) }.`should equal`(listOf("123456789012345678901234"))
    }

    @Test
    fun encodeFunctionCall_myMethod() {
        val enc = web3jAbi.encodeFunctionCall(listOf(UBigInteger(2345675643), "Hello!%"),
                "myMethod")
        enc.`should equal to`("0x24ee0097000000000000000000000000000000000000000000000000000000008bd02b7b0000000000000000000000000000000000000000000000000000000000000040000000000000000000000000000000000000000000000000000000000000000748656c6c6f212500000000000000000000000000000000000000000000000000")
    }

    @Test
    fun decodeParameters_myMethod() {
        val list = web3jAbi.decodeParameters(listOf(String::class.java, UBigInteger::class.java),
                "000000000000000000000000000000000000000000000000000000000000004000000000000000000000000000000000000000000000000000000000000000ea000000000000000000000000000000000000000000000000000000000000000848656c6c6f212521000000000000000000000000000000000000000000000000")
        list.`should equal`(listOf("Hello!%!", UBigInteger(234)))
    }
}