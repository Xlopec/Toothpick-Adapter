package com.oliynick.max.toothpickadapter

import java.util.concurrent.atomic.AtomicLong

/**
 * Created by Максим on 2/20/2017.
 */

private val ID_GENERATOR: AtomicLong = AtomicLong(0)

fun generateKey(): Key = Key("_Key#${ID_GENERATOR.incrementAndGet()}")

fun generateKey(who: Class<*>): Key = Key("_Key#$who#${ID_GENERATOR.incrementAndGet()}")

fun generateKey(who: Any): Key = generateKey(who.javaClass)
