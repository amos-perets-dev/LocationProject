package com.example.locationproject.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas

import android.graphics.drawable.BitmapDrawable

import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat


class IconUtil {

    fun drawableToBitmap(@DrawableRes drawableRes: Int, context: Context): Bitmap {

        val drawable = ContextCompat.getDrawable(
            context,
            drawableRes
        )

        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        val bitmap = Bitmap.createBitmap(
            drawable?.intrinsicWidth ?: 1,
            drawable?.intrinsicHeight ?: 1,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable?.setBounds(0, 0, canvas.width, canvas.height)
        drawable?.draw(canvas)
        return bitmap
    }


}