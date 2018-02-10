package cc.etherspace

import java.math.BigInteger

interface Greeter {
    fun newGreeting(greeting: String): String

    fun newGreeting(greeting: String, options: EtherSpace.Options): String

    @View
    fun greet(): String

    fun newPersonalGreeting(from: String, greeting: String): String

    @View
    fun personalGreet(): Pair<String, String>

    @View
    fun multipleReturns(): Tuple7<String, String, String, String, String, String, String>

    @View
    fun boolType(bool: Boolean): Boolean

    @View
    fun intType(int: BigInteger): BigInteger

    @View
    fun uintType(int: UBigInteger): UBigInteger

    @View
    fun int24Type(solInt: SolInt24): SolInt24

    @View
    fun addressType(solAddress: SolAddress): SolAddress

    @View
    fun uintsType(uints: List<UBigInteger>): List<UBigInteger>

    @View
    fun byteType(byte: Byte): Byte

    @View
    fun bytes15Type(aByte: SolBytes15): SolBytes15

    @View
    fun bytes32Type(aByte: SolBytes32): SolBytes32

    @View
    fun bytesType(byte: ByteArray): ByteArray
}