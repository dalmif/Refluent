package io.kayt.refluent.core.database.dto

enum class ReviewModeDto(val key: String) {
    FrontFirst("front-first"),
    BackFirst("back-first"),
    ShuffleSides("shuffle-sides"),
    DualSided("dual-sided");


    companion object {
        fun fromString(key: String) : ReviewModeDto {
            return when (key) {
                "front-first" -> FrontFirst
                "back-first" -> BackFirst
                "shuffle-sides" -> ShuffleSides
                "dual-sided" -> DualSided
                else -> throw IllegalArgumentException("Unknown review mode: $key")
            }
        }
    }
}