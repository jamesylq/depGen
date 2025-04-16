package com.example.depgen.view.fragments

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.depgen.EMAIL_REGEX
import com.example.depgen.Global
import com.example.depgen.LOGGED_OUT
import com.example.depgen.R
import com.example.depgen.model.Profile
import com.example.depgen.toast
import com.example.depgen.utils.clearFocusOnKeyboardDismiss
import com.example.depgen.utils.encryptSHA256
import com.example.depgen.utils.findProfile
import com.example.depgen.utils.safeNavigate
import com.example.depgen.utils.save
import com.example.depgen.view.components.CardButton


@Composable
fun SignUpPage() {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var usernameError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
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
                text = "Sign Up",
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
                    .fillMaxWidth()
                    .clearFocusOnKeyboardDismiss(),
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
                modifier = Modifier
                    .fillMaxWidth()
                    .clearFocusOnKeyboardDismiss(),
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
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = ""
                },
                label = { Text("Email Address") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clearFocusOnKeyboardDismiss(),
                supportingText = {
                    if (emailError != "") {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = emailError,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                trailingIcon = {
                    if (emailError != "") {
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
                text = "Sign Up",
                onClick = {
                    val profile = findProfile(username)
                    val uStr = username.lowercase().replace(" ", "")

                    emailError = if (email.isBlank()) { "Email Cannot be Blank!" }
                                 else if (!email.matches(EMAIL_REGEX)) { "Invalid Email!" }
                                 else { "" }

                    usernameError = if (uStr.isEmpty()) { "Username Cannot be Blank!" }
                                    else if (uStr == "admin" || uStr == "guest") { "This Username is Reserved!" }
                                    else if (profile != LOGGED_OUT) { "Username Already In Use!" }
                                    else { "" }

                    //TODO: Password Safety Requirements
                    passwordError = if (password.text.isEmpty()) { "Password Cannot be Blank!" }
                                    else { "" }

//                    if (emailError.isEmpty() && passwordError.isEmpty() && emailError.isEmpty()) {
//                        addUser(Profile(username, email), password.text.toString())
//                    }

                    if (emailError.isEmpty() && passwordError.isEmpty() && emailError.isEmpty()) {
                        Global.profileList.add(
                            Profile(
                                username,
                                password.text.toString().encryptSHA256(),
                                email
                            )
                        )
                        save()
                        toast("Signed up account $username!")
                        safeNavigate("Login")
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
                    "Already have an Account?   ",
                    modifier = Modifier
                        .clickable {
                            safeNavigate("Login")
                        },
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    "Log In",
                    modifier = Modifier
                        .clickable {
                            safeNavigate("Login")
                        },
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.inversePrimary
                )
            }
        }
    }
}