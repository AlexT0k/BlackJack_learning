package com.example.blackjack.ui.theme
import com.example.blackjack.model.Card

data class BlackjackUiState(
    val playerHand: List<Card> = emptyList(),
    val dealerHand: List<Card> = emptyList(),
    val hideDealerHoleCard: Boolean = true,
    val playerScore: Int = 0,
    val dealerScore: Int = 0,
    val isRoundOver: Boolean = false,
    val canDoubleDown: Boolean = false,
    val statusText: String = "Tap Deal to start"
)