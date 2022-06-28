package com.briolink.lib.dictionary.service

import com.briolink.lib.common.exception.EntityExistException
import com.briolink.lib.common.exception.EntityNotFoundException
import com.briolink.lib.common.exception.ValidationException
import com.briolink.lib.common.type.basic.Suggestion
import com.briolink.lib.dictionary.dto.SuggestionRequest
import com.briolink.lib.dictionary.dto.TagCreateRequest
import com.briolink.lib.dictionary.dto.TagGetRequest
import com.briolink.lib.dictionary.enumeration.SuggestionTypeEnum
import com.briolink.lib.dictionary.enumeration.TagType
import com.briolink.lib.dictionary.model.Tag
import com.briolink.lib.dictionary.model.TagId

open class DictionaryService(private val webClient: WebClientDictionaryService) {

    /**
     * Get suggestions tags
     * @param request
     * @return List of suggestions
     */
    open fun getSuggestions(request: SuggestionRequest): List<Suggestion> {
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
    ): List<Suggestion> {
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
     * Find tag if not exist then create
     * @param request tag creation request
     * @param withParent if true return tag with parents else return tag without parents
     *
     * @throws AccessDeniedException Access denied
     * @throws ValidationException Validation error
     * @throws EntityExistException Tag with path already exists
     * @return Tag
     */
    open fun getTagIfNotExistsCreate(request: TagCreateRequest, withParent: Boolean = true): Tag {
        val tag = if (request.id != null) getTagByIdOrNull(TagId(request.id, request.type), withParent)
        else getTagByNameOrNull(request.name, request.type, request.path, withParent)

        return tag ?: createTag(request)
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

    open fun createTags(
        tags: List<TagCreateRequest>
    ): List<Tag> {
        return webClient.createTags(tags).block() ?: listOf()
    }

    /**
     * Find tag by request and first tag or null
     *
     * @param request the tag get request
     * @return Tag or null if not found
     */
    private fun getTagOrNull(request: TagGetRequest): Tag? {
        return webClient.getTags(request).block()?.tags?.firstOrNull()
    }

    /**
     * Find tags by request
     *
     * @param request the tag get request
     * @return List tag
     */
    private fun getTags(request: TagGetRequest): List<Tag>? {
        return webClient.getTags(request).block()?.tags
    }

    /**
     * Find a tag
     * @param request the tag creation request
     * @throws EntityNotFoundException Tag not found
     * @return Tag or null if not found
     */
    private fun getTag(request: TagGetRequest): Tag {
        return getTagOrNull(request) ?: throw EntityNotFoundException("Tag not found")
    }

    /**
     * Find a tag
     * @param id
     * @param withParent If true, the parent tag will be returned
     * @return Tag or null if not found
     */
    open fun getTagByIdOrNull(
        id: TagId,
        withParent: Boolean = true
    ): Tag? {
        return webClient.getTag(id, withParent).block()
    }

    /**
     * Find tag
     * @param id
     * @param withParent If true, the parent tag will be returned
     * @throws EntityNotFoundException Tag not found
     * @return Tag
     */
    open fun getTagById(id: TagId, withParent: Boolean = true): Tag {
        return getTagByIdOrNull(id, withParent) ?: throw EntityNotFoundException("Tag not found")
    }

    /**
     * Find tag
     * @param name Name tag
     * @param type Tag type
     * @param path Tag path
     * @param withParent If true, the parent tag will be returned
     * @return Tag or null if not found
     */
    open fun getTagByNameOrNull(
        name: String,
        type: TagType,
        path: String? = null,
        withParent: Boolean = true
    ): Tag? {
        return getTagOrNull(
            TagGetRequest(
                ids = null,
                names = listOf(name),
                paths = path?.let { listOf(it) },
                types = listOf(type),
                withParent = withParent
            )
        )
    }

    /**
     * Find a tag
     * @param name Name tag
     * @param type Tag type
     * @param path Tag path
     * @param withParent If true, the parent tag will be returned
     * @throws EntityNotFoundException Tag not found
     * @return Tag
     */
    open fun getTagByName(name: String, type: TagType, path: String?, withParent: Boolean = true): Tag {
        return getTagByNameOrNull(name, type, path, withParent) ?: throw EntityNotFoundException("Tag not found")
    }

    /**
     * Find a tag
     * @param ids ids tags
     * @param withParent If true, the parent tag will be returned
     *
     * @throws EntityNotFoundException Tag not found if ids.size != tags.size
     */

    open fun getTagsByIds(
        ids: Set<String>,
        withParent: Boolean = true,
        limit: Int = 30,
        offset: Int = 0
    ): List<Tag> {
        return webClient.getTags(
            TagGetRequest(
                ids = ids.toList(),
                names = listOf(),
                paths = listOf(),
                types = listOf(),
                withParent = withParent,
                limit = limit,
                offset = offset
            )
        )
            .block()?.tags?.also {
                if (it.size != ids.size) throw EntityNotFoundException("Tags not found")
            } ?: throw EntityNotFoundException("Tags not found")
    }

    open fun findTags(request: TagGetRequest): List<Tag> {
        return getTags(request) ?: throw EntityNotFoundException("Tags not found")
    }
}
