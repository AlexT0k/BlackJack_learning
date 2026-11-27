package com.example.blackjack.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.blackjack.R
import com.example.blackjack.model.Card

@Composable
fun BlackjackApp(viewModel: BlackjackViewModel) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.MainMenu) }

    when (currentScreen) {
        Screen.MainMenu -> MainMenuScreen(
            onPlayClassic = {
                viewModel.startNewRound()
                currentScreen = Screen.Game
            }
        )
        Screen.Game -> {
            val state by viewModel.uiState.collectAsState()
            GameScreen(
                state = state,
                onBackToMenu = { currentScreen = Screen.MainMenu },
                onHit = viewModel::onHit,
                onStand = viewModel::onStand,
                onDoubleDown = viewModel::onDoubleDown,
                onNewRound = viewModel::startNewRound
            )
        }
    }
}

@Composable
fun MainMenuScreen(onPlayClassic: () -> Unit) {
    Column(
        modifier = Modifier
            .background(Color(0xFF06452a))
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("BLACKJACK", color = Color(0xFFe0b422), style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onPlayClassic,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2E7D32),
                contentColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Play Standard 8-Deck")
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2E7D32),
                contentColor = Color.White
            ),
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("More Modes (Coming Soon)")
        }
    }
}

@Composable
fun GameScreen(
    state: BlackjackUiState,
    onBackToMenu: () -> Unit,
    onHit: () -> Unit,
    onStand: () -> Unit,
    onDoubleDown: () -> Unit,
    onNewRound: () -> Unit
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .background(Color(0xFF06452a))
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onBackToMenu,
                    colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E7D32),
                    contentColor = Color.White
                ),) {
                    Text("Menu")
                }
                Text(
                    text = state.statusText,
                    color = Color(0xFFe0b422),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Dealer (${state.dealerScore})",
                    color = Color(0xFFe0b422),
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                HandRow(
                    cards = state.dealerHand,
                    hideSecondCard = state.hideDealerHoleCard
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "You (${state.playerScore})",
                    color = Color(0xFFe0b422),
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                HandRow(
                    cards = state.playerHand,
                    hideSecondCard = false
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (state.isRoundOver) {
                    Button(
                        onClick = onNewRound,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2E7D32),
                            contentColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Deal Next Hand")
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onHit,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2E7D32),
                                contentColor = Color.White
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Hit")
                        }
                        Button(
                            onClick = onStand,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2E7D32),
                                contentColor = Color.White
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Stand")
                        }
                        Button(
                            onClick = onDoubleDown,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2E7D32),
                                contentColor = Color.White
                            ),
                            enabled = state.canDoubleDown,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Double")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HandRow(
    cards: List<Card>,
    hideSecondCard: Boolean
) {
    val context = LocalContext.current
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        cards.forEachIndexed { index, card ->
            val isHiddenHoleCard = hideSecondCard && index == 1
            val resId = remember(card, isHiddenHoleCard) {
                if (isHiddenHoleCard) {
                    R.drawable.back_light
                } else {
                    card.toDrawableRes(context)
                }
            }
            Image(
                painter = painterResource(id = resId),
                contentDescription = if (hideSecondCard && index == 1) {
                    "Face-down card"
                } else {
                    "${card.rank.label} of ${card.suit.name.lowercase()}"
                },
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .width(72.dp)
                    .height(104.dp)
            )
            if (index < cards.lastIndex) {
                Spacer(modifier = Modifier.width(6.dp))
            }
        }
    }
}