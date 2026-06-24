package com.tylerdev.stonks.presentation.company_info

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.lineModel
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.tylerdev.stonks.domain.model.IntradayInfoDomainModel

@Composable
fun StockChart(
    infos: List<IntradayInfoDomainModel>,
    modifier: Modifier = Modifier,
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(infos) {
        if (infos.isNotEmpty()) {
            modelProducer.runTransaction {
                lineModel { series(infos.map { it.close }) }
            }
        }
    }

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(),
            startAxis = VerticalAxis.rememberStart(),
            bottomAxis = HorizontalAxis.rememberBottom(),
        ),
        modelProducer = modelProducer,
        modifier = modifier,
    )
}
