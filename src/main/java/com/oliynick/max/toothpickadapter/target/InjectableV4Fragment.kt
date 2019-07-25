package com.oliynick.max.toothpickadapter.target

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.oliynick.max.toothpickadapter.injector.FragmentInjector
import com.oliynick.max.toothpickadapter.injector.HasInjector
import toothpick.config.Module

abstract class InjectableV4Fragment protected constructor(private inline val provider: (InjectableV4Fragment, Bundle?) -> Array<out Module> = { _, _ -> emptyArray() }) : androidx.fragment.app.Fragment(), HasInjector<androidx.fragment.app.Fragment> {

    protected constructor(vararg modules: Module) : this({ _, _ -> arrayOf(*modules) })

    protected constructor(module: Module) : this({ _, _ -> arrayOf(module) })

    final override lateinit var injector: FragmentInjector

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

    protected open fun provideInjector(savedInstanceState: Bundle?): FragmentInjector {
        return FragmentInjector.newInstance(this, savedInstanceState)
    }

}