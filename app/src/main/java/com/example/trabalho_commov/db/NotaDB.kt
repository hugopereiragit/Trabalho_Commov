package com.example.trabalho_commov.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.trabalho_commov.entities.Nota
import com.example.trabalho_commov.dao.NotasDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// Annotates class to be a Room Database with a table (entity) of the Nota class

// Note: When you modify the database schema, you'll need to update the version number and define a migration strategy
//For a sample, a destroy and re-create strategy can be sufficient. But, for a real app, you must implement a migration strategy.

@Database(entities = arrayOf(Nota::class), version = 8, exportSchema = false)
public abstract class NotaDB : RoomDatabase() {

    abstract fun cityDao(): NotasDao

    private class WordDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {
                    var cityDao = database.cityDao()

                    // Delete all content here.
                   // cityDao.deleteAll()

                    // Add sample cities.
                    /*var city = Nota(
                        1,
                        "Viana do Castelo",
                        "Portugal"
                    )
                    cityDao.insert(city)
                    city =
                        Nota(
                            2,
                            "Porto",
                            "Portugal"
                        )
                    cityDao.insert(city)
                    city =
                        Nota(
                            3,
                            "Aveiro",
                            "Portugal"
                        )
                    cityDao.insert(city)
                    */
                }
            }
        }
    }

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: NotaDB? = null

        fun getDatabase(context: Context, scope: CoroutineScope): NotaDB {
            val tempInstance =
                INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NotaDB::class.java,
                    "cities_database"
                )
                //estratégia de destruição
                .fallbackToDestructiveMigration()
                .addCallback(
                    WordDatabaseCallback(
                        scope
                    )
                )
                .build()

                INSTANCE = instance
                return instance
            }
        }
    }
}