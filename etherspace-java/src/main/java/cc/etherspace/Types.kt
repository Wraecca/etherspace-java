package cc.etherspace

import java.math.BigInteger
import java.util.*

data class UBigInteger(var value: BigInteger) {
    constructor(long: Long) : this(BigInteger.valueOf(long))
}

data class SolInt24(var value: Int)

data class SolAddress(val address: String)

data class SolBytes15(val value: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SolBytes15

        if (!Arrays.equals(value, other.value)) return false

        return true
    }

    override fun hashCode(): Int {
        return Arrays.hashCode(value)
    }
}

data class SolBytes32(val value: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SolBytes32

        if (!Arrays.equals(value, other.value)) return false

        return true
    }

    override fun hashCode(): Int {
        return Arrays.hashCode(value)
    }
}