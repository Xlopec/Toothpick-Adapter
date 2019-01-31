package com.oliynick.max.toothpickadapter.injector

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import com.oliynick.max.toothpickadapter.misc.*

class FragmentInjector private constructor(override val key: Key,
                                           override val names: Array<Any>) : Injector<Fragment>() {

    companion object {
        private val TAG = FragmentInjector::class.java.name!!
        private const val ARG_KEY = "argKey"

        @JvmStatic
        fun newInstance(fragment: Fragment, savedInstanceState: Bundle?): FragmentInjector {
            val key = savedInstanceState?.getParcelable(ARG_KEY) ?: generateKey(fragment)
            val names = fragment.provideScopeNames(key)

            return FragmentInjector(key, names)
        }

        @JvmStatic
        fun newInstance(names: Array<Any>, savedInstanceState: Bundle?): FragmentInjector {
            val key = savedInstanceState?.getParcelable(ARG_KEY) ?: generateKey(this)

            return FragmentInjector(key, names)
        }

        @JvmStatic
        fun newInstance(key: Key, names: Array<Any>): FragmentInjector {
            return FragmentInjector(key, names)
        }
    }

    private var isOnSaveStateCalled = false

    fun onSaveInstanceState(outState: Bundle) {
        Log.d(TAG, "calling onSaveInstanceState, key=$key")
        outState.putParcelable(ARG_KEY, key)
        isOnSaveStateCalled = true
    }

    override fun shouldRelease(target: Fragment): Boolean {
        // We don't keep fragment injections when:
        // * activity is finishing
        // * activity is being destroyed because of non-config changes
        // * activity is being destroyed because of config changes,
        // BUT onSaveInstanceState haven't been called
        return target.requireActivity().willNotBeReCreated()
                || (target.requireActivity().isDestroyingBecauseOfConfigChanges() && !isOnSaveStateCalled)
    }

    override fun onPostDestroy(target: Fragment) {
        isOnSaveStateCalled = false
    }
}