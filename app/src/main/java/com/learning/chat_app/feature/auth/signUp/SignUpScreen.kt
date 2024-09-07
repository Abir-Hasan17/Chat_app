package com.learning.chat_app.feature.auth.signUp

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.learning.chat_app.R

@Composable
fun SignUpScreen(navController: NavController) {

    val viewModel: SignUpViewModel = hiltViewModel()
    val uiState = viewModel.state.collectAsState()

    var email by remember {
        mutableStateOf("")
    }
    var fullName by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    var confirmPassword by remember {
        mutableStateOf("")
    }

    val context = LocalContext.current
    LaunchedEffect(key1 = uiState.value) {
        when(uiState.value){
            is SignUpState.Success -> navController.navigate("home")
            is SignUpState.Error -> Toast.makeText(context,"Sign Up Failed", Toast.LENGTH_SHORT).show()
            else ->{}
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .padding(scaffoldPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //Logo
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null,
                modifier = Modifier.size(300.dp)
            )

            //Full name
            Spacer(modifier = Modifier.size(10.dp))
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text(text = "Full Name") }
            )

            //Email
            Spacer(modifier = Modifier.size(5.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(text = "Email") },
                placeholder = {
                    Text(
                        text = "example@gmail.com",
                        color = Color.Gray
                    )
                })

            //Password
            Spacer(modifier = Modifier.size(5.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(text = "Password") },
                visualTransformation = PasswordVisualTransformation()
            )
            //Confirm Password
            Spacer(modifier = Modifier.size(5.dp))
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text(text = "Confirm Password") },
                visualTransformation = PasswordVisualTransformation()
            )

            //Sign In button
            Spacer(modifier = Modifier.size(5.dp))
            if(uiState.value == SignUpState.Loading){
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                        viewModel.signUp(fullName, email, password)
                    },
                    enabled = confirmPassword == password && fullName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()
                ) {
                    Text(text = "Sign up")
                }
            }

            //Sign up
            Spacer(modifier = Modifier.size(100.dp))
            TextButton(onClick = { navController.popBackStack() }) {
                Text(
                    text = "Already have an account? ",
                    color = Color.DarkGray,
                )
                Text(
                    text = "Sign In",
                    color = Color.DarkGray,
                    textDecoration = TextDecoration.Underline,
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewSignUpScreen() {
    SignUpScreen(navController = rememberNavController())
}