package com.sznurczak.jf_inventur.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sznurczak.jf_inventur.data.database.MemberKind
import com.sznurczak.jf_inventur.data.database.MemberWithShoes
import com.sznurczak.jf_inventur.data.enums.abzeichen
import com.sznurczak.jf_inventur.data.enums.gender
import com.sznurczak.jf_inventur.data.repository.DatabaseRepository
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

    fun addMember(firstName: String, lastName: String, gender: gender, abzeichen: abzeichen) {
        viewModelScope.launch {
            val newMember = MemberKind(
                firstName = firstName,
                lastName = lastName,
                gender = gender,
                abzeichen = abzeichen
            )
            repository.insertMember(newMember)
        }
    }

    fun deleteMember(member: MemberKind) {
        viewModelScope.launch {
            repository.deleteMember(member)
        }
    }
}
