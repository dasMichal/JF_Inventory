package com.sznurczak.jf_inventur.data.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "shoes",
    foreignKeys = [
        ForeignKey(
            entity = MemberKind::class,
            parentColumns = ["uid"],
            childColumns = ["memberId"],
            onDelete = ForeignKey.SET_NULL // Or CASCADE if shoes should be deleted with member
        )
    ],
    indices = [Index(value = ["memberId"])]
)
data class Shoes(
    @PrimaryKey val id: String,  // Unique NFC/QR per shoe
    val pairId: String,          // Same for left+right
    val side: ShoeSide,          // LEFT or RIGHT
    val size: Int,
    val brand: String,
    val category: String,
    val dateOfPurchase: String,
    val memberId: Int? = null    // Foreign key to MemberKind
)

enum class ShoeSide { LEFT, RIGHT }