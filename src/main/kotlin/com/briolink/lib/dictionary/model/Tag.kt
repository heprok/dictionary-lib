package com.briolink.lib.dictionary.model

import com.briolink.lib.dictionary.enumeration.TagType
import com.fasterxml.jackson.annotation.JsonProperty

interface BaseTag {
    var id: String
    var type: TagType
    var name: String
    var path: String?
    var parent: BaseTag?
}

data class Tag(
    @JsonProperty
    override var id: String,
    @JsonProperty
    override var type: TagType,
    @JsonProperty
    override var name: String
) : BaseTag {
    override var path: String? = null
    override var parent: BaseTag? = null
}

data class TagWithCount(
    @JsonProperty
    override var id: String,
    @JsonProperty
    override var type: TagType,
    @JsonProperty
    override var name: String,
    @JsonProperty
    var count: Double?
) : BaseTag {

    constructor(id: String, type: TagType, name: String, count: Int?) : this(id, type, name, count?.toDouble())

    @JsonProperty
    override var path: String? = null

    @JsonProperty
    override var parent: BaseTag? = null
}

data class ListTags(
    @JsonProperty val tags: List<Tag>
)
