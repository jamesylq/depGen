package com.example.depgen.view.fragments

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.depgen.Global
import com.example.depgen.LOGGED_OUT
import com.example.depgen.R
import com.example.depgen.model.Navigation
import com.example.depgen.safeNavigate
import com.example.depgen.switchProfile
import com.example.depgen.view.components.CardButton

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
            else -> ""
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePage(idx: Int, prev: String) {
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
                    //TODO: Replace with Profile Picture
                    Image(
                        painterResource(R.drawable.icon_512),
                        "",
                        modifier = Modifier.clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        Global.profileList[idx].username,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
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
