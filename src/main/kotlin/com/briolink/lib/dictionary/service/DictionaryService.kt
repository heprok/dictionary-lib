package com.briolink.lib.dictionary.service

import com.briolink.lib.common.exception.EntityExistException
import com.briolink.lib.common.exception.EntityNotFoundException
import com.briolink.lib.common.exception.ValidationException
import com.briolink.lib.common.type.basic.BlSuggestion
import com.briolink.lib.dictionary.dto.SuggestionRequest
import com.briolink.lib.dictionary.dto.TagCreateRequest
import com.briolink.lib.dictionary.dto.TagGetRequest
import com.briolink.lib.dictionary.enumeration.SuggestionTypeEnum
import com.briolink.lib.dictionary.enumeration.TagType
import com.briolink.lib.dictionary.model.Tag

open class DictionaryService(private val webClient: WebClientDictionaryService) {

    /**
     * Get suggestions tags
     * @param request
     * @return List of suggestions
     */
    open fun getSuggestions(request: SuggestionRequest): List<BlSuggestion> {
        return webClient.getSuggestions(request).block()?.listSuggestion ?: listOf()
    }

    /**
     * Get suggestions tags
     *
     * @param type Suggestion type
     * @param query find str
     * @param limit max count
     * @param offset offset
     * @param parentIds path parent
     * @return List of suggestions
     */
    open fun getSuggestions(
        type: SuggestionTypeEnum,
        query: String? = null,
        limit: Int = 10,
        offset: Int = 0,
        parentIds: List<String>? = null
    ): List<BlSuggestion> {
        return getSuggestions(SuggestionRequest(type, query, limit, offset, parentIds?.toSet()))
    }

    /**
     * Create a new tag
     *
     * @param request the tag creation request
     * @throws EntityNotFoundException Parent tag doesn't exist
     * @throws AccessDeniedException Access denied
     * @throws ValidationException Validation error
     * @throws EntityExistException Tag with path already exists
     * @return Tag created
     */
    open fun createTag(request: TagCreateRequest): Tag {
        return webClient.createTag(request).block() ?: throw RuntimeException("Tag not created")
    }

    /**
     * Create tag
     * @param name the tag name
     * @param type the tag type
     * @param path the tag path
     * @param id UUID or slug or null
     * @throws EntityNotFoundException Parent tag doesn't exist
     * @throws AccessDeniedException Access denied
     * @throws ValidationException Validation error
     * @throws EntityExistException Tag with path already exists
     * @return Tag created
     *
     */
    open fun createTag(
        id: String? = null,
        name: String,
        type: TagType,
        path: String? = null,
    ): Tag {
        return createTag(TagCreateRequest(id, name, type, path))
    }

    /**
     * Find a new tag
     *
     * @param request the tag creation request
     * @return Tag or null if not found
     */
    open fun getTagOrNull(request: TagGetRequest): Tag? {
        return webClient.getTag(request).block()
    }

    /**
     * Find a tag
     * @param id UUID or slug
     * @param type Tag type
     * @param withParent If true, the parent tag will be returned
     * @return Tag or null if not found
     */
    open fun getTagOrNull(id: String, type: TagType, withParent: Boolean = true): Tag? {
        return getTagOrNull(TagGetRequest(id, type, withParent))
    }

    /**
     * Find a tag
     * @param request the tag creation request
     * @throws EntityNotFoundException Tag not found
     * @return Tag or null if not found
     */
    open fun getTag(request: TagGetRequest): Tag {
        return getTagOrNull(request) ?: throw EntityNotFoundException("Tag not found")
    }

    /**
     * Find a tag
     * @param id UUID or slug
     * @param type Tag type
     * @param withParent If true, the parent tag will be returned
     * @throws EntityNotFoundException Tag not found
     * @return Tag
     */
    open fun getTag(id: String, type: TagType, withParent: Boolean = true): Tag {
        return getTagOrNull(id, type, withParent) ?: throw EntityNotFoundException("Tag not found")
    }
}
