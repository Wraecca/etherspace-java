@file:Suppress("unused")

package cc.etherspace

import org.web3j.utils.Numeric
import java.math.BigInteger
import java.util.*

interface SolType

sealed class SolNumber(val value: BigInteger, val bits: Int, val unsigned: Boolean) : SolType {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SolNumber

        if (value != other.value) return false
        if (bits != other.bits) return false

        return true
    }

    override fun hashCode(): Int {
        var result = value.hashCode()
        result = 31 * result + bits
        return result
    }

    override fun toString(): String {
        return "SolNumber(value=$value, bits=$bits, unsigned=$unsigned)"
    }
}

sealed class SolInt(value: BigInteger, bits: Int) : SolNumber(value, bits, false)

class SolInt8(value: BigInteger) : SolInt(value, 8) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

class SolInt24(value: BigInteger) : SolInt(value, 24) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

class SolInt40(value: BigInteger) : SolInt(value, 40) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

class SolInt48(value: BigInteger) : SolInt(value, 48) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

class SolInt56(value: BigInteger) : SolInt(value, 56) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

class SolInt72(value: BigInteger) : SolInt(value, 72) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

class SolInt80(value: BigInteger) : SolInt(value, 80) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

class SolInt88(value: BigInteger) : SolInt(value, 88) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

class SolInt96(value: BigInteger) : SolInt(value, 96) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

class SolInt104(value: BigInteger) : SolInt(value, 104) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

class SolInt112(value: BigInteger) : SolInt(value, 112) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

class SolInt120(value: BigInteger) : SolInt(value, 120) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

class SolInt128(value: BigInteger) : SolInt(value, 128) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

class SolInt136(value: BigInteger) : SolInt(value, 136) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

class SolInt144(value: BigInteger) : SolInt(value, 144) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

class SolInt152(value: BigInteger) : SolInt(value, 152) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

class SolInt160(value: BigInteger) : SolInt(value, 160) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

class SolInt168(value: BigInteger) : SolInt(value, 168) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

class SolInt176(value: BigInteger) : SolInt(value, 176) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

class SolInt184(value: BigInteger) : SolInt(value, 184) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

class SolInt192(value: BigInteger) : SolInt(value, 192) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

class SolInt200(value: BigInteger) : SolInt(value, 200) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

class SolInt208(value: BigInteger) : SolInt(value, 208) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

class SolInt216(value: BigInteger) : SolInt(value, 216) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

class SolInt224(value: BigInteger) : SolInt(value, 224) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

class SolInt232(value: BigInteger) : SolInt(value, 232) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

class SolInt240(value: BigInteger) : SolInt(value, 240) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

class SolInt248(value: BigInteger) : SolInt(value, 248) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

sealed class SolUint(value: BigInteger, bits: Int) : SolNumber(value, bits, true)

class SolUint8(value: BigInteger) : SolUint(value, 8) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

class SolUint24(value: BigInteger) : SolUint(value, 24) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

class SolUint40(value: BigInteger) : SolUint(value, 40) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

class SolUint48(value: BigInteger) : SolUint(value, 48) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

class SolUint56(value: BigInteger) : SolUint(value, 56) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

class SolUint72(value: BigInteger) : SolUint(value, 72) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

class SolUint80(value: BigInteger) : SolUint(value, 80) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

class SolUint88(value: BigInteger) : SolUint(value, 88) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

class SolUint96(value: BigInteger) : SolUint(value, 96) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

class SolUint104(value: BigInteger) : SolUint(value, 104) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

class SolUint112(value: BigInteger) : SolUint(value, 112) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

class SolUint120(value: BigInteger) : SolUint(value, 120) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

class SolUint128(value: BigInteger) : SolUint(value, 128) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

class SolUint136(value: BigInteger) : SolUint(value, 136) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

class SolUint144(value: BigInteger) : SolUint(value, 144) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

class SolUint152(value: BigInteger) : SolUint(value, 152) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

class SolUint160(value: BigInteger) : SolUint(value, 160) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

class SolUint168(value: BigInteger) : SolUint(value, 168) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

class SolUint176(value: BigInteger) : SolUint(value, 176) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

class SolUint184(value: BigInteger) : SolUint(value, 184) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

class SolUint192(value: BigInteger) : SolUint(value, 192) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

class SolUint200(value: BigInteger) : SolUint(value, 200) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

class SolUint208(value: BigInteger) : SolUint(value, 208) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

class SolUint216(value: BigInteger) : SolUint(value, 216) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

class SolUint224(value: BigInteger) : SolUint(value, 224) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

class SolUint232(value: BigInteger) : SolUint(value, 232) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

class SolUint240(value: BigInteger) : SolUint(value, 240) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

class SolUint248(value: BigInteger) : SolUint(value, 248) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

class SolUint256(value: BigInteger) : SolUint(value, 256) {
    constructor(i: Int) : this(i.toBigInteger())
    constructor(l: Long) : this(l.toBigInteger())
}

data class SolAddress(val address: String) : SolType {
    init {
        require(address.startsWith("0x"), { "The address needs to start with '0x'" })
    }
}

sealed class SolBytes(val value: ByteArray, val size: Int) : SolType {
    init {
        require(value.size == size, { "The size of underling ByteArray is different from size" })
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SolBytes

        if (!Arrays.equals(value, other.value)) return false
        if (size != other.size) return false

        return true
    }

    override fun hashCode(): Int {
        var result = Arrays.hashCode(value)
        result = 31 * result + size
        return result
    }

