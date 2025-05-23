package com.example.depgen.view.fragments

import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.depgen.utils.clearFocusOnKeyboardDismiss
import com.example.depgen.utils.isInt
import com.example.depgen.utils.isNotInt
import com.example.depgen.utils.safeNavigate
import com.example.depgen.utils.save
import com.example.depgen.view.components.CardButton
import com.example.depgen.view.components.ConfirmationScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewSkillPage() {
    var confirmationShowing by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var maxLvl by remember { mutableStateOf("") }
    var defLvl by remember { mutableStateOf("0") }
    var maxLvlEmpty by remember { mutableStateOf(false) }
    var defLvlEmpty by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf(false) }

    BackHandler {
        confirmationShowing = true
    }

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
        if (confirmationShowing) {
            ConfirmationScreen(
                {
                    safeNavigate("SkillsTracker")
                    confirmationShowing = false
                },
                {
                    confirmationShowing = false
                }
            )
        }

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
                modifier = Modifier
                    .fillMaxWidth()
                    .clearFocusOnKeyboardDismiss(),
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
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                )
            )
            Text(
                text = "Skill Level Settings",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(bottom = 5.dp, top = 25.dp)
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
            )
            OutlinedTextField(
                value = maxLvl,
                onValueChange = {
                    maxLvl = it
                    maxLvlEmpty = maxLvl.isEmpty()
                },
                placeholder = { Text("Enter Maximum Skill Level") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clearFocusOnKeyboardDismiss(),
                supportingText = {
                    if (maxLvl.isNotInt()) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Maximum Skill Level must be an Integer!",
                            color = MaterialTheme.colorScheme.error
                        )
                    } else if (maxLvlEmpty) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Maximum Skill Level cannot be Empty!",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                trailingIcon = {
                    if (maxLvl.isNotInt() || maxLvlEmpty) {
                        Icon(
                            Icons.Rounded.Warning,
                            "",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                )
            )
            Text(
                text = "Default Skill Level",
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .padding(bottom = 5.dp, top = 5.dp)
            )
            OutlinedTextField(
                value = defLvl,
                onValueChange = {
                    defLvl = it
                    defLvlEmpty = defLvl.isEmpty()
                },
                placeholder = { Text("Enter Default Skill Level") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clearFocusOnKeyboardDismiss(),
                supportingText = {
                    if (defLvl.isNotInt()) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Default Skill Level must be an Integer!",
                            color = MaterialTheme.colorScheme.error
                        )
                    } else if (defLvlEmpty) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Default Skill Level cannot be Empty!",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                trailingIcon = {
                    if (defLvl.isNotInt() || defLvlEmpty) {
                        Icon(
                            Icons.Rounded.Warning,
                            "",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            CardButton(
                text = "Done",
                onClick = {
                    if (name.isEmpty()) {
                        nameError = true
                    } else if (maxLvl.isInt() && defLvl.isInt()) {
                        Global.skillsList.add(Skill(name, maxLvl.toInt(), defLvl.toInt()))
                        safeNavigate("SkillsTracker")
                    }
                    save()
                },
                colors = CardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = Color.Black,
                    disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    disabledContentColor = Color.Black
                )
            )
        }
    }
}
