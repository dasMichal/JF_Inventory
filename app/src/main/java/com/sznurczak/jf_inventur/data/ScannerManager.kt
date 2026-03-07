package com.sznurczak.jf_inventur.data

import android.app.Activity
import android.nfc.NfcAdapter
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ScannerManager(private val nfcAdapter: NfcAdapter?) {

    private val _scannedData = MutableStateFlow<TagData?>(null)
    val scannedData: StateFlow<TagData?> = _scannedData

    private val tagManager = TagManager()

    private val readerCallback = NfcAdapter.ReaderCallback { tag ->
        val parsedData = tagManager.parseTag(tag)
        _scannedData.value = parsedData
    }

    fun startScanning(activity: Activity) {
        try {
            nfcAdapter?.enableReaderMode(
                activity,
                readerCallback,
                NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS,
                null
            )
        } catch (e: Exception) {
            Log.e("ScannerManager", "Error enabling reader mode", e)
        }
    }

    fun stopScanning(activity: Activity) {
        try {
            nfcAdapter?.disableReaderMode(activity)
        } catch (e: Exception) {
            Log.e("ScannerManager", "Error disabling reader mode", e)
        }
    }

    private fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }
}