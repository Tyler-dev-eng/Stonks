package com.tylerdev.stonks.presentation.company_info

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun CompanyInfoScreen(
    symbol: String,
    viewModel: CompanyInfoViewModel = hiltViewModel(),
) {
    val state = viewModel.state

    when {
        state.isLoading -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }

        state.error != null -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = state.error,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
        }

        else -> Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            state.company?.let { company ->
                Text(text = company.symbol)
            }
        }
    }
}