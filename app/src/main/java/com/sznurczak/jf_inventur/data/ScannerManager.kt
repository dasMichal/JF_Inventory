package com.sznurczak.jf_inventur.data

import android.app.Activity
import android.nfc.NfcAdapter
import android.util.Log
import com.sznurczak.jf_inventur.data.tag.ScanResult
import com.sznurczak.jf_inventur.data.tag.TagManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ScannerManager(private val nfcAdapter: NfcAdapter?) {

    private val _scannedData = MutableStateFlow<ScanResult?>(null)
    val scannedData: StateFlow<ScanResult?> = _scannedData

    private val tagManager = TagManager()

    private val readerCallback = NfcAdapter.ReaderCallback { tag ->
        val parsedData = tagManager.parseTag(tag)
        _scannedData.value = parsedData
        Log.d("ScannerManager", "Scanned data: $parsedData")

    }

    fun startScanning(activity: Activity) {
        if (nfcAdapter == null) return
        try {
            // Broaden flags and REMOVE 'NO_PLATFORM_SOUNDS' to get the system beep
            val flags = NfcAdapter.FLAG_READER_NFC_A or 
                        NfcAdapter.FLAG_READER_NFC_B or 
                        NfcAdapter.FLAG_READER_NFC_F or 
                        NfcAdapter.FLAG_READER_NFC_V or 
                        NfcAdapter.FLAG_READER_NFC_BARCODE

            nfcAdapter.enableReaderMode(
                activity,
                readerCallback,
                flags,
                null
            )
            Log.d("ScannerManager", "Reader mode enabled with sound")
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

    fun clearScan() {
        _scannedData.value = null
    }

    private fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }
}
