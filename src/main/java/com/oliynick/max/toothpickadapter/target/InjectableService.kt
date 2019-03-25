package com.oliynick.max.toothpickadapter.target

import android.app.Service
import com.oliynick.max.toothpickadapter.injector.Injector
import com.oliynick.max.toothpickadapter.injector.ServiceInjector

abstract class InjectableService : Service() {

    protected open fun provideInjector(): Injector<Service> {
        return ServiceInjector.newInstance(this)
    }

}