package com.example.travelapp.core.repository

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.example.travelapp.core.entity.City
import com.example.travelapp.core.entity.Landmark
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

// Database repository for the application
class DatabaseRepository(context: Context) :
    SQLiteOpenHelper(context, "travel_app_database.sqlite", null, 1) {


    // Database creation queries
    override fun onCreate(db: SQLiteDatabase?) {
        val database = db ?: return
        database.execSQL(
            "create table if not exists Cities\n" +
                    "(\n" +
                    "    City_Id     integer primary key autoincrement,\n" +
                    "    Name        text not null unique,\n" +
                    "    Description text not null\n" +
                    ");"
        )
        database.execSQL(
            "create table if not exists Landmarks\n" +
                    "(\n" +
                    "    Landmark_Id integer primary key autoincrement,\n" +
                    "    Name        text    not null,\n" +
                    "    Description text    not null,\n" +
                    "    City_Id     integer not null,\n" +
                    "    foreign key (City_Id) references Cities (City_Id)\n" +
                    ")"
        )
        println("Created database")
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
//        val database = db ?: return
//        database.execSQL("drop table if exists $landmarksTableName;")
//        database.execSQL("drop table if exists $citiesTableName;")
//        onCreate(database)
    }


    // Insert a new city in the database
    suspend fun cityInsert(city: City): Long {
        lock.withLock {
            val writeableDb = try {
                writableDatabase
            } catch (e: SQLiteException) {
                e.printStackTrace()
                return -1
            }
            val input = ContentValues()
            input.put("Name", city.name)
            input.put("Description", city.description)

            val inserted = writeableDb.insert(
                citiesTableName,
                null,
                input
            )
            writeableDb.close()
            return inserted
        }
    }

    // Deletes a city from the database
    suspend fun cityDelete(cityId: Int): Int {
        val landmarks = landmarksFetch(cityId)
        if (landmarks.isNotEmpty()) return 0
        lock.withLock {
            val writeableDb = try {
                writableDatabase
            } catch (e: SQLiteException) {
                e.printStackTrace()
                return 0
            }
            val whereClause = "City_Id like ?"
            val params = arrayOf(cityId.toString())
            val deleted = writeableDb.delete(citiesTableName, whereClause, params)
            writeableDb.close()
            return deleted
        }
    }

    // Fetches all cities from the database
    suspend fun citiesFetch(): List<City> {
        lock.withLock {
            val readableDb = try {
                readableDatabase
            } catch (e: SQLiteException) {
                e.printStackTrace()
                return listOf()
            }

            val cursor =
                readableDb.rawQuery("Select * from $citiesTableName", null)

            val output = ArrayList<City>(cursor.count)

            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(0)
                    val name = cursor.getString(1)
                    val description = cursor.getString(2)
                    val city = City(id, name, description)
                    output.add(city)
                } while (cursor.moveToNext())
            }

            cursor.close()
            readableDb.close()
            return output
        }
    }


    // Fetches all landmarks from the database associated with a certain city
    suspend fun landmarksFetch(cityId: Int): List<Landmark> {
        lock.withLock {
            val readableDb = try {
                readableDatabase
            } catch (e: SQLiteException) {
                e.printStackTrace()
                return listOf()
            }

            val params = arrayOf(cityId.toString())
            val cursor =
                readableDb.rawQuery(
                    "Select * from $landmarksTableName where City_Id like ?",
                    params
                )

            val output = ArrayList<Landmark>(cursor.count)

            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(0)
                    val name = cursor.getString(1)
                    val description = cursor.getString(2)
                    val landmark = Landmark(id, name, description, cityId)
                    output.add(landmark)
                } while (cursor.moveToNext())
            }

            cursor.close()
            readableDb.close()
            return output
        }
    }

    // Inserts a new landmark in the database
    suspend fun landMarkInsert(landmark: Landmark): Long {
        lock.withLock {
            val writeableDb = try {
                writableDatabase
            } catch (e: SQLiteException) {
                e.printStackTrace()
                return -1
            }
            val input = ContentValues()
            input.put("Name", landmark.name)
            input.put("Description", landmark.description)
            input.put("City_Id", landmark.cityId)

            val inserted = writeableDb.insert(
                landmarksTableName,
                null,
                input
            )
            writeableDb.close()
            return inserted
        }
    }

    // Deletes a landmark from the database
    suspend fun landmarkDelete(landmarkId: Int): Int {
        lock.withLock {
            val writeableDb = try {
                writableDatabase
            } catch (e: SQLiteException) {
                e.printStackTrace()
                return 0
            }
            val whereClause = "Landmark_Id like ?"
            val params = arrayOf(landmarkId.toString())
            val deleted = writeableDb.delete(landmarksTableName, whereClause, params)
            writeableDb.close()
            return deleted
        }
    }

    // Static variables
    companion object {
        
        // Table names
        private const val citiesTableName = "Cities"
        private const val landmarksTableName = "Landmarks"

        // Static mutex ensuring the thread-safe access of the repository at any time
        private val lock = Mutex()
    }
}