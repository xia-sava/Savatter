package to.sava.savatter.di

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

inline fun <reified T> getF(): T {
    return object : KoinComponent {
        val value: T by inject()
    }.value
}
