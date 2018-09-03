package com.oliynick.max.toothpickadapter.rule

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import toothpick.Scope
import toothpick.Toothpick
import toothpick.config.Module
import toothpick.registries.FactoryRegistry
import toothpick.registries.FactoryRegistryLocator
import toothpick.registries.MemberInjectorRegistry
import toothpick.registries.MemberInjectorRegistryLocator
import toothpick.testing.ToothPickTestModule



class ToothpickTestRule(test: Any, scopeName: Any? = null, vararg testModules: Module) : TestRule {
    private val testModule = ToothPickTestModule(test)
    private val scope: Scope?

    init {
        scope = scopeName?.let { Toothpick.openScope(it) }?.also { it.installTestModules(testModule, *testModules) }
    }

    override fun apply(base: Statement?, description: Description?): Statement = object : Statement() {
        override fun evaluate() {
            try {
                base?.evaluate()
            } finally {
                Toothpick.reset()
            }
        }
    }

    fun setRootRegistryPackage(rootRegistryPackageName: String): ToothpickTestRule {
        try {
            val factoryRegistryClass = Class.forName("$rootRegistryPackageName.FactoryRegistry")
            val memberInjectorRegistryClass = Class.forName("$rootRegistryPackageName.MemberInjectorRegistry")
            FactoryRegistryLocator.setRootRegistry(factoryRegistryClass.newInstance() as FactoryRegistry)
            MemberInjectorRegistryLocator.setRootRegistry(memberInjectorRegistryClass.newInstance() as MemberInjectorRegistry)
            return this
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid package to find registries : $rootRegistryPackageName", e)
        }

    }

    fun inject(objectUnderTest: Any) {
        Toothpick.inject(objectUnderTest, scope)
    }

    fun <T> getInstance(clazz: Class<T>): T {
        return getInstance(clazz, null)
    }

    fun <T> getInstance(clazz: Class<T>, name: String?): T {
        return scope!!.getInstance(clazz, name)
    }
}