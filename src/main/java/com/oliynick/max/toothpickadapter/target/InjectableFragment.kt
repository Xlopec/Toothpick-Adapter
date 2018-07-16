package com.oliynick.max.toothpickadapter.target

import android.app.Fragment
import android.os.Bundle
import android.util.Log
import com.oliynick.max.toothpickadapter.misc.*
import toothpick.Scope
import toothpick.Toothpick
import toothpick.config.Module

abstract class InjectableFragment protected constructor(private inline val provider: (InjectableFragment) -> Array<out Module> = { emptyArray() }) : Fragment(),
        ComponentHolder {

    private companion object {
        private val TAG = InjectableFragment::class.java.name!!
        private const val ARG_KEY = "argKey"
    }

    protected constructor(vararg modules: Module) : this({ arrayOf(*modules) })

    protected constructor(module: Module) : this({ arrayOf(module) })

    final override lateinit var key: Key
    final override lateinit var scope: Scope

    private var isOnSaveStateCalled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        key = savedInstanceState?.getParcelable(ARG_KEY) ?: generateKey(this)

        Log.d(TAG, "Creating injections for key=$key")
        // opening scopes: App -> Activity -> Fragment
        scope = inject(provider(this), arrayOf(activity.application, scopeName(activity), key))
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
                Log.d(TAG, "Releasing all injections for ${javaClass.name}")
            } else {
                Log.d(TAG, "Keeping injections for ${javaClass.name}")
            }
        } finally {
            isOnSaveStateCalled = false
            super.onDestroy()
        }
    }
}