package cc.etherspace

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be greater than`
import org.amshove.kluent.`should equal`
import org.junit.Before
import org.junit.Test
import org.web3j.utils.Numeric
import java.io.File

class LocalGreeterTest {
    private lateinit var greeter: Greeter
    private val objectMapper = jacksonObjectMapper()

    @Before
    fun setUp() {
        var contractAddress = objectMapper.readValue(File("build/contracts/greeter.json"),
                ContractMetaData::class.java).networks.values.first().address

        val interceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message -> println(message) })
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build()

        val etherSpace = EtherSpace.build {
            client = okHttpClient
            credentials = WalletCredentials("0xc5aea8fb8b669d27da1c644064fb4cd80b7d3c9b7d2ebcab2bfade236834c784")
        }
        greeter = etherSpace.create(contractAddress, Greeter::class.java)
    }

    @Test
    fun twoDimensionArray() {
        val array = IntProgression.fromClosedRange(1, 5, 1)
                .map { SolUint256(it) }
                .toTypedArray()
        val arrays = listOf(SolArray5(array), SolArray5(array), SolArray5(array), SolArray5(array), SolArray5(array))
        val ret = greeter.twoDimensionArray(arrays, SolUint256(1), SolUint256(2))
        ret.`should equal`(SolUint256(3))
    }

    @Test
    fun newGreeting() {
        val receipt = greeter.newGreeting("Hello World")
        receipt.blockHash.length.`should be equal to`(66)
        receipt.transactionHash.length.`should be equal to`(66)
        receipt.logs.size.`should be greater than`(0)

        val events = receipt.listEvents(Greeter.Modified::class.java)
        events.size.`should be equal to`(1)
        events[0].event.`should be equal to`("Modified")
        events[0].returnValue.oldGreeting.`should be equal to`("Hello World")
        events[0].returnValue.newGreeting.`should be equal to`("Hello World")
        events[0].returnValue.oldGreetingIdx.`should equal`(SolBytes32(Numeric.hexStringToByteArray("0x592fa743889fc7f92ac2a37bb1f5ba1daf2a5c84741ca0e0061d243a2e6707ba")))
        events[0].returnValue.newGreetingIdx.`should equal`(SolBytes32(Numeric.hexStringToByteArray("0x592fa743889fc7f92ac2a37bb1f5ba1daf2a5c84741ca0e0061d243a2e6707ba")))
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class ContractMetaData(val networks: Map<String, Network>)

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Network(val address: String)
}