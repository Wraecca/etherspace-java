package cc.etherspace

import kotlin.reflect.KClass

/**
 * This is a constant method without sending any transaction
 */
@Target(AnnotationTarget.FUNCTION)
@MustBeDocumented
annotation class Call(val functionName: String = "")

/**
 * This is a method for sending transaction
 */
@Target(AnnotationTarget.FUNCTION)
@MustBeDocumented
annotation class Send(val functionName: String = "")

/**
 * Specify gas and gas price for this transaction
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@MustBeDocumented
annotation class Gas(val gas: String, val gasPrice: String)

/**
 * This is a constructor for Event. The parameters for this constructor must match the parameters from Solidity Event in order.
 */
@Target(AnnotationTarget.CONSTRUCTOR)
@MustBeDocumented
annotation class EventConstructor

/**
 * This parameter is an indexed parameter in Event.
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@MustBeDocumented
annotation class Indexed(val argumentType: KClass<out Any>)