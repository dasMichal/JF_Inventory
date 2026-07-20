package com.sznurczak.jf_inventur.data.repository

import com.sznurczak.jf_inventur.data.dao.InventurDao
import com.sznurczak.jf_inventur.data.database.Badge
import com.sznurczak.jf_inventur.data.database.MemberBadge
import com.sznurczak.jf_inventur.data.database.MemberKind
import com.sznurczak.jf_inventur.data.database.MemberWithBadges
import com.sznurczak.jf_inventur.data.database.MemberWithShoes
import com.sznurczak.jf_inventur.data.database.Shoes
import com.sznurczak.jf_inventur.data.database.ShoeWithMember
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

    suspend fun getShoeWithMember(shoeId: String): ShoeWithMember? {
        return inventurDao.getShoeWithMember(shoeId)
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

    suspend fun updateMember(member: MemberKind) {
        inventurDao.insertMember(member)
    }

    fun getMemberWithBadges(memberId: Int): Flow<MemberWithBadges?> {
        return inventurDao.getMemberWithBadges(memberId)
    }

    // Badges
    val allBadges: Flow<List<Badge>> = inventurDao.getAllBadges()

    suspend fun insertBadge(badge: Badge): Long {
        return inventurDao.insertBadge(badge)
    }

    suspend fun addBadgeToMember(memberId: Int, badgeId: Int) {
        inventurDao.insertMemberBadge(MemberBadge(memberId, badgeId))
    }

    suspend fun removeBadgeFromMember(memberId: Int, badgeId: Int) {
        inventurDao.deleteMemberBadge(MemberBadge(memberId, badgeId))
    }

    suspend fun clearBadgesForMember(memberId: Int) {
        inventurDao.deleteAllBadgesForMember(memberId)
    }

    suspend fun getBadgeByName(name: String): Badge? {
        return inventurDao.getBadgeByName(name)
    }
}
