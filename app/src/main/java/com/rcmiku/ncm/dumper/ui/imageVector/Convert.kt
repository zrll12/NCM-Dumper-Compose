package com.rcmiku.ncm.dumper.ui.imageVector

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Convert: ImageVector
    get() {
        if (_Convert != null) {
            return _Convert!!
        }
        _Convert = ImageVector.Builder(
            name = "Convert",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color(0x00000000)),
                stroke = SolidColor(Color(0xFF000000)),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(3.0f, 12.0f)
                arcToRelative(9.0f, 9.0f, 0.0f, true, false, 9.0f, -9.0f)
                arcToRelative(9.75f, 9.75f, 0.0f, false, false, -6.74f, 2.74f)
                lineTo(3.0f, 8.0f)

                moveTo(3.0f, 3.0f)
                verticalLineToRelative(5.0f)
                horizontalLineToRelative(5.0f)
                close()
            }
        }.build()

        return _Convert!!
    }

@Suppress("ObjectPropertyName")
private var _Convert: ImageVector? = null