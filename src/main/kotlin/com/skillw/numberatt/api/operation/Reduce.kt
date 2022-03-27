package com.skillw.numberatt.api.operation

object Reduce : BaseOperation("reduce") {
    override fun run(a: Double, b: Double): Double {
        return a - b
    }
}