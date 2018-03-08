package cc.etherspace.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import cc.etherspace.*
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.*
import org.jetbrains.anko.coroutines.experimental.Ref
import org.jetbrains.anko.coroutines.experimental.asReference
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.custom.style
import org.jetbrains.anko.sdk25.coroutines.onClick
import java.io.IOException
import javax.inject.Inject

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
class MainActivity : AppCompatActivity(), AnkoLogger {
    @Inject
    lateinit var greeter: Greeter
    @Inject
    lateinit var etherSpace: EtherSpace
    @Inject
    lateinit var credentials: Credentials
    @Inject
    lateinit var objectMapper: ObjectMapper
    private lateinit var greeting: EditText
    private lateinit var readButton: Button
    private lateinit var updateButton: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ExampleApp.components.inject(this)

        verticalLayout {
            padding = dip(30)

            textView("Greeting:")
            greeting = editText("")

            space { }.lparams(height = dip(30))

            linearLayout {
                readButton = button("Read") {
                    onClick {
                        greet()
                    }
                }.lparams {
                    weight = 1f
                }

                updateButton = button("Update") {
                    onClick {
                        newGreeting()
                    }
                }.lparams {
                    weight = 1f
                }
            }.lparams(width = matchParent)

            progressBar = progressBar{
                visibility = View.GONE
            }.lparams {
                width = matchParent
                height = wrapContent
            }
        }
    }

    private suspend fun greet() {
        disableInput()
        greeting.setText(greeter.greet().await())
        enableInput()
        toast("greet: ${greeting.text}")
    }

    private suspend fun newGreeting() {
        disableInput()
        val transactionObject = bg { createTransactionObject() }
        startWalletActivity(transactionObject.await(), this@MainActivity.asReference())
        enableInput()
    }

    private fun createTransactionObject(): Web3.TransactionObject {
        with(etherSpace.web3) {
            val encodedFunction = abi.encodeFunctionCall(listOf(greeting.text.toString()), "newGreeting")
            val nonce = eth.getTransactionCount(credentials.address)
            return Web3.TransactionObject(credentials.address,
                    "0x7c7fd86443a8a0b249080cfab29f231c31806527",
                    encodedFunction,
                    Options(credentials = credentials),
                    nonce)
        }
    }

    private suspend fun startWalletActivity(rawTx: Web3.TransactionObject, ref: Ref<MainActivity>) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.data = Uri.parse("etherspace:")
        sendIntent.putExtra(Intent.EXTRA_TEXT, objectMapper.writeValueAsString(rawTx))
        ref().startActivityForResult(sendIntent, RequestCode.REQUEST_USER.code)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode != RequestCode.REQUEST_USER.code) {
            return
        }

        if (resultCode != RESULT_OK) {
            toast("Transcation cancelled")
            return
        }

        toast("Sending transaction")
        launch(kotlinx.coroutines.experimental.android.UI) {
            disableInput()
            val transactionReceipt = bg {
                val signedMessage = data.getStringExtra(Intent.EXTRA_TEXT)
                sendSignedTransaction(signedMessage)
            }.await()
            enableInput()
            toast("newGreeting: ${greeting.text}, txHash: ${transactionReceipt.transactionHash}")
        }
    }

    private fun enableInput() {
        listOf(greeting, readButton, updateButton).forEach { it.isEnabled = true }
        progressBar.visibility = View.GONE
    }

    private fun disableInput() {
        listOf(greeting, readButton, updateButton).forEach { it.isEnabled = false }
        progressBar.visibility = View.VISIBLE
    }

    private fun sendSignedTransaction(signedMessage: String): TransactionReceipt {
        val transactionHash = etherSpace.web3.eth.sendSignedTransaction(signedMessage)
        for (i in 1..40) {
            val transactionReceipt = etherSpace.web3.eth.getTransactionReceipt(transactionHash)
            if (transactionReceipt != null) {
                return transactionReceipt
            }
            Thread.sleep(5 * 1000)
        }
        throw IOException("Unable to get transaction receipt because of timeout.")
    }

    enum class RequestCode(val code: Int) {
        REQUEST_USER(1)
    }
}
