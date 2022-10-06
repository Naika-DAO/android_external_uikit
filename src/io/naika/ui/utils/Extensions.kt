package io.naika.ui.utils

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.content.res.TypedArray
import android.util.TypedValue
import androidx.annotation.ColorInt
import androidx.preference.Preference
import androidx.preference.Preference.OnPreferenceClickListener

val Number.dp: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        toFloat(),
        Resources.getSystem().displayMetrics
    )

val Number.sp: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        toFloat(),
        Resources.getSystem().displayMetrics
    )

val Number.px: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_PX,
        toFloat(),
        Resources.getSystem().displayMetrics
    )

@ColorInt
fun Context.getColorAttrDefaultColor(attr: Int): Int {
    val ta: TypedArray = obtainStyledAttributes(intArrayOf(attr))
    @ColorInt val colorAccent: Int = ta.getColor(0, 0)
    ta.recycle()
    return colorAccent
}

fun Context.getColorAttr(attr: Int): ColorStateList? {
    val ta: TypedArray = obtainStyledAttributes(intArrayOf(attr))
    val stateList: ColorStateList? = try {
        ta.getColorStateList(0)
    } finally {
        ta.recycle()
    }
    return stateList
}