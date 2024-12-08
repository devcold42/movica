package `in`.devcold.movica.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import `in`.devcold.movica.data.local.dao.MovieDao
import `in`.devcold.movica.data.local.entity.MovieEntity

@Database(entities = [MovieEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao

    companion object {
        fun build(context: Context): AppDatabase {
            return Room
                .databaseBuilder(context.applicationContext, AppDatabase::class.java, "main.db")
                .build()
        }
    }
}
