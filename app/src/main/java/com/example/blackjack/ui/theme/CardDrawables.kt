package com.example.blackjack.ui.theme

import android.content.Context
import androidx.annotation.DrawableRes
import com.example.blackjack.model.Card
import com.example.blackjack.R
import com.example.blackjack.model.Rank
import com.example.blackjack.model.Suit

@DrawableRes
fun Card.toDrawableRes(context: Context): Int {
    val suitName = suit.name.lowercase()
    val rankName = when (rank) {
        Rank.TWO -> "2"
        Rank.THREE -> "3"
        Rank.FOUR -> "4"
        Rank.FIVE -> "5"
        Rank.SIX -> "6"
        Rank.SEVEN -> "7"
        Rank.EIGHT -> "8"
        Rank.NINE -> "9"
        Rank.TEN -> "10"
        Rank.JACK -> "j"
        Rank.QUEEN -> "q"
        Rank.KING -> "k"
        Rank.ACE -> "a"
    }

    val resName = "${suitName}_$rankName"
    val resId = context.resources.getIdentifier(resName, "drawable", context.packageName)
    return if (resId != 0) resId else R.drawable.back_light
}

fun calculateHandScore(hand: List<Card>): Int {
    var total = hand.sumOf { it.rank.baseValue }
    var acesAsEleven = hand.count { it.rank == Rank.ACE }

    while (total > 21 && acesAsEleven > 0) {
        total -= 10
        acesAsEleven -= 1
    }
    return total
}