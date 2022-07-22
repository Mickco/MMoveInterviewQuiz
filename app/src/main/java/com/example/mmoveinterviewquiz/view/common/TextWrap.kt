package com.example.mmoveinterviewquiz.view.common

import android.content.Context
import androidx.annotation.StringRes

abstract class TextWrap {
    abstract fun getString(context: Context): String
}

data class StringWrap(private val string: String = ""): TextWrap(){
    override fun getString(context: Context): String {
        return string
    }
}

data class ResourceWrap(@StringRes private val resId: Int?) : TextWrap() {
    override fun getString(context: Context): String {
        return resId?.let { context.getString(resId) }.orEmpty()
    }
}

data class FormatWrap(
    private val format: TextWrap,
    private val targets: List<TextWrap>
) : TextWrap() {
    constructor(format: TextWrap, vararg targets: TextWrap) : this(format, targets.toList())

    override fun getString(context: Context): String {
        val formatString = format.getString(context)
        val targetsString = targets.map { it.getString(context) }.toTypedArray()
        return formatString.format(*targetsString)
    }
}