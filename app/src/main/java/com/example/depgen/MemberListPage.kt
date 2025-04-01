package com.example.depgen

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

@Composable
fun MemberListPage() {
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
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(20.dp))
            MemberSearchScreen(
                onClickMember = {
                    navController.navigate("Profile/$it/${Navigation.MEMBERLIST}")
                },
                errorMessage = "Sorry, we didn't find any profiles matching your search query!"
            )
            Spacer(modifier = Modifier.height(20.dp))
            if (Global.profile == ADMIN) {
                CardButton(
                    text = "Add Member",
                    onClick = {
                        //TODO: Add Member
//                        val profile = Profile("test", "test")
//                        profileRem.add(profile)
//                        Global.profileList.add(profile)
//
//                        profilesFiltered.clear()
//                        for (i in minOf(2, Global.idx) ..< Global.profileList.size) {
//                            if (searchQuery.isBlank() ||
//                                profileRem[i].username.contains(searchQuery, true)
//                            ) {
//                                profilesFiltered.add(i)
//                            }
//                        }
                    }
                )
            }
        }
    }
}
