package com.skillw.numberatt.api.operation

import kotlin.math.min

object Min : BaseOperation("min") {
    override fun run(a: Double, b: Double): Double {
        return min(a, b)
    }
}