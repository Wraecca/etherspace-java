package cc.etherspace

import cc.etherspace.calladapter.CallAdapter
import cc.etherspace.calladapter.PassThroughCallAdaptor
import cc.etherspace.web3j.Web3jAdapter
import okhttp3.OkHttpClient
import org.web3j.crypto.Credentials
import java.io.IOException
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.lang.reflect.Type

class EtherSpace(private val web3: Web3jAdapter,
                 private val credentials: Credentials?,
                 private val callAdapters: List<CallAdapter<Any, Any>>) {
    @Suppress("UNCHECKED_CAST")
    fun <T> create(smartContract: SolAddress, service: Class<T>): T {
        return Proxy.newProxyInstance(service.classLoader, arrayOf(service)) { proxy, method, args ->
            invokeFunction(smartContract, method, args?.toList() ?: emptyList())
        } as T
    }

    @Throws(IOException::class)
    private fun invokeFunction(smartContract: SolAddress,
                               method: Method,
                               args: List<Any>): Any {
        val callAdapter = callAdapters.first { it.adaptable(method.genericReturnType, method.annotations) }
        val actualReturnType = callAdapter.toActualReturnType(method.genericReturnType)
        return callAdapter.adapt {
            if (method.getAnnotation(View::class.java) != null || method.getAnnotation(Pure::class.java) != null) {
                invokeViewFunction(smartContract, method.name, args, actualReturnType)
            } else {
                invokeTransactionFunction(smartContract,
                        method.name,
                        args,
                        actualReturnType)
            }
        }
    }

    @Throws(IOException::class)
    private fun invokeTransactionFunction(smartContract: SolAddress,
                                          functionName: String,
                                          args: List<Any>,
                                          returnType: java.lang.reflect.Type): String {
        val contractFunction = Web3.ContractFunction(functionName,
                args,
                returnType.listTupleActualTypes())
        val encodedFunction = web3.abi.encodeFunctionCall(contractFunction.args, contractFunction.name)
        val nonce = web3.eth.getTransactionCount(credentials!!.address)
        val transactionObject = Web3.TransactionObject(credentials.address,
                smartContract.address,
                data = encodedFunction,
                nonce = nonce)
        val transactionResponse = web3.eth.sendTransaction(transactionObject, credentials)
        if (transactionResponse.hasError()) {
            throw IOException("Error processing transaction request: " + transactionResponse.error.message)
        }
        return transactionResponse.transactionHash
    }

    @Throws(IOException::class)
    private fun invokeViewFunction(smartContract: SolAddress,
                                   functionName: String,
                                   args: List<Any>,
                                   returnType: Type): Any {
        val contractFunction = Web3.ContractFunction(functionName,
                args,
                returnType.listTupleActualTypes())
        val encodedFunction = web3.abi.encodeFunctionCall(contractFunction.args, contractFunction.name)
        val transactionObject = Web3.TransactionObject(credentials!!.address,
                smartContract.address,
                data = encodedFunction)
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

    companion object {
        inline fun build(block: Builder.() -> Unit) = Builder().apply(block).build()
    }
}