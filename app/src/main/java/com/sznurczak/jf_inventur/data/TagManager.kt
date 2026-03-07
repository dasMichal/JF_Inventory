package com.sznurczak.jf_inventur.data

import android.nfc.NdefRecord
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.util.Log

sealed class TagData {
    data class KeyValueData(val data: Map<String, String>) : TagData()
    data class SimpleId(val id: String) : TagData()
    data class Error(val message: String) : TagData()
}

class TagManager {
    fun parseTag(tag: Tag): TagData {
        val ndef = Ndef.get(tag)
        if (ndef != null) {
            try {
                ndef.connect()
                val ndefMessage = ndef.cachedNdefMessage
                if (ndefMessage != null && ndefMessage.records.isNotEmpty()) {
                    val record = ndefMessage.records[0]
                    val payloadString = if (record.tnf == NdefRecord.TNF_WELL_KNOWN && record.type.contentEquals(NdefRecord.RTD_TEXT)) {
                        val langCodeLength = record.payload[0].toInt() and 0x3F
                        String(record.payload, langCodeLength + 1, record.payload.size - langCodeLength - 1, Charsets.UTF_8)
                    } else {
                        String(record.payload, Charsets.UTF_8).trim()
                    }
                    val keyValueData = parseKeyValueString(payloadString)
                    if (keyValueData.isNotEmpty()) {
                        return TagData.KeyValueData(keyValueData)
                    } else {
                        return TagData.SimpleId(payloadString)
                    }
                }
            } catch (e: Exception) {
                Log.e("TagManager", "Error parsing tag", e)
                return TagData.Error("Error: ${e.message}")
            } finally {
                try {
                    ndef.close()
                } catch (e: Exception) {
                    Log.e("TagManager", "Error closing NDEF", e)
                }
            }
        }
        return TagData.SimpleId(tag.id.toHexString())
    }

    private fun parseKeyValueString(data: String): Map<String, String> {
        val map = mutableMapOf<String, String>()
        if (!data.contains("=")) {
            return emptyMap()
        }
        try {
            data.split(';').forEach { pair ->
                if (pair.isNotBlank()) {
                    val keyValue = pair.split('=', limit = 2)
                    if (keyValue.size == 2) {
                        map[keyValue[0].trim()] = keyValue[1].trim()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("TagManager", "Error parsing key-value string", e)
        }
        return map
    }

    private fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }


    fun writeTag(tag: Tag, data: String)
    {
        val ndef = Ndef.get(tag)


    }




}
