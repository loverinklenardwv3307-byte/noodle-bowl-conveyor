package com.broth.noodleconveyor.domain.model

data class AppSettings(
    val pace: ShiftPace = ShiftPace.STEADY,
    val haptics: Boolean = true,
    val leftHandMode: Boolean = false,
    val relaxedMode: Boolean = false
)
