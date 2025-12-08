package ru.shrprnbw.ideas.presentation.ui.theme

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

object CustomIcons {

    val Key: ImageVector
        get() {
            if (_Key != null) return _Key!!

            _Key = ImageVector.Builder(
                name = "Key",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 960f,
                viewportHeight = 960f
            ).apply {
                path(
                    fill = SolidColor(Color(0xFF000000))
                ) {
                    moveTo(280f, 560f)
                    quadToRelative(-33f, 0f, -56.5f, -23.5f)
                    reflectiveQuadTo(200f, 480f)
                    reflectiveQuadToRelative(23.5f, -56.5f)
                    reflectiveQuadTo(280f, 400f)
                    reflectiveQuadToRelative(56.5f, 23.5f)
                    reflectiveQuadTo(360f, 480f)
                    reflectiveQuadToRelative(-23.5f, 56.5f)
                    reflectiveQuadTo(280f, 560f)
                    moveToRelative(0f, 160f)
                    quadToRelative(-100f, 0f, -170f, -70f)
                    reflectiveQuadTo(40f, 480f)
                    reflectiveQuadToRelative(70f, -170f)
                    reflectiveQuadToRelative(170f, -70f)
                    quadToRelative(67f, 0f, 121.5f, 33f)
                    reflectiveQuadToRelative(86.5f, 87f)
                    horizontalLineToRelative(352f)
                    lineToRelative(120f, 120f)
                    lineToRelative(-180f, 180f)
                    lineToRelative(-80f, -60f)
                    lineToRelative(-80f, 60f)
                    lineToRelative(-85f, -60f)
                    horizontalLineToRelative(-47f)
                    quadToRelative(-32f, 54f, -86.5f, 87f)
                    reflectiveQuadTo(280f, 720f)
                    moveToRelative(0f, -80f)
                    quadToRelative(56f, 0f, 98.5f, -34f)
                    reflectiveQuadToRelative(56.5f, -86f)
                    horizontalLineToRelative(125f)
                    lineToRelative(58f, 41f)
                    lineToRelative(82f, -61f)
                    lineToRelative(71f, 55f)
                    lineToRelative(75f, -75f)
                    lineToRelative(-40f, -40f)
                    horizontalLineTo(435f)
                    quadToRelative(-14f, -52f, -56.5f, -86f)
                    reflectiveQuadTo(280f, 320f)
                    quadToRelative(-66f, 0f, -113f, 47f)
                    reflectiveQuadToRelative(-47f, 113f)
                    reflectiveQuadToRelative(47f, 113f)
                    reflectiveQuadToRelative(113f, 47f)
                }
            }.build()

            return _Key!!
        }

    private var _Key: ImageVector? = null

    val Mic: ImageVector
        get() {
            if (_Mic != null) return _Mic!!

            _Mic = ImageVector.Builder(
                name = "Mic",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 960f,
                viewportHeight = 960f
            ).apply {
                path(
                    fill = SolidColor(Color(0xFF000000))
                ) {
                    moveTo(480f, 560f)
                    quadToRelative(-50f, 0f, -85f, -35f)
                    reflectiveQuadToRelative(-35f, -85f)
                    verticalLineToRelative(-240f)
                    quadToRelative(0f, -50f, 35f, -85f)
                    reflectiveQuadToRelative(85f, -35f)
                    reflectiveQuadToRelative(85f, 35f)
                    reflectiveQuadToRelative(35f, 85f)
                    verticalLineToRelative(240f)
                    quadToRelative(0f, 50f, -35f, 85f)
                    reflectiveQuadToRelative(-85f, 35f)
                    moveToRelative(-40f, 280f)
                    verticalLineToRelative(-123f)
                    quadToRelative(-104f, -14f, -172f, -93f)
                    reflectiveQuadToRelative(-68f, -184f)
                    horizontalLineToRelative(80f)
                    quadToRelative(0f, 83f, 58.5f, 141.5f)
                    reflectiveQuadTo(480f, 640f)
                    reflectiveQuadToRelative(141.5f, -58.5f)
                    reflectiveQuadTo(680f, 440f)
                    horizontalLineToRelative(80f)
                    quadToRelative(0f, 105f, -68f, 184f)
                    reflectiveQuadToRelative(-172f, 93f)
                    verticalLineToRelative(123f)
                    close()
                    moveToRelative(40f, -360f)
                    quadToRelative(17f, 0f, 28.5f, -11.5f)
                    reflectiveQuadTo(520f, 440f)
                    verticalLineToRelative(-240f)
                    quadToRelative(0f, -17f, -11.5f, -28.5f)
                    reflectiveQuadTo(480f, 160f)
                    reflectiveQuadToRelative(-28.5f, 11.5f)
                    reflectiveQuadTo(440f, 200f)
                    verticalLineToRelative(240f)
                    quadToRelative(0f, 17f, 11.5f, 28.5f)
                    reflectiveQuadTo(480f, 480f)
                }
            }.build()

            return _Mic!!
        }

