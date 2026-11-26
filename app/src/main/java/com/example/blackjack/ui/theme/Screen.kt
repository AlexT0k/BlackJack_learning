package com.example.blackjack.ui.theme

sealed interface Screen {
    data object MainMenu : Screen
    data object Game : Screen
}