package cc.etherspace.wallet

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import cc.etherspace.Credentials
import cc.etherspace.EtherSpace
import cc.etherspace.Web3
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.jetbrains.anko.*
import org.jetbrains.anko.coroutines.experimental.Ref
import org.jetbrains.anko.coroutines.experimental.asReference
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.web3j.utils.Convert
import javax.inject.Inject

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
class MainActivity : AppCompatActivity(), AnkoLogger {
    @Inject
    lateinit var objectMapper: ObjectMapper
    @Inject
    lateinit var etherSpace: EtherSpace
    @Inject
    lateinit var credentials: Credentials
    private lateinit var to: EditText
    private lateinit var amount: EditText
    private lateinit var gasPrice: EditText
    private lateinit var gasLimit: EditText
    private lateinit var submit: Button
    private lateinit var tx: Web3.TransactionObject

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WalletApp.components.inject(this)

        verticalLayout {
            padding = dip(30)
            textView("To")
            to = editText {
                keyListener = null
            }
            textView("Amount")
            amount = editText {
                keyListener = null
            }
            textView("Gas Price")
            gasPrice = editText {
                keyListener = null
            }
            textView("Gas Limit")
            gasLimit = editText {
                keyListener = null
            }

            space { }.lparams(height = dip(30))

            submit = button("Submit") {
                isEnabled = false
                onClick {
                    submit()
                }
            }
        }

        if (Intent.ACTION_SEND == intent.action && intent.scheme != null) {
            if ("etherspace" == intent.scheme) {
                tx = objectMapper.readValue(intent.getStringExtra(Intent.EXTRA_TEXT))
                debug { "tx:$tx" }

                to.setText(tx.to)
                amount.setText(Convert.fromWei(tx.value.toBigDecimal(), Convert.Unit.ETHER).toPlainString())
                gasPrice.setText(tx.gasPrice.toString())
                gasLimit.setText(tx.gas.toString())
                submit.isEnabled = true
            }
        }
    }

    private suspend fun submit() {
        val ref = asReference()
        val signedMessage = bg { sign(tx) }
        sendIntentResult(signedMessage.await(), ref)
    }

    private suspend fun sendIntentResult(signedMessage: String, ref: Ref<MainActivity>) {
        val resultIntent = Intent()
        resultIntent.putExtra(Intent.EXTRA_TEXT, signedMessage)
        ref().setResult(Activity.RESULT_OK, resultIntent)
        ref().finish()
    }

    private fun sign(transactionObject: Web3.TransactionObject): String =
            etherSpace.web3.eth.signTransaction(transactionObject, credentials)
}
