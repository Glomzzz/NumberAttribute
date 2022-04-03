package com.skillw.numberatt.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.numberatt.NumberAttribute
import com.skillw.numberatt.api.attribute.ConfigAttribute
import com.skillw.numberatt.api.condition.ScriptCondition
import com.skillw.numberatt.api.read.ReadGroup
import com.skillw.pouvoir.api.manager.ConfigManager
import com.skillw.pouvoir.api.manager.Manager.Companion.addSingle
import com.skillw.pouvoir.util.FileUtils
import java.io.File

object NAConfig : ConfigManager(NumberAttribute) {
    override val priority = 0
    override fun defaultOptions(): Map<String, Map<String, Any>> = emptyMap()

    override val isCheckVersion
        get() = this["config"].getBoolean("options.check-version")

    override fun onInit() {
        this.createIfNotExists("attributes", "BaseAttribute.yml")
        this.createIfNotExists("conditions", "conditions.yml")
        this.createIfNotExists("read", "default.yml")
    }

    override fun onLoad() {
        AttributeSystem.readPatternManager.addSingle("Reload") {
            FileUtils.loadMultiply(
                File(NumberAttribute.plugin.dataFolder, "read"), ReadGroup::class.java
            ).forEach {
                it.key.register()
            }
        }
        AttributeSystem.attributeManager.addSingle("Reload") {
            FileUtils.loadMultiply(
                File(NumberAttribute.plugin.dataFolder, "attributes"), ConfigAttribute::class.java
            ).forEach {
                it.key.register()
            }
        }
        AttributeSystem.conditionManager.addSingle("Reload") {
            FileUtils.loadMultiply(
                File(NumberAttribute.plugin.dataFolder, "conditions"), ScriptCondition::class.java
            ).forEach {
                it.key.register()
            }
        }


    }

    override fun onEnable() {
        onReload()
    }

    override fun subReload() {
        AttributeSystem.reload()
    }

    val numberPattern: String
        get() = this["config"].getString("options.number-pattern")
            ?: "(?<value>(\\\\+|\\\\-)?(\\\\d+(?:(\\\\.\\\\d+))?))"
    val statStatus
        get() = this["config"].getString("stats.status") ?: "Status:"
    val attributeFormat
        get() = this["config"].getString("stats.attribute-format") ?: "&7{name} &8= &c{value}"

    val statNone
        get() = this["config"].getString("stats.none") ?: "None"

    val statStatusValue
        get() = this["config"].getString("stats.status-value") ?: "{key} = {value}"

    val statPlaceholder
        get() = this["config"].getString("stats.placeholder") ?: "Placeholder:"

    val statPlaceholderValue
        get() = this["config"].getString("stats.placeholder-value") ?: "{key} = {value}"
}
