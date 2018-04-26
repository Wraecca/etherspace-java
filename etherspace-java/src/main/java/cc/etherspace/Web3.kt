package cc.etherspace

import org.web3j.tx.Contract
import org.web3j.tx.ManagedTransaction
import org.web3j.utils.Numeric
import java.io.IOException
import java.lang.reflect.Type
import java.math.BigInteger

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
    }

    interface Accounts {
        @Throws(IOException::class)
        fun sign(message: String, privateKey: String): Signature

        @Throws(IOException::class)
        fun sign(messageHash: ByteArray, privateKey: String): Signature
    }

    data class Signature(val messageHash: String,
                         val v: String,
                         val r: String,
                         val s: String,
                         val signature: String)

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

}