package cc.etherspace.calladapter

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.concurrent.CompletableFuture

class CompletableFutureCallAdapter<T> : CallAdapter<T, CompletableFuture<T>> {
    override fun adapt(block: () -> T): CompletableFuture<T> {
        return CompletableFuture.supplyAsync {
            block()
        }
    }

    override fun adaptable(returnType: Type, annotations: Array<Annotation>): Boolean {
        if (returnType !is ParameterizedType) {
            return false
        }
        if (returnType.rawType != CompletableFuture::class.java) {
            return false
        }
        return true
    }

    override fun toActualReturnType(type: Type): Type = (type as ParameterizedType).actualTypeArguments[0]
}
