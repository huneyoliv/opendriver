package br.com.opendriver.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [TripOfferEntity::class, LocationPointEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tripOfferDao(): TripOfferDao
    abstract fun locationPointDao(): LocationPointDao
}
