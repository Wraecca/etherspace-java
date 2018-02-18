@file:Suppress("unused")

package cc.etherspace

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

interface Tuple

data class Tuple4<out A, out B, out C, out D>(val first: A, val second: B, val third: C, val fourth: D) : Tuple

data class Tuple5<out A, out B, out C, out D, out E>(val first: A,
                                                     val second: B,
                                                     val third: C,
                                                     val fourth: D,
                                                     val fifth: E) : Tuple

data class Tuple6<out A, out B, out C, out D, out E, out F>(val first: A,
                                                            val second: B,
                                                            val third: C,
                                                            val fourth: D,
                                                            val fifth: E,
                                                            val sixth: F) : Tuple

data class Tuple7<out A, out B, out C, out D, out E, out F, out G>(val first: A,
                                                                   val second: B,
                                                                   val third: C,
                                                                   val fourth: D,
                                                                   val fifth: E,
                                                                   val sixth: F,
                                                                   val seventh: G) : Tuple

data class Tuple8<out A, out B, out C, out D, out E, out F, out G, out H>(val first: A,
                                                                          val second: B,
                                                                          val third: C,
                                                                          val fourth: D,
                                                                          val fifth: E,
                                                                          val sixth: F,
                                                                          val seventh: G,
                                                                          val eighth: H) : Tuple

data class Tuple9<out A, out B, out C, out D, out E, out F, out G, out H, out I>(val first: A,
                                                                                 val second: B,
                                                                                 val third: C,
                                                                                 val fourth: D,
                                                                                 val fifth: E,
                                                                                 val sixth: F,
                                                                                 val seventh: G,
                                                                                 val eighth: H,
                                                                                 val ninth: I) : Tuple

data class Tuple10<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J>(val first: A,
                                                                                         val second: B,
                                                                                         val third: C,
                                                                                         val fourth: D,
                                                                                         val fifth: E,
                                                                                         val sixth: F,
                                                                                         val seventh: G,
                                                                                         val eighth: H,
                                                                                         val ninth: I,
                                                                                         val tenth: J) : Tuple

fun Type.isTuple(): Boolean {
    if (this !is ParameterizedType) {
        return false
    }
    @Suppress("UNCHECKED_CAST")
    val rawClass = rawType as Class<Any>
    if (Pair::class.java.isAssignableFrom(rawClass) ||
            Triple::class.java.isAssignableFrom(rawClass) ||
            Tuple::class.java.isAssignableFrom(rawClass)) {
        return true
    }
    return false
}

fun Type.listTupleActualTypes(): List<Type> {
    if (!isTuple()) {
        return listOf(this)
    }

    @Suppress("UNCHECKED_CAST")
    return (this as ParameterizedType).actualTypeArguments.toList()
}

fun Type.createTupleInstance(values: List<Any>): Any? {
    if (values.isEmpty()) {
        return null
    }

    if (!isTuple()) {
        return values[0]
    }

    @Suppress("UNCHECKED_CAST")
    val rawClass = (this as ParameterizedType).rawType as Class<Any>
    // TODO rethrow this exception
    val constructor = rawClass.constructors.first { it.parameterCount == values.size }
    return constructor.newInstance(*values.toTypedArray())
}
