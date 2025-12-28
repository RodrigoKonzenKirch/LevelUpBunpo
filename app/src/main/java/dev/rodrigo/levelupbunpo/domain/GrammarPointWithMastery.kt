package dev.rodrigo.levelupbunpo.domain

import dev.rodrigo.levelupbunpo.data.local.GrammarPoint

data class GrammarPointWithMastery(
    val grammarPoint: GrammarPoint,
    val currentMastery: Int,
    val maxMastery: Int
)
