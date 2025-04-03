package com.example.depgen.ui.fragments

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.depgen.model.Navigation
import com.example.depgen.navController
import com.example.depgen.ui.components.DefaultTopAppBar

@Composable
fun SkillsTrackerPage() {
    Scaffold (
        topBar = {
            DefaultTopAppBar("Master", Navigation.SKILLSTRACKER)
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate("NewSkill")
            }) {
                Icon(Icons.Filled.Add, "")
            }
        }
    ) {
        Column (
            modifier = Modifier.padding(it).padding(16.dp)
        ) {
            Text(
                text = "Skills Tracker",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
