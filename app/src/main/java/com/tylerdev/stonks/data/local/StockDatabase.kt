package com.tylerdev.stonks.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tylerdev.stonks.data.local.dao.StockDao
import com.tylerdev.stonks.data.local.entity.CompanyListingEntity

/**
 * Room database for locally cached stock listing data.
 *
 * Registers [CompanyListingEntity] tables and exposes [StockDao] for read and write access.
 * A Hilt module typically provides a singleton instance of this database for repositories.
 */
@Database(
    entities = [CompanyListingEntity::class],
    version = 1
)
abstract class StockDatabase : RoomDatabase() {

    /** Data access object for company listing persistence and search. */
    abstract val dao: StockDao
}