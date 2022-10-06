package io.naika.ui.utils

sealed class Corners(val dp: Float) {
    object Large : Corners(28.dp)
    object Small : Corners(5.dp)
    object Invalid : Corners(1251.dp)
}

sealed class CornersSet(
    val topLeft: Corners,
    val topRight: Corners,
    val bottomLeft: Corners,
    val bottomRight: Corners
) {
    object Top : CornersSet(Corners.Large, Corners.Large, Corners.Small, Corners.Small)
    object Middle : CornersSet(Corners.Small, Corners.Small, Corners.Small, Corners.Small)
    object Bottom : CornersSet(Corners.Small, Corners.Small, Corners.Large, Corners.Large)
    object Full : CornersSet(Corners.Large, Corners.Large, Corners.Large, Corners.Large)

    object Invalid : CornersSet(Corners.Invalid, Corners.Invalid, Corners.Invalid, Corners.Invalid)

    override fun equals(other: Any?): Boolean {
        if (other !is CornersSet) return false
        return this.topLeft == other.topLeft && this.topRight == other.topRight && this.bottomLeft == other.bottomLeft && this.bottomRight == other.bottomRight
    }
}