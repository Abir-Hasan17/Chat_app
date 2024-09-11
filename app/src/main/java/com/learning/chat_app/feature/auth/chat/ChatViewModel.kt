package com.learning.chat_app.feature.auth.chat

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
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


    fun sendMassage(channelId: String, massageText: String){
        val massageToSend = Massage(
            db.reference.push().key ?: UUID.randomUUID().toString(),
            Firebase.auth.currentUser?.uid ?: "",
            massageText,
            System.currentTimeMillis(),
            Firebase.auth.currentUser?.displayName ?: "",
            null,
            null
        )
        db.reference.child("massages").child(channelId).push().setValue(massageToSend)
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