package com.example.depgen.model

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.depgen.R
import com.example.depgen.utils.toImageBitmap
import com.example.depgen.utils.toSquare
import kotlinx.serialization.Serializable


@Serializable
data class ProfileLuxury(
    var profilePicture: String? = null,
    var preferences: HashMap<String, Int> = HashMap()
) {
    @Composable
    fun ProfilePicture(clip: Shape = RectangleShape, size: Dp = 256.dp) {
        if (profilePicture == null) {
            Image(
                painter = painterResource(R.drawable.icon_512),
                contentDescription = "",
                modifier = Modifier
                    .clip(clip)
                    .size(size)
            )
        } else {
            Image(
                bitmap = profilePicture!!.toImageBitmap()!!.toSquare(),
                contentDescription = "",
                modifier = Modifier
                    .clip(clip)
                    .size(size)
            )
        }
    }
}
