
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.FileProvider
import com.example.depgen.ctxt
import java.io.File


@Composable
fun UriFromCamera(
    onImageUriReady: (Uri) -> Unit,
    context: Context = ctxt
) {
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && photoUri != null) {
            onImageUriReady(photoUri!!)
        }
    }

    LaunchedEffect (Unit) {
        val photoFile = File.createTempFile("IMG_", ".jpg", context.cacheDir)
        photoUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            photoFile
        )
        cameraLauncher.launch(photoUri!!)
    }
}

@Composable
fun UriFromGallery(
    onImageUriReady: (Uri) -> Unit
) {
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            onImageUriReady(it)
        }
    }

    LaunchedEffect (Unit) {
        galleryLauncher.launch("image/*")
    }
}
