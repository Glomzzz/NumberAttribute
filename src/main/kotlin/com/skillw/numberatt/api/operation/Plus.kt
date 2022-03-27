package com.skillw.numberatt.api.operation

object Plus : BaseOperation("plus") {
    override fun run(a: Double, b: Double): Double {
        return a + b
    }
}