package com.oliynick.max.toothpickadapter.misc

import toothpick.Scope

/**
 * Injection target contract. Inject targets that implements this interface must have
 * key identifier [Key] and scope [Scope].
 *
 * Key identifier is used to allow Android components such as Activity or Fragment survive
 * configuration changes. For this purpose these components should store key in bundle or somewhere
 * else
 */
interface ComponentHolder {
    val key: Key
    val scope: Scope
}