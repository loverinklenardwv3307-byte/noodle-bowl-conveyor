package com.broth.noodleconveyor.domain.repository

import com.broth.noodleconveyor.domain.model.AppSettings
import com.broth.noodleconveyor.domain.model.DecorItem
import com.broth.noodleconveyor.domain.model.PlayerProgress
import com.broth.noodleconveyor.domain.model.Recipe
import com.broth.noodleconveyor.domain.model.Restaurant
import com.broth.noodleconveyor.domain.model.ShiftResult
import kotlinx.coroutines.flow.StateFlow

interface GameRepository {

    val progress: StateFlow<PlayerProgress>

    val settings: StateFlow<AppSettings>

    val restaurants: List<Restaurant>

    val decorations: List<DecorItem>

    val recipes: List<Recipe>

    fun selectShop(shopId: Int)

    fun saveSettings(settings: AppSettings)

    fun saveShift(result: ShiftResult)

    fun buyDecor(item: DecorItem)

    fun markFirstShiftDone()

    fun resetProgress()
}
