package com.sznurczak.jf_inventur.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.sznurczak.jf_inventur.data.database.Shoes

@Dao
interface appDao {
    @Insert
    fun insertAll(vararg shoes: Shoes)

    @Delete
    fun delete(shoes: Shoes)

    @Query("SELECT * FROM shoes")
    fun getAll(): List<Shoes>
}