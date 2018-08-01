package cc.etherspace

import java.math.BigInteger

interface NonceProvider {
    fun provideNonce(web3: Web3, address: String, sendTransaction: (BigInteger) -> String): String
}