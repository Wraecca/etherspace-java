package cc.etherspace

import unsigned.Uint
import java.math.BigInteger

interface Greeter {
    fun newGreeting(greeting: String): String

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
    fun int24Type(int: Int24): Int24

    @View
    fun addressType(address: Address): Address

    @View
    fun uintsType(uints: List<UBigInteger>): List<UBigInteger>

    @View
    fun byteType(byte: Byte): Byte

    @View
    fun bytes15Type(byte: Bytes15): Bytes15

    @View
    fun bytes32Type(byte: Bytes32): Bytes32

    @View
    fun bytesType(byte: ByteArray): ByteArray
}