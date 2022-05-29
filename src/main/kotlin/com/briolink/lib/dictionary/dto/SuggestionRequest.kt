package com.briolink.lib.dictionary.dto

import com.briolink.lib.dictionary.enumeration.SuggestionTypeEnum

data class SuggestionRequest(
    val suggestionType: SuggestionTypeEnum,
    val query: String? = null,
    val limit: Int = 10,
    val offset: Int = 0,
    /**
     * Parents path id, ltree PostgreSQL type
     * @sample "1.2.3"
     */
    val parentIds: Set<String>? = null
)
