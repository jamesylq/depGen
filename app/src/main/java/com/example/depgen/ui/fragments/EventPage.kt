package com.example.depgen.ui.fragments

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.depgen.model.Navigation
import com.example.depgen.ui.components.DefaultTopAppBar

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
