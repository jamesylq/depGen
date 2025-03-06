package com.example.depgen

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultTopAppBar() {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.tertiary,
            titleContentColor = Color.Black
        ),
        title = {
            Text(
                text = Global.profile.username,
                modifier = Modifier.clickable {
                    navController.navigate("Profile/${Global.idx}")
                }
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                //TODO: FIX THE NAVIGATION
                navController.navigate("Master")
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "")
            }
        },
        actions = {
            IconButton(onClick = {
                navController.navigate("Profile/${Global.idx}")
            }) {
                Icon(Icons.Default.AccountCircle, "")
            }
        }
    )
}