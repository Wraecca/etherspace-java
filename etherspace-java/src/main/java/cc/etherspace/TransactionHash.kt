package cc.etherspace

import cc.etherspace.EtherSpace.Companion.GET_TRANSACTION_RECEIPT_POLLING_ATTEMPTS
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
            delay(GET_TRANSACTION_RECEIPT_POLLING_ATTEMPTS)
        }
        throw IOException("transactionTimeout:transactionHash=$this")
    }
}