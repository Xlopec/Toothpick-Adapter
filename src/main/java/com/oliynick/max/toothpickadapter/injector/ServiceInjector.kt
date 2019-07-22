package com.oliynick.max.toothpickadapter.injector

import android.app.Service
import com.oliynick.max.toothpickadapter.misc.Key
import com.oliynick.max.toothpickadapter.misc.generateKey

class ServiceInjector private constructor(override val key: Key,
                                          override val names: Array<Any>) : Injector<Service>() {

    companion object {

        fun newInstance(service: Service): Injector<Service> {
            val key = generateKey(service)

            return ServiceInjector(key, arrayOf(service.application, key))
        }
    }

    override fun shouldRelease(target: Service): Boolean = true
}