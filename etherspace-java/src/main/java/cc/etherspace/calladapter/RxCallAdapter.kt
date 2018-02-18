package cc.etherspace.calladapter

import rx.Observable
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class RxCallAdapter<T> : CallAdapter<T, Observable<T>> {
    override fun adapt(block: () -> T): Observable<T> {
        return Observable.create {
            try {
                val ret = block()
                if (!it.isUnsubscribed) {
                    it.onNext(ret)
                    it.onCompleted()
                }
            } catch (e: Exception) {
                if (!it.isUnsubscribed) {
                    it.onError(e)
                }
            }
        }
    }

    override fun adaptable(returnType: Type, annotations: Array<Annotation>): Boolean {
        if (returnType !is ParameterizedType) {
            return false
        }
        if (returnType.rawType != Observable::class.java) {
            return false
        }
        return true
    }

    override fun toActualReturnType(type: Type): Type = (type as ParameterizedType).actualTypeArguments[0]
}
