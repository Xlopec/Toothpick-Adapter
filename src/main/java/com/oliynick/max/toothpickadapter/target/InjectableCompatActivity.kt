package com.oliynick.max.toothpickadapter.target

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.oliynick.max.toothpickadapter.injector.ActivityInjector
import com.oliynick.max.toothpickadapter.injector.HasInjector
import toothpick.config.Module

abstract class InjectableCompatActivity protected constructor(private inline val provider: (InjectableCompatActivity, Bundle?) -> Array<out Module> = { _, _ -> emptyArray() }) : AppCompatActivity(),
        HasInjector<Activity> {

    protected constructor(vararg modules: Module) : this({ _, _ -> arrayOf(*modules) })

    protected constructor(module: Module) : this({ _, _ -> arrayOf(module) })

    override lateinit var injector: ActivityInjector

    override fun onCreate(savedInstanceState: Bundle?) {
        injector = provideInjector(savedInstanceState)


        injector.inject(this, provider(this, savedInstanceState))


        super.onCreate(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        injector.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        injector.onDestroy(this)
        super.onDestroy()
    }

    protected open fun provideInjector(savedInstanceState: Bundle?): ActivityInjector {
        return ActivityInjector.newInstance(this, savedInstanceState)
    }

}