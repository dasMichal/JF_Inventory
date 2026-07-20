package com.sznurczak.jf_inventur.data

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sznurczak.jf_inventur.data.dao.InventurDao
import com.sznurczak.jf_inventur.data.database.Badge
import com.sznurczak.jf_inventur.data.database.Converters
import com.sznurczak.jf_inventur.data.database.MemberBadge
import com.sznurczak.jf_inventur.data.database.MemberKind
import com.sznurczak.jf_inventur.data.database.Shoes


@Database(
    version = 2,
    entities = [
        Shoes::class,
        MemberKind::class,
        Badge::class,
        MemberBadge::class],
    )
@TypeConverters(Converters::class)
abstract class databaseHolder: RoomDatabase()
{
    abstract fun appDao(): InventurDao

    companion object {
        @Volatile
        private var INSTANCE: databaseHolder? = null

        fun getDatabase(context: Context): databaseHolder {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    databaseHolder::class.java,
                    "jf_inventur_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}