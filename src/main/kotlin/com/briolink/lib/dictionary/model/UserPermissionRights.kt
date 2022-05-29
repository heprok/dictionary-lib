package com.briolink.lib.dictionary.model

import com.briolink.lib.dictionary.enumeration.PermissionRoleEnum
import com.fasterxml.jackson.annotation.JsonProperty

data class UserPermissionRights(
    @JsonProperty
    val permissionRole: PermissionRoleEnum,
    @JsonProperty
    val permissionRights: List<PermissionRight>,
)
