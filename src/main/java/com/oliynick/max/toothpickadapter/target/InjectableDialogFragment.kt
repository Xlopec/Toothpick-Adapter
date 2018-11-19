package com.oliynick.max.toothpickadapter.target

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.util.Log
import com.oliynick.max.toothpickadapter.misc.*
import toothpick.Scope
import toothpick.Toothpick
import toothpick.config.Module

abstract class InjectableDialogFragment protected constructor(private inline val provider: (InjectableDialogFragment, Bundle?) -> Array<out Module> = { _, _ -> emptyArray() }) : DialogFragment(),
        ComponentHolder {

    private companion object {
        private val TAG = InjectableDialogFragment::class.java.name!!
        private const val ARG_KEY = "argKey"
    }

    protected constructor(vararg modules: Module) : this({ _, _ -> arrayOf(*modules) })

    protected constructor(module: Module) : this({ _, _ -> arrayOf(module) })

    final override lateinit var key: Key
    final override lateinit var scope: Scope
    final override lateinit var names: Array<Any>

    private var isOnSaveStateCalled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        key = savedInstanceState?.getParcelable(ARG_KEY) ?: generateKey(this)

        val modules = provider(this, savedInstanceState)

        Log.d(TAG, "Creating injections for key=$key")
        // opening scopes: App -> Activity -> Fragment
        names = provideScopeNames(requireActivity(), key, parentFragment)
        scope = inject(modules, names)

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

    protected open fun provideScopeNames(activity: Activity, key: Key, parentFragment: Fragment?) = scopeName(requireActivity(), key)

    protected open fun onPostInject(key: Key, scope: Scope, modules: Array<out Module>, savedInstanceState: Bundle?) = Unit

}