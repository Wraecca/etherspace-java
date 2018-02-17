package cc.etherspace

import com.fasterxml.jackson.databind.ObjectMapper
import org.web3j.crypto.Keys
import org.web3j.crypto.Sign
import org.web3j.crypto.Wallet
import org.web3j.crypto.WalletFile
import org.web3j.utils.Numeric
import java.math.BigInteger

class Credentials {
    val address: String
    val privateKey: String

    constructor(privateKey: String) {
        this.privateKey = privateKey
        this.address = toAddress(privateKey)
    }

    constructor(password: String, wallet: String) {
        val walletFile = objectMapper.readValue(wallet, WalletFile::class.java)
        val ecKeyPair = Wallet.decrypt(password, walletFile)
        this.address = toAddress(ecKeyPair.publicKey)
        this.privateKey = Numeric.toHexStringWithPrefix(ecKeyPair.privateKey)
    }

    private fun toAddress(privateKey: String): String =
            toAddress(Sign.publicKeyFromPrivate(Numeric.toBigInt(privateKey)))

    private fun toAddress(publicKey: BigInteger): String = Numeric.prependHexPrefix(Keys.getAddress(publicKey))

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Credentials

        if (privateKey != other.privateKey) return false

        return true
    }

    override fun hashCode(): Int {
        return privateKey.hashCode()
    }

    companion object {
        private val objectMapper = ObjectMapper()
    }
}
