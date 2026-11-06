package io.github.artcraftandroid.procraft.ui

import android.content.Context
import android.content.SharedPreferences
import android.util.DisplayMetrics
import kotlin.math.min

/**
 * The size of the interface relative to the system's dp, for screens smaller than the tablets
 * the layout was drawn on. The canvas itself is untouched: only the bars, panels and text.
 *
 * Everything in the interface is in dp, sized for a tablet (the side bar alone is 519 dp tall),
 * so on a phone held sideways it fills the screen. Scaling the density the interface is laid out
 * with (see [ProCraftTheme]) shrinks all of it together.
 */
object UiScale {
    /** The preference's key (in "interface"); absent means automatic. */
    const val KEY = "ui_scale"
    const val MIN = 0.5f
    const val MAX = 1.2f

    /**
     * The shorter side, in dp, the interface is laid out for: the 519 dp side bar, the top bar
     * and their margins need about this much. A screen with at least this much is left as it is.
     */
    private const val LAYOUT_SHORT_SIDE_DP = 590f

    /** The scale that gives the interface [LAYOUT_SHORT_SIDE_DP] to lay out in (about 0.70 on a 411 dp wide phone). */
    fun auto(metrics: DisplayMetrics): Float {
        val shortPx = min(metrics.widthPixels, metrics.heightPixels).toFloat()
        val shortDp = shortPx / metrics.density
        if (!(shortDp > 0f)) return 1f
        return (shortDp / LAYOUT_SHORT_SIDE_DP).coerceIn(MIN, 1f)
    }

    /** The scale the person set, if they have. */
    fun stored(prefs: SharedPreferences): Float? = if (prefs.contains(KEY)) prefs.getFloat(KEY, 1f).coerceIn(MIN, MAX) else null

    /** The scale in use: the person's, else automatic. */
    fun effective(context: Context): Float {
        val prefs = context.getSharedPreferences("interface", Context.MODE_PRIVATE)
        return stored(prefs) ?: auto(context.resources.displayMetrics)
    }

    /** Pixels to a dp as the interface now measures it (what the native canvas draws its handles and grid by). */
    fun density(context: Context): Float = context.resources.displayMetrics.density * Pc.uiScale
}
