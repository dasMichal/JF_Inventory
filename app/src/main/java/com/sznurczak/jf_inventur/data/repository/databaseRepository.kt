package com.sznurczak.jf_inventur.data.repository

import com.sznurczak.jf_inventur.data.dao.InventurDao
import com.sznurczak.jf_inventur.data.database.MemberKind
import com.sznurczak.jf_inventur.data.database.MemberWithShoes
import com.sznurczak.jf_inventur.data.database.Shoes
import kotlinx.coroutines.flow.Flow

class DatabaseRepository(private val inventurDao: InventurDao) {

    // Shoes
    val allShoes: Flow<List<Shoes>> = inventurDao.getAllShoes()

    suspend fun insertShoe(shoe: Shoes) {
        inventurDao.insertShoes(shoe)
    }

    suspend fun deleteShoe(shoe: Shoes) {
        inventurDao.deleteShoe(shoe)
    }

    fun getShoesForMember(memberId: Int): Flow<List<Shoes>> {
        return inventurDao.getShoesForMember(memberId)
    }

    // Members
    val allMembers: Flow<List<MemberKind>> = inventurDao.getAllMembers()

    suspend fun insertMember(member: MemberKind): Long {
        return inventurDao.insertMember(member)
    }

    suspend fun deleteMember(member: MemberKind) {
        inventurDao.deleteMember(member)
    }

    suspend fun getMemberById(id: Int): MemberKind? {
        return inventurDao.getMemberById(id)
    }

    val membersWithShoes: Flow<List<MemberWithShoes>> = inventurDao.getMembersWithShoes()
}
