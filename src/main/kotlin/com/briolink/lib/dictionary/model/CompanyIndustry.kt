package com.briolink.lib.dictionary.model

import com.briolink.lib.dictionary.enumeration.TagType
import com.fasterxml.jackson.annotation.JsonProperty

data class CompanyIndustry(
    @JsonProperty
    val sector: Tag,
    @JsonProperty
    val group: Tag? = null,
    @JsonProperty
    val code: Tag? = null

) {
    init {
        if (code != null && group == null) throw IllegalArgumentException("Industry group must not be null")
        if (sector.id.type != TagType.Industry) throw IllegalArgumentException("Sector tag type must be Industry")
        if (group != null && group.id.type != TagType.Industry) throw IllegalArgumentException("Group tag type must be Industry")
        if (code != null && code.id.type != TagType.Industry) throw IllegalArgumentException("Code tag Type must be Industry")
    }

    val tagWithParent: Tag
        get() = if (code != null) {
            val tag = code.copy()
            tag.parent = group
            tag.parent!!.parent = sector
            tag
        } else if (group != null) {
            val tag = group.copy()
            tag.parent = sector
            tag
        } else sector
}
