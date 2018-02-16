package cc.etherspace.web3j

import cc.etherspace.Web3
import okhttp3.OkHttpClient
import org.web3j.crypto.Credentials
import org.web3j.crypto.RawTransaction
import org.web3j.crypto.TransactionEncoder
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameter
import org.web3j.protocol.core.methods.request.Transaction
import org.web3j.protocol.core.methods.response.EthCall
import org.web3j.protocol.core.methods.response.EthSendTransaction
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
        override fun getTransactionReceipt(transactionHash: String): Web3.TransactionReceipt? {
            val response = web3j.ethGetTransactionReceipt(transactionHash).send()
            if (response.hasError()) {
                throw IOException("Error processing request: " + response.error.message)
            }
            return response.transactionReceipt.map { it.toWeb3TransactionReceipt() }.orElse(null)
        }

        private fun TransactionReceipt.toWeb3TransactionReceipt(): Web3.TransactionReceipt {
            return Web3.TransactionReceipt(blockHash,
                    blockNumber,
                    transactionHash,
                    transactionIndex,
                    from,
                    to,
                    contractAddress,
                    cumulativeGasUsed,
                    gasUsed,
                    logs.map { it.toWeb3Log() })
        }

        private fun Log.toWeb3Log(): Web3.Log {
            return Web3.Log(address,
                    data,
                    topics,
                    logIndex,
                    transactionIndex,
                    transactionHash,
                    blockHash,
                    blockNumber)
        }

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
