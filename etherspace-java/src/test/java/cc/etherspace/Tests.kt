package cc.etherspace

import cc.etherspace.calladapter.CoroutineCallAdapter
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.web3j.crypto.Keys
import java.io.File

object Tests {
    const val TEST_WALLET_KEY = "0xab1e199623aa5bb2c381c349b1734e31b5be08de0486ffab68e3af4853d9980b"
    @JvmField
    val TEST_WALLET_ADDRESS = Keys.toChecksumAddress("0x39759a3c0ada2d61b6ca8eb6afc8243075307ed3")!!
    @JvmField
    val TEST_CONTRACT_ADDRESS = Keys.toChecksumAddress("0xa871c507184ecfaf947253e187826c1907e8dc7d")!!

    fun createEtherSpace(logLevel: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.NONE): EtherSpace {
        val interceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message -> println(message) })
        interceptor.level = logLevel
        val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build()

        return EtherSpace.build {
            client = okHttpClient
            credentials = WalletCredentials("0xc5aea8fb8b669d27da1c644064fb4cd80b7d3c9b7d2ebcab2bfade236834c784")
            callAdapters += CoroutineCallAdapter()
        }
    }

    fun <T> createContract(etherSpace: EtherSpace, clazz: Class<T>, objectMapper: ObjectMapper): T {
        val contractAddress = objectMapper.readValue(File("build/contracts/greeter.json"),
                LocalGreeterTest.ContractMetaData::class.java).networks.values.first().address
        return etherSpace.create(contractAddress, clazz)
    }

    fun <T> createContract(etherSpace: EtherSpace, clazz: Class<T>, contractAddress: String): T {
        return etherSpace.create(contractAddress, clazz)
    }
}
