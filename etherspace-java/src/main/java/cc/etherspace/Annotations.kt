package cc.etherspace

import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION)
@MustBeDocumented
annotation class Call

@Target(AnnotationTarget.FUNCTION)
@MustBeDocumented
annotation class Send

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@MustBeDocumented
annotation class Gas(val gas: String, val gasPrice: String)

@Target(AnnotationTarget.CONSTRUCTOR)
@MustBeDocumented
annotation class EventConstructor

@Target(AnnotationTarget.VALUE_PARAMETER)
@MustBeDocumented
annotation class Indexed(val argumentType: KClass<out Any>)