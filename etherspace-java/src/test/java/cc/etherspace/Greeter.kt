package cc.etherspace

import java.math.BigInteger

interface Greeter {
    @Send
    fun newGreeting(greeting: String): TransactionReceipt

    @Send
    fun newGreeting(greeting: String, options: EtherSpace.Options): String

    @Call
    fun greet(): String

    @Send
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
    fun uintType(int: SolUint256): SolUint256

    @Call
    fun int24Type(solInt: SolInt24): SolInt24

    @Call
    fun addressType(solAddress: SolAddress): SolAddress

    @Call
    fun uintsType(uints: List<SolUint256>): List<SolUint256>

    @Call
    fun byteType(byte: Byte): Byte

    @Call
    fun bytes15Type(aByte: SolBytes15): SolBytes15

    @Call
    fun bytes32Type(aByte: SolBytes32): SolBytes32

    @Call
    fun bytesType(byte: ByteArray): ByteArray

    data class Modified @EventConstructor constructor(@Indexed(argumentType = String::class) val oldGreetingIdx: SolBytes32,
                                                      @Indexed(argumentType = String::class) val newGreetingIdx: SolBytes32,
                                                      val oldGreeting: String,
                                                      val newGreeting: String)
}