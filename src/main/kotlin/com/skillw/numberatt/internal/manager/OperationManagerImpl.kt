package com.skillw.numberatt.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.numberatt.api.manager.OperationManager
import com.skillw.numberatt.api.operation.*
import com.skillw.numberatt.api.operation.ScriptOperation
import com.skillw.pouvoir.util.FileUtils
import java.io.File

object OperationManagerImpl : OperationManager() {
    override val key = "OperationManager"
    override val priority: Int = 0
    override val subPouvoir = AttributeSystem

    override fun onEnable() {
        Max.register()
        Min.register()
        Plus.register()
        Reduce.register()
        Scalar.register()
        onReload()
    }

    override fun onReload() {
        this.entries.filter { it.value.release }.forEach { this.remove(it.key) }
        FileUtils.loadMultiply(
            File(AttributeSystem.plugin.dataFolder, "operation"), ScriptOperation::class.java
        ).forEach {
            it.key.register()
        }
    }

}