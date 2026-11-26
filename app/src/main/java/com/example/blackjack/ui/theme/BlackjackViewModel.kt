package com.example.blackjack.ui.theme

import androidx.lifecycle.ViewModel
import com.example.blackjack.model.Card
import com.example.blackjack.model.Shoe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BlackjackViewModel : ViewModel() {
    private val shoe = Shoe(deckCount = 8)

    private val _uiState = MutableStateFlow(BlackjackUiState())
    val uiState: StateFlow<BlackjackUiState> = _uiState.asStateFlow()

    init {
        startNewRound()
    }

    fun startNewRound() {
        if (shoe.needsReshuffleAfterHand) {
            shoe.resetAndShuffle()
        }

        val player = listOf(shoe.draw(), shoe.draw())
        val dealer = listOf(shoe.draw(), shoe.draw())
        val playerScore = calculateHandScore(player)
        val dealerVisibleScore = dealer.first().rank.baseValue

        val naturalBlackjack = playerScore == 21
        _uiState.value = BlackjackUiState(
            playerHand = player,
            dealerHand = dealer,
            hideDealerHoleCard = !naturalBlackjack,
            playerScore = playerScore,
            dealerScore = if (naturalBlackjack) calculateHandScore(dealer) else dealerVisibleScore,
            isRoundOver = naturalBlackjack,
            canDoubleDown = !naturalBlackjack,
            statusText = if (naturalBlackjack) finishRound(player, dealer) else "Your move"
        )
    }

    fun onHit() {
        val current = _uiState.value
        if (current.isRoundOver) return

        val updatedPlayer = current.playerHand + shoe.draw()
        val playerScore = calculateHandScore(updatedPlayer)

        if (playerScore > 21) {
            _uiState.value = current.copy(
                playerHand = updatedPlayer,
                hideDealerHoleCard = false,
                playerScore = playerScore,
                dealerScore = calculateHandScore(current.dealerHand),
                isRoundOver = true,
                canDoubleDown = false,
                statusText = "Bust! Dealer wins."
            )
        } else {
            _uiState.value = current.copy(
                playerHand = updatedPlayer,
                playerScore = playerScore,
                canDoubleDown = false
            )
        }
    }

    fun onDoubleDown() {
        val current = _uiState.value
        if (current.isRoundOver || !current.canDoubleDown) return

        val updatedPlayer = current.playerHand + shoe.draw()
        val playerScore = calculateHandScore(updatedPlayer)

        if (playerScore > 21) {
            _uiState.value = current.copy(
                playerHand = updatedPlayer,
                hideDealerHoleCard = false,
                playerScore = playerScore,
                dealerScore = calculateHandScore(current.dealerHand),
                isRoundOver = true,
                canDoubleDown = false,
                statusText = "Doubled & busted! Dealer wins."
            )
        } else {
            playDealerTurn(updatedPlayer, doubled = true)
        }
    }

    fun onStand() {
        val current = _uiState.value
        if (current.isRoundOver) return
        playDealerTurn(current.playerHand, doubled = false)
    }

    private fun playDealerTurn(playerHand: List<Card>, doubled: Boolean) {
        val dealer = _uiState.value.dealerHand.toMutableList()
        while (calculateHandScore(dealer) < 17) {
            dealer.add(shoe.draw())
        }

        val playerScore = calculateHandScore(playerHand)
        val dealerScore = calculateHandScore(dealer)
        val prefix = if (doubled) "Double Down: " else ""

        _uiState.update {
            it.copy(
                playerHand = playerHand,
                dealerHand = dealer,
                hideDealerHoleCard = false,
                playerScore = playerScore,
                dealerScore = dealerScore,
                isRoundOver = true,
                canDoubleDown = false,
                statusText = prefix + finishRound(playerHand, dealer)
            )
        }
    }

    private fun finishRound(player: List<Card>, dealer: List<Card>): String {
        val pScore = calculateHandScore(player)
        val dScore = calculateHandScore(dealer)
        return when {
            pScore > 21 -> "Bust! Dealer wins."
            dScore > 21 -> "Dealer busts! You win!"
            pScore > dScore -> "You win! ($pScore vs $dScore)"
            dScore > pScore -> "Dealer wins ($dScore vs $pScore)"
            else -> "Push (tie) at $pScore"
        }
    }
}