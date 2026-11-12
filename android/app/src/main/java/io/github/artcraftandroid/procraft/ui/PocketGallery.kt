package io.github.artcraftandroid.procraft.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** One of the select bar's actions: its name, whether it can act now, and what it does. */
class PocketAction(val label: String, val enabled: Boolean, val onClick: () -> Unit)

private val PocketPill = 56.dp

/**
 * The phone Gallery's floating bar (Procraft Pocket), along the bottom edge: a rounded capsule of
 * Select, Import and Photo, and beside it a round button for a new canvas. While selecting, the capsule
 * holds the actions for the chosen artworks (scrolling sideways if they do not fit) and a blue round
 * button ends selecting.
 */
@Composable
fun PocketGalleryBar(
    selecting: Boolean,
    actions: List<PocketAction>,
    onSelect: () -> Unit,
    onImport: () -> Unit,
    onPhoto: () -> Unit,
    onNew: () -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 22.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
    ) {
        val capsule = Modifier.height(PocketPill).clip(CircleShape).background(Pc.Capsule).border(1.dp, Pc.CapsuleEdge, CircleShape)
        if (selecting) {
            Row(Modifier.weight(1f, fill = false).then(capsule).horizontalScroll(rememberScrollState()), verticalAlignment = Alignment.CenterVertically) {
                for (a in actions) {
                    Box(
                        Modifier.height(PocketPill).clickable(enabled = a.enabled, onClick = a.onClick).padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(a.label, color = if (a.enabled) Pc.GalleryTitle else Pc.GallerySize, fontSize = 16.sp, maxLines = 1, softWrap = false)
                    }
                }
            }
            Spacer(Modifier.width(10.dp))
            Box(Modifier.size(PocketPill).clip(CircleShape).background(Color(0xFF0190FF)).clickable(onClick = onDone), contentAlignment = Alignment.Center) {
                Icon(PcIcons.DoneTick, contentDescription = "Done", tint = Color.White, modifier = Modifier.requiredSize(44.dp))
            }
        } else {
            Row(capsule, verticalAlignment = Alignment.CenterVertically) {
                PocketGalleryButton("Select", onSelect) { selectGlyph(it) }
                PocketGalleryButton("Import file", onImport) { fileGlyph(it) }
                PocketGalleryButton("Import photo", onPhoto) { photoGlyph(it) }
            }
            Spacer(Modifier.width(10.dp))
            Box(
                Modifier.size(PocketPill).clip(CircleShape).background(Pc.Capsule).border(1.dp, Pc.CapsuleEdge, CircleShape).clickable(onClick = onNew),
                contentAlignment = Alignment.Center,
            ) {
                Icon(PcIcons.Add, contentDescription = "New canvas", tint = Pc.GalleryText, modifier = Modifier.requiredSize(48.dp))
            }
        }
    }
}

/** A button of the capsule: its glyph, drawn at 28 dp in the gallery's text colour. */
@Composable
private fun PocketGalleryButton(label: String, onClick: () -> Unit, glyph: DrawScope.(Color) -> Unit) {
    val ink = Pc.GalleryText
    Box(Modifier.size(PocketPill + 8.dp, PocketPill).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Box(Modifier.size(28.dp).drawBehind { glyph(ink) })
    }
}

private fun DrawScope.stroke(): Stroke = Stroke(2.2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)

/** A ring with a tick. */
private fun DrawScope.selectGlyph(ink: Color) {
    val s = size.minDimension
    val w = 2.2.dp.toPx()
    drawCircle(ink, radius = s / 2f - w / 2f, style = Stroke(w))
    val tick = Path().apply {
        moveTo(s * 0.29f, s * 0.52f)
        lineTo(s * 0.44f, s * 0.67f)
        lineTo(s * 0.72f, s * 0.35f)
    }
    drawPath(tick, ink, style = stroke())
}

/** A page with a folded corner. */
private fun DrawScope.fileGlyph(ink: Color) {
    val s = size.minDimension
    val page = Path().apply {
        moveTo(s * 0.22f, s * 0.08f)
        lineTo(s * 0.58f, s * 0.08f)
        lineTo(s * 0.80f, s * 0.30f)
        lineTo(s * 0.80f, s * 0.92f)
        lineTo(s * 0.22f, s * 0.92f)
        close()
    }
    drawPath(page, ink, style = stroke())
    val fold = Path().apply {
        moveTo(s * 0.58f, s * 0.08f)
        lineTo(s * 0.58f, s * 0.30f)
        lineTo(s * 0.80f, s * 0.30f)
    }
    drawPath(fold, ink, style = stroke())
}

/** A picture: its frame, a sun and a hill. */
private fun DrawScope.photoGlyph(ink: Color) {
    val s = size.minDimension
    drawRoundRect(ink, Offset(s * 0.08f, s * 0.18f), Size(s * 0.84f, s * 0.64f), CornerRadius(s * 0.12f), style = stroke())
    drawCircle(ink, radius = s * 0.07f, center = Offset(s * 0.32f, s * 0.40f))
    val hill = Path().apply {
        moveTo(s * 0.12f, s * 0.74f)
        lineTo(s * 0.40f, s * 0.52f)
        lineTo(s * 0.58f, s * 0.68f)
        lineTo(s * 0.70f, s * 0.58f)
        lineTo(s * 0.88f, s * 0.74f)
    }
    drawPath(hill, ink, style = stroke())
}
