package com.broth.noodleconveyor.domain.usecase

import com.broth.noodleconveyor.domain.model.Ingredient
import kotlin.random.Random

class BuildTicketUseCase {

    fun ticketSize(orderIndex: Int): Int = when {
        orderIndex < 3 -> 2
        orderIndex < 6 -> 3
        else -> 4
    }

    operator fun invoke(orderIndex: Int, random: Random): List<Ingredient> {
        val pool = Ingredient.entries
        return List(ticketSize(orderIndex)) { pool[random.nextInt(pool.size)] }
    }
}
