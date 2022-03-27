package com.skillw.numberatt.api.operation

import com.skillw.numberatt.NumberAttribute
import com.skillw.pouvoir.api.able.Keyable

abstract class BaseOperation(override val key: String) : Operation, Keyable<String> {

    var release = false
    override fun register() {
        NumberAttribute.operationManager.register(this)
    }
}