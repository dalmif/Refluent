package io.kayt.refluent.core.data.utils

import kotlinx.coroutines.CancellationException

fun <T> Result<T>.exemptCancellation(): Result<T> {
    return this.onFailure { if (it is CancellationException) throw it }
}
