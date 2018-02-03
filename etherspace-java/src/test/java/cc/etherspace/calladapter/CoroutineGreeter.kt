package cc.etherspace.calladapter

import cc.etherspace.View
import kotlinx.coroutines.experimental.Deferred

interface CoroutineGreeter {
    fun newGreeting(greeting: String): Deferred<String>

    @View
    fun greet(): Deferred<String>
}