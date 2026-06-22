package com.tylerdev.stonks.util

/**
 * Wraps the outcome of an asynchronous operation (e.g. a network or database call) so callers
 * can handle loading, success, and error states in a single, type-safe type.
 *
 * Repositories typically expose [Resource] from suspend functions or flows; ViewModels map
 * each variant to UI state (spinner, content, or error message).
 *
 * @param T The type of payload returned on success.
 * @property data Optional payload. Present on [Success], optionally on [Loading] (stale/cached
 *   data) and [Error] (partial result), otherwise null.
 * @property message Human-readable error text. Set only for [Error], otherwise null.
 */
sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Loading<T>(val isLoading: Boolean = true) : Resource<T>(null)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
}