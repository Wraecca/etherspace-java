package cc.etherspace.calladapter

import java.lang.reflect.Type

interface CallAdapter<in R, out T> {
    fun adapt(block: () -> R): T

    fun adaptable(returnType: Type, annotations: Array<Annotation>): Boolean

    fun toActualReturnType(type: Type): Type
}
