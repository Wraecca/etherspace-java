package cc.etherspace

import java.io.IOException
import java.math.BigInteger

interface Greeter {
    @Throws(IOException::class)
    @Send
    fun newGreeting(greeting: String): TransactionReceipt

    @Throws(IOException::class)
    @Send(functionName = "newGreeting")
    fun newGreeting_functionName(greeting: String): TransactionReceipt

    @Throws(IOException::class)
    @Send(functionName = "newGreeting")
    fun newGreeting_transactionHash(greeting: String): TransactionHash

    @Throws(IOException::class)
    @Send
    fun newGreeting(greeting: String, options: Options): String

    @Throws(IOException::class)
    @Call
    fun twoDimensionArray(twoDimensionArray: List<SolArray5<SolUint256>>, row: SolUint256, col: SolUint256): SolUint256

    @Throws(IOException::class)
    @Call
    fun greet(): String

    @Throws(IOException::class)
    @Call
    fun greet_wrongFunctionName(): String

    @Throws(IOException::class)
    @Call(functionName = "greet")
    fun greet_functionName(): String

    @Throws(IOException::class)
    @Send
    fun newPersonalGreeting(from: String, greeting: String): String

    @Throws(IOException::class)
    @Call
    fun personalGreet(): Pair<String, String>

    @Throws(IOException::class)
    @Call
    fun multipleReturns(): Tuple7<String, String, String, String, String, String, String>

    @Throws(IOException::class)
    @Call
    fun boolType(bool: Boolean): Boolean

    @Throws(IOException::class)
    @Call
    fun intType(int: BigInteger): BigInteger

    @Throws(IOException::class)
    @Call
    fun uintType(int: SolUint256): SolUint256

    @Throws(IOException::class)
    @Call
    fun int24Type(solInt: SolInt24): SolInt24

    @Throws(IOException::class)
    @Call
    fun addressType(solAddress: SolAddress): SolAddress

    @Throws(IOException::class)
    @Call
    fun uintsType(uints: List<SolUint256>): List<SolUint256>

    @Throws(IOException::class)
    @Call
    fun byteType(byte: Byte): Byte

    @Throws(IOException::class)
    @Call
    fun bytes15Type(aByte: SolBytes15): SolBytes15

    @Throws(IOException::class)
    @Call
    fun bytes32Type(aByte: SolBytes32): SolBytes32

    @Throws(IOException::class)
    @Call
    fun bytesType(byte: ByteArray): ByteArray

    data class Modified @EventConstructor constructor(@Indexed(String::class) val oldGreetingIdx: SolBytes32,
                                                      @Indexed(String::class) val newGreetingIdx: SolBytes32,
                                                      val oldGreeting: String,
                                                      val newGreeting: String)
}