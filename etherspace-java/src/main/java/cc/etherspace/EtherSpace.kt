package cc.etherspace

import cc.etherspace.calladapter.CallAdapter
import cc.etherspace.calladapter.PassThroughCallAdaptor
import cc.etherspace.web3j.Web3jAdapter
import com.google.common.reflect.TypeToken
import okhttp3.OkHttpClient
import java.io.IOException
import java.lang.Thread.sleep
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.lang.reflect.Type

class EtherSpace(private val web3: Web3jAdapter,
                 private val credentials: Credentials?,
                 private val callAdapters: List<CallAdapter<Any, Any?>>) {
    @Suppress("UNCHECKED_CAST")
    fun <T> create(toAddress: String, service: Class<T>): T {
        val defaultOptions = createOptionsFromAnnotation(service)
        return Proxy.newProxyInstance(service.classLoader, arrayOf(service)) { _, method, args ->
            invokeFunction(toAddress, method, args?.toList() ?: emptyList(), defaultOptions)
        } as T
    }

    @Throws(IOException::class)
    private fun invokeFunction(toAddress: String,
                               method: Method,
                               args: List<Any>,
                               defaultOptions: Options): Any? {
        val options = (args.firstOrNull { it is Options } ?: createOptionsFromAnnotation(method,
                defaultOptions)) as Options
        val params = args.filter { it !is Options }
        val callAdapter = callAdapters.first { it.adaptable(method.genericReturnType, method.annotations) }
        val actualReturnType = callAdapter.toActualReturnType(method.genericReturnType)
        return callAdapter.adapt {
            val callAnnotation = method.getAnnotation(Call::class.java)
            if (callAnnotation != null) {
                val functionName = callAnnotation.functionName
                return@adapt invokeViewFunction(toAddress,
                        if (functionName.isNotBlank()) functionName else method.name,
                        params,
                        actualReturnType,
                        options)
            }

            val sendAnnotation = method.getAnnotation(Send::class.java)
            if (sendAnnotation != null) {
                val functionName = sendAnnotation.functionName
                return@adapt invokeTransactionFunction(toAddress,
                        if (functionName.isNotBlank()) functionName else method.name,
                        params,
                        actualReturnType,
                        options
                )
            }

            throw IllegalArgumentException("There is no Send/Call annotation on this method")
        }
    }

    private fun createOptionsFromAnnotation(annotated: AnnotatedElement,
                                            defaultOptions: Options = Options()): Options =
            annotated.getAnnotation(Gas::class.java)?.let {
                Options(gas = it.gas.toBigInteger(),
                        gasPrice = it.gasPrice.toBigInteger())
            } ?: defaultOptions

    @Throws(IOException::class)
    private fun invokeTransactionFunction(toAddress: String,
                                          functionName: String,
                                          args: List<Any>,
                                          returnType: Type,
                                          options: Options): Any {
        val cd = options.credentials ?: credentials ?: throw IllegalArgumentException("Credentials not set")
        val contractFunction = ContractFunction(functionName,
                args,
                returnType.listTupleActualTypes())
        val encodedFunction = web3.abi.encodeFunctionCall(contractFunction.args, contractFunction.name)
        val nonce = web3.eth.getTransactionCount(cd.address)
        val transactionObject = Web3.TransactionObject(cd.address,
                toAddress,
                encodedFunction,
                options,
                nonce)
        val transactionHash = web3.eth.sendTransaction(transactionObject, cd)
        val returnTypeToken = TypeToken.of(returnType)
        when {
            returnTypeToken.isSubtypeOf(String::class.java) -> return transactionHash
            returnTypeToken.isSubtypeOf(TransactionReceipt::class.java) -> {
                for (i in 1..GET_TRANSACTION_RECEIPT_POLLING_ATTEMPTS) {
                    val transactionReceipt = web3.eth.getTransactionReceipt(transactionHash)
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
    private fun invokeViewFunction(toAddress: String,
                                   functionName: String,
                                   args: List<Any>,
                                   returnType: Type,
                                   options: Options): Any? {
        val contractFunction = ContractFunction(functionName,
                args,
                returnType.listTupleActualTypes())
        val encodedFunction = web3.abi.encodeFunctionCall(contractFunction.args, contractFunction.name)
        val transactionObject = Web3.TransactionObject(null,
                toAddress,
                encodedFunction,
                options)
        val data = web3.eth.call(transactionObject)
        val values = web3.abi.decodeParameters(contractFunction.returnTypes, data)
        return returnType.createTupleInstance(values)
    }

    @Suppress("unused", "MemberVisibilityCanBePrivate")
    class Builder {
        var provider: String = "http://localhost:8545/"

        var credentials: Credentials? = null

        var callAdapters: List<CallAdapter<Any, Any?>> = mutableListOf()

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

    private data class ContractFunction(val name: String,
                                        val args: List<Any>,
                                        val returnTypes: List<Type>)

    companion object {
        inline fun build(block: Builder.() -> Unit) = Builder().apply(block).build()
        private const val GET_TRANSACTION_RECEIPT_POLLING_INTERVAL_IN_MS = 1_000L
        private const val GET_TRANSACTION_RECEIPT_POLLING_ATTEMPTS = 40
    }
}