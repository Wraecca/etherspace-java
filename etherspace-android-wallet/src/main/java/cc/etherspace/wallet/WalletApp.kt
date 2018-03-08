package cc.etherspace.wallet

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

class WalletApp : Application() {
    companion object {
        lateinit var components: WalletComponent
    }

    override fun onCreate() {
        super.onCreate()
        components = DaggerWalletComponent.builder()
                .walletModule(WalletModule(this))
                .build()
        components.inject(this)

        AndroidThreeTen.init(this)
    }
}