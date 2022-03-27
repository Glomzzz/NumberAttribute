package com.skillw.numberatt.api.status

import com.skillw.attsystem.api.attribute.status.Status
import com.skillw.numberatt.api.operation.Operation
import com.skillw.numberatt.api.read.ReadGroup
import com.skillw.pouvoir.api.map.LowerMap
import org.bukkit.configuration.serialization.ConfigurationSerializable

class NumberStatus(val readGroup: ReadGroup) : Status, LowerMap<Double>(), ConfigurationSerializable {

    override fun toString(): String {
        return map.toString()
    }

    override fun clone(): Status {
        val attributeStatus = NumberStatus(readGroup)
        this.forEach {
            attributeStatus.register(it.key, it.value)
        }
        return attributeStatus
    }

    override fun get(key: String): Double {
        return super.get(key) ?: 0.0
    }

    fun opposite(): NumberStatus {
        this.replaceAll { k, _ -> -get(k) }
        return this
    }

    fun operation(key: String, value: Double, operation: Operation): NumberStatus {
        if (this.containsKey(key)) {
            this.register(key, operation.run(get(key), value))
        } else {
            this.register(key, value)
        }
        return this
    }

    override fun operation(status: Status): Status {
        if (status !is NumberStatus) return this
        for (key in status.keys) {
            if (this.containsKey(key)) {
                this.register(key, readGroup.operations[key]!!.run(get(key), status[key]))
            } else {
                this.register(key, status[key])
            }
        }
        return this
    }

    fun operation(attributeStatus: NumberStatus, operation: Operation): NumberStatus {
        for (key in attributeStatus.keys) {
            if (this.containsKey(key)) {
                this.register(key, operation.run(get(key), attributeStatus[key]))
            } else {
                this.register(key, attributeStatus[key])
            }
        }
        return this
    }

    override fun serialize(): MutableMap<String, Any> {
        return HashMap(this)
    }

}