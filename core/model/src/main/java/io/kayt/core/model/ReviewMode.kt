package io.kayt.core.model

enum class ReviewMode {
    /** Always shows front side for all cards*/
    FrontFirst,

    /** Always shows back side for all cards */
    BackFirst,

    /** Show back or front randomly */
    ShuffleSides,

    /** Show both sides for all cards */
    DualSided
}