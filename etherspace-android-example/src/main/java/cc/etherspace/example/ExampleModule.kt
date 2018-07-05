package cc.etherspace.example

import android.app.Application
import android.content.Context
import cc.etherspace.WalletCredentials
import cc.etherspace.EtherSpace
import cc.etherspace.calladapter.CoroutineCallAdapter
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Suppress("unused")
@Module
class ExampleModule(private val application: Application) {
    @Provides
    @Singleton
    @Named("applicationContext")
    fun provideApplicationContext(): Context = application

    @Provides
    @Singleton
    fun provideEtherSpace(): EtherSpace {
        // Please fill in your private key or wallet file.
        return EtherSpace.Builder()
                .provider("https://rinkeby.infura.io/")
                .credentials(WalletCredentials("YOUR_PRIVATE_KEY_OR_WALLET"))
                .addCallAdapter(CoroutineCallAdapter())
                .build()

        // Won't compile. A dagger bug?
//        return EtherSpace.build {
//            provider = "https://rinkeby.infura.io/"
//            credentials = WalletCredentials(Tests.TEST_WALLET_KEY)
//            callAdapters += CoroutineCallAdapter()
//        }
    }

    /**
     * The greeter smart contract has already been deployed to this address on rinkeby.
     */
    @Provides
    @Singleton
    fun provideGreeter(etherSpace: EtherSpace): Greeter =
            etherSpace.create("0x7c7fd86443a8a0b249080cfab29f231c31806527", Greeter::class.java)
}