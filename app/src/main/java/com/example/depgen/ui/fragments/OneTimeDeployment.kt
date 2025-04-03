package com.example.depgen.ui.fragments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.depgen.SORTED_ROLES
import com.example.depgen.model.Navigation
import com.example.depgen.ui.components.CardButton
import com.example.depgen.ui.components.DefaultTopAppBar
import com.example.depgen.ui.components.QuantityPicker

@Composable
fun OneTimeDeploymentPage() {
    val roleNums = remember { mutableStateListOf<Int>() }
    if (roleNums.isEmpty()) for (role in SORTED_ROLES) roleNums.add(role.minCount)

    Scaffold (
        topBar = {
            DefaultTopAppBar("Master", Navigation.ONETIMEDEPLOYMENT)
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
                for (i in SORTED_ROLES.indices) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(SORTED_ROLES[i].eventRole)
                            QuantityPicker(
                                { roleNums[i] = it },
                                initialQty = SORTED_ROLES[i].minCount,
                                minQty = SORTED_ROLES[i].minCount,
                                maxQty = SORTED_ROLES[i].maxCount
                            )
                        }
                    }
                }
            }
            CardButton(
                text = "Generate!",
                onClick = {
                    // ...
                }
            )
        }
    }
}