package com.example.depgen.ui.fragments

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
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
import com.example.depgen.ui.components.DefaultTopAppBar

@Composable
fun SettingsPage() {
    Scaffold (
        topBar = {
            DefaultTopAppBar("Master", Navigation.SETTINGS)
        }
    ) { innerPadding ->
        Column (
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))
            if (Global.idx == 1) {
                CardButton("Clear Data", {
                    //TODO: Alert
                    clear()
                    load()
                })
            } else {
                CardButton("Delete Account", {
                    //TODO: Alert
                    Global.profileList.removeAt(Global.idx)
                    switchProfile(LOGGED_OUT)
                    navController.navigate("Login")
                })
            }
        }
    }
}
