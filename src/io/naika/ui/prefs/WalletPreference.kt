package io.naika.ui.prefs

import android.content.Context
import android.util.AttributeSet
import io.naika.ui.R

class WalletPreference : RoundedPreference {

    override var preferenceLayoutRes: Int = R.layout.naika_preference_wallet

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