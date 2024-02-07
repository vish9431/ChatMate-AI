package com.example.chatmateai

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MessageAdapter(private val messageList: List<Message>) :
    RecyclerView.Adapter<MessageAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val chatView =
            LayoutInflater.from(parent.context).inflate(R.layout.chat_item, parent, false)
        return MyViewHolder(chatView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val message = messageList[position]
        if (message.content == Message.SENT_BY_ME) {
            holder.leftChatView.visibility = View.GONE
            holder.rightChatView.visibility = View.VISIBLE
            holder.rightTextView.text = message.role
        } else {
            holder.rightChatView.visibility = View.GONE
            holder.leftChatView.visibility = View.VISIBLE
            holder.leftTextView.text = message.role
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var leftChatView: LinearLayout = itemView.findViewById(R.id.left_chat_view)
        var rightChatView: LinearLayout = itemView.findViewById(R.id.right_chat_view)
        var leftTextView: TextView = itemView.findViewById(R.id.left_chat_text_view)
        var rightTextView: TextView = itemView.findViewById(R.id.right_chat_text_view)
    }
}