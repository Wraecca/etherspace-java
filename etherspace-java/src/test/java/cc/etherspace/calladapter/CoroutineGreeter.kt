package cc.etherspace.calladapter

import cc.etherspace.*
import kotlinx.coroutines.experimental.Deferred

interface CoroutineGreeter {
    @Send
    fun newGreeting(greeting: String): Deferred<TransactionReceipt>

    @Call
    fun greet(): Deferred<String>

    @Send
    fun newPersonalGreeting(from: String, greeting: String): Deferred<String>

    @Call
    fun personalGreet(): Deferred<Pair<String, String>>

    data class Modified @EventConstructor constructor(@Indexed(argumentType = String::class) val oldGreetingIdx: SolBytes32,
                                                      @Indexed(argumentType = String::class) val newGreetingIdx: SolBytes32,
                                                      val oldGreeting: String,
                                                      val newGreeting: String)
}