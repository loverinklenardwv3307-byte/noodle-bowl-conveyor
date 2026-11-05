package com.broth.noodleconveyor.core.ui

import com.broth.noodleconveyor.R
import com.broth.noodleconveyor.domain.model.Ingredient

fun ingredientDrawable(ingredient: Ingredient): Int = when (ingredient) {
    Ingredient.NOODLES -> R.drawable.sprite_1
    Ingredient.BROTH -> R.drawable.sprite_2
    Ingredient.EGG -> R.drawable.sprite_3
    Ingredient.GREENS -> R.drawable.sprite_4
    Ingredient.CHILLI -> R.drawable.sprite_5
}

fun ingredientLabel(ingredient: Ingredient): Int = when (ingredient) {
    Ingredient.NOODLES -> R.string.ing_noodles
    Ingredient.BROTH -> R.string.ing_broth
    Ingredient.EGG -> R.string.ing_egg
    Ingredient.GREENS -> R.string.ing_greens
    Ingredient.CHILLI -> R.string.ing_chilli
}

fun spriteForSlot(slot: Int): Int = when (((slot % 5) + 5) % 5) {
    0 -> R.drawable.sprite_1
    1 -> R.drawable.sprite_2
    2 -> R.drawable.sprite_3
    3 -> R.drawable.sprite_4
    else -> R.drawable.sprite_5
}
