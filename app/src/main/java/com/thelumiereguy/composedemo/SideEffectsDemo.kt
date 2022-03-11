package com.thelumiereguy.composedemo

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

@Composable
fun SideEffectsDemo(
    state: String,
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {

    // If the UI state contains an error, show snackbar
    if (state == "error") {

        // `LaunchedEffect` will cancel and re-launch if
        // `scaffoldState.snackbarHostState` changes
        LaunchedEffect(key1 = scaffoldState.snackbarHostState) {
            // Show snackbar using a coroutine, when the coroutine is cancelled the
            // snackbar will automatically dismiss. This coroutine will cancel whenever
            // `state.hasError` is false, and only start when `state.hasError` is true
            // (due to the above if-check), or if `scaffoldState.snackbarHostState` changes.
            scaffoldState.snackbarHostState.showSnackbar(
                message = "Error message",
                actionLabel = "Retry message"
            )
        }
    }


    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current

    val lifecycleOwner = LocalLifecycleOwner.current

    /**
    Hit on every recomposition. Can be used to call non-compose code
     **/
    SideEffect {
        trackEvent("Screen refreshed")
    }


    val currentLocation = remember {
        mutableStateOf("")
    }

    DisposableEffect(key1 = true) {

        currentLocation.value = ""

        onDispose {

        }
    }

    // SnapshotFlow - convert Compose's State into Flows
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .map { index -> index > 0 }
            .distinctUntilChanged()
            .filter { it }
            .collect {
//                MyAnalyticsService.sendScrolledPastFirstItemEvent()
            }
    }
}

///**
// * produceState - convert non-Compose state into Compose state
// */
//@Composable
//fun loadNetworkImage(
//    url: String,
//    imageRepository: ImageRepository
//): State<Result<Image>> {
//
//    // Creates a State<T> with Result.Loading as initial value
//    // If either `url` or `imageRepository` changes, the running producer
//    // will cancel and will be re-launched with the new inputs.
//    return produceState<Result<Image>>(initialValue = Result.Loading, url, imageRepository) {
//
//        // In a coroutine, can make suspend calls
//        val image = imageRepository.load(url)
//
//        // Update State with either an Error or Success result.
//        // This will trigger a recomposition where this State is read
//        value = if (image == null) {
//            Result.Error
//        } else {
//            Result.Success(image)
//        }
//    }
//}


private fun trackEvent(event: String) {
    // analytics
}