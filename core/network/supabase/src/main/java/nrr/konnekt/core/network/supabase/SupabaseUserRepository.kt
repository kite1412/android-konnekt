package nrr.konnekt.core.network.supabase

import nrr.konnekt.core.domain.Authentication
import nrr.konnekt.core.domain.model.UserEdit
import nrr.konnekt.core.domain.repository.UserRepository
import nrr.konnekt.core.domain.repository.UserRepository.UserError
import nrr.konnekt.core.domain.repository.UserResult
import nrr.konnekt.core.domain.util.Error
import nrr.konnekt.core.domain.util.Result
import nrr.konnekt.core.domain.util.Success
import nrr.konnekt.core.model.User
import nrr.konnekt.core.network.supabase.dto.response.SupabaseUser
import nrr.konnekt.core.network.supabase.dto.response.toUser
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
                            User::username like "%$username%"
                            User::email neq it.email
                        }
                    }.decodeList<User>()
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
                            User::id eq id
                        }
                    }.decodeSingle<User>()
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
                            fileName = fileName,
                            rootFolder = user.id
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
                        SupabaseUser::bio setTo payload.bio
                        SupabaseUser::imagePath setTo imagePath
                    }
                ) {
                    select()
                }
                    .decodeSingleOrNull<SupabaseUser>()
                    ?.let(SupabaseUser::toUser)
                    ?.let(Result<User, Nothing>::Success)
                    ?: Result.Error(UserError.Unknown)
            }
        }
}