package com.example.chatmateai

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.io.IOException

class TextGen : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var welcomeTextView: TextView
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: ImageButton
    private var messageList: MutableList<Message> = ArrayList()
    private lateinit var messageAdapter: MessageAdapter


    val apiKey = "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"


    val JSON = "application/json; charset=utf-8".toMediaType()
    val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_gen)
        messageList = ArrayList()

        recyclerView = findViewById(R.id.recycler_view)
        welcomeTextView = findViewById(R.id.welcome_text)
        messageEditText = findViewById(R.id.message_edit_text)
        sendButton = findViewById(R.id.send_btn)

        // Setup recycler view
        messageAdapter = MessageAdapter(messageList)
        recyclerView.adapter = messageAdapter

        val llm = LinearLayoutManager(this)
        llm.stackFromEnd = true
        recyclerView.layoutManager = WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        sendButton.setOnClickListener {
            val question = messageEditText.text.toString().trim()
            addToChat(question, Message.SENT_BY_ME)
            messageEditText.text.clear()
            callAPI(question)
            welcomeTextView.visibility = View.GONE
        }
    }

    fun addResponse(response: String) {
        messageList.removeAt(messageList.size - 1)
        addToChat(response, Message.SENT_BY_BOT)
    }
    

    fun callAPI(question: String) {
        // OkHttp

        messageList.add(Message("Typing... ", Message.SENT_BY_BOT))

        val jsonBody = JSONObject().apply {
            put("model", "gpt-3.5-turbo")
            put("messages", JSONArray()
                .put(JSONObject().put("role", "system").put("content", "You are a helpful assistant."))
                .put(JSONObject().put("role", "user").put("content", question))
            )

        }

        val body = jsonBody.toString().toRequestBody(JSON)
        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .header("Authorization", "Bearer $apiKey")
            .header("Content-Type", "application/json")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                // Update UI on the main thread
                runOnUiThread {
                    addResponse("Failed to load response due to ${e.message}")
                }
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.isSuccessful) {
                    try {
                        val jsonObject = JSONObject(response.body!!.string())
                        val jsonArray = jsonObject.optJSONArray("choices")

                        if (jsonArray != null && jsonArray.length() > 0) {
                            val assistantMessage = jsonArray.getJSONObject(0).optJSONObject("message")
                            val result = assistantMessage?.optString("content", "")

                            if (result != null && result.isNotEmpty()) {
                                runOnUiThread {
                                    addResponse(result.trim())
                                }
                            } else {
                                runOnUiThread {
                                    addResponse("Failed to parse response.")
                                }
                            }
                        } else {
                            runOnUiThread {
                                addResponse("Failed to parse response.")
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        runOnUiThread {
                            addResponse("Failed to parse response due to JSONException.")
                        }
                    }
                } else {
                    runOnUiThread {
                        addResponse("Failed to load response.")
                    }
                }
            }


        })
    }

    private fun addToChat(message: String, sentBy: String) {
        val newMessage = Message(message, sentBy)
        messageList.add(newMessage)
        messageAdapter.notifyItemInserted(messageList.size - 1)
        recyclerView.scrollToPosition(messageList.size - 1)
    }
}



