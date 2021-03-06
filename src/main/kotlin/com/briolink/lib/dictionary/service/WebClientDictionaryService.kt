package com.briolink.lib.dictionary.service

import com.briolink.lib.common.exception.AccessDeniedException
import com.briolink.lib.common.exception.BadRequestException
import com.briolink.lib.common.exception.EntityExistException
import com.briolink.lib.common.exception.EntityNotFoundException
import com.briolink.lib.common.exception.ValidationException
import com.briolink.lib.common.type.basic.ErrorResponse
import com.briolink.lib.common.type.basic.ListSuggestion
import com.briolink.lib.dictionary.dto.SuggestionRequest
import com.briolink.lib.dictionary.dto.TagCreateRequest
import com.briolink.lib.dictionary.dto.TagGetRequest
import com.briolink.lib.dictionary.enumeration.SuggestionTypeEnum
import com.briolink.lib.dictionary.model.ListTags
import com.briolink.lib.dictionary.model.Tag
import com.briolink.lib.dictionary.model.TagId
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono

open class WebClientDictionaryService(private val webClient: WebClient) {

    protected open val tagUrl = "tags"
    protected open val suggestionUrl = "suggestions"

    protected open fun <T : Exception> convertErrorResponseToMonoErrorAtTag(response: ErrorResponse): Mono<T> {
        return when (response.httpStatus) {
            HttpStatus.FORBIDDEN -> Mono.error(AccessDeniedException(response.message ?: "Access denied"))
            HttpStatus.BAD_REQUEST -> Mono.error(BadRequestException(response.message ?: "Bad request"))
            HttpStatus.NOT_ACCEPTABLE -> Mono.error(ValidationException(response.message ?: "Tag type not found"))
            HttpStatus.NOT_FOUND -> Mono.error(EntityNotFoundException(response.message ?: "Parent tag not found"))
            HttpStatus.CONFLICT -> Mono.error(EntityExistException(response.message ?: "Tag and path already exists"))
            else -> Mono.error(RuntimeException(response.message))
        }
    }

    protected open fun <T : Exception> convertErrorResponseToMonoErrorAtSuggestion(response: ErrorResponse): Mono<T> {
        return when (response.httpStatus) {
            HttpStatus.FORBIDDEN -> Mono.error(AccessDeniedException(response.message ?: "Access denied"))
            HttpStatus.NOT_ACCEPTABLE ->
                Mono.error(ValidationException(response.message ?: "Suggestion type not found"))
            else -> Mono.error(RuntimeException(response.message))
        }
    }

    open fun getSuggestions(request: SuggestionRequest): Mono<ListSuggestion> {
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
                return@onStatus response.bodyToMono(ErrorResponse::class.java).flatMap { error ->
                    return@flatMap convertErrorResponseToMonoErrorAtSuggestion(error)
                }
            }
            .bodyToMono(ListSuggestion::class.java)
    }

    open fun getSuggestions(
        type: SuggestionTypeEnum,
        query: String? = null,
        limit: Int = 10,
        offset: Int = 0,
        parentIds: List<String>? = null
    ): Mono<ListSuggestion> {
        return getSuggestions(SuggestionRequest(type, query, limit, offset, parentIds?.toSet()))
    }

    open fun getTags(request: TagGetRequest): Mono<ListTags> {
        return webClient.get()
            .uri { builder ->
                builder.path("/$tagUrl/")
                    .queryParam("ids", request.ids)
                    .queryParam("names", request.names)
                    .queryParam("paths", request.paths)
                    .queryParam("types", request.types?.map { it.name })
                    .queryParam("limit", request.limit)
                    .queryParam("offset", request.offset)
                    .queryParam("withParent", request.withParent)
                    .build()
            }
            .retrieve()
            .onStatus(HttpStatus::is4xxClientError) { response ->
                return@onStatus response.bodyToMono(ErrorResponse::class.java).flatMap { error ->
                    return@flatMap convertErrorResponseToMonoErrorAtTag(error)
                }
            }
            .bodyToMono(ListTags::class.java)
    }

    open fun getTag(id: TagId, withParent: Boolean = false): Mono<Tag> {
        return webClient.get()
            .uri { builder ->
                builder.path("/$tagUrl/{${id.type}/{${id.id}/")
                    .queryParam("withParent", withParent)
                    .build(id)
            }
            .retrieve()
            .onStatus(HttpStatus::is4xxClientError) { response ->
                return@onStatus response.bodyToMono(ErrorResponse::class.java).flatMap { error ->
                    return@flatMap convertErrorResponseToMonoErrorAtTag(error)
                }
            }
            .bodyToMono(Tag::class.java)
    }
    open fun createTags(requestList: List<TagCreateRequest>): Mono<List<Tag>> {
        val body = mapOf("tags" to requestList)

        return webClient.post()
            .uri { builder ->
                builder.path("/$tagUrl/bulk")
                    .build()
            }
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(body))
            .retrieve()
            .onStatus(HttpStatus::is4xxClientError) { response ->
                return@onStatus response.bodyToMono(ErrorResponse::class.java).flatMap { error ->
                    return@flatMap convertErrorResponseToMonoErrorAtTag(error)
                }
            }
            .bodyToMono()
    }
    open fun createTag(request: TagCreateRequest): Mono<Tag> {

        return webClient.post()
            .uri { builder ->
                builder.path("/$tagUrl/")
                    .build()
            }
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .onStatus(HttpStatus::is4xxClientError) { response ->
                return@onStatus response.bodyToMono(ErrorResponse::class.java).flatMap { error ->
                    return@flatMap convertErrorResponseToMonoErrorAtTag(error)
                }
            }
            .bodyToMono(Tag::class.java)
    }
}
