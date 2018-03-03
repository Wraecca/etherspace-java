package cc.etherspace.example

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

class ExampleApp : Application() {
    companion object {
        lateinit var components: ExampleComponent
    }

    override fun onCreate() {
        super.onCreate()
        components = DaggerExampleComponent.builder()
                .exampleModule(ExampleModule(this))
                .build()
        components.inject(this)

        AndroidThreeTen.init(this)
    }
}