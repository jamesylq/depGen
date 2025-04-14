package com.example.depgen.view.fragments

import UriFromCamera
import UriFromGallery
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.depgen.Global
import com.example.depgen.LOGGED_OUT
import com.example.depgen.luxuryManager
import com.example.depgen.model.Navigation
import com.example.depgen.model.ProfileLuxury
import com.example.depgen.utils.safeNavigate
import com.example.depgen.utils.saveLuxuries
import com.example.depgen.utils.switchProfile
import com.example.depgen.utils.uriToBase64
import com.example.depgen.view.components.CardButton

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfilePage(idx: Int, prev: Int) {
    ProfilePage(idx,
        when (prev % Navigation.M) {
            Navigation.MASTER -> "Master"
            Navigation.EVENTLIST -> "EventList"
            Navigation.MEMBERLIST -> "MemberList"
            Navigation.SETTINGS -> "Settings"
            Navigation.EVENT -> "Event/${prev / Navigation.M}"
            Navigation.ONETIMEDEPLOYMENT -> "OneTimeDeployment"
            Navigation.REPEATINGDEPLOYMENT -> "RepeatingDeployment"
            Navigation.SKILLSTRACKER -> "SkillsTracker"
            Navigation.SKILL -> "Skill/${prev / Navigation.M}"
            Navigation.AVAILABILITIES -> "Availabilities/${prev / Navigation.M}"
            else -> ""
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePage(idx: Int, prev: String) {
    val profile = Global.profileList[idx]
    var pfpDropdown by remember { mutableStateOf(false) }
    var cameraActive by remember { mutableStateOf(false) }
    var galleryActive by remember { mutableStateOf(false) }
    var profileLuxury by remember { mutableStateOf<ProfileLuxury?>(luxuryManager.getLuxury(profile)) }

    Scaffold (
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    IconButton(onClick = {
                        safeNavigate(prev)
                    }) {
                        Icon(Icons.Default.Close, "")
                    }
                }
            )
        }
    ) {
        innerPadding ->
        if (cameraActive) {
            UriFromCamera(
                onImageUriReady = {
                    luxuryManager.getLuxury(profile).updateProfilePicture(it.uriToBase64(quality = 40)!!)
                    saveLuxuries(profile)
                    profileLuxury = null
                }
            )
        } else if (galleryActive) {
            UriFromGallery(
                onImageUriReady = {
                    luxuryManager.getLuxury(profile).updateProfilePicture(it.uriToBase64(quality = 40)!!)
                    saveLuxuries(profile)
                    profileLuxury = null
                }
            )
        }

        LaunchedEffect (profileLuxury) {
            profileLuxury = luxuryManager.getLuxury(profile)
        }

        Column (
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .size((LocalConfiguration.current.screenWidthDp - 32).dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier
                            .clickable {
                                pfpDropdown = true
                            }
                    ) {
                        profileLuxury?.ProfilePicture(RoundedCornerShape(25.dp), 256.dp)

                        DropdownMenu(
                            expanded = pfpDropdown,
                            onDismissRequest = { pfpDropdown = false },
                            modifier = Modifier.width(256.dp)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Take New Picture") },
                                onClick = {
                                    pfpDropdown = false
                                    cameraActive = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Select Picture From Gallery") },
                                onClick = {
                                    pfpDropdown = false
                                    galleryActive = true
                                }
                            )
                        }
                    }
                }
            }
            Row (
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-30).dp)
            ) {
                Text(
                    text = profile.username,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
            if (Global.idx == idx) {
                CardButton(
                    text = "Sign Out",
                    onClick = {
                        switchProfile(LOGGED_OUT)
                        safeNavigate("Login")
                    }
                )
                Spacer(modifier = Modifier.height(15.dp))
            }
            CardButton(
                text = "Settings",
                onClick = {
                    safeNavigate("Settings")
                }
            )
        }
    }
}
