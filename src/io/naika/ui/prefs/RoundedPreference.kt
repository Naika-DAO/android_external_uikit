package io.naika.ui.prefs

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.util.AttributeSet
import android.view.View
import androidx.preference.PreferenceViewHolder
import androidx.recyclerview.widget.RecyclerView
import io.naika.ui.R
import io.naika.ui.utils.CornersSet
import io.naika.ui.utils.LayoutMargin
import io.naika.ui.utils.getColorAttrDefaultColor
import kotlin.math.roundToInt

open class RoundedPreference : ExtendedPreference {

    open val preferenceLayoutRes: Int = R.layout.naika_preference

    var attributes: AttributeSet? = null
    var definedStyles: Int = 0

    var useTintentStyle: Boolean = false
        protected set

    var showActionArrow: Boolean = false
        protected set


    private val rippleColor: Int
        get() = context.getColor(R.color.naika_pref_ripple)

    private val backgroundColor: Int
        get() {
            return if (!useTintentStyle) context.getColor(R.color.naika_pref_color)
            else context.getColorAttrDefaultColor(android.R.attr.colorAccent)
        }

    private var rootView: View? = null

    private var type: Int = 3

    var prefType: CornersSet = CornersSet.Invalid

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        attributes = attrs
        definedStyles = defStyleAttr
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        attributes = attrs
        definedStyles = defStyleAttr
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        attributes = attrs
    }

    constructor(context: Context) : super(context)

    init {
        layoutResource = preferenceLayoutRes
        attributes.let {
            val style = context.obtainStyledAttributes(
                it,
                R.styleable.RoundedPreference, definedStyles, 0
            )
            useTintentStyle = style.getBoolean(R.styleable.RoundedPreference_useTintendStyle, false)
            showActionArrow = style.getBoolean(R.styleable.RoundedPreference_showActionArrow, false)
            type = style.getInt(R.styleable.RoundedPreference_backgroundType, 3)
            style.recycle()
        }
    }

    fun updateMorphing(cornersSet: CornersSet) {
        //if (prefType == cornersSet) return
        val roundCorners = floatArrayOf(
            cornersSet.topLeft.dp, cornersSet.topLeft.dp,
            cornersSet.topRight.dp, cornersSet.topRight.dp,
            cornersSet.bottomRight.dp, cornersSet.bottomRight.dp,
            cornersSet.bottomLeft.dp, cornersSet.bottomLeft.dp
        )
        rootView?.let {
            it.background = applyBackground(roundCorners)
        }
        prefType = cornersSet
        val tyo = when (cornersSet) {
            CornersSet.Bottom -> LayoutMargin.PreferenceBottom
            CornersSet.Middle -> LayoutMargin.PreferenceMiddle
            CornersSet.Top -> LayoutMargin.PreferenceTop
            CornersSet.Full -> LayoutMargin.PreferenceFull
            else -> LayoutMargin.PreferenceFull
        }
        updateContentMargins(tyo)
    }

    private fun applyBackground(roundCorners: FloatArray): RippleDrawable {
        val shape = ShapeDrawable().apply {
            clearColorFilter()
            paint.color = backgroundColor
            shape = RoundRectShape(roundCorners, null, null)
        }
        return RippleDrawable(getPressedColorSelector(Color.TRANSPARENT, rippleColor), shape, shape)
    }

    private fun updateContentMargins(margin: LayoutMargin) {
        rootView?.let {
            val lp = it.layoutParams as RecyclerView.LayoutParams
            with(lp) {
                topMargin = margin.top.roundToInt()
                bottomMargin = margin.bottom.roundToInt()
                marginStart = margin.left.roundToInt()
                marginEnd = margin.right.roundToInt()
            }
            it.layoutParams = lp
        }
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        rootView = holder.itemView
        updateMorphing(prefType)
    }

    private fun getPressedColorSelector(normalColor: Int, pressedColor: Int): ColorStateList {
        return ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_pressed),
                intArrayOf(android.R.attr.state_focused),
                intArrayOf(android.R.attr.state_activated),
                intArrayOf()
            ), intArrayOf(
                pressedColor,
                pressedColor,
                pressedColor,
                normalColor
            )
        )
    }
}