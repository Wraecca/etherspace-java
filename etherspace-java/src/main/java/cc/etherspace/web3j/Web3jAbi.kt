package cc.etherspace.web3j

import cc.etherspace.UBigInteger
import cc.etherspace.Web3
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.FunctionReturnDecoder
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.*
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.generated.*
import java.lang.reflect.ParameterizedType
import java.math.BigInteger

class Web3jAbi : Web3.Abi {
    override fun decodeParameters(types: List<java.lang.reflect.Type>, hexString: String): List<Any> {
        return FunctionReturnDecoder.decode(hexString,
                types.mapNotNull { toWeb3jType(it) } as List<TypeReference<Type<Any>>>)
                .map { toJavaValue(it) }
    }

    override fun encodeFunctionCall(parameters: List<Any>,
                                    functionName: String): String {
        val function = Function(functionName,
                parameters.map { toWeb3jValue(it) },
                emptyList())
        return FunctionEncoder.encode(function)
    }

    private fun toWeb3jType(type: java.lang.reflect.Type): TypeReference<out Type<out Any>> {
        if (type is ParameterizedType) {
            @Suppress("UNCHECKED_CAST")
            val rawClass = type.rawType as Class<Any>
            val web3jType = when {
                List::class.java.isAssignableFrom(rawClass) -> {
                    DynamicArray::class.java
                }
                else -> throw IllegalArgumentException()
            }
            return TypeReference.create(web3jType)
        }

        @Suppress("UNCHECKED_CAST")
        val clazz = type as Class<Any>
        val web3jType = when {
            String::class.java.isAssignableFrom(clazz) -> Utf8String::class.java
            Boolean::class.java.isAssignableFrom(clazz) -> Bool::class.java
            BigInteger::class.java.isAssignableFrom(clazz) -> Int256::class.java
            UBigInteger::class.java.isAssignableFrom(clazz) -> Uint256::class.java
            cc.etherspace.SolInt24::class.java.isAssignableFrom(clazz) -> Int24::class.java
            cc.etherspace.SolAddress::class.java.isAssignableFrom(clazz) -> Address::class.java
            Byte::class.java.isAssignableFrom(clazz) -> Bytes1::class.java
            cc.etherspace.SolBytes15::class.java.isAssignableFrom(clazz) -> Bytes15::class.java
            cc.etherspace.SolBytes32::class.java.isAssignableFrom(clazz) -> Bytes32::class.java
            ByteArray::class.java.isAssignableFrom(clazz) -> DynamicBytes::class.java
            else -> throw IllegalArgumentException()
        }
        return TypeReference.create(web3jType)
    }

    private fun toWeb3jValue(value: Any): Type<out Any> {
        return when (value) {
            is String -> Utf8String(value)
            is Boolean -> Bool(value)
            is BigInteger -> Int256(value)
            is UBigInteger -> Uint256(value.value)
            is cc.etherspace.SolInt24 -> Int24(value.value.toBigInteger())
            is cc.etherspace.SolAddress -> Address(value.address)
            is List<*> -> {
                DynamicArray(value.map { toWeb3jValue(it!!) })
            }
            is Byte -> Bytes1(byteArrayOf(value))
            is cc.etherspace.SolBytes15 -> Bytes15(value.value)
            is cc.etherspace.SolBytes32 -> Bytes32(value.value)
            is ByteArray -> DynamicBytes(value)
            else -> throw IllegalArgumentException()
        }
    }

    private fun toJavaValue(value: Type<Any>): Any {
        return when (value) {
            is Utf8String -> value.value
            is Bool -> value.value
            is Int256 -> value.value
            is Uint256 -> UBigInteger(value.value)
            is Int24 -> cc.etherspace.SolInt24(value.value.toInt())
            is Address -> cc.etherspace.SolAddress(value.value)
            is DynamicArray<*> -> value.value
            is Bytes1 -> value.value[0]
            is Bytes15 -> cc.etherspace.SolBytes15(value.value)
            is Bytes32 -> cc.etherspace.SolBytes32(value.value)
            is DynamicBytes -> value.value
            else -> throw IllegalArgumentException()
        }
    }
}