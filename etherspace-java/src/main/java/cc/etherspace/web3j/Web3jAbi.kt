package cc.etherspace.web3j

import cc.etherspace.*
import com.google.common.reflect.TypeParameter
import com.google.common.reflect.TypeToken
import org.web3j.abi.EventEncoder
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.FunctionReturnDecoder
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.*
import org.web3j.abi.datatypes.Event
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.generated.*
import unsigned.toBigInt
import java.lang.reflect.Constructor
import java.math.BigInteger
import kotlin.reflect.KClass

class Web3jAbi : Web3.Abi {
    @Suppress("UNCHECKED_CAST")
    override fun decodeParameters(types: List<java.lang.reflect.Type>, hexString: String): List<Any> {
        return FunctionReturnDecoder.decode(hexString,
                types.mapNotNull { toWeb3jType(it) } as List<TypeReference<Type<Any>>>)
                .map { toSolValue(it) }
    }

    override fun encodeFunctionCall(parameters: List<Any>,
                                    functionName: String): String {
        val function = Function(functionName,
                parameters.map { toWeb3jValue(it) },
                emptyList())
        return FunctionEncoder.encode(function)
    }

    override fun <T> encodeEventSignature(clazz: Class<T>): String {
        val constructor = getEventConstructor(clazz)
        return EventEncoder.encode(toWeb3jEvent(clazz.simpleName, constructor))
    }

