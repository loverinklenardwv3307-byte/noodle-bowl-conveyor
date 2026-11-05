package com.broth.noodleconveyor.data.sample

import com.broth.noodleconveyor.R
import com.broth.noodleconveyor.domain.model.DecorItem
import com.broth.noodleconveyor.domain.model.Ingredient
import com.broth.noodleconveyor.domain.model.Recipe
import com.broth.noodleconveyor.domain.model.Restaurant

object SampleData {

    val restaurants: List<Restaurant> = listOf(
        Restaurant(1, R.string.shop_1, 0),
        Restaurant(2, R.string.shop_2, 1),
        Restaurant(3, R.string.shop_3, 2),
        Restaurant(4, R.string.shop_4, 3),
        Restaurant(5, R.string.shop_5, 4),
        Restaurant(6, R.string.shop_6, 5),
        Restaurant(7, R.string.shop_7, 6),
        Restaurant(8, R.string.shop_8, 7),
        Restaurant(9, R.string.shop_9, 8)
    )

    val recipes: List<Recipe> = listOf(
        Recipe(1, R.string.recipe_1, R.string.recipe_1_note, listOf(Ingredient.NOODLES, Ingredient.BROTH), 0),
        Recipe(2, R.string.recipe_2, R.string.recipe_2_note, listOf(Ingredient.BROTH, Ingredient.EGG), 0),
        Recipe(3, R.string.recipe_3, R.string.recipe_3_note, listOf(Ingredient.NOODLES, Ingredient.GREENS), 4),
        Recipe(4, R.string.recipe_4, R.string.recipe_4_note, listOf(Ingredient.NOODLES, Ingredient.NOODLES), 8),
        Recipe(5, R.string.recipe_5, R.string.recipe_5_note, listOf(Ingredient.NOODLES, Ingredient.BROTH, Ingredient.EGG), 12),
        Recipe(6, R.string.recipe_6, R.string.recipe_6_note, listOf(Ingredient.BROTH, Ingredient.GREENS, Ingredient.GREENS), 18),
        Recipe(7, R.string.recipe_7, R.string.recipe_7_note, listOf(Ingredient.NOODLES, Ingredient.EGG, Ingredient.CHILLI), 24),
        Recipe(8, R.string.recipe_8, R.string.recipe_8_note, listOf(Ingredient.NOODLES, Ingredient.BROTH, Ingredient.CHILLI), 32),
        Recipe(9, R.string.recipe_9, R.string.recipe_9_note, listOf(Ingredient.NOODLES, Ingredient.BROTH, Ingredient.EGG, Ingredient.GREENS), 42),
        Recipe(10, R.string.recipe_10, R.string.recipe_10_note, listOf(Ingredient.EGG, Ingredient.EGG, Ingredient.BROTH), 54),
        Recipe(11, R.string.recipe_11, R.string.recipe_11_note, listOf(Ingredient.GREENS, Ingredient.GREENS, Ingredient.CHILLI), 68),
        Recipe(12, R.string.recipe_12, R.string.recipe_12_note, listOf(Ingredient.NOODLES, Ingredient.BROTH, Ingredient.EGG, Ingredient.CHILLI), 84)
    )

    val decorations: List<DecorItem> = listOf(
        DecorItem(1, R.string.decor_1, 6, 1),
        DecorItem(2, R.string.decor_2, 8, 2),
        DecorItem(3, R.string.decor_3, 10, 3),
        DecorItem(4, R.string.decor_4, 12, 4),
        DecorItem(5, R.string.decor_5, 14, 5),
        DecorItem(6, R.string.decor_6, 16, 1),
        DecorItem(7, R.string.decor_7, 18, 2),
        DecorItem(8, R.string.decor_8, 20, 3),
        DecorItem(9, R.string.decor_9, 24, 4),
        DecorItem(10, R.string.decor_10, 28, 5),
        DecorItem(11, R.string.decor_11, 32, 1),
        DecorItem(12, R.string.decor_12, 36, 2)
    )
}
