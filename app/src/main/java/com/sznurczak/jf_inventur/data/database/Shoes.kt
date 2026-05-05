package com.sznurczak.jf_inventur.data.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(
    tableName = "shoes",
    foreignKeys = [
        ForeignKey(
            entity = MemberKind::class,
            parentColumns = ["uid"],
            childColumns = ["memberId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["memberId"])]
)
data class Shoes(
    @PrimaryKey val id: String,
    val pairId: String,
    val side: ShoeSide,
    val size: Int,
    val brand: String,
    val category: String,
    val dateOfPurchase: String,
    val memberId: Int? = null
)

enum class ShoeSide { LEFT, RIGHT }

data class ShoeWithMember(
    @Embedded val shoe: Shoes,
    @Relation(
        parentColumn = "memberId",
        entityColumn = "uid"
    )
    val member: MemberKind?
)
