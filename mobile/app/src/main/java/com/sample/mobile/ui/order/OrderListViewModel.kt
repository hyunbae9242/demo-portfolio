package com.sample.mobile.ui.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.mobile.data.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrderListViewModel(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<OrderUiState>(OrderUiState.Loading)
    val uiState: StateFlow<OrderUiState> = _uiState.asStateFlow()

    fun loadOrders(customerId: Long) {
        viewModelScope.launch {
            _uiState.value = OrderUiState.Loading
            try {
                val orders = orderRepository.getOrders(customerId)
                _uiState.value = OrderUiState.Success(orders)
            } catch (e: Exception) {
                _uiState.value = OrderUiState.Error(
                    message = e.message ?: "Unknown error",
                    code = "ERROR"
                )
            }
        }
    }

    fun confirmOrder(orderId: Long) {
        viewModelScope.launch {
            try {
                orderRepository.confirmOrder(orderId)
                // Reload orders after confirmation
            } catch (e: Exception) {
                _uiState.value = OrderUiState.Error(e.message ?: "Failed to confirm order")
            }
        }
    }
}
