package com.orost.view

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatEditText
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import com.orost.utils.AfterTextChangedListener
import com.orost.utils.BigDecimalUtils
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.math.min

const val DECIMAL_PLACES = 2

/**
 * Class that represents BigDecimal value with currency in the specific locale.
 * Implements the editing logic without currency symbol, leaves default hint 0.00 after removing all decimals
 */
internal class CurrencyEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    var locale: Locale = Locale.getDefault()
        set(value) {
            field = value
            updateText()
        }
    var currency: Currency = Currency.getInstance(locale)
        set(value) {
            field = value
            updateText()
        }

    var value = BigDecimal.ZERO
        set(value) {
            field = value
            updateText()
        }
    private var currentText = ""
    private val textWatcher = object : AfterTextChangedListener() {
        override fun afterTextChanged(s: Editable) {
            val str = s.toString()
            if (str.isNotEmpty() && str == currentText) {
                return
            }
            parseValue(str)
            updateText()
        }
    }

    private val textWatchers = mutableSetOf<TextWatcher>(textWatcher)

    /**
     * Returns decimal formatter for specified currency.
     *
     * @return Currency formatter for current locale and currency.
     */
    private val currencyFormatter: DecimalFormat
        get() {
            NumberFormat.getCurrencyInstance()
            val format = NumberFormat.getCurrencyInstance(locale)
            format.maximumFractionDigits = DECIMAL_PLACES
            format.currency = currency
            return format as DecimalFormat
        }

    fun addTextChangeListener(listener: TextWatcher) {
        textWatchers.add(listener)
        addTextChangedListener(listener)
    }

    /**
     * Parses value from text in edit field.
     *
     * @param str
     * String with digits.
     */
    private fun parseValue(str: String) {
        // Remove all non numeric chars
        val cleanString = str.replace("[^\\d]".toRegex(), "")
        value = if (cleanString.isNotEmpty()) {
            // Construct decimal value as only integer value and move
            // fraction point for two places.
            BigDecimal(cleanString)
                .setScale(DECIMAL_PLACES, BigDecimal.ROUND_FLOOR)
                .divide(BigDecimal(100), BigDecimal.ROUND_FLOOR)
        } else {
            // Input field have no any digit
            BigDecimal.ZERO
        }
    }

    private fun updateText() {
        val formatter = currencyFormatter

        currentText = formatter.format(value)

        // Now we need to find clear string without currency symbols
        val symbols = formatter.decimalFormatSymbols
        symbols.currencySymbol = ""
        formatter.decimalFormatSymbols = symbols
        var formattedClear = formatter.format(value)

        // Currency symbols may be placed with spaces (e.g. nbsp) at start or at end of string
        var start = 0
        if (Character.isSpaceChar(formattedClear[start]))
            ++start

        var end = formattedClear.length - 1
        if (Character.isSpaceChar(formattedClear[end]))
            --end

        // Trim spaces (String.trim() may skip most spaces)
        formattedClear = formattedClear.substring(start, end + 1)

        // Set position of cursor at end of clear string in formatted string
        val pos = currentText.indexOf(formattedClear) + formattedClear.length

        clearTextChangeListeners()
        setText(currentText)
        setSelection(min(pos, currentText.length))
        addTextChangeListeners()
    }

    private fun clearTextChangeListeners() {
        textWatchers.forEach { removeTextChangedListener(it) }
    }

    private fun addTextChangeListeners() {
        textWatchers.forEach { addTextChangedListener(it) }
    }

}
