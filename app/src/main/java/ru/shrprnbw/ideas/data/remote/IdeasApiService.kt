package ru.shrprnbw.ideas.data.remote

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import ru.shrprnbw.ideas.data.remote.dto.request.AddAttachmentRequest
import ru.shrprnbw.ideas.data.remote.dto.request.AddCollaboratorRequest
import ru.shrprnbw.ideas.data.remote.dto.request.AddNoteTextRequest
import ru.shrprnbw.ideas.data.remote.dto.request.AddNoteToGroupRequest
import ru.shrprnbw.ideas.data.remote.dto.request.AddTaskAssigneeRequest
import ru.shrprnbw.ideas.data.remote.dto.request.AddUserToGroupRequest
import ru.shrprnbw.ideas.data.remote.dto.request.CreateColumnRequest
import ru.shrprnbw.ideas.data.remote.dto.request.CreateGroupRequest
import ru.shrprnbw.ideas.data.remote.dto.request.CreateLabelRequest
import ru.shrprnbw.ideas.data.remote.dto.request.CreateNoteRequest
import ru.shrprnbw.ideas.data.remote.dto.request.CreateTagRequest
import ru.shrprnbw.ideas.data.remote.dto.request.CreateTaskRequest
import ru.shrprnbw.ideas.data.remote.dto.request.LoginRequestDto
import ru.shrprnbw.ideas.data.remote.dto.request.MoveTaskRequest
import ru.shrprnbw.ideas.data.remote.dto.request.RefreshRequest
import ru.shrprnbw.ideas.data.remote.dto.request.RegisterRequest
import ru.shrprnbw.ideas.data.remote.dto.request.RemoveNoteFromGroupRequest
import ru.shrprnbw.ideas.data.remote.dto.request.ReorderColumnsRequest
import ru.shrprnbw.ideas.data.remote.dto.request.ReorderTasksRequest
import ru.shrprnbw.ideas.data.remote.dto.request.TokenExchangeRequest
import ru.shrprnbw.ideas.data.remote.dto.request.UpdateColumnRequest
import ru.shrprnbw.ideas.data.remote.dto.request.UpdateGroupRequest
import ru.shrprnbw.ideas.data.remote.dto.request.UpdateNoteRequest
import ru.shrprnbw.ideas.data.remote.dto.request.UpdateNoteTextBatchRequest
import ru.shrprnbw.ideas.data.remote.dto.request.UpdatePersonalInfoRequest
import ru.shrprnbw.ideas.data.remote.dto.request.UpdateTaskRequest
import ru.shrprnbw.ideas.data.remote.dto.response.BoardColumnResponseDto
import ru.shrprnbw.ideas.data.remote.dto.response.GroupDto
import ru.shrprnbw.ideas.data.remote.dto.response.LoginResponseDto
import ru.shrprnbw.ideas.data.remote.dto.response.NoteDto
import ru.shrprnbw.ideas.data.remote.dto.response.NoteKeywordDto
import ru.shrprnbw.ideas.data.remote.dto.response.NotePreviewDto
import ru.shrprnbw.ideas.data.remote.dto.response.NoteSummaryDto
import ru.shrprnbw.ideas.data.remote.dto.response.PagedResponse
import ru.shrprnbw.ideas.data.remote.dto.response.StatusResponse
import ru.shrprnbw.ideas.data.remote.dto.response.TagDto
import ru.shrprnbw.ideas.data.remote.dto.response.TaskAttachmentDto
import ru.shrprnbw.ideas.data.remote.dto.response.TaskDetailResponseDto
import ru.shrprnbw.ideas.data.remote.dto.response.TaskLabelDto
import ru.shrprnbw.ideas.data.remote.dto.response.TaskResponseDto
import ru.shrprnbw.ideas.data.remote.dto.response.TaskSummaryResponseDto
import ru.shrprnbw.ideas.data.remote.dto.response.TokenResponseDto
import ru.shrprnbw.ideas.data.remote.dto.response.TranscriptionDto
import ru.shrprnbw.ideas.data.remote.dto.response.UserDto
import ru.shrprnbw.ideas.domain.entity.NoteType

interface IdeasApiService {

    companion object {
        const val VERSION_1 = "api/v1"
        const val VERSION_2 = "api/v2"
        const val VERSION_3 = "api/v3"
    }

    @GET("readyz")
    suspend fun checkHealth(): Response<StatusResponse>

    @POST("$VERSION_1/users/login")
    suspend fun login(
        @Body request: LoginRequestDto
    ): LoginResponseDto

    @POST("$VERSION_1/users/register")
    suspend fun register(
        @Body request: RegisterRequest
    )

