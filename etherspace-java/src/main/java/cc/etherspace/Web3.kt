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
        fun encodeFunctionCall(contractFunction: ContractFunction): String

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
    }

    data class ContractFunction(val name: String,
                                val args: List<Any>,
                                val returnTypes: List<Type>)


    data class TransactionObject(val from: String,
                                 val to: String,
                                 val value: BigInteger = BigInteger.ZERO,
                                 val gas: BigInteger = Contract.GAS_LIMIT,
                                 val gasPrice: BigInteger = ManagedTransaction.GAS_PRICE,
                                 val data: String,
                                 val nonce: BigInteger? = null)
}