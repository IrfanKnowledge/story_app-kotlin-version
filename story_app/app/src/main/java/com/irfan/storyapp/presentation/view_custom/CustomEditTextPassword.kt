package com.irfan.storyapp.presentation.view_custom

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.irfan.storyapp.R

class CustomEditTextPassword @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
) : AppCompatEditText(context, attrs), View.OnTouchListener {

    private var isPasswordVisible: Boolean = false

    init {
        showVisibilityButtonImageOn()

        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // Do nothing
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().length < 8) {
                    val messageError = context.getString(R.string.password_validate_length_min)
                    setError(messageError, null)
                } else {
                    error = null
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                // Do nothing
            }
        })
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null,
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }

    private fun getDrawableVisibility(isVisible: Boolean): Drawable {
        return ContextCompat.getDrawable(
            context,
            if (isVisible) R.drawable.baseline_visibility_24_black else R.drawable.baseline_visibility_off_24_black
        ) as Drawable
    }

    private fun showVisibilityButtonImageOn() {
        setButtonDrawables(endOfTheText = getDrawableVisibility(true))
        inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
        isPasswordVisible = false
    }

    private fun showVisibilityButtonImageOff() {
        setButtonDrawables(endOfTheText = getDrawableVisibility(false))
        inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        isPasswordVisible = true
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event != null) {
            val visibilityButtonImage = compoundDrawables[2]
            if (visibilityButtonImage != null) {
                val clearButtonStart: Float
                val clearButtonEnd: Float
                var isVisibilityButtonClicked = false

                if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                    clearButtonEnd = (visibilityButtonImage.intrinsicWidth + paddingStart).toFloat()
                    when {
                        event.x < clearButtonEnd -> isVisibilityButtonClicked = true
                    }
                } else {
                    clearButtonStart =
                        (width - paddingEnd - visibilityButtonImage.intrinsicWidth).toFloat()
                    when {
                        event.x > clearButtonStart -> isVisibilityButtonClicked = true
                    }
                }

                if (isVisibilityButtonClicked) {
                    val result: Boolean

                    when (event.action) {
                        MotionEvent.ACTION_UP -> {
                            if (isPasswordVisible) {
                                showVisibilityButtonImageOn()
                            } else {
                                showVisibilityButtonImageOff()
                            }
                            result = true
                        }

                        else -> result = false
                    }

                    text?.let { setSelection(it.length) }

                    return result
                } else return false
            }
        }
        return false
    }
}