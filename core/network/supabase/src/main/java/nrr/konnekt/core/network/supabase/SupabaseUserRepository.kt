package nrr.konnekt.core.network.supabase

import nrr.konnekt.core.common.result.Error
import nrr.konnekt.core.common.result.Result
import nrr.konnekt.core.common.result.Success
import nrr.konnekt.core.domain.Authentication
import nrr.konnekt.core.domain.model.UserEdit
import nrr.konnekt.core.domain.repository.UserRepository
import nrr.konnekt.core.domain.repository.UserRepository.UserError
import nrr.konnekt.core.domain.repository.UserResult
import nrr.konnekt.core.model.User
import nrr.konnekt.core.network.supabase.dto.response.SupabaseUser
import nrr.konnekt.core.network.supabase.dto.response.toModel
import nrr.konnekt.core.network.supabase.util.Bucket
import nrr.konnekt.core.network.supabase.util.createPath
import nrr.konnekt.core.network.supabase.util.perform
import javax.inject.Inject

internal class SupabaseUserRepository @Inject constructor(
    authentication: Authentication,
    private val fileNameFormatter: SupabaseFileNameFormatter
) : UserRepository, SupabaseService(authentication) {
    override suspend fun getUsersByUsername(username: String): UserResult<List<User>> =
        performSuspendingAuthenticatedAction {
            try {
                val res = users {
                    select {
                        filter {
                            SupabaseUser::username like "%$username%"
                            SupabaseUser::email neq it.email
                        }
                    }
                        .decodeList<SupabaseUser>()
                        .map(SupabaseUser::toModel)
                }
                Success(res)
            } catch (e: Exception) {
                e.printStackTrace()
                Error(UserError.Unknown)
            }
        }

    override suspend fun getUserById(id: String): UserResult<User> =
        performSuspendingAuthenticatedAction {
            try {
                val res = users {
                    select {
                        filter {
                            SupabaseUser::id eq id
                        }
                    }
                        .decodeSingle<SupabaseUser>()
                        .toModel()
                }
                Success(res)
            } catch (e: Exception) {
                e.printStackTrace()
                Error(UserError.Unknown)
            }
        }

    override suspend fun updateCurrentUser(payload: UserEdit): UserResult<User> =
        performSuspendingAuthenticatedAction { user ->
            var imagePath: String? = null

            users {
                payload.profileImage?.let { profileImage ->
                    with(Bucket.ICON) {
                        val fileName = fileNameFormatter.format(
                            rawName = profileImage.fileName,
                            ext = profileImage.fileExtension
                        )
                        val path = createPath(
                            fileName = "${user.id}/$fileName",
                            rootFolder = "person"
                        )

                        try {
                            perform {
                                upload(
                                    path = path.pathInBucket,
                                    data = profileImage.content
                                )
                                imagePath = path.fullPath
                            }
                        } catch (_: Exception) {
                            return@users Result.Error(UserError.FileUploadError)
                        }
                    }
                }

                update(
                    update = {
                        SupabaseUser::username setTo payload.username
                        if (payload.bio != null) SupabaseUser::bio setTo payload.bio
                        if (imagePath != null) SupabaseUser::imagePath setTo imagePath
                    }
                ) {
                    filter {
                        SupabaseUser::id eq user.id
                    }
                    select()
                }
                    .decodeSingleOrNull<SupabaseUser>()
                    ?.let(SupabaseUser::toModel)
                    ?.let(Result<User, Nothing>::Success)
                    ?: Result.Error(UserError.Unknown)
            }
        }
}