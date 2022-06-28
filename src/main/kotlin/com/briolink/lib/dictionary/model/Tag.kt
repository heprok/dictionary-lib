package com.briolink.lib.dictionary.model

import com.briolink.lib.dictionary.enumeration.TagType
import com.fasterxml.jackson.annotation.JsonProperty

// interface BaseTag {
//    var id: String
//    var type: TagType
//    var name: String
//    var path: String?
//    var parent: BaseTag?
// }

// data class Tag(
//    @JsonProperty
//    override var id: String,
//    @JsonProperty
//    override var type: TagType,
//    @JsonProperty
//    override var name: String
// ) : BaseTag {
//    override var path: String? = null
//    override var parent: Tag? = null
// }
data class Tag(
    @JsonProperty
    var id: TagId,
    @JsonProperty
    var name: String
) {
    @JsonProperty
    var path: String? = null

    @JsonProperty
    var parent: Tag? = null

    constructor(id: String, type: TagType, name: String, path: String? = null, parent: Tag? = null) :
        this(TagId(id, type), name) {

        this.path = path
        this.parent = parent
    }
}

data class TagId(
    @JsonProperty
    var id: String,
    @JsonProperty
    var type: TagType
) {
    override fun toString(): String {
        return "${type.name};$id"
    }

    companion object {
        fun fromString(s: String): TagId {
            val parts = s.split(";")
            if (parts.size != 2) {
                throw IllegalArgumentException("Invalid tag id: $s")
            }
            return TagId(parts[1], TagType.valueOf(parts[0]))
        }
    }
}

// data class TagWithCount(
//    @JsonProperty
//    override var id: String,
//    @JsonProperty
//    override var type: TagType,
//    @JsonProperty
//    override var name: String,
//    @JsonProperty
//    var count: Double?
// ) : BaseTag {
//
//    constructor(id: String, type: TagType, name: String, count: Int?) : this(id, type, name, count?.toDouble())
//
//    @JsonProperty
//    override var path: String? = null
//
//    @JsonProperty
//    override var parent: BaseTag? = null
// }

data class ListTags(
    @JsonProperty val tags: List<Tag>
)
