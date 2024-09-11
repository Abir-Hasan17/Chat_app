package com.learning.chat_app.model

data class Massage(
    val id: String = "",
    val senderId: String = "",
    //val receiverId: String = "",
    val massage: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val senderName: String = "",
    val senderImage: String? = null,
    val imageUrl: String? = null
)