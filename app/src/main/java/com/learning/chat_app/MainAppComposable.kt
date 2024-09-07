package com.learning.chat_app

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.learning.chat_app.feature.auth.signIn.SignInScreen
import com.learning.chat_app.feature.auth.signUp.SignUpScreen
import com.learning.chat_app.feature.home.HomeScreen

@Composable
fun MainApp(){
    Surface {
        val navController = rememberNavController()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val start = if (currentUser == null) "signin" else "home"

        NavHost(navController = navController, startDestination = start) {
            composable("signin"){
                SignInScreen(navController)
            }

            composable("signup"){
                SignUpScreen(navController)
            }

            composable("home"){
                HomeScreen(navController)
            }
        }
    }
}


