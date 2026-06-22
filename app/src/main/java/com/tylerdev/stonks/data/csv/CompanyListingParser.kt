package com.tylerdev.stonks.data.csv

import com.opencsv.CSVReader
import com.tylerdev.stonks.domain.model.CompanyListingDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [CSVParser] implementation for Alpha Vantage company listing CSV exports.
 *
 * Parses the `LISTING_STATUS` response format into [CompanyListingDomainModel] rows. Expects
 * columns in order: symbol, name, exchange. The header row is skipped; rows with missing
 * required fields are omitted.
 */
@Singleton
class CompanyListingParser @Inject constructor() : CSVParser<CompanyListingDomainModel> {

    /**
     * Reads listing CSV from the stream and maps each data row to a domain model.
     *
     * Parsing runs on [Dispatchers.IO]. The first row is treated as a header and discarded.
     *
     * @param stream Raw CSV from the listing-status endpoint.
     * @return Parsed listings; incomplete rows are filtered out.
     */
    override suspend fun parser(stream: InputStream): List<CompanyListingDomainModel> {
        val csvReader = CSVReader(InputStreamReader(stream))
        return withContext(Dispatchers.IO) {
            csvReader
                .readAll()
                .drop(1)
                .mapNotNull { line ->
                    val symbol = line.getOrNull(0)
                    val name = line.getOrNull(1)
                    val exchange = line.getOrNull(2)
                    CompanyListingDomainModel(
                        name = name ?: return@mapNotNull null,
                        symbol = symbol ?: return@mapNotNull null,
                        exchange = exchange ?: return@mapNotNull null
                    )
                }
                .also {
                    csvReader.close()
                }
        }
    }
}