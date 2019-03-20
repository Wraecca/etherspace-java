package cc.etherspace.calladapter

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class CoroutineCallAdapter<T> : CallAdapter<T, Deferred<T>> {
    override fun toActualReturnType(type: Type): Type = (type as ParameterizedType).actualTypeArguments[0]

    override fun adaptable(returnType: Type, annotations: Array<Annotation>): Boolean {
        if (returnType !is ParameterizedType) {
            return false
        }
        if (returnType.rawType != Deferred::class.java) {
            return false
        }
        return true
    }

    override fun adapt(block: () -> T): Deferred<T> {
        return GlobalScope.async { block() }
    }
}
