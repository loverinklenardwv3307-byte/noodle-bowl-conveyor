package com.broth.noodleconveyor.domain.usecase

import com.broth.noodleconveyor.domain.model.Ingredient
import com.broth.noodleconveyor.domain.model.ServeOutcome

class ServeBowlUseCase {

    fun matches(bowl: List<Ingredient>, ticket: List<Ingredient>): Boolean =
        bowl.size == ticket.size && countOf(bowl) == countOf(ticket)

    operator fun invoke(
        bowl: List<Ingredient>,
        ticket: List<Ingredient>,
        currentCombo: Int,
        tipBase: Int,
        comboCap: Int
    ): ServeOutcome {
        if (!matches(bowl, ticket)) return ServeOutcome(matched = false, tip = 0, combo = 0)
        val combo = currentCombo + 1
        val tip = tipBase * combo.coerceIn(1, comboCap)
        return ServeOutcome(matched = true, tip = tip, combo = combo)
    }

    private fun countOf(items: List<Ingredient>): Map<Ingredient, Int> =
        items.groupingBy { it }.eachCount()
}
