package com.tylerdev.stonks.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.tylerdev.stonks.presentation.company_info.CompanyInfoScreen
import com.tylerdev.stonks.presentation.company_listings.CompanyListingsScreen

/**
 * Root navigation graph for the app.
 *
 * Wires [NavHostController] to type-safe routes defined in this package. [MainActivity] hosts
 * this composable inside a [androidx.compose.material3.Scaffold].
 *
 * @param navController Controller used to navigate between [CompanyListings] and [CompanyInfo].
 * @param modifier Optional layout modifier applied to the [NavHost].
 */
@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = CompanyListings,
        modifier = modifier,
    ) {
        composable<CompanyListings> {
            CompanyListingsScreen(
                onCompanyClick = { symbol -> navController.navigate(CompanyInfo(symbol)) }
            )
        }
        composable<CompanyInfo> {
            val route = it.toRoute<CompanyInfo>()
            CompanyInfoScreen(symbol = route.symbol)
        }
    }
}
