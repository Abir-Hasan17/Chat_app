package com.learning.chat_app.feature.auth.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.learning.chat_app.model.Massage

@Composable
fun ChatScreen(navController: NavController, channelId: String) {

    val viewModel: ChatViewModel = hiltViewModel()
    LaunchedEffect(key1 = true) {
        viewModel.listenForMassages(channelId)
    }
    val massages = viewModel.massages.collectAsState()

    Scaffold { scaffoldPadding->
        Column(modifier = Modifier.fillMaxSize().padding(scaffoldPadding)) {
            ChatMassages(
                massages = massages.value,
                onSendMassage = {
                viewModel.sendMassage(channelId, it)
            })
        }
    }
}

@Composable
fun ChatMassages(massages: List<Massage>, onSendMassage: (String) -> Unit) {
    val msg = remember {
        mutableStateOf("")
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    Box(modifier = Modifier.fillMaxSize()) {
        //Chat Bubbles
        LazyColumn {
            items(massages) { massage ->
                ChatBubble(massage = massage)
            }
        }

        //Send massage
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(10.dp)
        ) {
            OutlinedTextField(
                value = msg.value,
                onValueChange = { msg.value = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text(text = "Massage") },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        onSendMassage(msg.value)
                        msg.value = ""
                        keyboardController?.hide()
                    }
                )
            )
            IconButton(onClick = {
                onSendMassage(msg.value)
                msg.value = ""
            }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = null)
            }
        }
    }
}

@Composable
fun ChatBubble(massage: Massage) {
    val isCurrentUser = massage.senderId == Firebase.auth.currentUser?.uid
    val bubbleColor =
        if (isCurrentUser) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.tertiary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        val alignment = if (isCurrentUser) Alignment.CenterEnd else Alignment.CenterStart
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = alignment) {
            Card(
                modifier = Modifier.padding(5.dp), colors = CardDefaults.cardColors(
                    containerColor = bubbleColor
                )
            ) {
                Text(text = massage.massage, modifier = Modifier.padding(5.dp))
            }
        }
    }

}
