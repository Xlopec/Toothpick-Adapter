package com.oliynick.max.toothpickadapter.injector

interface HasInjector<T: Any> {
    val injector: Injector<T>
}