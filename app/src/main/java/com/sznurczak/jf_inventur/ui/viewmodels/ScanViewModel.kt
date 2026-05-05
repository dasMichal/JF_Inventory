package com.sznurczak.jf_inventur.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.sznurczak.jf_inventur.data.repository.DatabaseRepository
import com.sznurczak.jf_inventur.data.tag.ScanResult
import com.sznurczak.jf_inventur.data.database.ShoeWithMember
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ScanUiState {
    object Idle : ScanUiState()
    object Scanning : ScanUiState()
    data class FoundItem(val data: ShoeWithMember) : ScanUiState()
    data class UnknownTag(val tagId: String) : ScanUiState()
    data class Error(val message: String) : ScanUiState()
}

class ScanViewModel(private val repository: DatabaseRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<ScanUiState>(ScanUiState.Idle)
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()


    fun onScan(result: ScanResult) {
        android.util.Log.d("ScanViewModel", "onScan triggered with: $result")
        viewModelScope.launch {
            _uiState.value = ScanUiState.Scanning
            
            when (result) {
                is ScanResult.SimpleId -> {
                    android.util.Log.d("ScanViewModel", "Processing SimpleId: ${result.id}")
                    processId(result.id)
                }
                is ScanResult.KeyValueData -> {
                    val id = result.data["id"] ?: result.data.values.firstOrNull()
                    if (id != null) {
                        processId(id)
                    } else {
                        _uiState.value = ScanUiState.Error("No valid ID found in tag data")
                    }
                }
                is ScanResult.Error -> {
                    _uiState.value = ScanUiState.Error(result.message)
                }
            }
        }
    }

    private suspend fun processId(id: String) {
        try {
            val shoeWithMember = repository.getShoeWithMember(id)
            if (shoeWithMember != null) {
                android.util.Log.d("ScanViewModel", "Item found in DB: ${shoeWithMember.shoe.id}")
                _uiState.value = ScanUiState.FoundItem(shoeWithMember)
            } else {
                android.util.Log.d("ScanViewModel", "ID not found in DB: $id")
                _uiState.value = ScanUiState.UnknownTag(id)
            }
        } catch (e: Exception) {
            android.util.Log.e("ScanViewModel", "DB Error", e)
            _uiState.value = ScanUiState.Error("Database error: ${e.message}")
        }
    }

    fun clearScan() {
        _uiState.value = ScanUiState.Idle
    }
}


