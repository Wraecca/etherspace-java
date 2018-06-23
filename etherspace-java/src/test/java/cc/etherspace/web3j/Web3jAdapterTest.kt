package cc.etherspace.web3j

import cc.etherspace.Web3
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.amshove.kluent.`should be equal to`
import org.junit.Before
import org.junit.Test
import org.web3j.crypto.Keys
import org.web3j.crypto.Sign
import org.web3j.utils.Numeric
import java.math.BigInteger

class Web3jAdapterTest {
    lateinit var web3jAdapter: Web3jAdapter
    lateinit var privateKey: String
    lateinit var publicKey: BigInteger
    lateinit var address: String

    @Before
    fun setUp() {
        val interceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message -> println(message) })
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build()
        web3jAdapter = Web3jAdapter("http://localhost:8545", okHttpClient)

        privateKey = "0xf2f48ee19680706196e2e339e5da3491186e0c4c5030670656b0e0164837257d"
        publicKey = Sign.publicKeyFromPrivate(Numeric.toBigInt(privateKey))
        address = Keys.toChecksumAddress(Numeric.prependHexPrefix(Keys.getAddress(publicKey)))
    }

    @Test
    fun sign() {
//        val sig = web3jAdapter.eth.sign("0x48540bdede62ae1b3868651336d31d36c29100af9db1559945879d14532a8067", "0x5409ed021d9299bf6814279a6a1411a7e866a631")
//        sig.`should be equal to`("0x342ddadac7d370278559a5cbf0b80a600a2945828434b1d5a7dfb0d7f9f64e3270757c5a9bad86a90c89951c078ff45e0830c99aa313576d7dba349568251a9c01")

        val data = Numeric.hexStringToByteArray("0x48540bdede62ae1b3868651336d31d36c29100af9db1559945879d14532a8067")
        val signature = web3jAdapter.eth.accounts.sign(data, privateKey)

        signature.v.`should be equal to`(28)
        Numeric.toHexString(signature.r).`should be equal to`("0x342ddadac7d370278559a5cbf0b80a600a2945828434b1d5a7dfb0d7f9f64e32")
        Numeric.toHexString(signature.s).`should be equal to`("0x70757c5a9bad86a90c89951c078ff45e0830c99aa313576d7dba349568251a9c")
        signature.signature.`should be equal to`("0x342ddadac7d370278559a5cbf0b80a600a2945828434b1d5a7dfb0d7f9f64e3270757c5a9bad86a90c89951c078ff45e0830c99aa313576d7dba349568251a9c1c")
    }

    @Test
    fun ecRecover() {
        val signature = Web3.Signature(28,
                Numeric.hexStringToByteArray("0x342ddadac7d370278559a5cbf0b80a600a2945828434b1d5a7dfb0d7f9f64e32"),
                Numeric.hexStringToByteArray("0x70757c5a9bad86a90c89951c078ff45e0830c99aa313576d7dba349568251a9c"))

        val data = Numeric.hexStringToByteArray("0x48540bdede62ae1b3868651336d31d36c29100af9db1559945879d14532a8067")
        val address = web3jAdapter.eth.personal.ecRecover(data, signature)
        address.`should be equal to`(address)
    }
    
    @Test
    fun sign_jsexample() {
        val signature = web3jAdapter.eth.accounts.sign("Some data", "0x4c0883a69102937d6231471b5dbb6204fe5129617082792ae468d01a3f362318")

        signature.v.`should be equal to`(28)
        Numeric.toHexString(signature.r).`should be equal to`("0xb91467e570a6466aa9e9876cbcd013baba02900b8979d43fe208a4a4f339f5fd")
        Numeric.toHexString(signature.s).`should be equal to`("0x6007e74cd82e037b800186422fc2da167c747ef045e5d18a5f5d4300f8e1a029")
        signature.signature.`should be equal to`("0xb91467e570a6466aa9e9876cbcd013baba02900b8979d43fe208a4a4f339f5fd6007e74cd82e037b800186422fc2da167c747ef045e5d18a5f5d4300f8e1a0291c")
    }
}