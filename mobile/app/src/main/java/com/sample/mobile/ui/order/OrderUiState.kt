package com.sample.mobile.ui.order

import com.sample.mobile.domain.model.Order

sealed class OrderUiState {
    object Loading : OrderUiState()
    data class Success(val orders: List<Order>) : OrderUiState()
    data class Error(val message: String, val code: String? = null) : OrderUiState()
}
