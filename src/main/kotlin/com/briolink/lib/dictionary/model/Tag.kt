package com.briolink.lib.dictionary.model

import com.briolink.lib.dictionary.enumeration.TagType
import com.fasterxml.jackson.annotation.JsonProperty

data class Tag(
    @JsonProperty
    var id: String,
    @JsonProperty
    var type: TagType,
    @JsonProperty
    var name: String
) {
    @JsonProperty
    var path: String? = null
    @JsonProperty
    var parent: Tag? = null
}
