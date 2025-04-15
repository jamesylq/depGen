package com.example.depgen.view.fragments

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.depgen.Global
import com.example.depgen.model.Navigation
import com.example.depgen.utils.safeNavigate
import com.example.depgen.view.components.CardButton
import com.example.depgen.view.components.ConfirmationScreen
import com.example.depgen.view.components.MultiDateSelector
import com.example.depgen.view.components.TopBarProfileIcon
import java.time.LocalDate


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AvailabilitiesPage(idx: Int) {
    var selectedDates by remember {
        mutableStateOf(
            Global.profileList[idx].unavailable.map{
                LocalDate.parse(it)
            }.toSet()
        )
    }
    var confirmation by remember { mutableStateOf(false) }

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
                            safeNavigate("Profile/${idx}/${Navigation.AVAILABILITIES + Navigation.M * idx}")
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        confirmation = true
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "")
                    }
                },
                actions = {
                    TopBarProfileIcon("Profile/${idx}/${Navigation.AVAILABILITIES + Navigation.M * idx}")
                }
            )
        }
    ) { innerPadding ->
        Column (
            modifier = Modifier
                .padding(innerPadding)
        ) {
            if (confirmation) {
                ConfirmationScreen(
                    onConfirm = {
                        confirmation = false
                        safeNavigate("Master")
                    },
                    onDecline = {
                        confirmation = false
                    }
                )
            }

            Column (
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "${Global.profile.username}\'s Availabilities",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 15.dp)
                )
                Text("Declare your availabilities below!")
            }

            MultiDateSelector(
                selectedDates = selectedDates,
                onDateToggle = { date ->
                    if (selectedDates.contains(date)) {
                        selectedDates -= date
                    } else {
                        selectedDates += date
                    }
                }
            )

            Column (
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Legend", fontWeight = FontWeight.Bold)
                Row (verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.tertiary)
                    )
                    Text("Available", modifier = Modifier.padding(start = 5.dp))
                }
                Row (verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                    )
                    Text("Unavailable", modifier = Modifier.padding(start = 5.dp))
                }

                Spacer(modifier = Modifier.weight(1f))
                CardButton(
                    text = "Save Changes",
                    onClick = {
                        Global.profileList[idx].unavailable = HashSet(selectedDates.map { it.toString() })
                        safeNavigate("Master")
                    }
                )
            }
        }
    }
}

