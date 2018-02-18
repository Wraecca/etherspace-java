package cc.etherspace.calladapter

import java.lang.reflect.Type

class PassThroughCallAdaptor<T> : CallAdapter<T, T?> {
    override fun toActualReturnType(type: Type): Type = type

    override fun adapt(block: () -> T?): T? {
        return block()
    }

    override fun adaptable(returnType: Type, annotations: Array<Annotation>): Boolean = true
}