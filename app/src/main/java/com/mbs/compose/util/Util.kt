package com.mbs.compose.util

fun calculateTotalTip(totalBill: Float, tipPercentage: Int): Float {
    return if (totalBill > 1 && totalBill.toString().isNotEmpty()) {
        (totalBill * tipPercentage) / 100
    } else 0.0f
}

fun calculateTotalPerPerson(
    totalBill: Float,
    splitBy: Int,
    tipPercentage: Int
): Float {
    val bill = calculateTotalTip(totalBill, tipPercentage) + totalBill
    return bill / splitBy
}