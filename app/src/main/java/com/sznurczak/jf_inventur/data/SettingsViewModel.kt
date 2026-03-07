package com.sznurczak.jf_inventur.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sznurczak.jf_inventur.SettingsViewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import androidx.datastore.preferences.core.Preferences

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsViewModel(private val context: Context) : ViewModel() {

    private val _multicastAddress = MutableStateFlow("") // Default value
    val multicastAddress: StateFlow<String> = _multicastAddress.asStateFlow()

    private val _port = MutableStateFlow("5005") // Default value
    val port: StateFlow<String> = _port.asStateFlow()

    private val _controlPort = MutableStateFlow("5008") // Default value
    val controlPort: StateFlow<String> = _controlPort.asStateFlow()


    private val _talkerId = MutableStateFlow("") // Default value
    val talkerId: StateFlow<String> = _talkerId.asStateFlow()

    private val _groupId = MutableStateFlow("") // Default value
    val groupId: StateFlow<String> = _groupId.asStateFlow()

    private val _useIPv6 = MutableStateFlow(false)
    val useIPv6: StateFlow<Boolean> = _useIPv6.asStateFlow()


    private val _codecMode = MutableStateFlow(3200) // Default value
    val codecMode: StateFlow<Int> = _codecMode.asStateFlow()

    // CODEC2 modes in bps
    val codecModes = listOf(3200, 2400, 1600, 1400, 1300, 1200, 700)


    private val _jitterBufferEnabled = MutableStateFlow(true)
    val jitterBufferEnabled: StateFlow<Boolean> = _jitterBufferEnabled.asStateFlow()




    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            context.dataStore.data.collect { preferences ->
                _multicastAddress.value = preferences[SettingsPreferences.MULTICAST_ADDRESS] ?: "239.1.1.1"
                _port.value = (preferences[SettingsPreferences.PORT] ?: 5005).toString()
                _codecMode.value = preferences[SettingsPreferences.CODEC_MODE] ?: 3200
                _controlPort.value = (preferences[SettingsPreferences.CONTROL_PORT] ?: 5008).toString()
                _talkerId.value = (preferences[SettingsPreferences.TALK_ID] ?: "421") as String
                //_groupId.value = preferences[SettingsPreferences.GROUP_ID] ?: 10
                _jitterBufferEnabled.value = preferences[SettingsPreferences.JITTER_BUFFER] ?: true
                _useIPv6.value = preferences[SettingsPreferences.USE_IPV6] ?: false
            }
        }
    }


    private fun saveSettings() {
        viewModelScope.launch {
            context.dataStore.edit { preferences ->
                preferences[SettingsPreferences.MULTICAST_ADDRESS] = _multicastAddress.value
                preferences[SettingsPreferences.PORT] = _port.value.toIntOrNull() ?: 5005
                preferences[SettingsPreferences.CODEC_MODE] = _codecMode.value
                preferences[SettingsPreferences.CONTROL_PORT] = _controlPort.value.toIntOrNull() ?: 5008
                preferences[SettingsPreferences.TALK_ID] = _talkerId.value.toIntOrNull() ?: 421
                //preferences[SettingsPreferences.GROUP_ID] = _groupId.value.toIntOrNull() ?: 10
                preferences[SettingsPreferences.JITTER_BUFFER] = _jitterBufferEnabled.value
                preferences[SettingsPreferences.USE_IPV6] = _useIPv6.value
            }
        }
    }
}