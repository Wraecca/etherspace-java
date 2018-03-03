package cc.etherspace.example

import cc.etherspace.Call
import cc.etherspace.Options
import cc.etherspace.Send
import cc.etherspace.TransactionReceipt
import kotlinx.coroutines.experimental.Deferred
import java.io.IOException

interface Greeter {
    @Throws(IOException::class)
    @Send
    fun newGreeting(greeting: String): Deferred<TransactionReceipt>

    @Throws(IOException::class)
    @Send
    fun newGreeting(greeting: String, options: Options): Deferred<TransactionReceipt>

    @Throws(IOException::class)
    @Call
    fun greet(): Deferred<String>
}
