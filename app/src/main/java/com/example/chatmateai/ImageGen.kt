package com.example.chatmateai

import android.content.Context
import android.content.Context.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class ImageGen : AppCompatActivity() {

    private lateinit var textInputLayout: TextInputLayout
    private lateinit var inputText: TextInputEditText
    private lateinit var generateButton: MaterialButton
    private lateinit var progressBar: ProgressBar
    private lateinit var lottieAnimationView: LottieAnimationView
    private lateinit var imageView: ImageView

    val apiKey = ApiKey.API_KEY
    private val JSON = "application/json; charset=utf-8".toMediaType()
    private val client = OkHttpClient()

    companion object {
        val JSON: MediaType = "application/json; charset=utf-8".toMediaType()
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_gen)

        textInputLayout = findViewById(R.id.text_input_layout)
        inputText = findViewById(R.id.input_text)
        generateButton = findViewById(R.id.generate_btn)
        progressBar = findViewById(R.id.progress_bar)
        lottieAnimationView = findViewById(R.id.lottie_animation_view)
        imageView = findViewById(R.id.image_view)

        lottieAnimationView.cancelAnimation()
        lottieAnimationView.pauseAnimation()

        generateButton.setOnClickListener {
            hideKeyboard()
            val searchText = inputText.text.toString().trim()
            if (searchText.isNotEmpty()) {
                searchImage(searchText)
            } else {
                textInputLayout.error = "Please enter text"
            }
        }

    }

    private fun hideKeyboard() {
        val view: View? = currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }


    private fun searchImage(searchText: String) {
        // Reset UI
        textInputLayout.error = null
        progressBar.visibility = View.VISIBLE
        lottieAnimationView.visibility = View.VISIBLE
        lottieAnimationView.playAnimation()
        imageView.visibility = View.GONE

        // Create JSON request body
        val jsonBody = JSONObject().apply {
            put("prompt", searchText)
            put("size","256x256")
        }

        Log.d("QWERTY",jsonBody.toString())


        val body = jsonBody.toString().toRequestBody(JSON)

        // Create HTTP request
        val request = Request.Builder()
            .url("https://api.openai.com/v1/images/generations")  // Replace with the correct API endpoint
            .header("Authorization", "Bearer $apiKey")
            .post(body)
            .build()

        // Make the request
        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                runOnUiThread {
                    handleError("Failed to load response due to ${e.message}")
                }
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                val responseBody = response.body?.string()
                try {
                    val jsonObject = JSONObject(responseBody)
                    val imageUrl = jsonObject.getJSONArray("data").getJSONObject(0).getString("url")

                    runOnUiThread {
                        handleImageResult(imageUrl)
                    }
                } catch (e: JSONException) {
                    runOnUiThread {
                        handleError("Failed to parse response: $e")
                    }
                }
            }
        })
    }

    private fun handleImageResult(imageUrl: String) {
        lottieAnimationView.pauseAnimation()
        lottieAnimationView.visibility = View.GONE
        progressBar.visibility = View.GONE
        imageView.visibility = View.VISIBLE


        // Load image using Glide
        Glide.with(this)
            .load(imageUrl)
            .apply(
                RequestOptions()
            )
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(imageView)

    }

    private fun handleError(message: String) {
        lottieAnimationView.pauseAnimation()
        lottieAnimationView.visibility = View.GONE
        progressBar.visibility = View.GONE
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}