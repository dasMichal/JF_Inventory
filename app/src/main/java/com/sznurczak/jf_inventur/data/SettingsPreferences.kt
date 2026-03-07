package com.sznurczak.jf_inventur.data

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey


object SettingsPreferences {
    val MULTICAST_ADDRESS = stringPreferencesKey("multicast_address")
    val PORT = intPreferencesKey("port")
    val CONTROL_PORT = intPreferencesKey("control_port")

    val TALK_ID = intPreferencesKey("talker_id")
    val GROUP_ID = intPreferencesKey("group_id")

    val JITTER_BUFFER = booleanPreferencesKey("jitter_buffer_enabled")
    val USE_IPV6 = booleanPreferencesKey("use_ipv6")

    val CODEC_MODE = intPreferencesKey("codec_mode")
}
