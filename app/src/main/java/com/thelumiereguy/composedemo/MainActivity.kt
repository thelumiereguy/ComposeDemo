package com.thelumiereguy.composedemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thelumiereguy.composedemo.ui.theme.ComposeDemoTheme
import kotlinx.coroutines.flow.flowOf

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ComposeDemoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ListComposable()
                }
            }
        }
    }
}

@OptIn(
    ExperimentalGraphicsApi::class, ExperimentalAnimationApi::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun ListComposable() {

    val names = remember {
        listOf(
            "Led Zeppelin",
            "Black Sabbath",
            "AC/DC",
            "Metallica",
            "Megadeth"
        )
    }

    // saved across recompositions
    var currentName by remember {
        mutableStateOf(names[0])
    }

    // saved across configuration changes
    val currentNameSaved by rememberSaveable(
        key = "currentName"
    ) {
        mutableStateOf(names[0])
    }

    val density = LocalDensity.current

    val configuration = LocalConfiguration.current

    var orientationState by remember {
        mutableStateOf(configuration.orientation)
    }

    val numbersFlow = remember {
        flowOf(1, 2, 3, 4)
    }

    LazyColumn {
        // Header
        item {
            Box(modifier = Modifier) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth(),
                ) {
                    Text(
                        text = "Top Item",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        items(names) { name ->

            Spacer(
                modifier = Modifier.height(30.dp)
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        translationX = if (currentName == name) {
                            15.5f
                        } else {
                            0f
                        }
                    }
                    .clickable {
                        currentName = name
                    },
                color = if (currentName == name) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.tertiary
                }
            ) {

                Text(
                    text = name,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }


        item {
            // https://developer.android.com/jetpack/compose/animation#animatedvisibility
            AnimatedVisibility(
                visible = currentName == "Metallica",
                enter = slideInVertically {
                    // Slide in from 40 dp from the top.
                    with(density) { -40.dp.roundToPx() }
                } + expandVertically(
                    // Expand from the top.
                    expandFrom = Alignment.Top
                ) + fadeIn(
                    // Fade in with the initial alpha of 0.3f.
                    initialAlpha = 0.3f
                ),
                exit = slideOutVertically() + shrinkVertically() + fadeOut()

            ) {
                Text(
                    text = "You've selected the right one",
                    modifier = Modifier.padding(8.dp)
                )
            }
        }


        item {

            AnimatedVisibility(
                visible = currentName == "AC/DC",
                enter = fadeIn(),
                exit = fadeOut()
            ) { // this: AnimatedVisibilityScope
                // Use AnimatedVisibilityScope#transition to add a custom animation
                // to the AnimatedVisibility.
                val background by transition.animateColor(label = "color Animation") { state ->
                    if (state == EnterExitState.Visible)
                        Color.Blue
                    else Color.Gray
                }
                Box(
                    modifier = Modifier
                        .size(
                            128.dp
                        )
                        .background(background)
                )
            }
        }
    }
}


@Preview(
    name = "Default",
    showBackground = true
)
@Preview(
    name = "Dark mode",
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    name = "Automotive device",
    showSystemUi = true,
    device = Devices.AUTOMOTIVE_1024p
)
@Composable
fun DefaultPreview() {
    ComposeDemoTheme {
        ListComposable()
    }
}