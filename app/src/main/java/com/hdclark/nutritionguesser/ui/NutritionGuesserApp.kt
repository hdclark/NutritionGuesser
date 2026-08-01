package com.hdclark.nutritionguesser.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hdclark.nutritionguesser.data.Food
import com.hdclark.nutritionguesser.game.AnswerFeedback
import com.hdclark.nutritionguesser.game.FoodSide
import com.hdclark.nutritionguesser.game.GameState
import com.hdclark.nutritionguesser.game.GameStatus
import com.hdclark.nutritionguesser.game.NutritionGameViewModel
import com.hdclark.nutritionguesser.game.QuizQuestion
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun NutritionGuesserApp(viewModel: NutritionGameViewModel = viewModel()) {
    val state = viewModel.state
    val feedback = viewModel.feedback

    LaunchedEffect(feedback) {
        if (feedback != null) {
            delay(1_050)
            viewModel.clearFeedback()
        }
    }

    Box(Modifier.fillMaxSize()) {
        FloatingBackground()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 18.dp, vertical = 12.dp),
        ) {
            ScoreHeader(state)
            Spacer(Modifier.height(12.dp))
            AnimatedContent(
                targetState = state.status,
                transitionSpec = {
                    (fadeIn(tween(250)) + scaleIn(initialScale = 0.97f)) togetherWith
                        (fadeOut(tween(170)) + scaleOut(targetScale = 1.03f))
                },
                label = "game-screen",
                modifier = Modifier.weight(1f),
            ) { status ->
                when (status) {
                    GameStatus.PLAYING -> QuizScreen(state, viewModel::answer)
                    GameStatus.ROUND_COMPLETE -> RoundCompleteScreen(state, viewModel::nextRound)
                    GameStatus.GAME_OVER -> GameOverScreen(state, viewModel::restart)
                }
            }
        }
        FeedbackBanner(
            feedback = feedback,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(18.dp),
        )
    }
}

@Composable
private fun ScoreHeader(state: GameState) {
    Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text("Nutrition Guesser", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                Text(
                    "Level ${state.level} · comparisons per 100 g",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                )
            }
            Text(
                buildString {
                    repeat(GameState.MAX_INCORRECT_GUESSES) { index ->
                        append(if (index < state.mistakesRemaining) "❤️" else "🩶")
                    }
                },
                fontSize = 18.sp,
                modifier = Modifier.semantics {
                    contentDescription = "${state.mistakesRemaining} mistakes remaining"
                },
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatPill("✅ ${state.correctGuesses} correct")
            StatPill("❌ ${state.incorrectGuesses} incorrect")
        }
    }
}

@Composable
private fun StatPill(text: String) {
    Surface(shape = CircleShape, tonalElevation = 2.dp) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun QuizScreen(state: GameState, onAnswer: (FoodSide) -> Unit) {
    val question = state.currentQuestion ?: return
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        LinearProgressIndicator(
            progress = { state.solvedInRound / GameState.QUESTIONS_PER_ROUND.toFloat() },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "${state.solvedInRound + 1} of ${GameState.QUESTIONS_PER_ROUND}" +
                if (question.attempt > 1) " · retry ${question.attempt - 1}" else "",
            style = MaterialTheme.typography.labelLarge,
        )
        AnimatedContent(
            targetState = question,
            transitionSpec = {
                (slideInVertically(tween(260)) { it / 5 } + fadeIn()) togetherWith
                    (slideOutVertically(tween(170)) { -it / 5 } + fadeOut())
            },
            contentKey = QuizQuestion::id,
            label = "question",
            modifier = Modifier.fillMaxSize(),
        ) { shown ->
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    shown.prompt,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp),
                )
                Text(
                    "Tap your best guess",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.64f),
                )
                Spacer(Modifier.height(12.dp))
                FoodChoiceCard(shown.leftFood, Modifier.weight(1f)) { onAnswer(FoodSide.LEFT) }
                Spacer(Modifier.height(12.dp))
                FoodChoiceCard(shown.rightFood, Modifier.weight(1f)) { onAnswer(FoodSide.RIGHT) }
            }
        }
    }
}

