package com.example.depgen.view.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.depgen.Global
import com.example.depgen.luxuryManager
import com.example.depgen.utils.safeNavigate

@Composable
fun TopBarProfileIcon(dest: String) {
    IconButton(
        onClick = {
            safeNavigate(dest)
        }
    ) {
        luxuryManager.getLuxury(Global.profileList[Global.idx]).ProfilePicture(
            CircleShape,
            64.dp
        )
    }
    Spacer(modifier = Modifier.width(6.dp))
}
