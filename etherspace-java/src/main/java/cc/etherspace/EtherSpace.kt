package cc.etherspace

import cc.etherspace.calladapter.CallAdapter
import cc.etherspace.calladapter.PassThroughCallAdaptor
import cc.etherspace.web3j.Web3jAdapter
import com.google.common.reflect.TypeToken
import okhttp3.OkHttpClient
import org.web3j.crypto.Credentials
import org.web3j.tx.Contract
import org.web3j.tx.ManagedTransaction
import java.io.IOException
import java.lang.Thread.sleep
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.lang.reflect.Type
import java.math.BigInteger

class EtherSpace(private val web3: Web3jAdapter,
                 private val credentials: Credentials?,
                 private val callAdapters: List<CallAdapter<Any, Any>>) {
    @Suppress("UNCHECKED_CAST")
    fun <T> create(smartContract: SolAddress, service: Class<T>): T {
        val defaultOptions = createOptionsFromAnnotation(service)
        return Proxy.newProxyInstance(service.classLoader, arrayOf(service)) { proxy, method, args ->
            invokeFunction(smartContract, method, args?.toList() ?: emptyList(), defaultOptions)
        } as T
    }

    @Throws(IOException::class)
    private fun invokeFunction(smartContract: SolAddress,
                               method: Method,
                               args: List<Any>,
                               defaultOptions: Options): Any {
        val options = (args.firstOrNull { it is Options } ?: createOptionsFromAnnotation(method,
                defaultOptions)) as Options
        val params = args.filter { it !is Options }
        val callAdapter = callAdapters.first { it.adaptable(method.genericReturnType, method.annotations) }
        val actualReturnType = callAdapter.toActualReturnType(method.genericReturnType)
        return callAdapter.adapt {
            when {
                method.getAnnotation(Call::class.java) != null -> invokeViewFunction(smartContract,
                        method.name,
                        params,
                        actualReturnType,
                        options)
                method.getAnnotation(Send::class.java) != null -> invokeTransactionFunction(smartContract,
                        method.name,
                        params,
                        actualReturnType,
                        options
                )
                else -> {
                    throw IllegalArgumentException("There is no Send/Call annotation on this method")
                }
            }
        }
    }

    private fun createOptionsFromAnnotation(annotated: AnnotatedElement,
                                            defaultOptions: Options = Options()): Options {
        val g = annotated.getAnnotation(Gas::class.java)
        return g?.let { Options(gas = g.gas.toBigInteger(), gasPrice = g.gasPrice.toBigInteger()) } ?: defaultOptions
    }

    @Throws(IOException::class)
    private fun invokeTransactionFunction(smartContract: SolAddress,
                                          functionName: String,
                                          args: List<Any>,
                                          returnType: Type,
                                          options: Options): Any {
        val contractFunction = ContractFunction(functionName,
                args,
                returnType.listTupleActualTypes())
        val encodedFunction = web3.abi.encodeFunctionCall(contractFunction.args, contractFunction.name)
        val nonce = web3.eth.getTransactionCount(credentials!!.address)
        val transactionObject = Web3.TransactionObject(credentials.address,
                smartContract.address,
                encodedFunction,
                options,
                nonce)
        val response = web3.eth.sendTransaction(transactionObject, credentials)
        if (response.hasError()) {
            throw IOException("Error processing transaction request: " + response.error.message)
        }
        val returnTypeToken = TypeToken.of(returnType)
        when {
            returnTypeToken.isSubtypeOf(String::class.java) -> return response.transactionHash
            returnTypeToken.isSubtypeOf(Web3.TransactionReceipt::class.java) -> {
                for (i in 1..GET_TRANSACTION_RECEIPT_POLLING_ATTEMPTS) {
                    val transactionReceipt = web3.eth.getTransactionReceipt(response.transactionHash)
                    if (transactionReceipt != null) {
                        return transactionReceipt
                    }
                    sleep(GET_TRANSACTION_RECEIPT_POLLING_INTERVAL_IN_MS)
                }
                throw IOException("Unable to get transaction receipt")
            }
            else -> throw IllegalArgumentException("Unknown return type")
        }
    }

    @Throws(IOException::class)
    private fun invokeViewFunction(smartContract: SolAddress,
                                   functionName: String,
                                   args: List<Any>,
                                   returnType: Type,
                                   options: Options): Any {
        val contractFunction = ContractFunction(functionName,
                args,
                returnType.listTupleActualTypes())
        val encodedFunction = web3.abi.encodeFunctionCall(contractFunction.args, contractFunction.name)
        val transactionObject = Web3.TransactionObject(credentials!!.address,
                smartContract.address,
                encodedFunction,
                options)
        val transactionResponse = web3.eth.call(transactionObject)
        if (transactionResponse.hasError()) {
            throw IOException("Error processing request: " + transactionResponse.error.message)
        }
        val values = web3.abi.decodeParameters(contractFunction.returnTypes, transactionResponse.value)
        return returnType.createTupleInstance(values)
    }

    @Suppress("unused")
    class Builder {
        var provider: String = "http://localhost:8545/"

        var credentials: Credentials? = null

        var callAdapters: List<CallAdapter<Any, Any>> = mutableListOf()

        var client: OkHttpClient? = null

        fun provider(provider: String) = apply { this.provider = provider }

        fun credentials(credentials: Credentials) = apply { this.credentials = credentials }

        fun client(client: OkHttpClient) = apply { this.client = client }

        fun addCallAdapter(callAdapter: CallAdapter<Any, Any>) = apply { this.callAdapters += callAdapter }

        fun build(): EtherSpace {
            val web3 = Web3jAdapter(provider, client)
            return EtherSpace(web3,
                    credentials,
                    callAdapters + PassThroughCallAdaptor())
        }
    }

    data class Options(val value: BigInteger = BigInteger.ZERO,
                       val gas: BigInteger = Contract.GAS_LIMIT,
                       val gasPrice: BigInteger = ManagedTransaction.GAS_PRICE)


    private data class ContractFunction(val name: String,
                                        val args: List<Any>,
                                        val returnTypes: List<Type>)

    companion object {
        inline fun build(block: Builder.() -> Unit) = Builder().apply(block).build()
        private const val GET_TRANSACTION_RECEIPT_POLLING_INTERVAL_IN_MS = 1_000L
        private const val GET_TRANSACTION_RECEIPT_POLLING_ATTEMPTS = 40
    }
}