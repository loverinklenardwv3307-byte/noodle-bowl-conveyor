package com.broth.noodleconveyor.domain.model

data class CustomerOrder(
    val id: Long,
    val station: Int,
    val spriteSlot: Int,
    val ticket: List<Ingredient>,
    val patienceMs: Long,
    val remainingMs: Long
) {
    val patienceFraction: Float
        get() = if (patienceMs <= 0L) 1f else (remainingMs.toFloat() / patienceMs.toFloat()).coerceIn(0f, 1f)
}
