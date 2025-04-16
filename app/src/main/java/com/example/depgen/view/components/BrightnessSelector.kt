
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.depgen.utils.applyBrightness
import kotlin.math.pow
import kotlin.math.roundToInt

@Composable
fun BrightnessSelector(
    selectedColor: Color,
    brightness: Float,
    onBrightnessChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 30.dp,
    handleRadius: Dp = 10.dp
) {
    val density = LocalDensity.current
    val handleRadiusPx = with(density) { handleRadius.toPx() }

    var sliderWidthPx by remember { mutableFloatStateOf(1f) }
    val effectiveSliderWidth = sliderWidthPx - 4 * handleRadiusPx
    val handleOffsetX = (brightness * effectiveSliderWidth)

    Box(
        modifier = modifier
            .height(height)
            .onSizeChanged { size ->
                sliderWidthPx = size.width.toFloat()
            }
            .padding(horizontal = handleRadius) // reserve space for handle
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(selectedColor, Color.Black)
                ),
                shape = CircleShape
            )
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, _ ->
                    val localX = change.position.x - handleRadiusPx
                    val clampedX = localX.coerceIn(0f, effectiveSliderWidth)
                    val newBrightness = (clampedX / effectiveSliderWidth).coerceIn(0f, 1f)
                    onBrightnessChange(newBrightness)
                }
            }
    ) {
        Surface(
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = handleOffsetX.roundToInt(),
                        y = 16
                    )
                }
                .size(handleRadius * 2),
            shape = CircleShape,
            color = selectedColor.applyBrightness(1 - brightness),
            border = BorderStroke(2.dp, Color.White.applyBrightness(1.2f * brightness.pow(2.7f)))
        ) {}
    }
}
