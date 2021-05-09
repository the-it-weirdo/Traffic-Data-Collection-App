package dev.debaleen.project20050120.util

import android.view.View

fun View.hide() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.disable() {
    isEnabled = false
}

fun View.enable() {
    isEnabled = true
}