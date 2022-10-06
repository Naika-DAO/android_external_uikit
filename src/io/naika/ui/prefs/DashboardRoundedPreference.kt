package io.naika.ui.prefs

import android.content.Context
import android.util.AttributeSet
import io.naika.ui.R

open class DashboardRoundedPreference : RoundedPreference {

    override val preferenceLayoutRes: Int = R.layout.naika_dashboard_preference

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context) : super(context)

    init {
        layoutResource = preferenceLayoutRes
    }
}