package cc.etherspace.example

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.EditText
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import javax.inject.Inject

class MainActivity : AppCompatActivity(), AnkoLogger {
    @Inject
    lateinit var greeter: Greeter
    private lateinit var greeting: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ExampleApp.components.inject(this)

        verticalLayout {
            padding = dip(30)

            textView("Greeting:")
            greeting = editText("")

            space { }.lparams(height = dip(30))

            linearLayout {
                button("Read") {
                    onClick {
                        greet()
                    }
                }.lparams {
                    weight = 1f
                }

                button("Update") {
                    onClick {
                        newGreeting()
                    }
                }.lparams {
                    weight = 1f
                }
            }.lparams(width = matchParent)
        }
    }

    private suspend fun greet() {
        greeting.isEnabled = false
        greeting.setText(greeter.greet().await())
        greeting.isEnabled = true
        toast("greet: ${greeting.text}")
    }

    private suspend fun newGreeting() {
        greeting.isEnabled = false
        greeter.newGreeting(greeting.text.toString()).await()
        greeting.isEnabled = true
        toast("newGreeting: ${greeting.text}")
    }
}
