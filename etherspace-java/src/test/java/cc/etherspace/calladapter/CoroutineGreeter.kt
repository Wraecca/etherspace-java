package cc.etherspace.calladapter

import cc.etherspace.Call
import cc.etherspace.Send
import cc.etherspace.TransactionReceipt
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
}