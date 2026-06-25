package com.tylerdev.stonks.domain.usecase

import com.tylerdev.stonks.domain.repository.StockRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: StockRepository
) {
    suspend operator fun invoke(symbol: String, isFavorite: Boolean) {
        repository.toggleFavorite(symbol, isFavorite)
    }
}