    private var _Mic: ImageVector? = null

    val Magic: ImageVector
        get() {
            if (_Magic_button != null) return _Magic_button!!

            _Magic_button = ImageVector.Builder(
                name = "Magic_button",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(
                    fill = SolidColor(Color(0xFF000000))
                ) {
                    moveToRelative(10f, 19f)
                    lineToRelative(-2.5f, -5.5f)
                    lineTo(2f, 11f)
                    lineToRelative(5.5f, -2.5f)
                    lineTo(10f, 3f)
                    lineToRelative(2.5f, 5.5f)
                    lineTo(18f, 11f)
                    lineToRelative(-5.5f, 2.5f)
                    close()
                    moveToRelative(8f, 2f)
                    lineToRelative(-1.25f, -2.75f)
                    lineTo(14f, 17f)
                    lineToRelative(2.75f, -1.25f)
                    lineTo(18f, 13f)
                    lineToRelative(1.25f, 2.75f)
                    lineTo(22f, 17f)
                    lineToRelative(-2.75f, 1.25f)
                    close()
                }
            }.build()

            return _Magic_button!!
        }

    private var _Magic_button: ImageVector? = null

    val AudioFile: ImageVector
        get() {
            if (_Audio_file != null) return _Audio_file!!

            _Audio_file = ImageVector.Builder(
                name = "Audio_file",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 960f,
                viewportHeight = 960f
            ).apply {
                path(
                    fill = SolidColor(Color(0xFF000000))
                ) {
                    moveTo(430f, 760f)
                    quadToRelative(38f, 0f, 64f, -26f)
                    reflectiveQuadToRelative(26f, -64f)
                    verticalLineToRelative(-150f)
                    horizontalLineToRelative(120f)
                    verticalLineToRelative(-80f)
                    horizontalLineTo(480f)
                    verticalLineToRelative(155f)
                    quadToRelative(-11f, -8f, -23.5f, -11.5f)
                    reflectiveQuadTo(430f, 580f)
                    quadToRelative(-38f, 0f, -64f, 26f)
                    reflectiveQuadToRelative(-26f, 64f)
                    reflectiveQuadToRelative(26f, 64f)
                    reflectiveQuadToRelative(64f, 26f)
                    moveTo(240f, 880f)
                    quadToRelative(-33f, 0f, -56.5f, -23.5f)
                    reflectiveQuadTo(160f, 800f)
                    verticalLineToRelative(-640f)
                    quadToRelative(0f, -33f, 23.5f, -56.5f)
                    reflectiveQuadTo(240f, 80f)
                    horizontalLineToRelative(320f)
                    lineToRelative(240f, 240f)
                    verticalLineToRelative(480f)
                    quadToRelative(0f, 33f, -23.5f, 56.5f)
                    reflectiveQuadTo(720f, 880f)
                    close()
                    moveToRelative(280f, -520f)
                    verticalLineToRelative(-200f)
                    horizontalLineTo(240f)
                    verticalLineToRelative(640f)
                    horizontalLineToRelative(480f)
                    verticalLineToRelative(-440f)
                    close()
                    moveTo(240f, 160f)
                    verticalLineToRelative(200f)
                    close()
                    verticalLineToRelative(640f)
                    close()
                }
            }.build()

            return _Audio_file!!
        }

