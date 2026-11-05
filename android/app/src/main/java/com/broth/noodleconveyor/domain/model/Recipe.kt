package com.broth.noodleconveyor.domain.model

data class Recipe(
    val id: Int,
    val nameRes: Int,
    val noteRes: Int,
    val ingredients: List<Ingredient>,
    val unlockAtBowls: Int
)
