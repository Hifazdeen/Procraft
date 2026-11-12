package io.github.artcraftandroid.procraft.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** How tall a bottom sheet is, as a share of the screen, for each of its stops: small, ordinary, tall. */
val SheetStops = floatArrayOf(0.42f, 0.74f, 0.92f)

/**
 * The grabber over a bottom sheet (Procraft Pocket's swipe card): dragged up it stretches the
 * sheet to its next stop, dragged down it shrinks it, and dragged down from the smallest it closes it.
 */
@Composable
fun SheetHandle(stop: Int, onStop: (Int) -> Unit, onClose: () -> Unit, width: Dp, modifier: Modifier = Modifier) {
    val haptics = LocalHapticFeedback.current
    val current = rememberUpdatedState(stop)
    Box(
        modifier.width(width).height(30.dp).pointerInput(Unit) {
            var total = 0f
            detectVerticalDragGestures(
                onDragStart = { total = 0f },
                onDragEnd = {
                    val step = 40.dp.toPx()
                    val now = current.value
                    if (total < -step && now < SheetStops.size - 1) {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onStop(now + 1)
                    } else if (total > step) {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        if (now > 0) onStop(now - 1) else onClose()
                    }
                },
                onDragCancel = {},
                onVerticalDrag = { change, amount ->
                    total += amount
                    change.consume()
                },
            )
        },
        contentAlignment = Alignment.Center,
    ) {
        Box(Modifier.size(46.dp, 5.dp).clip(RoundedCornerShape(50)).background(Color.White.copy(alpha = 0.75f)))
    }
}
