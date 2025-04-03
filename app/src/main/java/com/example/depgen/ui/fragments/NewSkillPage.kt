package com.example.depgen.ui.fragments

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.depgen.navController
import com.example.depgen.ui.components.ConfirmationScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewSkillPage() {
    var confirmationShowing by remember { mutableStateOf(false) }

    if (confirmationShowing) {
        ConfirmationScreen(
            {
                navController.navigate("SkillsTracker")
                confirmationShowing = false
            },
            {
                confirmationShowing = false
            }
        )

    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    confirmationShowing = true
                                },
                                modifier = Modifier.height(30.dp)
                            ) {
                                Icon(Icons.Default.Close, "")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Create New Skill",
                                fontSize = 24.sp,
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        MaterialTheme.colorScheme.tertiary
                    )
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {

            }
        }
    }
}
