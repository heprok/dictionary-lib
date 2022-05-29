package com.briolink.lib.dictionary.model

import com.briolink.lib.dictionary.exception.ParseFailedPermissionRightException

data class PermissionRight(
    val accessObjectType: String,
    val action: String,
) {

    override fun toString(): String {
        return "$action@$accessObjectType"
    }
    companion object {
        fun fromString(value: String): PermissionRight {
            val v = value.split("@")
            if (v.count() != 2) throw ParseFailedPermissionRightException(value)

            return PermissionRight(v[1], v[0])
        }
    }
}
