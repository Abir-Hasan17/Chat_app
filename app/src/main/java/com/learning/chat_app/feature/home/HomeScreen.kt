package com.learning.chat_app.feature.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

private const val TAG = "HomeScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val viewModel: HomeViewModel = hiltViewModel()
    val channels = viewModel.channels.collectAsState()
    val addChannelState = remember {
        mutableStateOf(false)
    }
    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(onClick = { addChannelState.value = true }) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    ) { scaffoldPadding ->
        Box(
            modifier = Modifier
                .padding(scaffoldPadding)
                .fillMaxSize()
        ) {
            LazyColumn {
                items(channels.value) { channel ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .clickable { navController.navigate("chat/${channel.id}")}
                    ) {
                        Text(text = channel.name, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            //AddChannelDialogue(onAddChannel = {})
        }
    }
    if (addChannelState.value) {
        ModalBottomSheet(
            onDismissRequest = { addChannelState.value = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ) {
            AddChannelDialogue {
                viewModel.addChannel(it)
                addChannelState.value = false
            }
        }
    }
}

@Composable
fun AddChannelDialogue(onAddChannel: (String) -> Unit) {
    val channelName = remember {
        mutableStateOf("")
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Add Channel",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.size(15.dp))
        OutlinedTextField(
            value = channelName.value,
            onValueChange = { channelName.value = it },
            colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = MaterialTheme.colorScheme.onPrimaryContainer),
            singleLine = true,
            label = { Text(text = "Channel Name") }
        )
        Spacer(modifier = Modifier.size(15.dp))
        Button(
            onClick = { onAddChannel(channelName.value) },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                disabledContentColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.size(5.dp))
            Text(text = "Add", modifier = Modifier.padding(end = 5.dp))
        }
    }

}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(navController = rememberNavController())
}