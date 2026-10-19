package com.fridgeflats.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import com.fridgeflats.data.Category
import com.fridgeflats.data.Mood

/**
 * An original little blob resident. The body colour comes from the food
 * category; the face and the bobbing speed come from how close it is to its date.
 */
@Composable
fun ResidentFace(category: Category, mood: Mood, modifier: Modifier = Modifier) {
    val period = when (mood) {
        Mood.FINE -> 1400
        Mood.MILD -> 1100
        Mood.WORRIED -> 650
        Mood.CRITICAL -> 280
        Mood.GONE -> 2400
    }
    val transition = rememberInfiniteTransition(label = "bob")
    val bob by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(period, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "bobValue",
    )

    val body = if (mood == Mood.GONE) Color(0xFFDADDE3) else Color(category.color)

    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val dy = (bob - 0.5f) * h * if (mood == Mood.CRITICAL) 0.03f else 0.07f
        val dx = if (mood == Mood.CRITICAL) (bob - 0.5f) * w * 0.07f else 0f

        translate(left = dx, top = dy) {
            drawBody(body, mood, w, h)
            drawFace(mood, w, h)
        }
    }
}

private fun DrawScope.drawBody(body: Color, mood: Mood, w: Float, h: Float) {
    val alpha = if (mood == Mood.GONE) 0.75f else 1f
    // stem + leaf on top
    drawLine(
        color = lerp(body, Color.Black, 0.35f),
        start = Offset(w * 0.5f, h * 0.17f),
        end = Offset(w * 0.55f, h * 0.05f),
        strokeWidth = w * 0.05f,
        cap = StrokeCap.Round,
        alpha = alpha,
    )
    drawOval(
        color = if (mood == Mood.GONE) Color(0xFFB9BEC6) else Color(0xFF5DAA4B),
        topLeft = Offset(w * 0.55f, h * 0.02f),
        size = Size(w * 0.17f, w * 0.09f),
        alpha = alpha,
    )
    // body blob
    drawRoundRect(
        color = body,
        topLeft = Offset(w * 0.12f, h * 0.15f),
        size = Size(w * 0.76f, h * 0.75f),
        cornerRadius = CornerRadius(w * 0.34f, w * 0.34f),
        alpha = alpha,
    )
    // soft highlight
    drawOval(
        color = Color.White,
        topLeft = Offset(w * 0.24f, h * 0.22f),
        size = Size(w * 0.18f, h * 0.09f),
        alpha = 0.35f * alpha,
    )
}

private fun DrawScope.drawFace(mood: Mood, w: Float, h: Float) {
    val ink = Color(0xFF2B2B2B)
    val eyeY = h * 0.45f
    val ex1 = w * 0.38f
    val ex2 = w * 0.62f
    val er = w * 0.06f
    val line = w * 0.032f

    // eyes
    if (mood == Mood.GONE) {
        for (ex in listOf(ex1, ex2)) {
            drawLine(ink, Offset(ex - er, eyeY - er), Offset(ex + er, eyeY + er), strokeWidth = line, cap = StrokeCap.Round)
            drawLine(ink, Offset(ex - er, eyeY + er), Offset(ex + er, eyeY - er), strokeWidth = line, cap = StrokeCap.Round)
        }
    } else {
        for (ex in listOf(ex1, ex2)) {
            drawCircle(ink, radius = er, center = Offset(ex, eyeY))
            drawCircle(Color.White, radius = er * 0.35f, center = Offset(ex + er * 0.3f, eyeY - er * 0.3f))
        }
    }

    // worried eyebrows: inner ends raised
    if (mood == Mood.WORRIED || mood == Mood.CRITICAL) {
        drawLine(ink, Offset(ex1 - er * 1.4f, eyeY - er * 1.5f), Offset(ex1 + er * 1.1f, eyeY - er * 2.4f), strokeWidth = line * 0.8f, cap = StrokeCap.Round)
        drawLine(ink, Offset(ex2 + er * 1.4f, eyeY - er * 1.5f), Offset(ex2 - er * 1.1f, eyeY - er * 2.4f), strokeWidth = line * 0.8f, cap = StrokeCap.Round)
    }

    // blush when happy
    if (mood == Mood.FINE) {
        drawCircle(Color(0xFFFF6B6B), radius = w * 0.05f, center = Offset(w * 0.27f, h * 0.56f), alpha = 0.35f)
        drawCircle(Color(0xFFFF6B6B), radius = w * 0.05f, center = Offset(w * 0.73f, h * 0.56f), alpha = 0.35f)
    }

    // mouth
    val mouthY = h * 0.62f
    val mw = w * 0.15f
    val stroke = Stroke(width = line, cap = StrokeCap.Round)
    when (mood) {
        Mood.FINE -> drawArc(
            color = ink, startAngle = 20f, sweepAngle = 140f, useCenter = false,
            topLeft = Offset(w * 0.5f - mw, mouthY - mw * 0.8f), size = Size(mw * 2, mw * 1.4f), style = stroke,
        )
        Mood.MILD -> drawLine(
            ink, Offset(w * 0.5f - mw * 0.7f, mouthY), Offset(w * 0.5f + mw * 0.7f, mouthY),
            strokeWidth = line, cap = StrokeCap.Round,
        )
        Mood.WORRIED, Mood.CRITICAL -> drawArc(
            color = ink, startAngle = 200f, sweepAngle = 140f, useCenter = false,
            topLeft = Offset(w * 0.5f - mw, mouthY - mw * 0.1f), size = Size(mw * 2, mw * 1.4f), style = stroke,
        )
        Mood.GONE -> drawCircle(
            ink, radius = w * 0.04f, center = Offset(w * 0.5f, mouthY + mw * 0.2f),
            style = Stroke(width = line * 0.8f),
        )
    }

    // sweat drop on the last day
    if (mood == Mood.CRITICAL) {
        drawOval(
            color = Color(0xFF7EC8F2),
            topLeft = Offset(w * 0.76f, h * 0.27f),
            size = Size(w * 0.08f, h * 0.11f),
        )
    }
}
