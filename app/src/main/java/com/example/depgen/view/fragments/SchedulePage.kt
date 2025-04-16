package com.example.depgen.view.fragments

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.depgen.Global
import com.example.depgen.model.DeploymentRecord
import com.example.depgen.model.Navigation
import com.example.depgen.utils.getDate
import com.example.depgen.utils.getDeployments
import com.example.depgen.utils.safeNavigate
import com.example.depgen.utils.toHHMMTime
import com.example.depgen.view.components.DisplayProfileDeployment
import com.example.depgen.view.components.MultiDateSelector
import com.example.depgen.view.components.TopBarProfileIcon
import java.time.LocalDate


@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun SchedulePage(idx: Int) {
    val profile = Global.profileList[idx]

    val selectedDates by remember {
        mutableStateOf(
            getDeployments(profile).map {
                it.getDate()
            }.toSet()
        )
    }
    var viewing by remember { mutableStateOf<LocalDate?>(null) }
    val deployments = remember { mutableStateListOf<DeploymentRecord>() }

    Scaffold (
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    titleContentColor = Color.Black
                ),
                title = {
                    Text(
                        text = Global.profile.username,
                        modifier = Modifier.clickable {
                            safeNavigate("Profile/${idx}/${Navigation.SCHEDULE + Navigation.M * idx}")
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        safeNavigate("Master")
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "")
                    }
                },
                actions = {
                    TopBarProfileIcon("Profile/${idx}/${Navigation.SCHEDULE + Navigation.M * idx}")
                }
            )
        }
    ) { innerPadding ->
        Column (
            modifier = Modifier
                .padding(innerPadding)
        ) {
            Column (
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "${Global.profile.username}\'s Deployment Schedule",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 15.dp)
                )
                Text("Click on a highlighted date to view your deployment!")
            }

            MultiDateSelector(
                selectedDates = selectedDates,
                onDateToggle = {
                    Log.d("DEPGENDEBUG", "WAHOO")
                    viewing = if (viewing == it) null else it
                },
                highlightedDate = viewing,
                selectedColor = MaterialTheme.colorScheme.tertiary,
                unselectedColor = Color.LightGray
            )

            Column (
                modifier = Modifier.padding(16.dp)
            ) {
                LaunchedEffect (viewing) {
                    if (viewing != null) {
                        deployments.clear()
                        deployments.addAll(getDeployments(profile).getDate(viewing!!))
                    }
                }

                LazyColumn {
                    item {
                        if (viewing == null) {
                            Text(
                                text = "Legend",
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.tertiary)
                                )
                                Text("Available", modifier = Modifier.padding(start = 5.dp))
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(Color.LightGray)
                                )
                                Text("Unavailable", modifier = Modifier.padding(start = 5.dp))
                            }

                        } else {
                            if (deployments.isEmpty()) {
                                Text("No Deployments!", fontWeight = FontWeight.SemiBold)
                            }
                            for (deployment in deployments) {
                                ElevatedCard (
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.tertiary,
                                        contentColor = Color.Black
                                    )
                                ) {
                                    Column (
                                        modifier = Modifier.padding(8.dp)
                                    ) {
                                        Text("${deployment.event.name} ${deployment.event.getComponentType(deployment.component)!!.componentType}", fontWeight = FontWeight.Bold)
                                        Row {
                                            Text("Time: ", fontWeight = FontWeight.SemiBold)
                                            Text("${deployment.component.getStart().toHHMMTime()} to ${deployment.component.getEnd().toHHMMTime()}")
                                        }
                                        Spacer(modifier = Modifier.height(5.dp))
                                        DisplayProfileDeployment(profile, deployment.roles)
                                    }
                                }

                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

