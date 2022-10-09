package io.naika.ui.controller

import androidx.preference.Preference
import androidx.preference.PreferenceScreen
import io.naika.ui.prefs.RoundedPreference
import io.naika.ui.utils.CornersSet

class PreferenceCornerHandler(private val screen: PreferenceScreen) {

    init {
        update()
    }

    private fun update(i: Int) {
        val pref = screen.getPreference(i)
        val count = screen.preferenceCount
        if (pref is RoundedPreference) {
            if (count == 1) {
                pref.updateMorphing(CornersSet.Full)
            } else if (i == 0 && count > 1) {
                pref.updateMorphing(CornersSet.Top)
                val afterPref: Preference = screen.getPreference(i + 1)
                if (afterPref !is RoundedPreference) {
                    pref.updateMorphing(CornersSet.Full)
                } else
                    pref.updateMorphing(CornersSet.Top)
            } else if (i < count - 1) {
                val beforePref: Preference = screen.getPreference(i - 1)
                val afterPref: Preference = screen.getPreference(i + 1)
                if ((beforePref is RoundedPreference &&
                            (beforePref.prefType == CornersSet.Top || beforePref.prefType == CornersSet.Middle)) ||
                    (afterPref is RoundedPreference &&
                            (afterPref.prefType == CornersSet.Middle || afterPref.prefType == CornersSet.Bottom))
                ) {
                    pref.updateMorphing(CornersSet.Middle)
                }
                if (afterPref !is RoundedPreference) {
                    pref.updateMorphing(CornersSet.Bottom)
                }
                if (beforePref !is RoundedPreference) {
                    pref.updateMorphing(CornersSet.Top)
                }
                if (beforePref !is RoundedPreference && afterPref !is RoundedPreference) {
                    pref.updateMorphing(CornersSet.Full)
                }
            } else if (i == count - 1) {
                val beforePref: Preference = screen.getPreference(i - 1)
                if (beforePref !is RoundedPreference) {
                    pref.updateMorphing(CornersSet.Full)
                } else
                    pref.updateMorphing(CornersSet.Bottom)
            }
        }
    }

    fun update() {
        for (i in 0 until screen.preferenceCount) {
            update(i)
        }
    }
}