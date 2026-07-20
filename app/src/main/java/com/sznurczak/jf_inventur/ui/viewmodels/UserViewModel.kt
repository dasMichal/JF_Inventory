package com.sznurczak.jf_inventur.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sznurczak.jf_inventur.data.database.Badge
import com.sznurczak.jf_inventur.data.database.MemberKind
import com.sznurczak.jf_inventur.data.database.MemberWithBadges
import com.sznurczak.jf_inventur.data.database.MemberWithShoes
import com.sznurczak.jf_inventur.data.enums.Jugendflamme
import com.sznurczak.jf_inventur.data.enums.gender
import com.sznurczak.jf_inventur.data.repository.DatabaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserViewModel(private val repository: DatabaseRepository) : ViewModel() {

    // Automatically updating list of members with their equipment
    val membersWithShoes: StateFlow<List<MemberWithShoes>> = repository.membersWithShoes
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allBadges: StateFlow<List<Badge>> = repository.allBadges
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addMember(firstName: String, lastName: String, gender: gender) {
        viewModelScope.launch {
            val newMember = MemberKind(
                firstName = firstName,
                lastName = lastName,
                gender = gender,
                jugendflamme = Jugendflamme.NONE,
                hasLeistungsspange = false
            )
            repository.insertMember(newMember)
        }
    }

    fun updateMember(member: MemberKind) {
        viewModelScope.launch {
            repository.updateMember(member)
        }
    }

    fun deleteMember(member: MemberKind) {
        viewModelScope.launch {
            repository.deleteMember(member)
        }
    }

    fun getMemberWithBadges(memberId: Int): Flow<MemberWithBadges?> {
        return repository.getMemberWithBadges(memberId)
    }

    fun getShoesForMember(memberId: Int) = repository.getShoesForMember(memberId)

    fun addBadgeToMember(memberId: Int, badgeId: Int) {
        viewModelScope.launch {
            repository.addBadgeToMember(memberId, badgeId)
        }
    }

    fun removeBadgeFromMember(memberId: Int, badgeId: Int) {
        viewModelScope.launch {
            repository.removeBadgeFromMember(memberId, badgeId)
        }
    }

    fun createAndAddBadge(memberId: Int, badgeName: String) {
        viewModelScope.launch {
            val existingBadge = repository.getBadgeByName(badgeName)
            val badgeId = existingBadge?.id?.toLong() ?: repository.insertBadge(Badge(name = badgeName))
            repository.addBadgeToMember(memberId, badgeId.toInt())
        }
    }
}
