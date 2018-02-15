package cc.etherspace.calladapter

import cc.etherspace.Call
import cc.etherspace.Send
import kotlinx.coroutines.experimental.Deferred

interface CoroutineGreeter {
    @Send
    fun newGreeting(greeting: String): Deferred<String>

    @Call
    fun greet(): Deferred<String>
}