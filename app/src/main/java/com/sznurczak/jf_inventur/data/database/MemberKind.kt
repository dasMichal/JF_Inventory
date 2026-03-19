package com.sznurczak.jf_inventur.data.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.sznurczak.jf_inventur.data.enums.abzeichen
import com.sznurczak.jf_inventur.data.enums.gender

@Entity(tableName = "members")
data class MemberKind(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    val firstName: String?,
    val lastName: String?,
    val gender: gender?,
    val abzeichen: abzeichen?
)

// This data class allows you to fetch a Member and their Shoes together
data class MemberWithShoes(
    @Embedded val member: MemberKind,
    @Relation(
        parentColumn = "uid",    // The Primary Key in MemberKind
        entityColumn = "memberId" // The Foreign Key in your Shoes entity
    )
    val shoes: List<Shoes>
)