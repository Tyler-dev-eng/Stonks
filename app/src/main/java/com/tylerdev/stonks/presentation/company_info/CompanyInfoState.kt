package com.tylerdev.stonks.presentation.company_info

import com.tylerdev.stonks.domain.model.CompanyInfoDomainModel
import com.tylerdev.stonks.domain.model.IntradayInfoDomainModel

/**
 * UI state for the company detail screen.
 *
 * Exposed by [CompanyInfoViewModel] and collected by Compose UI to render company profile data,
 * intraday chart points, loading state, and error messages.
 */
data class CompanyInfoState(
    /** Hourly intraday price observations used to render the stock chart. */
    val stockInfos: List<IntradayInfoDomainModel> = emptyList(),
    /** Company profile and overview data for the selected symbol. */
    val company: CompanyInfoDomainModel? = null,
    /** True while company or intraday data is being fetched. */
    val isLoading: Boolean = false,
    /** Error message to display when a repository call fails; null when there is no error. */
    val error: String? = null
)
