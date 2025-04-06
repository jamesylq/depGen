package com.example.depgen.ui.fragments

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.depgen.Global
import com.example.depgen.model.Navigation
import com.example.depgen.navController
import com.example.depgen.ui.components.DefaultTopAppBar


@Composable
fun RolesListPage() {
    Scaffold(
        topBar = {
            DefaultTopAppBar("Master", Navigation.EVENTLIST)
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate("NewRole")
            }) {
                Icon(Icons.Filled.Add, "")
            }
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(16.dp)
        ) {
            Text(
                text = "List of Roles",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 15.dp)
            )
            //TODO: Display Roles Properly
            for (role in Global.rolesList) {
                ElevatedCard (
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(role.eventRole)
                }
            }
        }
    }
}