package com.oliynick.max.toothpickadapter.misc

import android.app.Activity
import android.support.v4.app.Fragment
import com.oliynick.max.toothpickadapter.injector.HasInjector
import toothpick.Scope
import toothpick.Toothpick
import toothpick.config.Module
import java.util.concurrent.atomic.AtomicLong

private val ID_GENERATOR: AtomicLong = AtomicLong(0)

fun generateKey(): Key = Key("_Key#${ID_GENERATOR.incrementAndGet()}")

fun generateKey(who: Class<*>): Key = Key("_Key#$who#${ID_GENERATOR.incrementAndGet()}")

fun generateKey(who: Any): Key = generateKey(who.javaClass)

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

internal fun Activity.willNotBeReCreated(): Boolean {
    return isFinishing || !isDestroyingBecauseOfConfigChanges()
}

internal fun Activity.isDestroyingBecauseOfConfigChanges(): Boolean {
    return changingConfigurations != 0
}

internal fun Activity.provideScopeNames(key: Key): Array<Any> {
    return arrayOf(application, key)
}

internal fun Activity.provideChildScopeNames(key: Key): Array<Any> {
    if (this is HasInjector<*>) {
        return injector.childScopeName(key)
    }

    return arrayOf(application, key)
}

internal fun Fragment.provideScopeNames(key: Key): Array<Any> {
    var parentFragment: Fragment? = parentFragment

    while (parentFragment != null && parentFragment !is HasInjector<*>) {
        parentFragment = parentFragment.parentFragment
    }

    if (parentFragment is HasInjector<*>) {
        return parentFragment.injector.childScopeName(key)
    }
    // try host activity
    return requireActivity().provideChildScopeNames(key)
}