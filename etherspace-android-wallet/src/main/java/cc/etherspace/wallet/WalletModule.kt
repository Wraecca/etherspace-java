package cc.etherspace.wallet

import android.app.Application
import android.content.Context
import cc.etherspace.Credentials
import cc.etherspace.EtherSpace
import cc.etherspace.calladapter.CoroutineCallAdapter
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Suppress("unused")
@Module
class WalletModule(private val application: Application) {
    @Provides
    @Singleton
    @Named("applicationContext")
    fun provideApplicationContext(): Context = application

    @Provides
    @Singleton
    fun provideCredentials(): Credentials = Credentials("0xab1e199623aa5bb2c381c349b1734e31b5be08de0486ffab68e3af4853d9980b")

    @Provides
    @Singleton
    fun provideEtherSpace(credentials: Credentials): EtherSpace {
        // Please fill in your private key or wallet file.
        return EtherSpace.Builder()
                .provider("https://rinkeby.infura.io/")
                .credentials(credentials)
                .addCallAdapter(CoroutineCallAdapter())
                .build()

        // Won't compile. A dagger bug?
//        return EtherSpace.build {
//            provider = "https://rinkeby.infura.io/"
//            credentials = Credentials(Tests.TEST_WALLET_KEY)
//            callAdapters += CoroutineCallAdapter()
//        }
    }

    @Provides
    @Singleton
    fun provideObjectMapper(): ObjectMapper = jacksonObjectMapper()
}