    private var _Audio_file: ImageVector? = null

    val Tag: ImageVector
        get() {
            if (_Sell != null) return _Sell!!

            _Sell = ImageVector.Builder(
                name = "Sell",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 960f,
                viewportHeight = 960f
            ).apply {
                path(
                    fill = SolidColor(Color(0xFF000000)),
                    pathFillType = androidx.compose.ui.graphics.PathFillType.EvenOdd
                ) {
                    moveTo(856f, 570f)
                    lineTo(570f, 856f)
                    quadToRelative(-12f, 12f, -27f, 18f)
                    reflectiveQuadToRelative(-30f, 6f)
                    reflectiveQuadToRelative(-30f, -6f)
                    reflectiveQuadToRelative(-27f, -18f)
                    lineTo(103f, 503f)
                    quadToRelative(-11f, -11f, -17f, -25.5f)
                    reflectiveQuadTo(80f, 447f)
                    verticalLineToRelative(-287f)
                    quadToRelative(0f, -33f, 23.5f, -56.5f)
                    reflectiveQuadTo(160f, 80f)
                    horizontalLineToRelative(287f)
                    quadToRelative(16f, 0f, 31f, 6.5f)
                    reflectiveQuadToRelative(26f, 17.5f)
                    lineToRelative(352f, 353f)
                    quadToRelative(12f, 12f, 17.5f, 27f)
                    reflectiveQuadToRelative(5.5f, 30f)
                    reflectiveQuadToRelative(-5.5f, 29.5f)
                    reflectiveQuadTo(856f, 570f)
                    moveTo(513f, 800f)
                    lineToRelative(286f, -286f)
                    lineToRelative(-353f, -354f)
                    horizontalLineTo(160f)
                    verticalLineToRelative(286f)
                    close()
                    moveTo(260f, 320f)
                    quadToRelative(25f, 0f, 42.5f, -17.5f)
                    reflectiveQuadTo(320f, 260f)
                    reflectiveQuadToRelative(-17.5f, -42.5f)
                    reflectiveQuadTo(260f, 200f)
                    reflectiveQuadToRelative(-42.5f, 17.5f)
                    reflectiveQuadTo(200f, 260f)
                    reflectiveQuadToRelative(17.5f, 42.5f)
                    reflectiveQuadTo(260f, 320f)
                    moveToRelative(220f, 160f)
                }
            }.build()

            return _Sell!!
        }

    private var _Sell: ImageVector? = null

    val Pause: ImageVector
        get() {
            if (_Pause != null) return _Pause!!

            _Pause = ImageVector.Builder(
                name = "Pause",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 960f,
                viewportHeight = 960f
            ).apply {
                path(
                    fill = SolidColor(Color(0xFF000000)),
                    pathFillType = androidx.compose.ui.graphics.PathFillType.EvenOdd
                ) {
                    moveTo(520f, 760f)
                    verticalLineToRelative(-560f)
                    horizontalLineToRelative(240f)
                    verticalLineToRelative(560f)
                    close()
                    moveToRelative(-320f, 0f)
                    verticalLineToRelative(-560f)
                    horizontalLineToRelative(240f)
                    verticalLineToRelative(560f)
                    close()
                    moveToRelative(-320f, 0f)
                    horizontalLineToRelative(80f)
                    verticalLineToRelative(-400f)
                    horizontalLineToRelative(-80f)
                    close()
                    moveToRelative(0f, -400f)
                    verticalLineToRelative(400f)
                    close()
                    moveToRelative(320f, 0f)
                    verticalLineToRelative(400f)
                    close()
                }
            }.build()

            return _Pause!!
        }

    private var _Pause: ImageVector? = null

}