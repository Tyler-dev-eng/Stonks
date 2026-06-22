package com.tylerdev.stonks.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.tylerdev.stonks.data.local.entity.CompanyListingEntity

/**
 * Room data access object for cached company listings.
 *
 * Defines read and write operations against [CompanyListingEntity] rows. Repositories call these
 * methods to persist remote listing data locally and to query listings by name or symbol.
 */
@Dao
interface StockDao {

    /**
     * Inserts or replaces company listing rows in the local database.
     *
     * Existing rows with the same primary key are overwritten when a conflict occurs.
     *
     * @param companyListingEntities Listing records to persist.
     */
    @Insert(onConflict = REPLACE)
    suspend fun insertCompanyListings(
        companyListingEntities: List<CompanyListingEntity>
    )

    /**
     * Removes all cached company listing rows.
     */
    @Query("DELETE FROM companylistingentity")
    suspend fun clearCompanyListings()

    /**
     * Searches cached listings by company name or exact ticker symbol.
     *
     * Name matches are case-insensitive partial matches; symbol matches require the uppercased
     * query to equal [CompanyListingEntity.symbol] exactly.
     *
     * @param query User-supplied search term.
     * @return Matching listing entities, or an empty list when nothing matches.
     */
    @Query(
        """
            SELECT *
            FROM companylistingentity
            WHERE LOWER(name) LIKE '%' || LOWER(:query) || '%' OR
                UPPER(:query) == symbol
        """
    )
    suspend fun searchCompanyListing(query: String): List<CompanyListingEntity>
}