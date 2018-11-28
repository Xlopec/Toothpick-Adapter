package com.oliynick.max.toothpickadapter.injector

import android.util.Log
import com.oliynick.max.toothpickadapter.misc.Key
import com.oliynick.max.toothpickadapter.misc.inject
import toothpick.Toothpick
import toothpick.config.Module

abstract class Injector<T : Any> {

    private companion object {
        val TAG = Injector::class.java.name!!
    }

    protected abstract val key: Key
    protected abstract val names: Array<Any>

    fun inject(target: T, modules: Array<out Module>) {
        Log.d(TAG, "Injecting modules=$modules into $target")
        target.inject(modules, names)
        onPostInject(target)
    }

    fun onDestroy(target: T) {
        onPreDestroy(target)

        try {
            if (shouldRelease(target)) {
                Toothpick.closeScope(key)
                Log.d(TAG, "Releasing all injections for $target, key=$key")
            } else {
                Log.d(TAG, "Keeping injections for $target, key=$key")
            }
        } finally {
            onPostDestroy(target)
        }
    }

    protected abstract fun shouldRelease(target: T): Boolean

    protected open fun onPreDestroy(target: T) = Unit

    protected open fun onPostDestroy(target: T) = Unit

    protected open fun onPostInject(target: T) = Unit

    fun childScopeName(name: Any): Array<Any> = arrayOf(*names, name)

    fun childScopeName(name: Any, vararg names: Any): Array<Any> = arrayOf(*this.names, name, *names)

}