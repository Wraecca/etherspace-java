package cc.etherspace

import org.web3j.tx.Contract
import org.web3j.tx.ManagedTransaction
import java.math.BigInteger

data class Options @JvmOverloads constructor(val value: BigInteger = BigInteger.ZERO,
                                             val gas: BigInteger = Contract.GAS_LIMIT,
                                             val gasPrice: BigInteger = ManagedTransaction.GAS_PRICE,
                                             val credentials: Credentials? = null)