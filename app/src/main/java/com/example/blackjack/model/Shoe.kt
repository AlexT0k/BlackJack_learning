package com.example.blackjack.model

import kotlin.random.Random

class Shoe(
    private val deckCount: Int = 8,
    private val penetrationRatio: Double = 0.75,
    private val random: Random = Random.Default
) {
    private val cards = ArrayDeque<Card>(deckCount * 52)
    private var initialSize: Int = 0
    var needsReshuffleAfterHand: Boolean = false
        private set

    init {
        resetAndShuffle()
    }

    fun resetAndShuffle() {
        val allCards = buildList(deckCount * 52) {
            repeat(deckCount) {
                for (suit in Suit.entries) {
                    for (rank in Rank.entries) {
                        add(Card(suit = suit, rank = rank))
                    }
                }
            }
        }.toMutableList()
        allCards.shuffle(random)

        cards.clear()
        cards.addAll(allCards)
        initialSize = cards.size
        needsReshuffleAfterHand = false
    }

    fun draw(): Card {
        if (cards.isEmpty()) {
            resetAndShuffle()
        }
        val drawn = cards.removeFirst()
        val dealtCount = initialSize - cards.size
        if (dealtCount >= (initialSize * penetrationRatio).toInt()) {
            needsReshuffleAfterHand = true
        }
        return drawn
    }

    val remainingCount: Int get() = cards.size

    fun remainingByCard(): Map<Card, Int> =
        cards.groupingBy { it }.eachCount()
}