package com.example.depgen.ui.fragments

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.depgen.model.Navigation
import com.example.depgen.ui.components.DefaultTopAppBar

@Composable
fun OTDPage() {
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

        }
    }
}