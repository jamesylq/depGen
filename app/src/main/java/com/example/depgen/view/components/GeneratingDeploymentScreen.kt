package com.example.depgen.view.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.depgen.model.EventComponent
import com.example.depgen.utils.OTDCompleteSearchHelper
import com.example.depgen.utils.save


@Composable
fun GeneratingDeploymentScreen(component: EventComponent,  onExit: () -> Unit) {
    var canExit by remember { mutableStateOf(true) }

    ConfirmationScreen(
        onConfirm = {
            canExit = false
            component.setDeployment(OTDCompleteSearchHelper(component).generate())
            save()
            onExit()
        },
        onDecline = {
            if (canExit) {
                onExit()
            }
        },
        body = "This will automatically generate\nan Event Deployment!"
    )
}