package com.example.depgen.view.fragments

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.depgen.Global
import com.example.depgen.model.Navigation
import com.example.depgen.utils.safeNavigate
import com.example.depgen.view.components.DefaultTopAppBar
import com.example.depgen.view.components.MemberSearchScreen


@Composable
fun MemberListPage() {
    BackHandler {
        safeNavigate("Master")
    }

    Scaffold (
        topBar = {
            DefaultTopAppBar("Master", Navigation.MEMBERLIST)
        }
    ) { innerPadding ->
        Column (
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text(
                text = "Member List",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 5.dp)
            )
            Text("Here, you can view the list of members in your database!")
            Spacer(modifier = Modifier.height(15.dp))
            MemberSearchScreen(
                onClickMember = {
                    safeNavigate("Profile/$it/${Navigation.MEMBERLIST}")
                },
                errorMessage = "Sorry, we didn't find any profiles matching your search query!",
                allowDelete = Global.isAdmin()
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
