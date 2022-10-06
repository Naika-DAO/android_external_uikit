package io.naika.ui.prefs

import android.content.Context
import android.util.AttributeSet
import androidx.preference.Preference

/**
 * Simple extension of basic Android Preference that allows multiple
 * instances of Preference.OnPreferenceClickListener
 */
open class ExtendedPreference : Preference {

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

    private val onClickList = ArrayList<OnPreferenceClickListener>()

    init {
        setOnPreferenceClickListener {
            for (listener in onClickList) {
                listener.onPreferenceClick(it)
            }
            onClickList.isNotEmpty()
        }
    }

    fun addOnPreferenceClickListener(listener: OnPreferenceClickListener) {
        onClickList.add(listener)
    }

    fun removeOnPreferenceClickListener(listener: OnPreferenceClickListener) {
        onClickList.remove(listener)
    }

    fun clearOnPreferenceClickListener() {
        onClickList.clear()
    }

}