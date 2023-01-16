package com.toxillo.vocabtrainer.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

//sealed class Screens (
//    val route: String,
//    val title: String,
//    val icon: ImageVector
//) {
//    object Home: Screens(
//        route = "home_screen",
//        title = "Home",
//        icon = Icons.Default.Home
//    )
//    object Review: Screens(
//        route = "review_screen",
//        title = "Review",
//        icon = Icons.Default.Star
//    )
//}


interface Destinations {
    val icon: ImageVector
    val title: String
    val route: String
}

object Home : Destinations {
    override val icon = Icons.Default.Home
    override val title = "Home"
    override val route = "home_screen"
}

object Review : Destinations {
    override val route = "review_screen"
    override val title = "Review"
    override val icon = Icons.Default.Star
}



