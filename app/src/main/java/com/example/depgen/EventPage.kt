package com.example.depgen

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun EventPage(idx: Int) {
    Scaffold (
        topBar = {
            DefaultTopAppBar("EventList", Navigation.EVENT + Navigation.M * idx)
        }
    ) {
        LazyColumn (
            modifier = Modifier.padding(it)
        ) {

        }
    }
}
