package week11.st968323.finalproject.util

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import java.util.Locale

class SpeechToTextHelper(
    private val context: Context,
    private val onResult: (String) -> Unit,
    private val onError: (String) -> Unit,
    private val onListeningChanged: (Boolean) -> Unit
) {

    private var speechRecognizer: SpeechRecognizer? = null
    private var shouldContinueListening = false
    private val handler = Handler(Looper.getMainLooper())
    private var lastResultText = ""

    // Audio manager for muting beeps
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var originalStreamVolume = 0

    private fun muteBeep() {
        try {
            // Save original volume
            originalStreamVolume = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION)
            // Mute notification stream (where beep sound plays)
            audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0, 0)
            Log.d("STT", "Muted beep sound")
        } catch (e: Exception) {
            Log.e("STT", "Failed to mute: ${e.message}")
        }
    }

    private fun unmuteBeep() {
        try {
            // Restore original volume
            audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, originalStreamVolume, 0)
            Log.d("STT", "Unmuted beep sound")
        } catch (e: Exception) {
            Log.e("STT", "Failed to unmute: ${e.message}")
        }
    }

    private fun createRecognizerIntent(): Intent {
        return Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)

            // Long timeouts to minimize restarts
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 30000L)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 30000L)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 30000L)
        }
    }

    private val recognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            Log.d("STT", "Ready for speech")
            // Unmute briefly so user knows it's ready (only first time)
            if (!shouldContinueListening) {
                handler.postDelayed({ muteBeep() }, 100)
            }
        }

        override fun onBeginningOfSpeech() {
            Log.d("STT", "Beginning of speech")
        }

        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}

        override fun onEndOfSpeech() {
            Log.d("STT", "End of speech")
        }

        override fun onError(error: Int) {
            Log.e("STT", "Error: $error")

            if (shouldContinueListening &&
                (error == SpeechRecognizer.ERROR_NO_MATCH ||
                        error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT)) {

                Log.d("STT", "Restarting due to timeout/no match")
                handler.postDelayed({
                    if (shouldContinueListening) {
                        muteBeep() // Mute before restart
                        restartRecognizer()
                    }
                }, 500)
            } else if (error != SpeechRecognizer.ERROR_CLIENT) {
                Log.d("STT", "Ignoring error and continuing")
            }
        }

        override fun onResults(results: Bundle?) {
            Log.d("STT", "Got results")
            val texts = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            val best = texts?.firstOrNull() ?: ""

            if (best.isNotBlank() && best != lastResultText) {
                Log.d("STT", "New text: $best")
                onResult(best)
                lastResultText = best
            }

            if (shouldContinueListening) {
                Log.d("STT", "Restarting after results")
                handler.postDelayed({
                    if (shouldContinueListening) {
                        lastResultText = ""
                        muteBeep() // Mute before restart
                        restartRecognizer()
                    }
                }, 500)
            }
        }

        override fun onPartialResults(partialResults: Bundle?) {}
        override fun onEvent(eventType: Int, params: Bundle?) {}
    }

    private fun restartRecognizer() {
        if (!shouldContinueListening) return

        try {
            speechRecognizer?.startListening(createRecognizerIntent())
        } catch (e: Exception) {
            Log.e("STT", "Restart error: ${e.message}")
        }
    }

    fun startListening() {
        Log.d("STT", "START listening")
        shouldContinueListening = true
        lastResultText = ""

        // Mute beep sounds
        muteBeep()

        if (speechRecognizer == null) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context.applicationContext)
            speechRecognizer?.setRecognitionListener(recognitionListener)
        }

        onListeningChanged(true)
        speechRecognizer?.startListening(createRecognizerIntent())
    }

    fun stopListening() {
        Log.d("STT", "STOP listening")
        shouldContinueListening = false
        lastResultText = ""
        onListeningChanged(false)

        handler.removeCallbacksAndMessages(null)
        speechRecognizer?.cancel()

        // Unmute after stopping
        unmuteBeep()
    }

    fun destroy() {
        Log.d("STT", "DESTROY")
        shouldContinueListening = false
        lastResultText = ""
        handler.removeCallbacksAndMessages(null)
        speechRecognizer?.destroy()
        speechRecognizer = null

        // Unmute when destroyed
        unmuteBeep()
    }
}