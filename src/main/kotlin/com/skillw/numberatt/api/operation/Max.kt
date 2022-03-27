package com.skillw.numberatt.api.operation

import kotlin.math.max

object Max : BaseOperation("max") {
    override fun run(a: Double, b: Double): Double {
        return max(a, b)
    }
}