@Composable
private fun FoodChoiceCard(food: Food, modifier: Modifier, onClick: () -> Unit) {
    val interactions = remember { MutableInteractionSource() }
    val pressed by interactions.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.96f else 1f, tween(100), label = "press")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .semantics {
                role = Role.Button
                contentDescription = "Choose ${food.name}"
            }
            .clickable(interactionSource = interactions, indication = null, onClick = onClick),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(defaultElevation = if (pressed) 2.dp else 8.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(food.emoji, fontSize = 62.sp)
            Spacer(Modifier.height(5.dp))
            Text(food.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
            Text(
                food.category.name.replace('_', ' ').lowercase().replaceFirstChar { it.titlecase() },
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
            )
        }
    }
}

@Composable
private fun FeedbackBanner(feedback: AnswerFeedback?, modifier: Modifier) {
    AnimatedVisibility(
        visible = feedback != null,
        modifier = modifier,
        enter = slideInVertically { it } + fadeIn() + scaleIn(initialScale = 0.92f),
        exit = slideOutVertically { it } + fadeOut() + scaleOut(targetScale = 0.94f),
    ) {
        Surface(
            color = if (feedback?.wasCorrect == true) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error,
            contentColor = Color.White,
            shape = RoundedCornerShape(22.dp),
            shadowElevation = 10.dp,
        ) {
            Text(
                feedback?.message.orEmpty(),
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun RoundCompleteScreen(state: GameState, onNext: () -> Unit) {
    Box(Modifier.fillMaxSize()) {
        Confetti()
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("🎉", fontSize = 82.sp)
            Text("Level ${state.level} cleared!", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
            Spacer(Modifier.height(10.dp))
            Text("Next level brings closer, trickier food matchups.", textAlign = TextAlign.Center)
            Spacer(Modifier.height(24.dp))
            Button(onClick = onNext, contentPadding = PaddingValues(horizontal = 28.dp, vertical = 14.dp)) {
                Text("Start level ${state.level + 1}", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun GameOverScreen(state: GameState, onRestart: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("🥑💥", fontSize = 72.sp)
        Text("Kitchen closed!", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(10.dp))
        Text(
            "Five misses ends the run. You reached level ${state.level} with ${state.correctGuesses} correct guesses.",
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(24.dp))
        Button(onClick = onRestart) { Text("Restart at level 1", fontWeight = FontWeight.Bold) }
    }
}

private data class Bubble(val x: Float, val y: Float, val radius: Float, val phase: Float)

@Composable
private fun FloatingBackground() {
    val transition = rememberInfiniteTransition(label = "background")
    val time by transition.animateFloat(
        0f,
        1f,
        infiniteRepeatable(tween(8_000), RepeatMode.Restart),
        label = "background-time",
    )
    val random = remember { Random(17) }
    val bubbles = remember { List(18) { Bubble(random.nextFloat(), random.nextFloat(), random.nextFloat() * 24f + 18f, random.nextFloat() * 6.28f) } }
    val bubbleColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.06f)
    Canvas(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        bubbles.forEach { bubble ->
            drawCircle(
                bubbleColor,
                bubble.radius,
                Offset(
                    bubble.x * size.width,
                    (bubble.y * size.height + sin(time * 2f * PI.toFloat() + bubble.phase) * 18f).coerceIn(0f, size.height),
                ),
            )
        }
    }
}

private data class ConfettiPiece(val x: Float, val y: Float, val speed: Float, val size: Float, val color: Int)

@Composable
private fun Confetti() {
    val transition = rememberInfiniteTransition(label = "confetti")
    val progress by transition.animateFloat(0f, 1f, infiniteRepeatable(tween(3_400), RepeatMode.Restart), label = "fall")
    val pieces = remember {
        val random = Random(91)
        List(40) { ConfettiPiece(random.nextFloat(), random.nextFloat(), random.nextFloat() * 0.7f + 0.4f, random.nextFloat() * 8f + 5f, random.nextInt(4)) }
    }
    val colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.tertiary, Color(0xFF3A86FF))
    Canvas(Modifier.fillMaxSize()) {
        pieces.forEach { piece ->
            val y = ((piece.y + progress * piece.speed) % 1f) * size.height
            drawRect(colors[piece.color], Offset(piece.x * size.width, y), Size(piece.size, piece.size * 1.8f))
        }
    }
}
