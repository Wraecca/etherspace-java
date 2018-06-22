package cc.etherspace.example

import cc.etherspace.Call
import cc.etherspace.Options
import cc.etherspace.Send
import cc.etherspace.TransactionHash
import kotlinx.coroutines.experimental.Deferred
import java.io.IOException

interface Greeter {
    @Throws(IOException::class)
    @Send
    fun newGreeting(greeting: String): Deferred<TransactionHash>

    @Throws(IOException::class)
    @Send
    fun newGreeting(greeting: String, options: Options): Deferred<TransactionHash>

    @Throws(IOException::class)
    @Call
    fun greet(): Deferred<String>
}
