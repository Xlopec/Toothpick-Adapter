package com.oliynick.max.toothpickadapter.target

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.oliynick.max.toothpickadapter.misc.ComponentHolder
import com.oliynick.max.toothpickadapter.misc.Key
import com.oliynick.max.toothpickadapter.misc.generateKey
import com.oliynick.max.toothpickadapter.misc.inject
import toothpick.Scope
import toothpick.Toothpick
import toothpick.config.Module

abstract class InjectableCompatActivity protected constructor(private inline val provider: (InjectableCompatActivity, Bundle?) -> Array<out Module> = { _, _ -> emptyArray() }) : AppCompatActivity(),
        ComponentHolder {

    private companion object {
        private val TAG = InjectableCompatActivity::class.java.name!!
        private const val ARG_KEY = "argKey"
    }

    protected constructor(vararg modules: Module) : this({ _, _ -> arrayOf(*modules) })

    protected constructor(module: Module) : this({ _, _ -> arrayOf(module) })

    final override lateinit var key: Key
    final override lateinit var scope: Scope
    final override lateinit var names: Array<Any>

    override fun onCreate(savedInstanceState: Bundle?) {
        key = savedInstanceState?.getParcelable(ARG_KEY) ?: generateKey(this)

        val modules = provider(this, savedInstanceState)

        Log.d(TAG, "Creating injections for key=$key")
        // opening scopes: App -> Activity
        names = arrayOf(application, key)
        scope = inject(modules, names)

        onPostInject(key, scope, modules, savedInstanceState)
        super.onCreate(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(ARG_KEY, key)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        try {
            if (isFinishing) {
                // activity won't be recreated later
                // destroy to avoid memory leaks
                Toothpick.closeScope(key)
                Log.d(TAG, "Releasing all injections for ${javaClass.name}, key=$key")
            } else {
                Log.d(TAG, "Keeping injections for ${javaClass.name}, key=$key")
            }
        } finally {
            super.onDestroy()
        }
    }

    protected open fun onPostInject(key: Key, scope: Scope, modules: Array<out Module>, savedInstanceState: Bundle?) = Unit

}