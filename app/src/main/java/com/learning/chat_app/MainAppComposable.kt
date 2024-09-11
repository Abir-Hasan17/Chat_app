package com.learning.chat_app

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.NavType

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.learning.chat_app.feature.auth.chat.ChatScreen
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

            composable("chat/{channelId}", arguments = listOf(
                navArgument("channelId"){
                    type = NavType.StringType
                }
            )){
                val channelId = it.arguments?.getString("channelId") ?: ""
                ChatScreen(navController, channelId)
            }
        }
    }
}