    @GET("$VERSION_1/users/personal_info")
    suspend fun getUserInfo(
        @Header("Authorization") token: String,
    ): UserDto

    @PATCH("$VERSION_1/users/personal_info")
    suspend fun updateUserInfo(
        @Header("Authorization") token: String,
        @Body request: UpdatePersonalInfoRequest
    )

    @GET("$VERSION_2/tags")
    suspend fun getUserCreatedTags(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") limit: Int,
    ): PagedResponse<TagDto>

    @GET("$VERSION_1/notes/personal")
    suspend fun getUserNotes(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") limit: Int,
    ): PagedResponse<NotePreviewDto>

    @GET("$VERSION_1/notes/shared")
    suspend fun getSharedNotes(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") limit: Int,
    ): PagedResponse<NotePreviewDto>

    @GET("$VERSION_1/notes/search")
    suspend fun searchNotes(
        @Header("Authorization") token: String,
        @Query("globalSearch") globalSearch: Boolean,
        @Query("title") title: String?,
        @Query("content") content: String?,
        @Query("tagIds") tagIds: List<Long>?,
        @Query("noteType") noteType: NoteType?,
        @Query("page") page: Int,
        @Query("size") limit: Int,
    ): PagedResponse<NotePreviewDto>

    @GET("$VERSION_1/notes/groups/{groupId}")
    suspend fun getGroupNotes(
        @Header("Authorization") token: String,
        @Path("groupId") groupId: Long,
        @Query("page") page: Int,
        @Query("size") limit: Int
    ): PagedResponse<NotePreviewDto>

    @POST("$VERSION_2/auth/login-password")
    suspend fun loginV2(
        @Body request: LoginRequestDto
    ): TokenResponseDto

    @POST("$VERSION_2/auth/refresh")
    suspend fun refreshAccessToken(
        @Body request: RefreshRequest
    ): TokenResponseDto

    @POST("$VERSION_2/auth/exchange/{provider}")
    suspend fun loginWithProvider(
        @Body request: TokenExchangeRequest,
        @Path("provider") provider: String
    ): TokenResponseDto

    @POST("$VERSION_1/notes")
    suspend fun createNote(
        @Header("Authorization") token: String,
        @Body createRequest: CreateNoteRequest
    )

    @DELETE("$VERSION_1/notes/{noteId}")
    suspend fun deleteNote(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String
    )

    @POST("$VERSION_1/notes/{noteId}/contents/text")
    suspend fun addNoteText(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String,
        @Body textRequest: AddNoteTextRequest
    )

    @GET("$VERSION_1/notes/{noteId}")
    suspend fun getNoteDetail(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String
    ): NoteDto

    @PATCH("$VERSION_1/notes/{noteId}")
    suspend fun updateNote(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String,
        @Body updateRequest: UpdateNoteRequest
    )

    @PATCH("$VERSION_1/notes/{noteId}/contents/text")
    suspend fun updateNoteText(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String,
        @Body updateRequest: UpdateNoteTextBatchRequest
    )

    @Multipart
    @POST("$VERSION_1/notes/{noteId}/contents/image")
    suspend fun uploadNoteImage(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String,
        @Part file: MultipartBody.Part
    )

    @Multipart
    @POST("$VERSION_1/notes/{noteId}/contents/audio")
    suspend fun uploadNoteAudio(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String,
        @Part file: MultipartBody.Part
    )

    @DELETE("$VERSION_1/notes/{noteId}/contents/{contentId}")
    suspend fun deleteNoteContent(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String,
        @Path("contentId") contentId: Long
    )

    @GET("$VERSION_1/notes/{noteId}/transcriptions")
    suspend fun getTranscriptions(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String,
        @Query("page") page: Int,
        @Query("size") limit: Int
    ): PagedResponse<TranscriptionDto>

    @GET("$VERSION_1/notes/{noteId}/transcriptions/{transcriptionId}")
    suspend fun getTranscription(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String,
        @Path("transcriptionId") transcriptionId: String
    ): TranscriptionDto

    @POST("$VERSION_1/notes/{noteId}/transcriptions")
    suspend fun generateNoteTranscription(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String
    )

    @DELETE("$VERSION_1/notes/{noteId}/transcriptions/{transcriptionId}")
    suspend fun deleteTranscription(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String,
        @Path("transcriptionId") transcriptionId: Long
    )

    @GET("$VERSION_1/notes/{noteId}/keywords")
    suspend fun getKeywords(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String,
        @Query("page") page: Int,
        @Query("size") limit: Int
    ): PagedResponse<NoteKeywordDto>

    @POST("$VERSION_1/notes/{noteId}/keywords")
    suspend fun generateNoteKeywords(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String
    )

