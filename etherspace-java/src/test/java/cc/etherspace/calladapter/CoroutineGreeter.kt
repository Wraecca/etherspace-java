package cc.etherspace.calladapter

import cc.etherspace.*
import kotlinx.coroutines.Deferred
import java.io.IOException

interface CoroutineGreeter {
    @Throws(IOException::class)
    @Send
    fun newGreeting(greeting: String): Deferred<TransactionReceipt>

    @Throws(IOException::class)
    @Send
    fun newGreeting(greeting: String, options: Options): Deferred<TransactionReceipt>

    @Throws(IOException::class)
    @Send(functionName = "newGreeting")
    fun newGreeting_transactionHash(greeting: String): Deferred<TransactionHash>

    @Throws(IOException::class)
    @Call
    fun greet(): Deferred<String>

    @Throws(IOException::class)
    @Call
    fun greet_wrongFunctionName(): Deferred<String>

    @Throws(IOException::class)
    @Call
    fun boolType(bool: Boolean): Deferred<Boolean>

    data class Modified @EventConstructor constructor(@Indexed(value = String::class) val oldGreetingIdx: SolBytes32,
                                                      @Indexed(value = String::class) val newGreetingIdx: SolBytes32,
                                                      val oldGreeting: String,
                                                      val newGreeting: String)
}