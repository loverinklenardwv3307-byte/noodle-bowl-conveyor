package com.broth.noodleconveyor.domain.usecase

class ScoreShiftUseCase {

    fun stars(served: Int, missed: Int, totalOrders: Int): Int = when {
        served >= totalOrders && missed == 0 -> 3
        served >= 6 -> 2
        served >= 3 -> 1
        else -> 0
    }

    fun stamps(tips: Int, tipsPerStamp: Int, doubled: Boolean): Int {
        val base = if (tipsPerStamp <= 0) 0 else tips / tipsPerStamp
        return if (doubled) base * 2 else base
    }
}