    @DELETE("$VERSION_1/notes/{noteId}/keywords/{keywordId}")
    suspend fun deleteKeywords(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String,
        @Path("keywordId") keywordId: Long,
    )

    @GET("$VERSION_1/notes/{noteId}/summaries")
    suspend fun getSummaries(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String,
        @Query("page") page: Int,
        @Query("size") limit: Int
    ): PagedResponse<NoteSummaryDto>

    @GET("$VERSION_1/notes/{noteId}/summaries/{summaryId}")
    suspend fun getSummary(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String,
        @Path("summaryId") summaryId: String
    ): NoteSummaryDto

    @DELETE("$VERSION_1/notes/{noteId}/summaries/{summaryId}")
    suspend fun deleteSummary(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String,
        @Path("summaryId") summaryId: Long
    )

    @POST("$VERSION_1/notes/{noteId}/summaries")
    suspend fun generateNoteSummary(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String
    )

    @POST("$VERSION_1/notes/{noteId}/tags")
    suspend fun addNoteTag(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String,
        @Query("tag") tag: String
    )

    @DELETE("$VERSION_1/notes/{noteId}/tags")
    suspend fun removeNoteTag(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String,
        @Query("tag") tag: String
    )

    @POST("$VERSION_2/tags")
    suspend fun createTag(
        @Header("Authorization") token: String,
        @Body request: CreateTagRequest
    )

    @PATCH("$VERSION_2/tags/{tagId}")
    suspend fun updateTag(
        @Header("Authorization") token: String,
        @Path("tagId") tagId: Long,
        @Body request: CreateTagRequest
    )

    @DELETE("$VERSION_2/tags/{tagId}")
    suspend fun deleteTag(
        @Header("Authorization") token: String,
        @Path("tagId") tagId: Long
    )

    @GET("$VERSION_1/groups/owned")
    suspend fun getOwnedGroups(
        @Header("Authorization") token: String
    ): List<GroupDto>

    @POST("$VERSION_1/groups")
    suspend fun createGroup(
        @Header("Authorization") token: String,
        @Body request: CreateGroupRequest
    )

    @PATCH("$VERSION_1/groups/{groupId}")
    suspend fun updateGroup(
        @Header("Authorization") token: String,
        @Path("groupId") groupId: Long,
        @Body request: UpdateGroupRequest
    )

    @DELETE("$VERSION_1/groups/{groupId}")
    suspend fun deleteGroup(
        @Header("Authorization") token: String,
        @Path("groupId") groupId: Long
    )

    @GET("$VERSION_1/groups/{groupId}/users")
    suspend fun getGroupUsers(
        @Header("Authorization") token: String,
        @Path("groupId") groupId: Long,
        @Query("page") page: Int,
        @Query("size") limit: Int
    ): PagedResponse<UserDto>

    @POST("$VERSION_1/groups/{groupId}/users")
    suspend fun addUserToGroup(
        @Header("Authorization") token: String,
        @Path("groupId") groupId: Long,
        @Body request: AddUserToGroupRequest
    )

    @HTTP(method = "DELETE", path = "$VERSION_1/groups/{groupId}/users", hasBody = true)
    suspend fun removeUserFromGroup(
        @Header("Authorization") token: String,
        @Path("groupId") groupId: Long,
        @Body request: AddUserToGroupRequest
    )

    // Note Sharing API

    @POST("$VERSION_1/notes/{noteId}/collaborators")
    suspend fun addNoteCollaborator(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String,
        @Body request: AddCollaboratorRequest
    )

    @DELETE("$VERSION_1/notes/{noteId}/collaborators/{collaboratorId}")
    suspend fun removeNoteCollaborator(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String,
        @Path("collaboratorId") collaboratorId: String
    )

    @POST("$VERSION_1/notes/{noteId}/groups")
    suspend fun addNoteToGroup(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String,
        @Body request: AddNoteToGroupRequest
    )

    @HTTP(method = "DELETE", path = "$VERSION_1/notes/{noteId}/groups", hasBody = true)
    suspend fun removeNoteFromGroup(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String,
        @Body request: RemoveNoteFromGroupRequest
    )

    // Board API (v3)

    @GET("$VERSION_3/board/my-tasks")
    suspend fun getMyTasks(
        @Header("Authorization") token: String
    ): List<TaskSummaryResponseDto>

    // Board Columns

    @POST("$VERSION_3/notes/{noteId}/board/columns")
    suspend fun createColumn(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String,
        @Body request: CreateColumnRequest
    ): BoardColumnResponseDto

