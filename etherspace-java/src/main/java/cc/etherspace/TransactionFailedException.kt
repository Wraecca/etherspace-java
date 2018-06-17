package cc.etherspace

import java.io.IOException

class TransactionFailedException(val transactionReceipt: TransactionReceipt) : IOException()