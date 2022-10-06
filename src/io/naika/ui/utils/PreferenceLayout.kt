package io.naika.ui.utils

sealed class LayoutMargin(
    val top: Float = 1.dp,
    val left: Float = 16.dp,
    val bottom: Float = 1.dp,
    val right: Float = 16.dp
) {
    object PreferenceTop : LayoutMargin(
        top = 16.dp,
    )

    object PreferenceMiddle : LayoutMargin()
    object PreferenceBottom : LayoutMargin(
        bottom = 0.dp,
    )

    object PreferenceFull : LayoutMargin(
        top = 16.dp
    )

    object PreferenceFullAccent : LayoutMargin(
        top = 16.dp,
        bottom = 16.dp,
    )
}