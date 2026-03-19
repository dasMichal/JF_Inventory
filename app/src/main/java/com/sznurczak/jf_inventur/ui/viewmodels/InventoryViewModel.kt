package com.sznurczak.jf_inventur.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sznurczak.jf_inventur.data.database.Shoes
import com.sznurczak.jf_inventur.data.database.ShoeSide
import com.sznurczak.jf_inventur.data.repository.DatabaseRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class InventoryViewModel(private val repository: DatabaseRepository) : ViewModel() {

    // Automatically updating list of all shoes
    val allShoes: StateFlow<List<Shoes>> = repository.allShoes
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun registerNewShoe(
        id: String,
        pairId: String,
        side: ShoeSide,
        size: Int,
        brand: String,
        category: String,
        date: String,
        memberId: Int? = null
    ) {
        viewModelScope.launch {
            val shoe = Shoes(id, pairId, side, size, brand, category, date, memberId)
            repository.insertShoe(shoe)
        }
    }

    fun deleteShoe(shoe: Shoes) {
        viewModelScope.launch {
            repository.deleteShoe(shoe)
        }
    }
}
