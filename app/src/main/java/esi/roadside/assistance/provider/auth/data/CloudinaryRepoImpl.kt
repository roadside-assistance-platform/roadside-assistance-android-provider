package esi.roadside.assistance.provider.auth.data

import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import esi.roadside.assistance.provider.auth.domain.repository.CloudinaryRepo

class CloudinaryRepoImpl(
    private val mediaManager: MediaManager
): CloudinaryRepo {
    override fun uploadImage(
        image: Uri,
        onSuccess: (String) -> Unit,
        onProgress: (Float) -> Unit,
        onFailure: (ErrorInfo?) -> Unit,
        onFinished: () -> Unit,
    ) {
        if (image == Uri.EMPTY) {
            onSuccess("")
            onFinished()
            return
        }
        mediaManager
            .upload(image)
            .unsigned("MyPreset")
            .callback(object : UploadCallback {
                override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                    onSuccess(resultData?.get("secure_url") as String)
                    onFinished()
                }

                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                    onProgress(bytes.toFloat() / totalBytes.toFloat())
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    onFailure(error)
                    onFinished()
                }

                override fun onStart(requestId: String?) {
                }
            }).dispatch()
    }
}