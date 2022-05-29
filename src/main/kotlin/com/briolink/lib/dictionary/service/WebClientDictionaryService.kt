package com.briolink.lib.dictionary.service

import com.briolink.lib.common.exception.AccessDeniedException
import com.briolink.lib.common.exception.EntityExistException
import com.briolink.lib.common.exception.EntityNotFoundException
import com.briolink.lib.common.exception.ValidationException
import com.briolink.lib.common.type.basic.BlErrorResponse
import com.briolink.lib.common.type.basic.ListBlSuggestion
import com.briolink.lib.dictionary.dto.SuggestionRequest
import com.briolink.lib.dictionary.dto.TagCreateRequest
import com.briolink.lib.dictionary.dto.TagGetRequest
import com.briolink.lib.dictionary.enumeration.SuggestionTypeEnum
import com.briolink.lib.dictionary.model.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

open class WebClientDictionaryService(private val webClient: WebClient) {

    protected open val tagUrl = "tags"
    protected open val suggestionUrl = "suggestions"

    protected open fun <T : Exception> convertErrorResponseToMonoErrorAtTag(response: BlErrorResponse): Mono<T> {
        return when (response.httpsStatus) {
            HttpStatus.FORBIDDEN -> Mono.error(AccessDeniedException(response.message ?: "Access denied"))
            HttpStatus.NOT_ACCEPTABLE -> Mono.error(ValidationException(response.message ?: "Tag type not found"))
            HttpStatus.NOT_FOUND -> Mono.error(EntityNotFoundException(response.message ?: "Parent tag not found"))
            HttpStatus.CONFLICT -> Mono.error(EntityExistException(response.message ?: "Tag and path already exists"))
            else -> Mono.error(RuntimeException(response.message))
        }
    }

    protected open fun <T : Exception> convertErrorResponseToMonoErrorAtSuggestion(response: BlErrorResponse): Mono<T> {
        return when (response.httpsStatus) {
            HttpStatus.FORBIDDEN -> Mono.error(AccessDeniedException(response.message ?: "Access denied"))
            HttpStatus.NOT_ACCEPTABLE ->
                Mono.error(ValidationException(response.message ?: "Suggestion type not found"))
            else -> Mono.error(RuntimeException(response.message))
        }
    }

    open fun getSuggestions(request: SuggestionRequest): Mono<ListBlSuggestion> {
        return webClient.get()
            .uri { builder ->
                builder.path("/$suggestionUrl/")
                    .queryParam("suggestionType", request.suggestionType.name)
                    .queryParam("query", request.query)
                    .queryParam("limit", request.limit)
                    .queryParam("offset", request.offset)
                    .queryParam("parentIds", request.parentIds)
                    .build()
            }
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .onStatus(HttpStatus::is4xxClientError) { response ->
                return@onStatus response.bodyToMono(BlErrorResponse::class.java).flatMap { error ->
                    return@flatMap convertErrorResponseToMonoErrorAtSuggestion(error)
                }
            }
            .bodyToMono(ListBlSuggestion::class.java)
    }

    open fun getSuggestions(
        type: SuggestionTypeEnum,
        query: String? = null,
        limit: Int = 10,
        offset: Int = 0,
        parentIds: List<String>? = null
    ): Mono<ListBlSuggestion> {
        return getSuggestions(SuggestionRequest(type, query, limit, offset, parentIds?.toSet()))
    }

    open fun getTag(request: TagGetRequest): Mono<Tag> {
        return webClient.get()
            .uri { builder ->
                builder.path("/$tagUrl/")
                    .queryParam("id", request.id)
                    .queryParam("type", request.type.name)
                    .queryParam("withParent", request.withParent)
                    .build()
            }
            .retrieve()
            .onStatus(HttpStatus::is4xxClientError) { response ->
                return@onStatus response.bodyToMono(BlErrorResponse::class.java).flatMap { error ->
                    return@flatMap convertErrorResponseToMonoErrorAtTag(error)
                }
            }
            .bodyToMono(Tag::class.java)
    }

    open fun createTag(request: TagCreateRequest): Mono<Tag> {
        return webClient.post()
            .uri { builder ->
                builder.path("/$tagUrl/")
                    .queryParam("id", request.id)
                    .queryParam("name", request.name)
                    .queryParam("type", request.type.name)
                    .queryParam("path", request.path)
                    .build()
            }
            .retrieve()
            .onStatus(HttpStatus::is4xxClientError) { response ->
                return@onStatus response.bodyToMono(BlErrorResponse::class.java).flatMap { error ->
                    return@flatMap convertErrorResponseToMonoErrorAtTag(error)
                }
            }
            .bodyToMono(Tag::class.java)
    }
}
