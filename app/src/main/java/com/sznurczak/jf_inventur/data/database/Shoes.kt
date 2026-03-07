package com.sznurczak.jf_inventur.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shoes")
data class Shoes(
    @PrimaryKey val id: String,  // Unique NFC/QR per shoe (left/right separate)
    val pairId: String,          // Same for left+right (e.g. "PAIR_001_LR")
    val side: ShoeSide,          // LEFT or RIGHT
    val size: Int,
    val brand: String,
    val category: String,
    val dateOfPurchase: String
    // Remove idString - redundant with id
)

enum class ShoeSide { LEFT, RIGHT }