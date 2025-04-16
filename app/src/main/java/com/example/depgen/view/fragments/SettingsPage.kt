package com.example.depgen.view.fragments

import android.preference.PreferenceManager.getDefaultSharedPreferences
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.depgen.Global
import com.example.depgen.ctxt
import com.example.depgen.model.Navigation
import com.example.depgen.profilePreferenceManager
import com.example.depgen.toast
import com.example.depgen.utils.clear
import com.example.depgen.utils.load
import com.example.depgen.view.components.CardButton
import com.example.depgen.view.components.ConfirmationScreen
import com.example.depgen.view.components.DefaultTopAppBar
import com.example.depgen.view.theme.THEMES

@Composable
fun SettingsPage(onSelectTheme: (Int) -> Unit) {
    var confirmationShowing by remember { mutableStateOf(false) }
    var selectedTheme by remember {
        mutableIntStateOf(profilePreferenceManager.getPreference("theme").toInt())
    }

    Scaffold(
        topBar = {
            DefaultTopAppBar("Master", Navigation.SETTINGS)
        }

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (confirmationShowing) {
                if (Global.isAdmin()) {
                    ConfirmationScreen(
                        {
                            clear()
                            load()
                            confirmationShowing = false
                        },
                        {
                            confirmationShowing = false
                        }
                    )
                }
            }

            Text("Theme Selection", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("Select your preferred theme below!", modifier = Modifier.padding(top = 5.dp, bottom = 15.dp))
            Spacer(modifier = Modifier.weight(0.5f))
            LazyRow (
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                for (i in THEMES.indices) {
                    val option = THEMES[i]
                    item {
                        Column (
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Column(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .height(300.dp)
                                    .let {
                                        if (i == selectedTheme) {
                                            it.border(
                                                5.dp,
                                                MaterialTheme.colorScheme.primary,
                                                RoundedCornerShape(20.dp)
                                            )
                                        } else {
                                            it
                                        }
                                    }
                                    .clickable {
                                        selectedTheme = i
                                        onSelectTheme(i)
                                        profilePreferenceManager.setPreference("theme", i.toString())
                                    }
                            ) {
                                Image(
                                    painter = painterResource(option.resource),
                                    contentDescription = "",
                                )
                            }
                            Text(
                                text = option.name,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 8.dp),
                                color = if (i == selectedTheme) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
//            Text("Others", fontSize = 22.sp, fontWeight = FontWeight.Bold)
//            Row (verticalAlignment = Alignment.CenterVertically) {
//                Text("Display Profile Pictures")
//                Spacer(modifier = Modifier.weight(1f))
//                Switch(
//                    displayingPfp,
//                    {
//                        displayingPfp = it
//                        profilePreferenceManager.setPreference("luxury", it.toString())
//                    }
//                )
//            }


            Spacer(modifier = Modifier.weight(1f))
            CardButton(
                text = "Clear Onboarding",
                onClick = {
                    getDefaultSharedPreferences(ctxt).edit().clear().apply()
                    toast("Cleared Onboarding!")
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Replay,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            )
            Spacer(modifier = Modifier.height(20.dp))
            if (Global.isAdmin()) {
                CardButton(
                    text = "Clear Data",
                    onClick = {
                        confirmationShowing = true
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.DeleteForever,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                )
            }
        }
    }
}