    @PATCH("$VERSION_3/notes/{noteId}/board/columns/{columnId}")
    suspend fun updateColumn(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String,
        @Path("columnId") columnId: Long,
        @Body request: UpdateColumnRequest
    ): BoardColumnResponseDto

    @DELETE("$VERSION_3/notes/{noteId}/board/columns/{columnId}")
    suspend fun deleteColumn(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String,
        @Path("columnId") columnId: Long
    )

    @PATCH("$VERSION_3/notes/{noteId}/board/columns/reorder")
    suspend fun reorderColumns(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String,
        @Body request: ReorderColumnsRequest
    )

    // Board Tasks

    @POST("$VERSION_3/notes/{noteId}/board/columns/{columnId}/tasks")
    suspend fun createTask(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String,
        @Path("columnId") columnId: Long,
        @Body request: CreateTaskRequest
    ): TaskResponseDto

    @GET("$VERSION_3/notes/{noteId}/board/tasks/{taskId}")
    suspend fun getTaskDetail(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String,
        @Path("taskId") taskId: String
    ): TaskDetailResponseDto

    @PATCH("$VERSION_3/notes/{noteId}/board/tasks/{taskId}")
    suspend fun updateTask(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String,
        @Path("taskId") taskId: String,
        @Body request: UpdateTaskRequest
    ): TaskResponseDto

    @DELETE("$VERSION_3/notes/{noteId}/board/tasks/{taskId}")
    suspend fun deleteTask(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String,
        @Path("taskId") taskId: String
    )

    @PATCH("$VERSION_3/notes/{noteId}/board/tasks/{taskId}/move")
    suspend fun moveTask(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String,
        @Path("taskId") taskId: String,
        @Body request: MoveTaskRequest
    ): TaskResponseDto

    @PATCH("$VERSION_3/notes/{noteId}/board/tasks/{taskId}/complete")
    suspend fun completeTask(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String,
        @Path("taskId") taskId: String
    ): TaskResponseDto

    @PATCH("$VERSION_3/notes/{noteId}/board/tasks/{taskId}/reopen")
    suspend fun reopenTask(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String,
        @Path("taskId") taskId: String
    ): TaskResponseDto

    @PATCH("$VERSION_3/notes/{noteId}/board/columns/{columnId}/tasks/reorder")
    suspend fun reorderTasks(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String,
        @Path("columnId") columnId: Long,
        @Body request: ReorderTasksRequest
    )

    // Board Labels

    @GET("$VERSION_3/notes/{noteId}/board/labels")
    suspend fun getBoardLabels(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String
    ): List<TaskLabelDto>

    @POST("$VERSION_3/notes/{noteId}/board/labels")
    suspend fun createLabel(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String,
        @Body request: CreateLabelRequest
    ): TaskLabelDto

    @DELETE("$VERSION_3/notes/{noteId}/board/labels/{labelId}")
    suspend fun deleteLabel(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String,
        @Path("labelId") labelId: Long
    )

    @POST("$VERSION_3/notes/{noteId}/board/tasks/{taskId}/labels/{labelId}")
    suspend fun addLabelToTask(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String,
        @Path("taskId") taskId: String,
        @Path("labelId") labelId: Long
    )

    @DELETE("$VERSION_3/notes/{noteId}/board/tasks/{taskId}/labels/{labelId}")
    suspend fun removeLabelFromTask(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String,
        @Path("taskId") taskId: String,
        @Path("labelId") labelId: Long
    )

    // Board Assignees

    @GET("$VERSION_3/notes/{noteId}/board/assignees")
    suspend fun getBoardAssignees(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String
    ): List<UserDto>

    @POST("$VERSION_3/notes/{noteId}/board/tasks/{taskId}/assignees")
    suspend fun addTaskAssignee(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String,
        @Path("taskId") taskId: String,
        @Body request: AddTaskAssigneeRequest
    )

    @DELETE("$VERSION_3/notes/{noteId}/board/tasks/{taskId}/assignees/{userId}")
    suspend fun removeTaskAssignee(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String,
        @Path("taskId") taskId: String,
        @Path("userId") userId: String
    )

    // Board Task Attachments

    @POST("$VERSION_3/notes/{noteId}/board/tasks/{taskId}/attachments")
    suspend fun addTaskAttachment(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String,
        @Path("taskId") taskId: String,
        @Body request: AddAttachmentRequest
    ): TaskAttachmentDto

    @DELETE("$VERSION_3/notes/{noteId}/board/tasks/{taskId}/attachments/{attachmentId}")
    suspend fun deleteTaskAttachment(
        @Header("Authorization") token: String,
        @Path("noteId") noteId: String,
        @Path("taskId") taskId: String,
        @Path("attachmentId") attachmentId: Long
    )

}