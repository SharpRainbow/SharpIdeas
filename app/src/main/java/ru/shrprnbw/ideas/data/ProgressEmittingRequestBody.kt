package ru.shrprnbw.ideas.data

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.channels.ProducerScope
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink

class ProgressEmittingRequestBody(
    private val context: Context,
    private val fileUri: Uri,
    private val mediaType: String,
    private val progressChannel: ProducerScope<Float>
) : RequestBody() {

    override fun contentType(): MediaType? = mediaType.toMediaTypeOrNull()

    override fun contentLength(): Long {
        return try {
            context.contentResolver.openAssetFileDescriptor(fileUri, "r")?.use {
                it.length
            } ?: -1L
        } catch (e: Exception) {
            -1L
        }
    }

    override fun writeTo(sink: BufferedSink) {
        val inputStream = context.contentResolver.openInputStream(fileUri)
            ?: throw IllegalArgumentException("Cannot open input stream for URI: $fileUri")

        val buffer = ByteArray(BUFFER_SIZE)
        var uploaded: Long = 0
        val fileSize = contentLength()

        try {
            inputStream.use { stream ->
                while (true) {
                    val read = stream.read(buffer)
                    if (read == -1) break

                    uploaded += read
                    sink.write(buffer, 0, read)

                    val progress = if (fileSize > 0) {
                        (uploaded / fileSize.toDouble()).toFloat()
                    } else {
                        0f
                    }
                    progressChannel.trySend(progress)
                }
            }
        } catch (e: Exception) {
            progressChannel.close(e)
            throw e
        }
    }

    companion object {
        const val BUFFER_SIZE = 8192 // Increased buffer size for better performance
    }
}