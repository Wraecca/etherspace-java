package cc.etherspace.web3j

import cc.etherspace.Event
import cc.etherspace.Web3
import com.fasterxml.jackson.annotation.JsonValue
import okhttp3.OkHttpClient
import org.web3j.crypto.Credentials
import org.web3j.crypto.RawTransaction
import org.web3j.crypto.TransactionEncoder
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameter
import org.web3j.protocol.core.methods.request.Transaction
import org.web3j.protocol.core.methods.response.Log
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.protocol.http.HttpService
import org.web3j.utils.Numeric
import java.io.IOException
import java.math.BigInteger

class Web3jAdapter(val web3j: Web3j) : Web3 {
    constructor(provider: String, client: OkHttpClient?) : this(Web3j.build(
            if (client != null) HttpService(provider, client, false) else HttpService(provider)
    ))

    override val abi: Web3.Abi = Web3jAbi()

    override val eth: Web3jEth = Web3jEth()

    inner class Web3jEth : Web3.Eth {
        @Throws(IOException::class)
        override fun getTransactionReceipt(transactionHash: String): cc.etherspace.TransactionReceipt? {
            val response = web3j.ethGetTransactionReceipt(transactionHash).send()
            if (response.hasError()) {
                throw IOException("Error processing request: " + response.error.message)
            }
            // require android sdk 24
            //return response.transactionReceipt.map { it.toWeb3TransactionReceipt() }.orElse(null)
            return if (response.result != null) response.result.toWeb3TransactionReceipt() else null
        }

        private fun TransactionReceipt.toWeb3TransactionReceipt(): cc.etherspace.TransactionReceipt {
            return TransactionReceiptImpl(blockHash,
                    blockNumber,
                    transactionHash,
                    transactionIndex,
                    from,
                    to,
                    contractAddress,
                    cumulativeGasUsed,
                    gasUsed,
                    logs.map { it.toWeb3Log() },
                    abi)
        }

        private fun Log.toWeb3Log(): cc.etherspace.Log {
            return cc.etherspace.Log(address,
                    data,
                    topics,
                    logIndex,
                    transactionIndex,
                    transactionHash,
                    blockHash,
                    blockNumber)
        }

        @Throws(IOException::class)
        override fun call(transactionObject: Web3.TransactionObject, defaultBlock: Web3.DefaultBlock): String {
            val transaction = Transaction.createEthCallTransaction(transactionObject.from,
                    transactionObject.to,
                    transactionObject.data)
            val response = web3j.ethCall(transaction, defaultBlock.toDefaultBlockParameter()).send()
            if (response.hasError()) {
                throw IOException("Error processing request: " + response.error.message)
            }
            return response.value
        }

        @Throws(IOException::class)
        override fun getTransactionCount(address: String, defaultBlock: Web3.DefaultBlock): BigInteger {
            val response = web3j.ethGetTransactionCount(address, defaultBlock.toDefaultBlockParameter()).send()
            if (response.hasError()) {
                throw IOException("Error processing request: " + response.error.message)
            }
            return response.transactionCount
        }

        override fun signTransaction(transactionObject: Web3.TransactionObject,
                                     credentials: cc.etherspace.Credentials): String {
            val signedMessage = TransactionEncoder.signMessage(
                    transactionObject.toRawTransaction(),
                    credentials.toWeb3jCredentials())
            return Numeric.toHexString(signedMessage)
        }

        @Throws(IOException::class)
        override fun sendTransaction(transactionObject: Web3.TransactionObject,
                                     credentials: cc.etherspace.Credentials): String {
            val signTransaction = signTransaction(transactionObject, credentials)
            return sendSignedTransaction(signTransaction)
        }

        @Throws(IOException::class)
        override fun sendSignedTransaction(signedTransactionData: String): String {
            val response = web3j.ethSendRawTransaction(signedTransactionData).send()
            if (response.hasError()) {
                throw IOException("Error processing transaction request: " + response.error.message)
            }
            return response.transactionHash
        }

        private fun Web3.TransactionObject.toRawTransaction(): RawTransaction = RawTransaction.createTransaction(nonce,
                gasPrice,
                gas,
                to,
                value,
                data)

        private fun cc.etherspace.Credentials.toWeb3jCredentials(): Credentials = Credentials.create(privateKey)

        @Suppress("ObjectLiteralToLambda")
        private fun Web3.DefaultBlock.toDefaultBlockParameter(): DefaultBlockParameter {
            return object : DefaultBlockParameter {
                @JsonValue
                override fun getValue(): String {
                    return this@toDefaultBlockParameter.value
                }
            }
        }
    }

    data class TransactionReceiptImpl(override val blockHash: String,
                                      override val blockNumber: BigInteger,
                                      override val transactionHash: String,
                                      override val transactionIndex: BigInteger,
                                      override val from: String,
                                      override val to: String,
                                      override val contractAddress: String?,
                                      override val cumulativeGasUsed: BigInteger,
                                      override val gasUsed: BigInteger,
                                      override val logs: List<cc.etherspace.Log>,
                                      private val abi: Web3.Abi) : cc.etherspace.TransactionReceipt {
        override fun <T> listEvents(clazz: Class<T>): List<Event<T>> {
            val signature = abi.encodeEventSignature(clazz)
            return logs.mapNotNull { log ->
                if (log.topics[0] != signature) return@mapNotNull null
                abi.decodeLog(clazz, log.data, log.topics.subList(1, log.topics.size))?.let { value ->
                    Event(clazz.simpleName,
                            signature,
                            value,
                            log)
                }
            }
        }
    }
}
