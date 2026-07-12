package br.com.opendriver.ocr

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class MlKitOcrProcessor {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    suspend fun process(bitmap: Bitmap): String {
        val image = InputImage.fromBitmap(bitmap, 0)
        return suspendCancellableCoroutine { cont ->
            recognizer.process(image)
                .addOnSuccessListener { result ->
                    cont.resume(result.text)
                }
                .addOnFailureListener {
                    cont.resume("")
                }
        }
    }

    fun close() {
        recognizer.close()
    }
}
