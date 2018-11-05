package com.oliynick.max.toothpickadapter.misc

import android.app.Activity
import toothpick.Scope
import toothpick.Toothpick
import toothpick.config.Module
import java.util.concurrent.atomic.AtomicLong

private val ID_GENERATOR: AtomicLong = AtomicLong(0)

fun generateKey(): Key = Key("_Key#${ID_GENERATOR.incrementAndGet()}")

fun generateKey(who: Class<*>): Key = Key("_Key#$who#${ID_GENERATOR.incrementAndGet()}")

fun generateKey(who: Any): Key = generateKey(who.javaClass)

internal fun scopeName(activity: Activity, vararg names: Any): Array<Any> {
    if (activity is ComponentHolder) {
        return arrayOf(*activity.names, *names)
    }

    return arrayOf(activity.application, activity, *names)
}

internal fun scopeName(activity: Activity, name: Any): Array<Any> {
    if (activity is ComponentHolder) {
        return arrayOf(*activity.names, name)
    }

    return arrayOf(activity.application, activity, name)
}

fun <T, A : Annotation> Scope.getInstance(clazz: Class<out T>, name: Class<out A>): T = getInstance(clazz, name.name)

fun Any.inject(modules: Array<out Module>, names: Array<Any>): Scope = Toothpick.openScopes(*names)
        .also { it.installModules(*modules) }
        .also { Toothpick.inject(this, it) }

fun Any.inject(module: Module, names: Array<Any>): Scope = Toothpick.openScopes(*names)
        .also { it.installModules(module) }
        .also { Toothpick.inject(this, it) }

fun Any.inject(module: Module, name: Any): Scope = Toothpick.openScope(name)
        .also { it.installModules(module) }
        .also { Toothpick.inject(this, it) }