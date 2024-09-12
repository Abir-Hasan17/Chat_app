package com.learning.chat_app.feature.auth.chat

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.learning.chat_app.model.Massage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor() : ViewModel() {

    private val _massages = MutableStateFlow<List<Massage>>(emptyList())
    val massages = _massages.asStateFlow()
    private val db = Firebase.database


    fun sendMassage(channelId: String, massageText: String?, image: String? = null){
        val massageToSend = Massage(
            db.reference.push().key ?: UUID.randomUUID().toString(),
            Firebase.auth.currentUser?.uid ?: "",
            massageText,
            System.currentTimeMillis(),
            Firebase.auth.currentUser?.displayName ?: "",
            null,
            image
        )
        db.reference.child("massages").child(channelId).push().setValue(massageToSend)
    }

    fun sendImage(channelId: String, imageUri: Uri){
        val imageRef = Firebase.storage.reference.child("images/${UUID.randomUUID()}")
        imageRef.putFile(imageUri).continueWithTask { task ->
            if(!task.isSuccessful){
                task.exception?.let { throw it }
            }
            imageRef.downloadUrl
        }.addOnCompleteListener { task ->
            val currentUser = Firebase.auth.currentUser
            if (task.isSuccessful){
                val downloadUrl = task.result
                sendMassage(channelId = channelId, massageText = null, image = downloadUrl.toString())
            }
        }
    }

    fun listenForMassages(channelId: String) {
        db.getReference("massages").child(channelId).orderByChild("createdAt")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<Massage>()
                    snapshot.children.forEach{ snap ->
                        val massage = snap.getValue(Massage::class.java)
                        massage?.let { list.add(it) }
                    }
                    _massages.value = list
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

}