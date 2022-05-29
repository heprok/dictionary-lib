package com.briolink.lib.dictionary.dto

import com.briolink.lib.dictionary.enumeration.TagType

data class TagCreateRequest(
    val id: String? = null,
    val name: String,
    /**
     * id must be only contain alphanumeric characters. (a-z0-9-)
     * @see TagType isIdUUID, if true id must be uuid
     * @sample "12345678-1234-1234-1234-123456789012"
     * @sample "tag-only-current
     */
    val type: TagType,
    val path: String? = null,
)

data class TagGetRequest(
    val id: String,
    val type: TagType,
    val withParent: Boolean = false,
)
