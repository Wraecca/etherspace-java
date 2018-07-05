package cc.etherspace

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.io.Files
import org.web3j.crypto.*
import org.web3j.utils.Numeric
import java.io.File
import java.math.BigInteger
import java.nio.charset.Charset

interface Credentials {
    val address: String
    fun signTransaction(transactionObject: Web3.TransactionObject): String
}

open class WalletCredentials : Credentials {
    final override val address: String
    private val privateKey: String

    constructor(privateKey: String) {
        this.privateKey = privateKey
        this.address = toAddress(privateKey)
    }

    constructor(password: String, wallet: String, objectMapper: ObjectMapper) {
        val walletFile = objectMapper.readValue(wallet, WalletFile::class.java)
        val ecKeyPair = Wallet.decrypt(password, walletFile)
        this.address = toAddress(ecKeyPair.publicKey)
        this.privateKey = Numeric.toHexStringWithPrefix(ecKeyPair.privateKey)
    }

    constructor(password: String, file: File, objectMapper: ObjectMapper) : this(password,
            Files.asCharSource(file, Charset.forName("UTF-8")).read(),
            objectMapper)

    private fun toAddress(privateKey: String): String =
            toAddress(Sign.publicKeyFromPrivate(Numeric.toBigInt(privateKey)))

    private fun toAddress(publicKey: BigInteger): String =
            Keys.toChecksumAddress(Numeric.prependHexPrefix(Keys.getAddress(publicKey)))

    override fun signTransaction(transactionObject: Web3.TransactionObject): String {
        val signedMessage = TransactionEncoder.signMessage(
                transactionObject.toRawTransaction(),
                toWeb3jCredentials())
        return Numeric.toHexString(signedMessage)
    }

    private fun toWeb3jCredentials(): org.web3j.crypto.Credentials = org.web3j.crypto.Credentials.create(privateKey)

    private fun Web3.TransactionObject.toRawTransaction(): RawTransaction = RawTransaction.createTransaction(nonce,
            gasPrice,
            gas,
            to,
            value,
            data)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WalletCredentials

        if (privateKey != other.privateKey) return false

        return true
    }

    override fun hashCode(): Int {
        return privateKey.hashCode()
    }
}
