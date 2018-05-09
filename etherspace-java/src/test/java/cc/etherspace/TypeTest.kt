package cc.etherspace

import com.google.common.reflect.TypeToken
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be true`
import org.amshove.kluent.`should equal`
import org.junit.Test
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.createType
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.jvm.jvmErasure
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class TypeTest {
    fun listTypes() {
        val method = TestClass::class.java.methods.first { it.name == "test1" }
        method.genericParameterTypes.size.`should be equal to`(3)

        var type = method.genericParameterTypes[0]
        assertFalse { type is ParameterizedType }
        type.`should equal`(String::class.java)

        type = method.genericParameterTypes[1]
        assertTrue { type is ParameterizedType }
        (type as ParameterizedType).rawType.`should equal`(List::class.java)
        type.actualTypeArguments[0].`should equal`(String::class.java)

        type = method.genericParameterTypes[2]
        assertTrue { type is ParameterizedType }
        (type as ParameterizedType).rawType.`should equal`(List::class.java)
        type = type.actualTypeArguments[0]
        assertTrue { type is ParameterizedType }
        (type as ParameterizedType).rawType.`should equal`(List::class.java)
        type.actualTypeArguments[0].`should equal`(String::class.java)
    }

    @Test
    fun listTypeTokens() {
        val method = TestClass::class.java.methods.first { it.name == "test1" }
        method.genericParameterTypes.size.`should be equal to`(3)

        var token = TypeToken.of(method.genericParameterTypes[0])
        token.rawType.`should equal`(String::class.java)

        token = TypeToken.of(method.genericParameterTypes[1])
        token.rawType.`should equal`(List::class.java)
        token = token.resolveType(List::class.java.getMethod("get", Int::class.java).genericReturnType)
        token.rawType.`should equal`(String::class.java)

        token = TypeToken.of(method.genericParameterTypes[2])
        token.rawType.`should equal`(List::class.java)
        token = token.resolveType(List::class.java.getMethod("get", Int::class.java).genericReturnType)
        token.rawType.`should equal`(List::class.java)
        token = token.resolveType(List::class.java.getMethod("get", Int::class.java).genericReturnType)
        token.rawType.`should equal`(String::class.java)
    }

    @Test
    fun listTypeTokens_return() {
        val method = TestClass::class.java.methods.first { it.name == "test1" }
        method.genericParameterTypes.size.`should be equal to`(3)

        var token = TypeToken.of(method.genericReturnType)
        token.rawType.`should equal`(Triple::class.java)

        val tripleType = token.type as ParameterizedType
        token = TypeToken.of(tripleType.actualTypeArguments[0])
        token.rawType.`should equal`(String::class.java)

        token = TypeToken.of(tripleType.actualTypeArguments[1])
        token.rawType.`should equal`(List::class.java)
        token = token.resolveType(List::class.java.getMethod("get", Int::class.java).genericReturnType)
        token.rawType.`should equal`(String::class.java)

        token = TypeToken.of(tripleType.actualTypeArguments[2])
        token.rawType.`should equal`(List::class.java)
        token = token.resolveType(List::class.java.getMethod("get", Int::class.java).genericReturnType)
        token.rawType.`should equal`(List::class.java)
        token = token.resolveType(List::class.java.getMethod("get", Int::class.java).genericReturnType)
        token.rawType.`should equal`(String::class.java)
    }

    @Test
    fun listTypeTokens_array() {
        val method = TestClass::class.java.methods.first { it.name == "test2" }
        method.genericParameterTypes.size.`should be equal to`(3)

        var token = TypeToken.of(method.genericParameterTypes[0])
        token.rawType.`should equal`(String::class.java)

        token = TypeToken.of(method.genericParameterTypes[1])
        token.isArray.`should be true`()
        token = token.componentType
        token.rawType.`should equal`(String::class.java)

        token = TypeToken.of(method.genericParameterTypes[2])
        token.isArray.`should be true`()
        token = token.componentType
        token.isArray.`should be true`()
        token = token.componentType
        token.rawType.`should equal`(String::class.java)
    }

    @Test
    fun listTypeTokens_arrayReturn() {
        val method = TestClass::class.java.methods.first { it.name == "test2" }
        method.genericParameterTypes.size.`should be equal to`(3)

        var token = TypeToken.of(method.genericReturnType)
        token.rawType.`should equal`(Triple::class.java)

        val tripleType = token.type as ParameterizedType
        token = TypeToken.of(tripleType.actualTypeArguments[0])
        token.rawType.`should equal`(String::class.java)

        token = TypeToken.of(tripleType.actualTypeArguments[1])
        token.isArray.`should be true`()
        token = token.componentType
        token.rawType.`should equal`(String::class.java)

        token = TypeToken.of(tripleType.actualTypeArguments[2])
        token.isArray.`should be true`()
        token = token.componentType
        token.isArray.`should be true`()
        token = token.componentType
        token.rawType.`should equal`(String::class.java)
    }

    @Test
    fun kotlinTypes_return() {
        val arguments = TestClass::test1.returnType.arguments
        arguments.size.`should be equal to`(3)
        arguments[0].type.`should equal`(String::class.createType())

        arguments[1].type.`should equal`(List::class.createType(listOf(KTypeProjection.invariant(String::class.starProjectedType))))
        arguments[1].type!!.jvmErasure.java.`should equal`(List::class.java)
        arguments[1].type!!.arguments.size.`should be equal to`(1)
        arguments[1].type!!.arguments[0].type.`should equal`(String::class.createType())
        arguments[1].type!!.arguments[0].type!!.jvmErasure.java.`should equal`(String::class.java)

        arguments[2].type!!.arguments.size.`should be equal to`(1)
        arguments[2].type!!.arguments[0].type!!.arguments[0].type.`should equal`(String::class.createType())
    }

    class TestClass {
        fun test1(str: String,
                  strs: List<String>,
                  strInStrs: List<List<String>>): Triple<String, List<String>, List<List<String>>> {
            return Triple(str, strs, strInStrs)
        }

        fun test2(str: String,
                  strs: Array<String>,
                  strInStrs: Array<Array<String>>): Triple<String, Array<String>, Array<Array<String>>> {
            return Triple(str, strs, strInStrs)
        }
    }
}