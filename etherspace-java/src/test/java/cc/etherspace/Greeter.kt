package cc.etherspace

import java.math.BigInteger

interface Greeter {
    @Send
    fun newGreeting(greeting: String): String

    @Send
    fun newGreeting(greeting: String, options: EtherSpace.Options): String

    @Call
    fun greet(): String

    fun newPersonalGreeting(from: String, greeting: String): String

    @Call
    fun personalGreet(): Pair<String, String>

    @Call
    fun multipleReturns(): Tuple7<String, String, String, String, String, String, String>

    @Call
    fun boolType(bool: Boolean): Boolean

    @Call
    fun intType(int: BigInteger): BigInteger

    @Call
    fun uintType(int: UBigInteger): UBigInteger

    @Call
    fun int24Type(solInt: SolInt24): SolInt24

    @Call
    fun addressType(solAddress: SolAddress): SolAddress

    @Call
    fun uintsType(uints: List<UBigInteger>): List<UBigInteger>

    @Call
    fun byteType(byte: Byte): Byte

    @Call
    fun bytes15Type(aByte: SolBytes15): SolBytes15

    @Call
    fun bytes32Type(aByte: SolBytes32): SolBytes32

    @Call
    fun bytesType(byte: ByteArray): ByteArray
}