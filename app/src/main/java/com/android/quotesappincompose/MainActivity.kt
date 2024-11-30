package com.android.quotesappincompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.android.quotesappincompose.ui.theme.DarkBackground
import com.android.quotesappincompose.ui.theme.QuotesAppInComposeTheme
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        /*enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT, Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT, Color.TRANSPARENT
            )
        )*/
        super.onCreate(savedInstanceState)
        setContent {
            QuotesAppInComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navHostController = rememberNavController()
                    NavGraph(navHostController)
                }
            }
        }
    }
}

@Serializable
object MAIN_SCREEN

@Serializable
object ADD_QUOTES_SCREEN

@Serializable
data class UPDATE_QUOTES_SCREEN(
    var quote: String,
    var author: String,
    var key: String,
)


@Composable
fun CircleProgressWithOutBackgroundColor() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = Modifier.width(40.dp),
        )
    }
}

@Composable
fun CircleProgressWithBackgroundColor() {
    Column(
        modifier = Modifier.fillMaxSize().background(
            color =
            if (isSystemInDarkTheme())
                Color.Unspecified
            else
                Color.White
        ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = Modifier.width(40.dp),
        )
    }
}