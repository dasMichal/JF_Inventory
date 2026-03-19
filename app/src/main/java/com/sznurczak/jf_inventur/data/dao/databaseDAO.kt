package com.sznurczak.jf_inventur.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.sznurczak.jf_inventur.data.database.MemberKind
import com.sznurczak.jf_inventur.data.database.MemberWithShoes
import com.sznurczak.jf_inventur.data.database.Shoes
import kotlinx.coroutines.flow.Flow

@Dao
interface InventurDao {
    // Shoes
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShoes(vararg shoes: Shoes)

    @Delete
    suspend fun deleteShoe(shoe: Shoes)

    @Query("SELECT * FROM shoes")
    fun getAllShoes(): Flow<List<Shoes>>

    @Query("SELECT * FROM shoes WHERE memberId = :memberId")
    fun getShoesForMember(memberId: Int): Flow<List<Shoes>>

    // Members
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: MemberKind): Long

    @Delete
    suspend fun deleteMember(member: MemberKind)

    @Query("SELECT * FROM members")
    fun getAllMembers(): Flow<List<MemberKind>>
    
    @Query("SELECT * FROM members WHERE uid = :id")
    suspend fun getMemberById(id: Int): MemberKind?

    @Transaction
    @Query("SELECT * FROM members")
    fun getMembersWithShoes(): Flow<List<MemberWithShoes>>

    //TODO: Add Join Query
}


