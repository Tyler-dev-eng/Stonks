package com.tylerdev.stonks.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.tylerdev.stonks.presentation.company_listings.CompanyListingsScreen

@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = CompanyListings,
        modifier = modifier,
    ) {
        composable<CompanyListings> {
            CompanyListingsScreen()
        }
        composable<CompanyDetail> { backStackEntry ->
            val route: CompanyDetail = backStackEntry.toRoute()
            // TODO: CompanyDetailScreen(symbol = route.symbol)
        }
    }
}
