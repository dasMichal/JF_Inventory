package com.sznurczak.jf_inventur.data.tag

import com.sznurczak.jf_inventur.data.database.MemberKind

import java.util.UUID
import kotlin.toString


class TagData
{

    //var tagId: String? = null
    var tagId: String? = UUID.randomUUID().toString()
    var keyValueData: Map<String, String>? = null
    var MemberKind: MemberKind? = null
}




