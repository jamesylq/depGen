package com.example.depgen.view.fragments

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CardColors
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.depgen.Global
import com.example.depgen.model.Navigation
import com.example.depgen.utils.safeNavigate
import com.example.depgen.view.components.TopBarProfileIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MasterPage() {
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
                            safeNavigate("Profile/${Global.idx}/${Navigation.MASTER}")
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        //TODO: Menu
                    }) {
                        Icon(Icons.Default.Menu, "")
                    }
                },
                actions = {
                    TopBarProfileIcon("Profile/${Global.idx}/${Navigation.MASTER}")
                }
            )
        }
    ) { innerPadding ->
        Column (
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Hello ${Global.profile.username},\nWelcome to depGen!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row (
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                ElevatedCard (
                    onClick = {
                        safeNavigate("MemberList")
                    },
                    modifier = Modifier
                        .size(((LocalConfiguration.current.screenWidthDp - 50) / 2).dp),
                    colors = CardColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = Color.Black,
                        disabledContentColor = Color.Black,
                        disabledContainerColor = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Column (
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Text("View Members")
                    }
                }
                ElevatedCard (
                    onClick = {
                        safeNavigate("EventList")
                    },
                    modifier = Modifier
                        .width(((LocalConfiguration.current.screenWidthDp - 50) / 2).dp)
                        .height(((LocalConfiguration.current.screenWidthDp - 50) / 2).dp),
                    colors = CardColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = Color.Black,
                        disabledContentColor = Color.Black,
                        disabledContainerColor = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Column (
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Text("View Events")
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            if (Global.isAdmin()) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ElevatedCard(
                        onClick = {
                            safeNavigate("OneTimeDeployment")
                        },
                        modifier = Modifier
                            .width(((LocalConfiguration.current.screenWidthDp - 50) / 2).dp)
                            .height(((LocalConfiguration.current.screenWidthDp - 50) / 2).dp),
                        colors = CardColors(
                            containerColor = MaterialTheme.colorScheme.tertiary,
                            contentColor = Color.Black,
                            disabledContentColor = Color.Black,
                            disabledContainerColor = MaterialTheme.colorScheme.tertiary
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom
                        ) {

                            Text(
                                text = "Generate One-time Deployment",
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    ElevatedCard(
                        onClick = {
                            safeNavigate("RepeatingDeployment")
                        },
                        modifier = Modifier
                            .width(((LocalConfiguration.current.screenWidthDp - 50) / 2).dp)
                            .height(((LocalConfiguration.current.screenWidthDp - 50) / 2).dp),
                        colors = CardColors(
                            containerColor = MaterialTheme.colorScheme.tertiary,
                            contentColor = Color.Black,
                            disabledContentColor = Color.Black,
                            disabledContainerColor = MaterialTheme.colorScheme.tertiary
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            Text(
                                text = "Generate Repeating Deployment",
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
            Row (
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                ElevatedCard (
                    onClick = {
                        safeNavigate("SkillsTracker")
                    },
                    modifier = Modifier
                        .width(((LocalConfiguration.current.screenWidthDp - 50) / 2).dp)
                        .height(((LocalConfiguration.current.screenWidthDp - 50) / 2).dp),
                    colors = CardColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = Color.Black,
                        disabledContentColor = Color.Black,
                        disabledContainerColor = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Column (
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {

                        Text(
                            text = "View Skills Tracker",
                            textAlign = TextAlign.Center
                        )
                    }
                }
                ElevatedCard (
                    onClick = {
                        safeNavigate("RolesList")
                    },
                    modifier = Modifier
                        .width(((LocalConfiguration.current.screenWidthDp - 50) / 2).dp)
                        .height(((LocalConfiguration.current.screenWidthDp - 50) / 2).dp),
                    colors = CardColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = Color.Black,
                        disabledContentColor = Color.Black,
                        disabledContainerColor = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Column (
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Text(
                            text = "View Roles",
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            if (!Global.isAdmin()) {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ElevatedCard(
                        onClick = {
                            safeNavigate("Availabilities/${Global.profile.getIdx()}")
                        },
                        modifier = Modifier
                            .width(((LocalConfiguration.current.screenWidthDp - 50) / 2).dp)
                            .height(((LocalConfiguration.current.screenWidthDp - 50) / 2).dp),
                        colors = CardColors(
                            containerColor = MaterialTheme.colorScheme.tertiary,
                            contentColor = Color.Black,
                            disabledContentColor = Color.Black,
                            disabledContainerColor = MaterialTheme.colorScheme.tertiary
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom
                        ) {

                            Text(
                                text = "Declare Availabilities",
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    ElevatedCard(
                        onClick = {
                            safeNavigate("Schedule/${Global.profile.getIdx()}")
                        },
                        modifier = Modifier
                            .width(((LocalConfiguration.current.screenWidthDp - 50) / 2).dp)
                            .height(((LocalConfiguration.current.screenWidthDp - 50) / 2).dp),
                        colors = CardColors(
                            containerColor = MaterialTheme.colorScheme.tertiary,
                            contentColor = Color.Black,
                            disabledContentColor = Color.Black,
                            disabledContainerColor = MaterialTheme.colorScheme.tertiary
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom
                        ) {

                            Text(
                                text = "View Schedule",
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}