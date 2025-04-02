package com.example.depgen.ui.fragments

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.depgen.Global
import com.example.depgen.model.LOGGED_OUT
import com.example.depgen.model.Navigation
import com.example.depgen.model.clear
import com.example.depgen.model.load
import com.example.depgen.model.switchProfile
import com.example.depgen.navController
import com.example.depgen.ui.components.CardButton
import com.example.depgen.ui.components.ConfirmationScreen
import com.example.depgen.ui.components.DefaultTopAppBar

@Composable
fun SettingsPage() {
    var confirmationShowing by remember { mutableStateOf(false) }

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

        } else {
            ConfirmationScreen(
                {
                    Global.profileList.removeAt(Global.idx)
                    switchProfile(LOGGED_OUT)
                    navController.navigate("Login")
                    confirmationShowing = false
                },
                {
                    confirmationShowing = false
                }
            )
        }

    } else {
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
                Spacer(modifier = Modifier.weight(1f))
                if (Global.isAdmin()) {
                    CardButton("Clear Data", {
                        confirmationShowing = true
                    })
                } else {
                    CardButton("Delete Account", {
                        confirmationShowing = true
                    })
                }
            }
        }
    }
}
