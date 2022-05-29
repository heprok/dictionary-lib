package com.briolink.lib.dictionary.exception

import com.fasterxml.jackson.annotation.JsonProperty

data class ErrorResponse(
    @JsonProperty
    val id: String? = null,
    @JsonProperty
    val exceptionId: String? = null,
    @JsonProperty
    val error: String? = null,
    @JsonProperty
    override var message: String? = null,
    @JsonProperty
    val status: Int,
) : RuntimeException()
