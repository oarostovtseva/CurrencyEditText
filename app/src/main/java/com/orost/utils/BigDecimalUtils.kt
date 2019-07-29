package com.orost.utils

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.text.ParseException
import java.util.*

/**
 * Class for operating with BigDecimals numbers
 */
internal object BigDecimalUtils {

    const val DECIMAL_PLACES = 2

    /*Using 10 decimal places to multiply and divide BigDecimals to ensure higher accuracy*/
    private const val OPERATIONS_SCALE = 10

    /*Round mode used when dividing or setting a scale. HALF_UP will round to the closest neighbor and, in case of a tie, will round up*/
    private val ROUNDING_MODE = RoundingMode.HALF_UP

    private val NUMBER_FORMAT = NumberFormat.getInstance(Locale.US)

    init {
        NUMBER_FORMAT.minimumFractionDigits = DECIMAL_PLACES
        NUMBER_FORMAT.maximumFractionDigits = DECIMAL_PLACES
        NUMBER_FORMAT.isGroupingUsed = true
    }

    fun equals(value1: BigDecimal?, value2: BigDecimal?): Boolean {
        return compareTo(value1, value2) == 0
    }

    fun compareTo(val1: BigDecimal?, val2: BigDecimal?): Int {
        val value1 = nullToZero(val1)
        val value2 = nullToZero(val2)
        return value1.compareTo(value2)
    }

    fun multiply(val1: BigDecimal, val2: BigDecimal): BigDecimal {
        val value1 = prepare(val1)
        val value2 = prepare(val2)
        return value1.multiply(value2)
    }

    fun divide(val1: BigDecimal, val2: BigDecimal): BigDecimal {
        val value1 = prepare(val1)
        val value2 = prepare(val2)
        return value1.divide(value2, ROUNDING_MODE)
    }

    fun divide(value1: BigDecimal, value2: BigDecimal, finalScale: Int): BigDecimal {
        return divide(value1, value2).setScale(finalScale, ROUNDING_MODE)
    }

    private fun prepare(val1: BigDecimal?): BigDecimal {
        val value = nullToZero(val1)
        return value.setScale(OPERATIONS_SCALE, ROUNDING_MODE)
    }

    fun getBigDecimal(value: Any): BigDecimal? {
        if (value is BigDecimal) {
            return value
        }

        if (value is Number) {
            return BigDecimal(value.toString())
        }

        var str: String? = null

        if (value is String) {
            var string = value
            string = string.trim { it <= ' ' }
            if (string.isNotEmpty()) {
                str = string
            }
        }

        if (str != null) {
            try {
                return BigDecimal(NUMBER_FORMAT.parse(str).toString())
            } catch (e: ParseException) {
                throw IllegalArgumentException("$value is not a valid decimal value", e)
            }

        }

        return null
    }

    fun convertToCents(amount: BigDecimal): BigDecimal {
        return amount.multiply(BigDecimal("100")).setScale(0, RoundingMode.DOWN)
    }

    private fun nullToZero(value: BigDecimal?) = value ?: BigDecimal.ZERO
}
