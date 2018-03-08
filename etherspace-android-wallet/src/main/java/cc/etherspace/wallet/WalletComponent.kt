package cc.etherspace.wallet

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [WalletModule::class])
interface WalletComponent {
    fun inject(walletApp: WalletApp)
    fun inject(mainActivity: MainActivity)
}