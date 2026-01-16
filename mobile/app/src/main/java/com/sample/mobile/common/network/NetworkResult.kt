package com.sample.mobile.common.network

sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val code: String, val message: String) : NetworkResult<Nothing>()
    object Loading : NetworkResult<Nothing>()
}
