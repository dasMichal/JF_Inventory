package com.sznurczak.jf_inventur.data.database

import androidx.room.TypeConverter
import com.sznurczak.jf_inventur.data.enums.gender

class Converters {
    @TypeConverter
    fun fromShoeSide(value: ShoeSide) = value.name

    @TypeConverter
    fun toShoeSide(value: String) = ShoeSide.valueOf(value)

    @TypeConverter
    fun fromGender(value: gender?) = value?.name

    @TypeConverter
    fun toGender(value: String?) = value?.let { gender.valueOf(it) }
}