package cc.etherspace.web3j

import cc.etherspace.Event
import cc.etherspace.Web3
import com.fasterxml.jackson.annotation.JsonValue
import okhttp3.OkHttpClient
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils
import org.web3j.crypto.ECKeyPair
import org.web3j.crypto.Keys
import org.web3j.crypto.Sign
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
    constructor(provider: String, client: OkHttpClient?) : this(createWeb3j(client, provider))

    override val abi: Web3.Abi = Web3jAbi()

    override val eth: Web3jEth = Web3jEth()

    class Web3jPersonal : Web3.Personal {
        override fun ecRecover(dataThatWasSigned: ByteArray, signature: Web3.Signature): String {
            val prefixMsgHash = Web3.attachEthereumSignedMessage(dataThatWasSigned)
            val publicKey = Sign.signedMessageToKey(prefixMsgHash,
                    Sign.SignatureData(signature.v, signature.r, signature.s))
            return Keys.toChecksumAddress(Numeric.prependHexPrefix(Keys.getAddress(publicKey)))
        }
    }

    class Web3jAccounts : Web3.Accounts {
        override fun sign(message: String, privateKey: String): Web3.Signature =
                sign(message.toByteArray(Charsets.UTF_8), privateKey)

        /**
         * from: https://github.com/EuroHsu/TestLibrary/blob/6883528dbcddb283f14955620f042b4ea3253624/src/main/java/com/example/testlibrary/cryptos/signer.java
         */
        override fun sign(messageHash: ByteArray, privateKey: String): Web3.Signature {
            val prefixMsgHash = Web3.attachEthereumSignedMessage(messageHash)
            val ecKeyPair = ECKeyPair.create(Numeric.toBigInt(privateKey))
            val signatureData = Sign.signMessage(prefixMsgHash, ecKeyPair)
            return Web3.Signature(signatureData.v,
                    signatureData.r,
                    signatureData.s)
        }
    }

    inner class Web3jEth : Web3.Eth {
        override val accounts: Web3.Accounts = Web3jAccounts()
        override val personal: Web3.Personal = Web3jPersonal()

        override fun sign(dataToSign: String, address: String): String {
            return web3j.ethSign(address, dataToSign).send().signature
        }

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
                    from?.let { Keys.toChecksumAddress(it) },
                    to?.let { Keys.toChecksumAddress(it) },
                    contractAddress?.let { Keys.toChecksumAddress(it) },
                    cumulativeGasUsed,
                    gasUsed,
                    logs.map { it.toWeb3Log() },
                    status,
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
            return credentials.signTransaction(transactionObject)
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

        @Suppress("ObjectLiteralToLambda")
        private fun Web3.DefaultBlock.toDefaultBlockParameter(): DefaultBlockParameter {
            return object : DefaultBlockParameter {
                @JsonValue
                override fun getValue(): String {
                    return this@toDefaultBlockParameter.value
                }
            }
        }

        override fun getBalance(address: String, defaultBlock: Web3.DefaultBlock): BigInteger {
            val response = web3j.ethGetBalance(address, defaultBlock.toDefaultBlockParameter()).send()
            if (response.hasError()) {
                throw IOException("Error processing request: " + response.error.message)
            }
            return response.balance
        }
    }

    data class TransactionReceiptImpl(override val blockHash: String,
                                      override val blockNumber: BigInteger,
                                      override val transactionHash: String,
                                      override val transactionIndex: BigInteger,
                                      override val from: String?,
                                      override val to: String?,
                                      override val contractAddress: String?,
                                      override val cumulativeGasUsed: BigInteger,
                                      override val gasUsed: BigInteger,
                                      override val logs: List<cc.etherspace.Log>,
                                      override val status: String?,
                                      private val abi: Web3.Abi) : cc.etherspace.TransactionReceipt {
        override val success: Boolean
            get() {
                if (status != null) {
                    if (Numeric.toBigInt(status) == BigInteger.ONE) return true
                }
                return false
            }

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

    companion object {
        private fun createWeb3j(client: OkHttpClient?, provider: String): Web3j {
            val httpService = if (client != null) HttpService(provider, client, false) else HttpService(provider)
            return try {
                // For android
                val web3jFactoryClass = Class.forName("org.web3j.protocol.Web3jFactory")
                val web3jServiceClass = Class.forName("org.web3j.protocol.Web3jService")
                val buildMethod = web3jFactoryClass.getMethod("build", web3jServiceClass)
                buildMethod.invoke(null, httpService) as Web3j
            } catch (e: Exception) {
                Web3j.build(httpService)
            }
        }
    }
}
