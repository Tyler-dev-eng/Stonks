package com.tylerdev.stonks.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.tylerdev.stonks.data.local.dao.StockDao
import com.tylerdev.stonks.data.local.entity.CompanyListingEntity

@Database(
    entities = [CompanyListingEntity::class],
    version = 2
)
abstract class StockDatabase : RoomDatabase() {

    abstract val dao: StockDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE companylistingentity ADD COLUMN isFavorite INTEGER NOT NULL DEFAULT 0"
                )
            }
        }
    }
}