package com.tylerdev.stonks.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.outlined.Article
import androidx.compose.material.icons.outlined.ShowChart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tylerdev.stonks.presentation.navigation.CompanyInfo
import com.tylerdev.stonks.presentation.navigation.CompanyListings
import com.tylerdev.stonks.presentation.navigation.MarketNews
import com.tylerdev.stonks.presentation.navigation.NavGraph
import com.tylerdev.stonks.presentation.ui.theme.StonksTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StonksTheme {
                val navController = rememberNavController()
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = backStackEntry?.destination
                val isDetailScreen = currentDestination?.hasRoute<CompanyInfo>() == true

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (!isDetailScreen) {
                            NavigationBar {
                                val onMarkets = currentDestination?.hasRoute<CompanyListings>() == true
                                val onNews = currentDestination?.hasRoute<MarketNews>() == true

                                NavigationBarItem(
                                    selected = onMarkets,
                                    onClick = {
                                        // popBackStack resumes the existing CompanyListings entry
                                        // (STARTED→RESUMED). Using navigate() here would create a
                                        // new entry stuck at CREATED, breaking collectAsStateWithLifecycle.
                                        if (!onMarkets) {
                                            navController.popBackStack<CompanyListings>(inclusive = false)
                                        }
                                    },
                                    icon = {
                                        Icon(
                                            if (onMarkets) Icons.Filled.ShowChart else Icons.Outlined.ShowChart,
                                            contentDescription = "Markets"
                                        )
                                    },
                                    label = { Text("Markets") }
                                )
                                NavigationBarItem(
                                    selected = onNews,
                                    onClick = {
                                        navController.navigate(MarketNews) {
                                            popUpTo(CompanyListings) { inclusive = false }
                                            launchSingleTop = true
                                        }
                                    },
                                    icon = {
                                        Icon(
                                            if (onNews) Icons.Filled.Article else Icons.Outlined.Article,
                                            contentDescription = "News"
                                        )
                                    },
                                    label = { Text("News") }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavGraph(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}
