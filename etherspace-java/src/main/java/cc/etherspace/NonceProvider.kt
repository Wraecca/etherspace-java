package cc.etherspace

import java.math.BigInteger

interface NonceProvider {
    fun getNonce(web3: Web3, address: String): BigInteger
}