package me.gracejang.scripturememorization.viewmodel

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.SpeechRecognizer.RESULTS_RECOGNITION
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData


class MainViewModel(application: Application) : AndroidViewModel(application), RecognitionListener { // TODO: What is the RecognitionListener actually for and why does the ViewModel / Activity have to implement the interface vs just creating an anonymous object implementing the interface?

    private val TAG = "MainViewModel"

    // SpeechRecognizer that will drive speech to text
    private val speechRecognizer: SpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(application.applicationContext).apply {
        setRecognitionListener(this@MainViewModel)
    }

    val result = MutableLiveData<String>()

    // An intent to be reused to send to drive speech to text
    private val recognizerIntent: Intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        // TODO: What do each of these extras provide for the SpeechRecognizer
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, application.packageName)
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
    }

    // Listen for user to speak
    fun startListening() {
        speechRecognizer.startListening(recognizerIntent)
        Log.d(TAG, "startListening: started")
    }

    fun stopListening() {
        speechRecognizer.stopListening()
    }

    override fun onPartialResults(partialResults: Bundle?) {
        // TODO
        // Gets partial hypothesis of speech to text?
        val results = partialResults?.getStringArrayList(RESULTS_RECOGNITION)
        Log.d(TAG, "partialResult: ${results.toString()}")
    }

    override fun onResults(results: Bundle?) {
        // TODO
        val resultsArray = results?.getStringArrayList(RESULTS_RECOGNITION)
        val resultString = resultsArray?.get(0) ?: ""
        Log.d(TAG, "onResults: ${resultString}")
        result.value = resultString
    }

    // Called when SpeechRecognizer believes user stopped speaking and auto stops listening (? Confirm it stops listening)
    override fun onEndOfSpeech() {
        // TODO
        Log.d(TAG, "onEndOfSpeech: started");
    }

    override fun onError(error: Int) {
        // TODO
    }

    // Unused overrides
    override fun onReadyForSpeech(params: Bundle?) {
        // TODO
    }

    override fun onRmsChanged(rmsdB: Float) {
        // TODO
    }

    override fun onBufferReceived(buffer: ByteArray?) {
        // TODO
    }

    override fun onEvent(eventType: Int, params: Bundle?) {
        // TODO
    }

    override fun onBeginningOfSpeech() {
        // TODO
    }
}