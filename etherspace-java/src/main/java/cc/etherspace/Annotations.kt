package cc.etherspace

@Target(AnnotationTarget.FUNCTION)
@MustBeDocumented
annotation class Call

@Target(AnnotationTarget.FUNCTION)
@MustBeDocumented
annotation class Send

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@MustBeDocumented
annotation class Gas(val gas: String, val gasPrice: String)