    override fun toString(): String {
        return "SolBytes(value=${Numeric.toHexString(value)}, size=$size)"
    }
}

class SolBytes2(value: ByteArray) : SolBytes(value, 2)
class SolBytes3(value: ByteArray) : SolBytes(value, 3)
class SolBytes4(value: ByteArray) : SolBytes(value, 4)
class SolBytes5(value: ByteArray) : SolBytes(value, 5)
class SolBytes6(value: ByteArray) : SolBytes(value, 6)
class SolBytes7(value: ByteArray) : SolBytes(value, 7)
class SolBytes8(value: ByteArray) : SolBytes(value, 8)
class SolBytes9(value: ByteArray) : SolBytes(value, 9)
class SolBytes10(value: ByteArray) : SolBytes(value, 10)
class SolBytes11(value: ByteArray) : SolBytes(value, 11)
class SolBytes12(value: ByteArray) : SolBytes(value, 12)
class SolBytes13(value: ByteArray) : SolBytes(value, 13)
class SolBytes14(value: ByteArray) : SolBytes(value, 14)
class SolBytes15(value: ByteArray) : SolBytes(value, 15)
class SolBytes16(value: ByteArray) : SolBytes(value, 16)
class SolBytes17(value: ByteArray) : SolBytes(value, 17)
class SolBytes18(value: ByteArray) : SolBytes(value, 18)
class SolBytes19(value: ByteArray) : SolBytes(value, 19)
class SolBytes20(value: ByteArray) : SolBytes(value, 20)
class SolBytes21(value: ByteArray) : SolBytes(value, 21)
class SolBytes22(value: ByteArray) : SolBytes(value, 22)
class SolBytes23(value: ByteArray) : SolBytes(value, 23)
class SolBytes24(value: ByteArray) : SolBytes(value, 24)
class SolBytes25(value: ByteArray) : SolBytes(value, 25)
class SolBytes26(value: ByteArray) : SolBytes(value, 26)
class SolBytes27(value: ByteArray) : SolBytes(value, 27)
class SolBytes28(value: ByteArray) : SolBytes(value, 28)
class SolBytes29(value: ByteArray) : SolBytes(value, 29)
class SolBytes30(value: ByteArray) : SolBytes(value, 30)
class SolBytes31(value: ByteArray) : SolBytes(value, 31)
class SolBytes32(value: ByteArray) : SolBytes(value, 32)

sealed class SolFixedArray<T>(val value: Array<T>) : SolType {
    fun get(i: Int): T = value[i]

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SolFixedArray<*>

        if (!Arrays.equals(value, other.value)) return false

        return true
    }

    override fun hashCode(): Int {
        return Arrays.hashCode(value)
    }

    override fun toString(): String {
        return "SolFixedArray(value=${Arrays.toString(value)})"
    }
}

class SolArray1<T>(value: Array<T>) : SolFixedArray<T>(value)
class SolArray2<T>(value: Array<T>) : SolFixedArray<T>(value)
class SolArray3<T>(value: Array<T>) : SolFixedArray<T>(value)
class SolArray4<T>(value: Array<T>) : SolFixedArray<T>(value)
class SolArray5<T>(value: Array<T>) : SolFixedArray<T>(value)
class SolArray6<T>(value: Array<T>) : SolFixedArray<T>(value)
class SolArray7<T>(value: Array<T>) : SolFixedArray<T>(value)
class SolArray8<T>(value: Array<T>) : SolFixedArray<T>(value)
class SolArray9<T>(value: Array<T>) : SolFixedArray<T>(value)
class SolArray10<T>(value: Array<T>) : SolFixedArray<T>(value)
class SolArray11<T>(value: Array<T>) : SolFixedArray<T>(value)
class SolArray12<T>(value: Array<T>) : SolFixedArray<T>(value)
class SolArray13<T>(value: Array<T>) : SolFixedArray<T>(value)
class SolArray14<T>(value: Array<T>) : SolFixedArray<T>(value)
class SolArray15<T>(value: Array<T>) : SolFixedArray<T>(value)
class SolArray16<T>(value: Array<T>) : SolFixedArray<T>(value)
class SolArray17<T>(value: Array<T>) : SolFixedArray<T>(value)
class SolArray18<T>(value: Array<T>) : SolFixedArray<T>(value)
class SolArray19<T>(value: Array<T>) : SolFixedArray<T>(value)
class SolArray20<T>(value: Array<T>) : SolFixedArray<T>(value)
class SolArray21<T>(value: Array<T>) : SolFixedArray<T>(value)
class SolArray22<T>(value: Array<T>) : SolFixedArray<T>(value)
class SolArray23<T>(value: Array<T>) : SolFixedArray<T>(value)
class SolArray24<T>(value: Array<T>) : SolFixedArray<T>(value)
class SolArray25<T>(value: Array<T>) : SolFixedArray<T>(value)
class SolArray26<T>(value: Array<T>) : SolFixedArray<T>(value)
class SolArray27<T>(value: Array<T>) : SolFixedArray<T>(value)
class SolArray28<T>(value: Array<T>) : SolFixedArray<T>(value)
class SolArray29<T>(value: Array<T>) : SolFixedArray<T>(value)
class SolArray30<T>(value: Array<T>) : SolFixedArray<T>(value)
class SolArray31<T>(value: Array<T>) : SolFixedArray<T>(value)
class SolArray32<T>(value: Array<T>) : SolFixedArray<T>(value)
