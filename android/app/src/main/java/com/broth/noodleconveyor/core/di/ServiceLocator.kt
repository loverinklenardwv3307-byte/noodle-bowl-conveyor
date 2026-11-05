package com.broth.noodleconveyor.core.di

import android.content.Context
import com.broth.noodleconveyor.data.local.NoodlePreferences
import com.broth.noodleconveyor.data.repository.GameRepositoryImpl
import com.broth.noodleconveyor.domain.repository.GameRepository
import com.broth.noodleconveyor.domain.usecase.BuildTicketUseCase
import com.broth.noodleconveyor.domain.usecase.ScoreShiftUseCase
import com.broth.noodleconveyor.domain.usecase.ServeBowlUseCase

object ServiceLocator {

    private var store: NoodlePreferences? = null

    fun init(context: Context) {
        if (store == null) store = NoodlePreferences(context)
    }

    private fun requireStore(): NoodlePreferences =
        checkNotNull(store) { "ServiceLocator.init was not called" }

    val gameRepository: GameRepository by lazy { GameRepositoryImpl(requireStore()) }

    val serveBowl: ServeBowlUseCase by lazy { ServeBowlUseCase() }

    val scoreShift: ScoreShiftUseCase by lazy { ScoreShiftUseCase() }

    val buildTicket: BuildTicketUseCase by lazy { BuildTicketUseCase() }
}
