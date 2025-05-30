package com.absinthe.libchecker.ui.base

import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.absinthe.libchecker.R
import com.absinthe.libchecker.constant.GlobalValues
import com.absinthe.libchecker.utils.OsUtils
import rikka.material.app.LocaleDelegate
import rikka.material.app.MaterialActivity
import timber.log.Timber

abstract class BaseActivity<VB : ViewBinding> :
  MaterialActivity(),
  IBinding<VB> {

  override lateinit var binding: VB

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = (inflateBinding(layoutInflater) as VB).also {
      setContentView(it.root)
    }
  }

  override fun onResume() {
    super.onResume()
    if (OsUtils.atLeastT()) {
      if (LocaleDelegate.defaultLocale != GlobalValues.locale) {
        LocaleDelegate.defaultLocale = GlobalValues.locale
        recreate()
      }
    }
  }

  override fun invalidateMenu() {
    // It will somehow cause a crash when calling super.invalidateMenu() in some cases
    // java.lang.IllegalStateException: The specified child already has a parent. You must call removeView() on the child's parent first.
    runCatching {
      super.invalidateMenu()
    }.onFailure {
      Timber.e(it)
    }
  }

  override fun shouldApplyTranslucentSystemBars(): Boolean {
    return true
  }

  override fun computeUserThemeKey(): String {
    return GlobalValues.darkMode
  }

  @Suppress("DEPRECATION")
  override fun onApplyTranslucentSystemBars() {
    super.onApplyTranslucentSystemBars()
    window.apply {
      decorView.post {
        if (!OsUtils.atLeastV()) {
          statusBarColor = Color.TRANSPARENT
          navigationBarColor = Color.TRANSPARENT
        }
        if (OsUtils.atLeastQ()) {
          isNavigationBarContrastEnforced = false
        }
      }
    }
  }

  override fun onApplyUserThemeResource(theme: Resources.Theme, isDecorView: Boolean) {
    theme.applyStyle(R.style.ThemeOverlay, true)
    theme.applyStyle(rikka.material.preference.R.style.ThemeOverlay_Rikka_Material3_Preference, true)
  }

  protected fun isBindingInitialized(): Boolean {
    return ::binding.isInitialized
  }
}
