package com.example.vocabtrainer.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.vocabtrainer.ui.home.HomeScreen
import com.example.vocabtrainer.ui.review.ReviewScreen

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    modifier: Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Home.route,
        modifier = modifier
    ) {
        composable(route = Home.route) {
            HomeScreen(
                onClickText = {
                    navController.navigate(Review.route)
                }
            )
        }

        composable(route = Review.route) {
            ReviewScreen()
        }
    }
}