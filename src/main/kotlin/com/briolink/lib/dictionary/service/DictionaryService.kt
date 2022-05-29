package com.briolink.lib.dictionary.service

import com.briolink.lib.dictionary.enumeration.AccessObjectTypeEnum
import com.briolink.lib.dictionary.enumeration.PermissionRoleEnum
import com.briolink.lib.dictionary.exception.exist.PermissionRoleExistException
import com.briolink.lib.dictionary.exception.notfound.PermissionRightNotFoundException
import com.briolink.lib.dictionary.exception.notfound.UserPermissionRoleNotFoundException
import com.briolink.lib.dictionary.model.PermissionRight
import com.briolink.lib.dictionary.model.UserPermissionRights
import com.briolink.lib.dictionary.model.UserPermissionRole
import java.util.UUID

open class DictionaryService(private val webClient: WebClientDictionaryService) {
    open fun getPermissionRole(
        userId: UUID,
        accessObjectId: UUID,
        accessObjectType: String
    ): PermissionRoleEnum? {
        return try {
            webClient.getPermissionRole(
                userId = userId,
                accessObjectId = accessObjectId,
                accessObjectType = accessObjectType
            ).block()?.let {
                PermissionRoleEnum.ofId(it.role.id)
            }
        } catch (ex: UserPermissionRoleNotFoundException) {
            null
        }
    }

    open fun setPermissionRights(
        userId: UUID,
        accessObjectId: UUID,
        accessObjectType: String,
        permissionRole: String,
        permissionRights: List<String>
    ): UserPermissionRights? {

        return webClient.setPermissionRights(
            userId = userId,
            accessObjectId = accessObjectId,
            accessObjectType = accessObjectType,
            permissionRole = permissionRole,
            permissionRights = permissionRights
        ).block()?.let {
            UserPermissionRights(
                permissionRole = it.userRole,
                permissionRights = it.rights,
            )
        }
    }

    open fun getUserPermissionRights(
        userId: UUID,
        accessObjectId: UUID,
        accessObjectType: AccessObjectTypeEnum
    ): UserPermissionRights? {
        return try {
            webClient.getUserPermissionRights(
                userId = userId,
                accessObjectId = accessObjectId,
                accessObjectType = accessObjectType
            ).block()?.let {
                UserPermissionRights(
                    permissionRole = it.userRole,
                    permissionRights = it.rights,
                )
            }
        } catch (ex: PermissionRightNotFoundException) {
            null
        }
    }

    open fun checkPermission(
        userId: UUID,
        accessObjectId: UUID,
        right: PermissionRight
    ): Boolean {
        return webClient.checkPermission(
            userId = userId,
            accessObjectId = accessObjectId,
            right = right
        ).block() ?: false
    }

    open fun checkPermission(
        userId: UUID,
        accessObjectId: UUID,
        right: String
    ): Boolean {
        return checkPermission(userId, accessObjectId, PermissionRight.fromString(right))
    }

    @Throws(PermissionRoleExistException::class)
    open fun createPermissionRole(
        userId: UUID,
        accessObjectType: AccessObjectTypeEnum,
        accessObjectId: UUID,
        permissionRole: PermissionRoleEnum
    ): UserPermissionRole? {
        return webClient.createPermissionRole(
            userId = userId,
            accessObjectType = accessObjectType,
            accessObjectId = accessObjectId,
            permissionRole = permissionRole

        ).block()?.let { UserPermissionRole.fromDto(it) }
    }

    open fun editPermissionRole(
        userId: UUID,
        accessObjectType: AccessObjectTypeEnum,
        accessObjectId: UUID,
        permissionRole: PermissionRoleEnum
    ): UserPermissionRole? {
        return webClient.editPermissionRole(
            userId = userId,
            accessObjectType = accessObjectType,
            accessObjectId = accessObjectId,
            permissionRole = permissionRole
        ).block()?.let { UserPermissionRole.fromDto(it) }
    }

    open fun deletePermissionRole(
        userId: UUID,
        accessObjectType: AccessObjectTypeEnum,
        accessObjectId: UUID,
    ): Boolean {
        return webClient.deletePermissionRole(
            userId = userId,
            accessObjectType = accessObjectType,
            accessObjectId = accessObjectId
        ).block() ?: false
    }
}
