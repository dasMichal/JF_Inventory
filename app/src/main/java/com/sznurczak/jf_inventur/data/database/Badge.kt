package com.sznurczak.jf_inventur.data.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "badges")
data class Badge(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
)

@Entity(
    tableName = "member_badges",
    primaryKeys = ["memberId", "badgeId"],
    foreignKeys = [
        ForeignKey(
            entity = MemberKind::class,
            parentColumns = ["uid"],
            childColumns = ["memberId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Badge::class,
            parentColumns = ["id"],
            childColumns = ["badgeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("badgeId")]
)
data class MemberBadge(
    val memberId: Int,
    val badgeId: Int
)
