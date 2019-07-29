package com.orost.utils

import android.text.TextWatcher

/**
 * Abstract class that leaves required implementation only for the afterTextChanged method
 */
internal abstract class AfterTextChangedListener : TextWatcher {

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

}
