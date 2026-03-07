package com.sznurczak.jf_inventur.data.database

import android.media.Image
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sznurczak.jf_inventur.data.gender

@Entity
data class MemberKind(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    val firstName: String?,
    val lastName: String?,
    val gender: gender?,
    val image: Image?,
    val shoes: Shoes?

)





