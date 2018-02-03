package cc.etherspace.web3j

import cc.etherspace.UBigInteger
import cc.etherspace.Web3
import okhttp3.OkHttpClient
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.FunctionReturnDecoder
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.*
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.generated.*
import org.web3j.crypto.Credentials
import org.web3j.crypto.RawTransaction
import org.web3j.crypto.TransactionEncoder
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameter
import org.web3j.protocol.core.methods.request.Transaction
import org.web3j.protocol.core.methods.response.EthCall
import org.web3j.protocol.core.methods.response.EthSendTransaction
import org.web3j.protocol.http.HttpService
import org.web3j.utils.Numeric
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.math.BigInteger

class Web3jAdapter(val web3j: Web3j) : Web3 {
    constructor(provider: String, client: OkHttpClient?) : this(Web3j.build(
            if (client != null) HttpService(provider, client, false) else HttpService(provider)
    ))

    override val abi: Web3.Abi = Web3jAbi()

    override val eth: Web3jEth = Web3jEth()

    inner class Web3jAbi : Web3.Abi {
        override fun decodeParameters(types: List<java.lang.reflect.Type>, hexString: String): List<Any> {
            return FunctionReturnDecoder.decode(hexString,
                    types.mapNotNull { toWeb3jType(it) } as List<TypeReference<Type<Any>>>)
                    .map { toJavaValue(it) }
        }

        override fun encodeFunctionCall(contractFunction: Web3.ContractFunction): String {
            val function = Function(contractFunction.name,
                    contractFunction.args.map { toWeb3jValue(it) },
                    contractFunction.returnTypes.mapNotNull { toWeb3jType(it) })
            return FunctionEncoder.encode(function)
        }
    }

    private fun toWeb3jType(type: java.lang.reflect.Type): TypeReference<out Type<out Any>> {
        if (type is ParameterizedType) {
            @Suppress("UNCHECKED_CAST")
            val rawClass = type.rawType as Class<Any>
            val web3jType = when {
                List::class.java.isAssignableFrom(rawClass) -> {
                    DynamicArray::class.java
                }
                else -> throw IllegalArgumentException()
            }
            return TypeReference.create(web3jType)
        }

        @Suppress("UNCHECKED_CAST")
        val clazz = type as Class<Any>
        val web3jType = when {
            String::class.java.isAssignableFrom(clazz) -> Utf8String::class.java
            Boolean::class.java.isAssignableFrom(clazz) -> Bool::class.java
            BigInteger::class.java.isAssignableFrom(clazz) -> Int256::class.java
            UBigInteger::class.java.isAssignableFrom(clazz) -> Uint256::class.java
            cc.etherspace.Int24::class.java.isAssignableFrom(clazz) -> Int24::class.java
            cc.etherspace.Address::class.java.isAssignableFrom(clazz) -> Address::class.java
            Byte::class.java.isAssignableFrom(clazz) -> Bytes1::class.java
            cc.etherspace.Bytes15::class.java.isAssignableFrom(clazz) -> Bytes15::class.java
            cc.etherspace.Bytes32::class.java.isAssignableFrom(clazz) -> Bytes32::class.java
            ByteArray::class.java.isAssignableFrom(clazz) -> DynamicBytes::class.java
            else -> throw IllegalArgumentException()
        }
        return TypeReference.create(web3jType)
    }

    private fun toWeb3jValue(value: Any): Type<out Any> {
        return when (value) {
            is String -> Utf8String(value)
            is Boolean -> Bool(value)
            is BigInteger -> Int256(value)
            is UBigInteger -> Uint256(value.value)
            is cc.etherspace.Int24 -> Int24(value.value.toBigInteger())
            is cc.etherspace.Address -> Address(value.address)
            is List<*> -> {
                DynamicArray(value.map { toWeb3jValue(it!!) })
            }
            is Byte -> Bytes1(byteArrayOf(value))
            is cc.etherspace.Bytes15 -> Bytes15(value.value)
            is cc.etherspace.Bytes32 -> Bytes32(value.value)
            is ByteArray -> DynamicBytes(value)
            else -> throw IllegalArgumentException()
        }
    }

    private fun toJavaValue(value: Type<Any>): Any {
        return when (value) {
            is Utf8String -> value.value
            is Bool -> value.value
            is Int256 -> value.value
            is Uint256 -> UBigInteger(value.value)
            is Int24 -> cc.etherspace.Int24(value.value.toInt())
            is Address -> cc.etherspace.Address(value.value)
            is DynamicArray<*> -> value.value
            is Bytes1 -> value.value[0]
            is Bytes15 -> cc.etherspace.Bytes15(value.value)
            is Bytes32 -> cc.etherspace.Bytes32(value.value)
            is DynamicBytes -> value.value
            else -> throw IllegalArgumentException()
        }
    }

    inner class Web3jEth : Web3.Eth {
        override fun call(transactionObject: Web3.TransactionObject,
                          blockParameter: DefaultBlockParameter): EthCall {
            val transaction = Transaction.createEthCallTransaction(transactionObject.from,
                    transactionObject.to,
                    transactionObject.data)
            return web3j.ethCall(transaction, blockParameter).send()
        }

        override fun getTransactionCount(address: String,
                                         blockParameter: DefaultBlockParameter): BigInteger {
            val response = web3j.ethGetTransactionCount(address, blockParameter).send()
            if (response.hasError()) {
                throw IOException("Error processing request: " + response.error.message)
            }
            return response.transactionCount
        }

        override fun signTransaction(transactionObject: Web3.TransactionObject, credentials: Credentials): String {
            val signedMessage = TransactionEncoder.signMessage(
                    transactionObject.toRawTransaction(),
                    credentials)
            return Numeric.toHexString(signedMessage)
        }

        override fun sendTransaction(transactionObject: Web3.TransactionObject,
                                     credentials: Credentials): EthSendTransaction {
            val signTransaction = signTransaction(transactionObject, credentials)
            return sendSignedTransaction(signTransaction)
        }

        override fun sendSignedTransaction(signedTransactionData: String): EthSendTransaction {
            return web3j.ethSendRawTransaction(signedTransactionData).send()
        }
    }
}

fun Web3.TransactionObject.toRawTransaction(): RawTransaction {
    return RawTransaction.createTransaction(nonce,
            gasPrice,
            gas,
            to,
            value,
            data)
}
