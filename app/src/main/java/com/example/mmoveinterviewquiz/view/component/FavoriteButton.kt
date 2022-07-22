package com.example.mmoveinterviewquiz.view.component

import android.content.Context
import android.util.AttributeSet
import com.example.mmoveinterviewquiz.R

class FavoriteButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : androidx.appcompat.widget.AppCompatImageView(context, attrs, defStyleAttr){

    var isFavorite: Boolean = false
        set(value) {
            field = value
            setImageResource(
                when(value) {
                    true -> R.drawable.ic_fav_on
                    false -> R.drawable.ic_fav_off
                }
            )
        }
}