package cc.etherspace

import kotlinx.coroutines.experimental.delay
import java.io.IOException

data class TransactionHash(private val web3: Web3,
                           val hash: String) {
    suspend fun requestTransactionReceipt(): TransactionReceipt {
        for (i in 1..EtherSpace.GET_TRANSACTION_RECEIPT_POLLING_ATTEMPTS) {
            val transactionReceipt = web3.eth.getTransactionReceipt(hash)
            if (transactionReceipt != null) {
                if (!transactionReceipt.success) throw TransactionFailedException(transactionReceipt)
                return transactionReceipt
            }
            delay(EtherSpace.GET_TRANSACTION_RECEIPT_POLLING_INTERVAL_IN_MS)
        }
        throw IOException("transactionTimeout:transactionHash=$this")
    }

    override fun toString(): String {
        return "TransactionHash(hash='$hash')"
    }
}