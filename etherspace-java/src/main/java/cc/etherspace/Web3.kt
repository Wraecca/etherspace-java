package cc.etherspace

import org.web3j.crypto.Credentials
import org.web3j.protocol.core.DefaultBlockParameter
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.response.EthCall
import org.web3j.protocol.core.methods.response.EthSendTransaction
import org.web3j.tx.Contract
import org.web3j.tx.ManagedTransaction
import java.lang.reflect.Type
import java.math.BigInteger

interface Web3 {
    val eth: Eth
    val abi: Abi

    interface Abi {
        fun encodeFunctionCall(parameters: List<Any>, functionName: String): String

        fun decodeParameters(types: List<Type>, hexString: String): List<Any>
    }

    interface Eth {
        fun call(transactionObject: TransactionObject,
                 blockParameter: DefaultBlockParameter = DefaultBlockParameterName.LATEST): EthCall

        fun getTransactionCount(address: String,
                                blockParameter: DefaultBlockParameter = DefaultBlockParameterName.LATEST): BigInteger

        fun signTransaction(transactionObject: TransactionObject, credentials: Credentials): String

        fun sendTransaction(transactionObject: TransactionObject,
                            credentials: Credentials): EthSendTransaction

        fun sendSignedTransaction(signedTransactionData: String): EthSendTransaction

        fun getTransactionReceipt(transactionHash: String): TransactionReceipt?
    }

    data class TransactionObject(val from: String,
                                 val to: String,
                                 val value: BigInteger = BigInteger.ZERO,
                                 val gas: BigInteger = Contract.GAS_LIMIT,
                                 val gasPrice: BigInteger = ManagedTransaction.GAS_PRICE,
                                 val data: String,
                                 val nonce: BigInteger? = null) {
        constructor(from: String,
                    to: String,
                    data: String,
                    options: EtherSpace.Options,
                    nonce: BigInteger? = null) : this(from,
                to,
                options.value,
                options.gas,
                options.gasPrice,
                data,
                nonce)
    }

    data class Log(val address: String,
                   val data: String,
                   val topics: List<String>,
                   val logIndex: BigInteger,
                   val transactionIndex: BigInteger,
                   val transactionHash: String,
                   val blockHash: String,
                   val blockNumber: BigInteger)

    data class TransactionReceipt(val blockHash: String,
                                  val blockNumber: BigInteger,
                                  val transactionHash: String,
                                  val transactionIndex: BigInteger,
                                  val from: String,
                                  val to: String,
                                  val contractAddress: String?,
                                  val cumulativeGasUsed: BigInteger,
                                  val gasUsed: BigInteger,
                                  val logs: List<Log>)
}