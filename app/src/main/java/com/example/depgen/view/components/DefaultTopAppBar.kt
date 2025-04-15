package com.example.depgen.view.components

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.depgen.Global
import com.example.depgen.utils.safeNavigate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultTopAppBar(prev: String, curr: Int) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.tertiary,
            titleContentColor = Color.Black
        ),
        title = {
            Text(
                text = Global.profile.username,
                modifier = Modifier.clickable {
                    safeNavigate("Profile/${Global.idx}/$curr")
                }
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                safeNavigate(prev)
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "")
            }
        },
        actions = {
            TopBarProfileIcon("Profile/${Global.idx}/$curr")
        }
    )
}