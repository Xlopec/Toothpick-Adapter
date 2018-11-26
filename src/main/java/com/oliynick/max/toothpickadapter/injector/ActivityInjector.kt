package com.oliynick.max.toothpickadapter.injector

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.oliynick.max.toothpickadapter.misc.Key
import com.oliynick.max.toothpickadapter.misc.generateKey
import com.oliynick.max.toothpickadapter.misc.provideScopeNames
import com.oliynick.max.toothpickadapter.misc.willNotBeReCreated

class ActivityInjector private constructor(override val key: Key,
                                           override val names: Array<Any>) : Injector<Activity>() {

    companion object {
        private val TAG = FragmentInjector::class.java.name!!
        private const val ARG_KEY = "argKey"

        @JvmStatic
        fun newInstance(activity: Activity, savedInstanceState: Bundle?): ActivityInjector {
            val key = savedInstanceState?.getParcelable(ARG_KEY) ?: generateKey(this)
            val names = activity.provideScopeNames(key)

            return ActivityInjector(key, names)
        }

        @JvmStatic
        fun newInstance(names: Array<Any>, savedInstanceState: Bundle?): ActivityInjector {
            val key = savedInstanceState?.getParcelable(ARG_KEY) ?: generateKey(this)

            return ActivityInjector(key, names)
        }

        @JvmStatic
        fun newInstance(key: Key, names: Array<Any>): ActivityInjector {
            return ActivityInjector(key, names)
        }
    }

    fun onSaveInstanceState(outState: Bundle) {
        Log.d(TAG, "calling onSaveInstanceState")
        outState.putParcelable(ARG_KEY, key)
    }

    override fun shouldRelease(target: Activity): Boolean {
        // We don't keep fragment injections when:
        // * activity is finishing
        // * activity is being destroyed because of non-config changes
        return target.willNotBeReCreated()
    }

}