package com.broth.noodleconveyor.domain.model

data class BowlState(
    val index: Int,
    val items: List<Ingredient> = emptyList()
) {
    fun isFull(capacity: Int): Boolean = items.size >= capacity

    fun isEmpty(): Boolean = items.isEmpty()
}
