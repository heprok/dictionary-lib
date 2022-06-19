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

/**
 * Get request find tags by ids AND
 * Find by names and types AND
 * Find by paths and types
 *
 */
data class TagGetRequest(
    val ids: List<String>?,
    val names: List<String>?,
    val paths: List<String>?,
    val types: List<TagType>?,
    val withParent: Boolean = false,
    val limit: Int = 30,
    val offset: Int = 0,
) {
    init {
        if (ids.isNullOrEmpty() && names.isNullOrEmpty() && paths.isNullOrEmpty())
            throw IllegalArgumentException("ids, names, paths must be not null or empty")

        if (!ids.isNullOrEmpty() && !types.isNullOrEmpty() && names.isNullOrEmpty() && paths.isNullOrEmpty())
            throw IllegalArgumentException("Query must be names or paths")

        if (limit < 0 || offset < 0)
            throw IllegalArgumentException("limit and offset must be greater than 0")

        if (limit > 100)
            throw IllegalArgumentException("limit must be less than 100")
    }
}
