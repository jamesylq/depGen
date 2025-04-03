package com.example.depgen.ui.fragments

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.depgen.LOGGED_OUT
import com.example.depgen.R
import com.example.depgen.encryptSHA256
import com.example.depgen.findProfile
import com.example.depgen.navController
import com.example.depgen.switchProfile
import com.example.depgen.ui.components.CardButton

@Composable
fun LoginPage() {
    var username by remember { mutableStateOf("") }
    var usernameError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    val password = remember { TextFieldState() }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.Start
        ) {
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.icon_512),
                    contentDescription = "",
                    modifier = Modifier.clip(CircleShape)
                )
            }
            Text(
                text = "Login",
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it
                    usernameError = ""
                    passwordError = ""
                },
                label = { Text("Username") },
                modifier = Modifier
                    .fillMaxWidth(),
                supportingText = {
                    if (usernameError != "") {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = usernameError,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                trailingIcon = {
                    if (usernameError != "") {
                        Icon(
                            Icons.Rounded.Warning,
                            "",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
            OutlinedSecureTextField(
                state = password,
                modifier = Modifier.fillMaxWidth(),
                textObfuscationMode = TextObfuscationMode.RevealLastTyped,
                label = {
                    Text("Password")
                },
                supportingText = {
                    if (passwordError != "") {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = passwordError,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                trailingIcon = {
                    if (passwordError != "") {
                        Icon(
                            Icons.Rounded.Warning,
                            "",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
            CardButton (
                text = "Login",
                onClick = {
                    if (username.lowercase() == "guest") {
                        usernameError = "Username Not Found!"
                    } else {
                        val profile = findProfile(username)
                        if (profile == LOGGED_OUT) {
                            usernameError = "Username Not Found!"
                        } else if (profile.password == password.text.toString().encryptSHA256()) {
                            switchProfile(profile)
                            navController.navigate("Master")
                        } else {
                            passwordError = "Incorrect Password!"
                        }
                    }
                }
            )
            Spacer(modifier = Modifier.height(25.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    "Forgot Password",
                    modifier = Modifier
                        .clickable {
                            //TODO: Forgot Password
                        },
//                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    "Sign Up",
                    modifier = Modifier
                        .clickable {
                            navController.navigate("SignUp")
                        },
//                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}