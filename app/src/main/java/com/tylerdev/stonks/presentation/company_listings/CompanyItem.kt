package com.tylerdev.stonks.presentation.company_listings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tylerdev.stonks.domain.model.CompanyListingDomainModel
import com.tylerdev.stonks.domain.model.StockQuoteDomainModel
import com.tylerdev.stonks.presentation.ui.PriceChangeBadge
import com.tylerdev.stonks.presentation.ui.theme.ProfitGreen

@Composable
fun CompanyItem(
    company: CompanyListingDomainModel,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
    quote: StockQuoteDomainModel? = null
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = company.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.size(4.dp))
                Text(
                    text = company.exchange,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "(${company.symbol})",
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (quote != null) {
                    Spacer(Modifier.weight(1f))
                    PriceChangeBadge(
                        current = quote.current,
                        previousClose = quote.previousClose
                    )
                }
            }
        }
        IconButton(onClick = onFavoriteClick) {
            Icon(
                imageVector = if (company.isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = if (company.isFavorite) "Remove from watchlist" else "Add to watchlist",
                tint = if (company.isFavorite) ProfitGreen else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
