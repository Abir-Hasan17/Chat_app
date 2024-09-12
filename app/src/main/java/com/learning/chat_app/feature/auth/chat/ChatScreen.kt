package com.learning.chat_app.feature.auth.chat

import android.Manifest
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.learning.chat_app.model.Massage
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatScreen(navController: NavController, channelId: String) {

    val viewModel: ChatViewModel = hiltViewModel()
    LaunchedEffect(key1 = true) {
        viewModel.listenForMassages(channelId)
    }
    val massages = viewModel.massages.collectAsState()

    val dialogueVisible = remember {
        mutableStateOf(false)
    }

    val cameraImageUri = remember {
        mutableStateOf<Uri?>(null)
    }
    val cameraImageLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                cameraImageUri.value?.let {
                    //send msg
                    viewModel.sendImage(imageUri = it, channelId = channelId)
                }
            }
        }

    val galleryImageLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { viewModel.sendImage(imageUri = it, channelId = channelId) }
        }

    fun createImageUri(): Uri {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = ContextCompat.getExternalFilesDirs(
            navController.context,
            Environment.DIRECTORY_PICTURES
        ).first()

        return FileProvider.getUriForFile(
            navController.context,
            "${navController.context.packageName}.provider",
            File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
                .apply { cameraImageUri.value = Uri.fromFile(this) }
        )
    }

    val cameraPermissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                cameraImageLauncher.launch(createImageUri())
            }
        }

    Scaffold { scaffoldPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
        ) {
            ChatMassages(
                massages = massages.value,
                onSendMassage = {
                    if (it.isNotEmpty() && it.isNotBlank())
                        viewModel.sendMassage(channelId, it)
                },
                onSendAttachment = {
                    dialogueVisible.value = true

                }
            )
        }
        if (dialogueVisible.value) {
            CameraOrGalleryDialogue(
                onCameraSelected = {
                    dialogueVisible.value = false
                    if (navController.context.checkSelfPermission(Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                        cameraImageLauncher.launch(createImageUri())
                    } else {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
                onGallerySelected = {
                    dialogueVisible.value = false
                    galleryImageLauncher.launch("image/*")
                },
                onDismiss = {dialogueVisible.value = false}
            )
        }
    }
}

@Composable
fun ChatMassages(
    massages: List<Massage>,
    onSendMassage: (String) -> Unit,
    onSendAttachment: () -> Unit
) {
    val msg = remember {
        mutableStateOf("")
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(modifier = Modifier.fillMaxSize()) {
        //Chat Bubbles
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(massages) { massage ->
                ChatBubble(massage = massage)
            }
        }

        //Send massage
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            //attachment button
            IconButton(onClick = { onSendAttachment() }) {
                Icon(imageVector = Icons.Default.Share, contentDescription = null)
            }

            OutlinedTextField(
                value = msg.value,
                onValueChange = { msg.value = it },
                modifier = Modifier
                    .clip(RoundedCornerShape(percent = 50)),
                placeholder = {
                    Text(
                        text = "Massage",
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        onSendMassage(msg.value)
                        msg.value = ""
                        keyboardController?.hide()
                    }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent
                )
            )

            //send button
            IconButton(onClick = {
                onSendMassage(msg.value)
                msg.value = ""
                keyboardController?.hide()
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
                if (massage.imageUrl != null){
                    AsyncImage(model = massage.imageUrl, contentDescription = null)
                    //Log.i("abir", "img")
                }else{
                    Text(text = massage.massage ?: "", modifier = Modifier.padding(5.dp))
                    //Log.i("abir", "txt")
                }
            }
        }
    }

}

//@Preview(showBackground = true)
@Composable
fun CameraOrGalleryDialogue(onCameraSelected: () -> Unit, onGallerySelected: () -> Unit, onDismiss:()->Unit) {
    Dialog(onDismissRequest = { onDismiss() }) {
        ElevatedCard(modifier = Modifier.height(200.dp)) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text(text = "Select a Source", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.size(50.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    //camera button
                    Button(onClick = { onCameraSelected() }) {
                        Text(text = "Camera")
                    }
                    Spacer(modifier = Modifier.size(15.dp))
                    //Gallery Button
                    Button(onClick = { onGallerySelected() }) {
                        Text(text = "Gallery")
                    }
                }
            }
        }
    }
}
