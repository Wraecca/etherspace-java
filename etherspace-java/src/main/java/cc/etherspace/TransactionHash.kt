package cc.etherspace

import cc.etherspace.calladapter.CallAdapter
import java.io.IOException

data class TransactionHash(private val web3: Web3,
                           val hash: String,
                           private val callAdapter: CallAdapter<Any, Any>) {
    @Suppress("UNCHECKED_CAST")
    fun <T> requestTransactionReceipt(): T {
        return callAdapter.adapt {
            return@adapt requestTransactionReceiptBlocking()
        } as T
    }

    internal fun requestTransactionReceiptBlocking(): TransactionReceipt {
        for (i in 1..EtherSpace.GET_TRANSACTION_RECEIPT_POLLING_ATTEMPTS) {
            val transactionReceipt = web3.eth.getTransactionReceipt(hash)
            if (transactionReceipt != null) {
                if (!transactionReceipt.success) throw TransactionFailedException(transactionReceipt)
                return transactionReceipt
            }
            Thread.sleep(EtherSpace.GET_TRANSACTION_RECEIPT_POLLING_INTERVAL_IN_MS)
        }
        throw IOException("transactionTimeout:transactionHash=$this")
    }

    override fun toString(): String {
        return "TransactionHash(hash='$hash')"
    }
}