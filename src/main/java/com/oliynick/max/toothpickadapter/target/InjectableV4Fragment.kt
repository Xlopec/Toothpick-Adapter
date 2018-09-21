package com.oliynick.max.toothpickadapter.target

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import com.oliynick.max.toothpickadapter.misc.*
import toothpick.Scope
import toothpick.Toothpick
import toothpick.config.Module

abstract class InjectableV4Fragment protected constructor(private inline val provider: (InjectableV4Fragment) -> Array<out Module> = { emptyArray() }) : Fragment(),
        ComponentHolder {

    private companion object {
        private val TAG = InjectableV4Fragment::class.java.name!!
        private const val ARG_KEY = "argKey"
    }

    protected constructor(vararg modules: Module) : this({ arrayOf(*modules) })

    protected constructor(module: Module) : this({ arrayOf(module) })

    final override lateinit var key: Key
    final override lateinit var scope: Scope

    private var isOnSaveStateCalled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        key = savedInstanceState?.getParcelable(ARG_KEY) ?: generateKey(this)

        val modules = provider(this)

        Log.d(TAG, "Creating injections for key=$key")
        // opening scopes: App -> Activity -> Fragment
        scope = requireActivity().let { inject(modules, arrayOf(it.application, scopeName(it), key)) }

        onPostInject(key, scope, modules, savedInstanceState)
        super.onCreate(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(ARG_KEY, key)
        isOnSaveStateCalled = true
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        try {
            if (activity!!.isFinishing || !isOnSaveStateCalled) {
                // fragment won't be recreated later
                // destroy to avoid memory leaks
                Toothpick.closeScope(key)
                Log.d(TAG, "Releasing all injections for ${javaClass.name}, key=$key")
            } else {
                Log.d(TAG, "Keeping injections for ${javaClass.name}, key=$key")
            }
        } finally {
            isOnSaveStateCalled = false
            super.onDestroy()
        }
    }

    protected open fun onPostInject(key: Key, scope: Scope, modules: Array<out Module>, savedInstanceState: Bundle?) = Unit

}