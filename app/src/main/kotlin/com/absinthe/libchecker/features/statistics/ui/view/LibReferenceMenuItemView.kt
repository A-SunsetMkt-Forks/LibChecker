package com.absinthe.libchecker.features.statistics.ui.view

import android.content.Context
import android.widget.FrameLayout
import com.absinthe.libchecker.R
import com.absinthe.libchecker.constant.Constants
import com.absinthe.libchecker.constant.GlobalValues
import com.absinthe.libchecker.utils.Telemetry
import com.absinthe.libchecker.utils.extensions.dp
import com.absinthe.libchecker.utils.extensions.getColor
import com.absinthe.libchecker.view.app.CheckableChipView

class LibReferenceMenuItemView(context: Context) : FrameLayout(context) {
  val chip = CheckableChipView(context).also {
    it.layoutParams = MarginLayoutParams(
      LayoutParams.WRAP_CONTENT,
      LayoutParams.WRAP_CONTENT
    ).also { lp ->
      lp.setMargins(4.dp, 4.dp, 4.dp, 4.dp)
    }
    val checkedColor = R.color.advanced_menu_item_text_checked.getColor(context)
    val uncheckedColor = R.color.advanced_menu_item_text_not_checked.getColor(context)
    it.textColorPair = checkedColor to uncheckedColor
  }

  private var onCheckedChangeCallback: ((isChecked: Boolean) -> Unit)? = null

  init {
    addView(chip)
  }

  fun setOption(labelRes: Int, option: Int) {
    chip.apply {
      text = context.getString(labelRes)
      isChecked = (GlobalValues.libReferenceOptions and option) > 0
      onCheckedChangeListener = { _: CheckableChipView, isChecked: Boolean ->
        val newOptions = if (isChecked) {
          GlobalValues.libReferenceOptions or option
        } else {
          GlobalValues.libReferenceOptions and option.inv()
        }
        GlobalValues.libReferenceOptions = newOptions
        onCheckedChangeCallback?.invoke(isChecked)
        Telemetry.recordEvent(
          Constants.Event.LIB_REF_ADVANCED_MENU_ITEM_CHANGED,
          mapOf(Telemetry.Param.CONTENT to text, Telemetry.Param.VALUE to isChecked)
        )
      }
    }
  }

  fun setChecked(isChecked: Boolean) {
    chip.setCheckedAnimated(isChecked, null)
  }

  fun setOnCheckedChangeCallback(action: (isChecked: Boolean) -> Unit) {
    onCheckedChangeCallback = action
  }

  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    chip.onCheckedChangeListener = null
    onCheckedChangeCallback = null
  }
}
