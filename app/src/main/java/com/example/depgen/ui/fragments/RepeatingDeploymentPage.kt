package com.example.depgen.ui.fragments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.depgen.Global
import com.example.depgen.model.Navigation
import com.example.depgen.ui.components.CardButton
import com.example.depgen.ui.components.DefaultTopAppBar

@Composable
fun RepeatingDeploymentPage() {
    val roleNums = remember { mutableStateListOf<Int>() }
    if (roleNums.isEmpty()) for (role in Global.rolesList) roleNums.add(role.minCount)

    Scaffold (
        topBar = {
            DefaultTopAppBar("Master", Navigation.REPEATINGDEPLOYMENT)
        }
    ) { innerPadding ->
        Column (
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Deployment",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Select the roles needed for your event!",
            )
            Spacer(modifier = Modifier.height(20.dp))
            LazyColumn (
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                for (i in Global.rolesList.indices) {
                    item {
                        ElevatedCard {

                        }
                    }
                }
            }
            CardButton(
                text = "Add Role Needed",
                onClick = {

                },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
            Spacer(modifier = Modifier.height(10.dp))
            CardButton(
                text = "Generate!",
                onClick = {
                    // ...
                }
            )
        }
    }
}