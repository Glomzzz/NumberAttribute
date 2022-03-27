package com.skillw.numberatt.api.operation

fun interface Operation {
    fun run(a: Double, b: Double): Double
}