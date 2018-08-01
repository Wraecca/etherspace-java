package cc.etherspace

import org.bouncycastle.pqc.math.linearalgebra.ByteUtils
import org.web3j.tx.Contract
import org.web3j.tx.ManagedTransaction
import org.web3j.utils.Numeric
import java.io.IOException
import java.lang.reflect.Type
import java.math.BigInteger
import java.nio.ByteBuffer

@Suppress("unused")
interface Web3 {
    val eth: Eth
    val abi: Abi

    interface Abi {
        fun encodeFunctionCall(parameters: List<Any>, functionName: String): String

        fun decodeParameters(types: List<Type>, hexString: String): List<Any>

        fun <T> decodeLog(clazz: Class<T>, hexString: String, topics: List<String>): T?

        fun <T> encodeEventSignature(clazz: Class<T>): String
    }

    interface Eth {
        val accounts: Accounts
        val personal: Personal

        @Throws(IOException::class)
        fun sign(dataToSign: String, address: String): String

        @Throws(IOException::class)
        fun call(transactionObject: TransactionObject, defaultBlock: DefaultBlock = DefaultBlock.LATEST): String

        @Throws(IOException::class)
        fun getTransactionCount(address: String, defaultBlock: DefaultBlock = DefaultBlock.LATEST): BigInteger

        fun signTransaction(transactionObject: TransactionObject, credentials: Credentials): String

        @Throws(IOException::class)
        fun sendTransaction(transactionObject: TransactionObject, credentials: Credentials): String

        @Throws(IOException::class)
        fun sendSignedTransaction(signedTransactionData: String): String

        @Throws(IOException::class)
        fun getTransactionReceipt(transactionHash: String): TransactionReceipt?

        @Throws(IOException::class)
        fun getBalance(address: String, defaultBlock: DefaultBlock = DefaultBlock.LATEST): BigInteger
    }

    interface Accounts {
        @Throws(IOException::class)
        fun sign(message: String, privateKey: String): Signature

        @Throws(IOException::class)
        fun sign(messageHash: ByteArray, privateKey: String): Signature
    }

    interface Personal {
        @Throws(IOException::class)
        fun ecRecover(dataThatWasSigned: ByteArray, signature: Signature): String
    }

    data class Signature(val v: Byte,
                         val r: ByteArray,
                         val s: ByteArray) {
        val signature: String
            get() = Numeric.toHexString(signatureEncode(v, r, s))

        private fun signatureEncode(v: Byte, r: ByteArray, s: ByteArray): ByteArray {
            assert(r.size == 32)
            assert(s.size == 32)
            assert(v.toInt() == 27 || v.toInt() == 28)
            val buffer = ByteBuffer.allocate(SIGNATURE_LENGTH)
            buffer.put(r)
            buffer.put(s)
            buffer.put(v)
            assert(buffer.position() == SIGNATURE_LENGTH)
            return buffer.array()
        }
    }

    data class DefaultBlock(val value: String) {
        constructor(blockNumber: BigInteger) : this(Numeric.encodeQuantity(blockNumber))

        companion object {
            val EARLIEST = DefaultBlock("earliest")
            val LATEST = DefaultBlock("latest")
            val PENDING = DefaultBlock("pending")
        }
    }

    data class TransactionObject(val from: String?,
                                 val to: String,
                                 val value: BigInteger = BigInteger.ZERO,
                                 val gas: BigInteger = Contract.GAS_LIMIT,
                                 val gasPrice: BigInteger = ManagedTransaction.GAS_PRICE,
                                 val data: String,
                                 val nonce: BigInteger? = null) {
        constructor(from: String?,
                    to: String,
                    data: String,
                    options: Options,
                    nonce: BigInteger? = null) : this(from,
                to,
                options.value,
                options.gas,
                options.gasPrice,
                data,
                nonce)
    }

    companion object {
        private const val SIGNATURE_LENGTH = 65

        fun attachEthereumSignedMessage(messageHash: ByteArray): ByteArray? {
            val prefix = "\u0019Ethereum Signed Message:\n".toByteArray(Charsets.UTF_8)
            val prefixSize = ByteUtils.concatenate(prefix, messageHash.size.toString().toByteArray(Charsets.UTF_8))
            return ByteUtils.concatenate(prefixSize, messageHash)
        }
    }
}