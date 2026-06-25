package com.tylerdev.stonks.presentation.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.tylerdev.stonks.presentation.ui.theme.LossRed
import com.tylerdev.stonks.presentation.ui.theme.ProfitGreen

@Composable
fun PriceChangeBadge(
    current: Double,
    previousClose: Double,
    modifier: Modifier = Modifier
) {
    val change = current - previousClose
    val changePct = if (previousClose != 0.0) (change / previousClose) * 100.0 else 0.0
    val isPositive = change >= 0.0
    val sign = if (isPositive) "+" else ""
    val color = if (isPositive) ProfitGreen else LossRed

    Text(
        text = "$sign${"%.2f".format(change)} ($sign${"%.2f".format(changePct)}%)",
        color = color,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Medium,
        modifier = modifier
    )
}
