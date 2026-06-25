package com.tylerdev.stonks.presentation.company_info

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.tylerdev.stonks.domain.model.StockQuoteDomainModel
import com.tylerdev.stonks.presentation.ui.PriceChangeBadge

@Composable
fun CompanyInfoScreen(
    symbol: String,
    viewModel: CompanyInfoViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when {
        state.isLoading -> CompanyInfoSkeleton()

        state.error != null -> CompanyInfoErrorState(symbol = symbol)

        else -> CompanyInfoContent(state)
    }
}

@Composable
private fun CompanyInfoContent(state: CompanyInfoState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        state.company?.let { company ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (company.logoUrl.isNotBlank()) {
                    AsyncImage(
                        model = company.logoUrl,
                        contentDescription = "${company.name} logo",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                    )
                }
                Column {
                    Text(
                        text = company.symbol,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = company.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (company.country.isNotBlank()) {
                    Text(text = company.country, style = MaterialTheme.typography.bodySmall)
                }
                if (company.industry.isNotBlank()) {
                    Text(text = "·", style = MaterialTheme.typography.bodySmall)
                    Text(text = company.industry, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        if (state.isQuoteLoading) {
            QuoteSkeleton(brush = shimmerBrush())
        } else {
            state.quote?.let { quote ->
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "$${String.format("%.2f", quote.current)}",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold
                )
                PriceChangeBadge(
                    current = quote.current,
                    previousClose = quote.previousClose
                )
                Spacer(modifier = Modifier.height(16.dp))
                QuoteStatsRow(quote = quote)
            }
        }
    }
}

@Composable
private fun CompanyInfoSkeleton() {
    val brush = shimmerBrush()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(brush)
            )
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(22.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(brush)
                )
                Box(
                    modifier = Modifier
                        .width(150.dp)
                        .height(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(brush)
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .width(44.dp)
                    .height(12.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(brush)
            )
            Box(
                modifier = Modifier
                    .width(88.dp)
                    .height(12.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(brush)
            )
        }
        QuoteSkeleton(brush)
    }
}

@Composable
private fun QuoteSkeleton(brush: Brush) {
    Spacer(Modifier.height(24.dp))
    HorizontalDivider()
    Spacer(Modifier.height(16.dp))
    Box(
        modifier = Modifier
            .width(130.dp)
            .height(44.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(brush)
    )
    Spacer(Modifier.height(16.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        repeat(4) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(10.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(brush)
                )
                Box(
                    modifier = Modifier
                        .width(52.dp)
                        .height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(brush)
                )
            }
        }
    }
}

@Composable
private fun shimmerBrush(): Brush {
    val isDark = isSystemInDarkTheme()
    val shimmerColors = if (isDark) {
        listOf(Color(0xFF2A302B), Color(0xFF404942), Color(0xFF2A302B))
    } else {
        listOf(Color(0xFFDDE5DE), Color(0xFFF2F7F2), Color(0xFFDDE5DE))
    }
    val transition = rememberInfiniteTransition(label = "shimmer")
    val offset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_offset"
    )
    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(offset - 600f, 0f),
        end = Offset(offset, 0f)
    )
}

@Composable
private fun QuoteStatsRow(quote: StockQuoteDomainModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        QuoteStat(label = "Open", value = quote.open)
        QuoteStat(label = "High", value = quote.high)
        QuoteStat(label = "Low", value = quote.low)
        QuoteStat(label = "Prev Close", value = quote.previousClose)
    }
}

@Composable
private fun QuoteStat(label: String, value: Double) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "$${String.format("%.2f", value)}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}