    override fun <T> decodeLog(clazz: Class<T>, hexString: String, topics: List<String>): T? {
        val constructor = getEventConstructor(clazz)
        val event = toWeb3jEvent(clazz.simpleName, constructor)

        val nonIndexedValues = FunctionReturnDecoder.decode(hexString, event.nonIndexedParameters)
        val indexedValues = event.indexedParameters.mapIndexed { index, typeReference ->
            FunctionReturnDecoder.decodeIndexedValue(topics[index], typeReference)
        }

        var nonIndexedValuesIdx = 0
        var indexValuesIdx = 0
        val args = constructor.parameters.map {
            if (it.getAnnotation(Indexed::class.java) != null) {
                indexedValues[indexValuesIdx++]
            } else {
                nonIndexedValues[nonIndexedValuesIdx++]
            }
        }.map { toSolValue(it) }
        return constructor.newInstance(*args.toTypedArray())
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> getEventConstructor(clazz: Class<T>): Constructor<T> {
        return (clazz.constructors.firstOrNull { it.getAnnotation(EventConstructor::class.java) != null }
                ?: throw IllegalArgumentException("Need at least one constructor annotated with @EventConstructor")) as Constructor<T>
    }

    private fun <T> toWeb3jEvent(eventName: String, constructor: Constructor<T>): Event {
        val indexedParams = constructor.parameters.filter { it.getAnnotation(Indexed::class.java) != null }
        val dataParams = constructor.parameters.filter { !indexedParams.contains(it) }
        return Event(eventName,
                indexedParams.map {
                    toWeb3jType(it.getAnnotation(Indexed::class.java).argumentType.java)
                },
                dataParams.map { toWeb3jType(it.type) })
    }

    private fun toWeb3jValue(value: Any): Type<out Any> {
        return when (value) {
            is String -> Utf8String(value)
            is Boolean -> Bool(value)
            is BigInteger -> Int256(value)
            is Short -> Int16(value.toBigInt())
            is Int -> Int32(value.toBigInteger())
            is Long -> Int64(value.toBigInteger())
            is SolNumber -> value.toWeb3jValue()
            is SolAddress -> Address(value.address)
            is Byte -> Bytes1(byteArrayOf(value))
            is SolBytes15 -> Bytes15(value.value)
            is SolBytes32 -> Bytes32(value.value)
            is ByteArray -> DynamicBytes(value)
            is SolFixedArray<*> -> StaticArray(value.value.map { toWeb3jValue(it!!) })
            is Array<*> -> StaticArray(value.map { toWeb3jValue(it!!) })
            is List<*> -> DynamicArray(value.map { toWeb3jValue(it!!) })
            else -> throw IllegalArgumentException()
        }
    }

    private fun toWeb3jType(type: java.lang.reflect.Type): TypeReference<out Type<out Any>> {
        val typeToken = TypeToken.of(type)
        @Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
        val web3jType = when {
            typeToken.childOf(String::class) -> Utf8String::class.java
            typeToken.childOf(Boolean::class) -> Bool::class.java
            typeToken.childOf(BigInteger::class) -> Int256::class.java
            typeToken.childOf(SolNumber::class) -> typeToken.toWeb3jType()
            typeToken.childOf(SolAddress::class) -> Address::class.java
            typeToken.childOf(Byte::class) -> Bytes1::class.java
            typeToken.childOf(SolBytes15::class) -> Bytes15::class.java
            typeToken.childOf(SolBytes32::class) -> Bytes32::class.java
            typeToken.childOf(ByteArray::class) -> DynamicBytes::class.java
            typeToken.childOf(List::class) -> toWeb3jArrayType(typeToken)
            typeToken.childOf(SolFixedArray::class) -> return toWeb3jStaticArrayTypeReference(typeToken)
            else -> throw IllegalArgumentException()
        }
        return createTypeReference(web3jType)
    }

    @Suppress("UNCHECKED_CAST")
    private fun toWeb3jStaticArrayTypeReference(typeToken: TypeToken<*>): TypeReference.StaticArrayTypeReference<Type<*>> {
        val parameterizedType = typeToken.resolveType(SolFixedArray::class.java.getMethod("get",
                Int::class.java).genericReturnType)
        val web3jType = toWeb3jType(parameterizedType.type).type
        val size = typeToken.toSolTypeSize()
        return createStaticArrayTypeReference(arrayToken(TypeToken.of(web3jType) as TypeToken<Type<*>>).type, size)
    }

    @Suppress("UNCHECKED_CAST")
    private fun toWeb3jArrayType(typeToken: TypeToken<*>): java.lang.reflect.Type {
        val parameterizedType = typeToken.resolveType(List::class.java.getMethod("get",
                Int::class.java).genericReturnType)
        val web3jType = toWeb3jType(parameterizedType.type).type
        return listToken(TypeToken.of(web3jType) as TypeToken<Type<*>>).type
    }

    private fun <T : Type<*>> listToken(elementToken: TypeToken<T>): TypeToken<DynamicArray<T>> =
            object : TypeToken<DynamicArray<T>>() {}
                    .where(object : TypeParameter<T>() {}, elementToken)

    private fun <T : Type<*>> arrayToken(elementToken: TypeToken<T>): TypeToken<StaticArray4<T>> =
            object : TypeToken<StaticArray4<T>>() {}
                    .where(object : TypeParameter<T>() {}, elementToken)

    private fun <T : Type<*>> createStaticArrayTypeReference(type: java.lang.reflect.Type,
                                                             size: Int): TypeReference.StaticArrayTypeReference<T> {
        return object : TypeReference.StaticArrayTypeReference<T>(size) {
            override fun getType(): java.lang.reflect.Type {
                return type
            }
        }
    }

    private fun <T : org.web3j.abi.datatypes.Type<*>> createTypeReference(type: java.lang.reflect.Type): TypeReference<T> {
        return object : TypeReference<T>() {
            override fun getType(): java.lang.reflect.Type {
                return type
            }
        }
    }

    private fun toSolValue(value: Type<Any>): Any {
        return when (value) {
            is Utf8String -> value.value
            is Bool -> value.value
            is Int256 -> value.value
            is Int64 -> value.value.toLong()
            is Int32 -> value.value.toInt()
            is Int16 -> value.value.toShort()
            is Uint64 -> value.value.toLong()
            is Uint32 -> value.value.toInt()
            is Uint16 -> value.value.toShort()
            is NumericType -> value.toSolValue()
            is Address -> cc.etherspace.SolAddress(value.value)
            is Bytes1 -> value.value[0]
            is Bytes15 -> cc.etherspace.SolBytes15(value.value)
            is Bytes32 -> cc.etherspace.SolBytes32(value.value)
            is DynamicBytes -> value.value
            is StaticArray<*> -> value.toSolFixedArray()
            is DynamicArray<*> -> value.value.map { toSolValue(it) }
            else -> throw IllegalArgumentException()
        }
    }

    private fun StaticArray<*>.toSolFixedArray(): SolFixedArray<*> {
        val constructor = Class.forName(javaClass.name.replace("$WEB3J_ABI_DATATYPES_PACKAGE.Static",
                "$SOL_TYPE_PACKAGE.Sol")).getConstructor(Array<Any>::class.java)
        return constructor.newInstance(value.map { toSolValue(it) }.toTypedArray()) as SolFixedArray<*>
    }

    private fun TypeToken<*>.toWeb3jType(): Class<*> = Class.forName(type.typeName.replace("$SOL_TYPE_PACKAGE.Sol",
            "$WEB3J_ABI_DATATYPES_PACKAGE.")) as Class<*>

    private fun TypeToken<out Any>.childOf(kClass: KClass<out Any>): Boolean {
        return isSubtypeOf(kClass.java)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : NumericType> SolNumber.toWeb3jValue(): Type<T> = Class.forName(javaClass.name.replace("$SOL_TYPE_PACKAGE.Sol",
            "$WEB3J_ABI_DATATYPES_PACKAGE."))
            .getConstructor(BigInteger::class.java)
            .newInstance(value) as Type<T>

    @Suppress("UNCHECKED_CAST")
    private fun <T : SolNumber> NumericType.toSolValue(): T {
        return Class.forName(javaClass.name.replace("$WEB3J_ABI_DATATYPES_PACKAGE.",
                "$SOL_TYPE_PACKAGE.Sol")).getConstructor(BigInteger::class.java).newInstance(value) as T
    }

    private fun TypeToken<*>.toSolTypeSize(): Int {
        return "[a-zA-Z.]+(\\d)+<[\\w.]+>".toRegex().matchEntire(type.typeName)!!.groups[1]!!.value.toInt()
    }

    companion object {
        private val WEB3J_ABI_DATATYPES_PACKAGE = AbiTypes::class.java.`package`.name
        private val SOL_TYPE_PACKAGE = SolType::class.java.`package`.name
    }
}
