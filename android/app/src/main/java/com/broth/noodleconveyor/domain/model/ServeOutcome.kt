package com.broth.noodleconveyor.domain.model

data class ServeOutcome(
    val matched: Boolean,
    val tip: Int,
    val combo: Int
)
