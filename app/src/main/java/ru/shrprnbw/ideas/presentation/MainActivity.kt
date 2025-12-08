package ru.shrprnbw.ideas.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import ru.shrprnbw.ideas.presentation.navigation.NavGraph
import ru.shrprnbw.ideas.presentation.ui.theme.SharpIdeasTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SharpIdeasTheme {
                NavGraph()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AudioPlayerPreview() {
    SharpIdeasTheme {

    }
}