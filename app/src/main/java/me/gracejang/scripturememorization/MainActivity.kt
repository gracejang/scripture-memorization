package me.gracejang.scripturememorization

import android.Manifest
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_main.*
import me.gracejang.scripturememorization.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    private val REQUEST_RECORD_AUDIO_PERMISSION = 1

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        // Check and request mic permission if necessary
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_RECORD_AUDIO_PERMISSION)

        micButton.setOnClickListener {
            scripture.visibility = View.INVISIBLE
            micButton.isEnabled = false
            viewModel.startListening()
        }

        // Test
        Log.d(TAG, "wordIncies: ${wordIndices("hello, world")}");

        // Observer speech to text result and display it
        viewModel.result.observe(this, Observer {
            val scriptureStr = scripture.text as String
            result.text = it
            calculateIndices(scriptureStr.toLowerCase(), it.toLowerCase())
            val indices = calculateIndices(scriptureStr, it)
            highlight(indices, it)
            scripture.visibility = View.VISIBLE
            micButton.isEnabled = true
        })

    }

    private fun calculateIndices(expected: String, actual: String): ArrayList<Pair<Int, Int>> {
        val expected_words = expected.split(" ")
        val actual_words = actual.split(" ")
        val actualIndices = wordIndices(actual)

        val map: MutableMap<String, Int> = mutableMapOf<String, Int>()
        expected_words.forEach {
            val word = it
            map.get(word)?.let {
                map[word] = it + 1
            }

            if (map[word] == null) map[word] = 1
        }

        val result = arrayListOf<Pair<Int, Int>>()
        actual_words.forEachIndexed { index, word ->
            val i = index
            if (map[word] != null) {
                result.add(actualIndices[i])
                map[word] = map[word]!!.minus(1)
                if (map[word] == 0) map.remove(word)
            }
        }
        Log.d(TAG, "result: $result");
        return result

    }

    private fun wordIndices(str: String): List<Pair<Int, Int>> {
        val words = str.split(" ")
        var wordIndex = 0
        val result = arrayListOf<Pair<Int, Int>>()
        var i = 0
        while (i < str.length) {
            if (str[i] == ' ') i += 1
            val lengthCurrWord = words[wordIndex].length
            result.add(Pair(i, i + lengthCurrWord))
            i += lengthCurrWord
            wordIndex += 1
        }
        return result
    }

    private fun highlight(indices: List<Pair<Int, Int>>, sentence: String) {
        Log.d(TAG, "started: ");
        val spannableString = SpannableString(sentence).apply {
            indices.forEach {
                val start = it.first
                val end = it.second
                setSpan(ForegroundColorSpan(getColor(R.color.blueapp)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }

        result.text = spannableString
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>?, grantResults: IntArray?) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
//            viewModel.startListening() // TODO: Do we need to start after granting request?
        }
    }
}
