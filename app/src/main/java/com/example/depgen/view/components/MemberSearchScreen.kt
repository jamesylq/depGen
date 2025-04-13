package com.example.depgen.view.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.depgen.Global
import com.example.depgen.R
import com.example.depgen.model.Profile
import com.example.depgen.utils.clearFocusOnKeyboardDismiss
import com.example.depgen.utils.save

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MemberSearchScreen(
    onClickMember: (Int) -> Unit,
    errorMessage: String,
    exclude: Set<Int>? = null,
    allowDelete: Boolean = false
) {
    var searchQuery by remember { mutableStateOf("") }
    var deleting by remember { mutableIntStateOf(-1) }
    val profileRem = remember { mutableStateListOf<Profile>() }
    val profilesFiltered = remember { mutableStateListOf<Int>() }

    if (deleting != -1) {
        ConfirmationScreen(
            onConfirm = {
                Global.profileList.removeAt(deleting)
                profileRem.removeAt(deleting)
                save()
                deleting = -1
            },
            onDecline = {
                deleting = -1
            },
            body = "This action is irreversible!"
        )
    }

    profileRem.clear()
    profilesFiltered.clear()
    for (i in 0 ..< Global.profileList.size) {
        profileRem.add(Global.profileList[i])
        if (exclude != null && exclude.contains(i)) continue
        if (i >= minOf(2, Global.idx) &&
            (searchQuery.isBlank() ||
            Global.profileList[i].username.contains(searchQuery, true))
        ) profilesFiltered.add(i)
    }

    OutlinedTextField(
        value = searchQuery,
        onValueChange = {
            searchQuery = it
            profilesFiltered.clear()
            for (i in minOf(2, Global.idx) ..< Global.profileList.size) {
                if (searchQuery.isBlank() ||
                    Global.profileList[i].username.contains(searchQuery, true)) {
                    profilesFiltered.add(i)
                }
            }
        },
        placeholder = {
            Row {
                Icon(
                    Icons.Rounded.Search,
                    "",
                )
                Text("Enter Search Query", modifier = Modifier.padding(start = 10.dp))
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .clearFocusOnKeyboardDismiss(),
        trailingIcon = {
            Icon(
                Icons.Rounded.MoreVert,
                "",
                modifier = Modifier.clickable {
                    //TODO: Advanced Search
                }
            )
        }
    )
    Spacer(modifier = Modifier.height(20.dp))
    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            if (profilesFiltered.isNotEmpty()) {
                for (i in 0 ..< profilesFiltered.size) {
                    item {
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(114.dp)
                                .padding(vertical = 7.dp),
                            onClick = {
                                onClickMember(profilesFiltered[i])
                            }
                        ) {
                            Column {
                                Row {
                                    Image(
                                        //TODO: Profile Picture
                                        painter = painterResource(R.drawable.icon_512),
                                        contentDescription = "",
                                        modifier = Modifier.size(100.dp)
                                    )
                                    Text(
                                        Global.profileList[profilesFiltered[i]].username,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 19.sp,
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .weight(1f)
                                    )
                                    if (allowDelete) {
                                        IconButton(
                                            onClick = {
                                                deleting = profilesFiltered[i]
                                            }
                                        ) {
                                            Icon(Icons.Default.Delete, "")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Spacer(modifier = Modifier.height(100.dp))
                        Text(
                            text = errorMessage,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                        )
                    }
                }
            }
        }
    }
}