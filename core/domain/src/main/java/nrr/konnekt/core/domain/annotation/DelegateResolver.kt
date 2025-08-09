package nrr.konnekt.core.domain.annotation

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.FIELD
)
annotation class DelegateResolver
