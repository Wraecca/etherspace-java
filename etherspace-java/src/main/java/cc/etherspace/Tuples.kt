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

/**
 * @return true if this is a Pair, Triple, or Tuple4~10
 */
@Suppress("UNCHECKED_CAST")
fun Type.isTuple(): Boolean {
    if (this !is ParameterizedType) {
        return false
    }
    val rawClass = rawType as Class<Any>
    if (Pair::class.java.isAssignableFrom(rawClass) ||
            Triple::class.java.isAssignableFrom(rawClass) ||
            Tuple::class.java.isAssignableFrom(rawClass)) {
        return true
    }
    return false
}

@Suppress("UNCHECKED_CAST")
fun Type.listTupleActualTypes(): List<Type> {
    if (!isTuple()) {
        return listOf(this)
    }
    return (this as ParameterizedType).actualTypeArguments.toList()
}

@Suppress("UNCHECKED_CAST")
fun Type.createTupleInstance(values: List<Any>): Any {
    require(values.isNotEmpty(), { "A call to Solidity function should return something instead of empty data" })

    if (!isTuple()) {
        return values[0]
    }

    val rawClass = (this as ParameterizedType).rawType as Class<Any>
    val constructor = rawClass.constructors.firstOrNull { it.parameterCount == values.size }
    checkNotNull(constructor, { "Number of arguments is not the same as the size of this Tuple(or Pair, Triple)" })
    return constructor!!.newInstance(*values.toTypedArray())
}
