package com.example.blackjack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.blackjack.ui.theme.BlackJackTheme
import com.example.blackjack.ui.theme.BlackjackViewModel
import com.example.blackjack.ui.theme.BlackjackApp

class MainActivity : ComponentActivity() {
    private val viewModel: BlackjackViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BlackJackTheme {
                BlackjackApp(viewModel = viewModel)
            }
        }
    }
}