package cc.etherspace

import java.math.BigInteger

interface TransactionReceipt {
    val blockHash: String
    val blockNumber: BigInteger
    val transactionHash: String
    val transactionIndex: BigInteger
    val from: String
    val to: String
    val contractAddress: String?
    val cumulativeGasUsed: BigInteger
    val gasUsed: BigInteger
    val logs: List<Log>
    fun <T> listEvents(clazz: Class<T>): List<Event<T>>
}

data class Log(val address: String,
               val data: String,
               val topics: List<String>,
               val logIndex: BigInteger,
               val transactionIndex: BigInteger,
               val transactionHash: String,
               val blockHash: String,
               val blockNumber: BigInteger)

data class Event<out T>(val event: String,
                        val signature: String?,
                        val address: String,
                        val returnValue: T,
                        val logIndex: BigInteger,
                        val transactionIndex: BigInteger,
                        val transactionHash: String,
                        val blockHash: String,
                        val blockNumber: BigInteger,
                        val rawData: String,
                        val rawTopics: List<String>) {
    constructor(event: String, signature: String?, returnValue: T, log: Log) : this(event,
            signature,
            log.address,
            returnValue,
            log.logIndex,
            log.transactionIndex,
            log.transactionHash,
            log.blockHash,
            log.blockNumber,
            log.data,
            log.topics)
}
