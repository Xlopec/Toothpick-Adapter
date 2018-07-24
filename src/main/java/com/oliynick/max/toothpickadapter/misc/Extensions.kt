package com.oliynick.max.toothpickadapter.misc

import toothpick.Scope
import toothpick.Toothpick
import toothpick.config.Module
import java.util.concurrent.atomic.AtomicLong

private val ID_GENERATOR: AtomicLong = AtomicLong(0)

fun generateKey(): Key = Key("_Key#${ID_GENERATOR.incrementAndGet()}")

fun generateKey(who: Class<*>): Key = Key("_Key#$who#${ID_GENERATOR.incrementAndGet()}")

fun generateKey(who: Any): Key = generateKey(who.javaClass)

internal fun scopeName(any: Any): Any = any.let { it as? ComponentHolder }?.key ?: any

fun Any.inject(modules: Array<out Module>, names: Array<Any>): Scope = Toothpick.openScopes(*names)
        .also { it.installModules(*modules) }
        .also { Toothpick.inject(this, it) }