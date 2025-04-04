package com.example.depgen.ui.fragments

import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.CardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.depgen.Global
import com.example.depgen.model.Skill
import com.example.depgen.navController
import com.example.depgen.save
import com.example.depgen.ui.components.CardButton
import com.example.depgen.ui.components.ConfirmationScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewSkillPage() {
    var confirmationShowing by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var maxLvl by remember { mutableStateOf("") }
    var maxLvlError by remember { mutableStateOf(false) }
    var defLvl by remember { mutableStateOf("0") }
    var defLvlError by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    var nameError by remember { mutableStateOf(false) }

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
                Text(
                    text = "Skill Name",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 15.dp)
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = false
                    },
                    placeholder = { Text("Enter Skill Name") },
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = {
                        if (nameError) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = "Name cannot be empty!",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    trailingIcon = {
                        if (nameError) {
                            Icon(
                                Icons.Rounded.Warning,
                                "",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )
                Text(
                    text = "Skill Level Settings",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(bottom = 5.dp, top = 25.dp)
                        .hoverable(interactionSource)
                )
                Text(
                    text = "Skill Level refers to how qualified a member is to take up a certain role for deployment.",
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                Text(
                    text = "Maximum Skill Level",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(bottom = 5.dp, top = 10.dp)
                        .hoverable(interactionSource)
                )
                OutlinedTextField(
                    value = maxLvl,
                    onValueChange = {
                        maxLvl = it
                        maxLvlError = (maxLvl.isNotBlank() && maxLvl.toIntOrNull() == null)
                    },
                    placeholder = { Text("Enter Maximum Skill Level") },
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = {
                        if (maxLvlError) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = "Maximum Skill Level must be an Integer!",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    trailingIcon = {
                        if (maxLvlError) {
                            Icon(
                                Icons.Rounded.Warning,
                                "",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )
                Text(
                    text = "Default Skill Level",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(bottom = 5.dp, top = 5.dp)
                        .hoverable(interactionSource)
                )
                OutlinedTextField(
                    value = defLvl,
                    onValueChange = {
                        defLvl = it
                        defLvlError = (defLvl.isNotBlank() && defLvl.toIntOrNull() == null)
                    },
                    placeholder = { Text("Enter Default Skill Level") },
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = {
                        if (defLvlError) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = "Default Skill Level must be an Integer!",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    trailingIcon = {
                        if (defLvlError) {
                            Icon(
                                Icons.Rounded.Warning,
                                "",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )
                Spacer(modifier = Modifier.weight(1f))
                CardButton(
                    text = "Done",
                    onClick = {
                        if (name.isEmpty()) {
                            nameError = true
                        } else if (!maxLvlError && !defLvlError) {
                            Global.skillsList.add(Skill(name, maxLvl.toInt(), defLvl.toInt()))
                            navController.navigate("SkillsTracker")
                        }
                        save()
                    },
                    colors = CardColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.Black,
                        disabledContainerColor = MaterialTheme.colorScheme.primary,
                        disabledContentColor = Color.Black
                    )
                )
            }
        }
    }
}
