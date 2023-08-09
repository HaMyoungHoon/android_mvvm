package mhha.sample.android_mvvm.models.fDate

import kotlin.math.pow

/**
 * FDateTimeFormat
 * - .net 배끼는 중
 * - pattern
 *      - y
 *      - h     0   시 (12시간제)
 *      - hh    00  시 (12시간제)
 *
 * @constructor Create empty F date time format
 */
object FDateTimeFormat {
    const val maxSecondsFractionDigits = 7
    fun format(dateTime: FDateTime, format: String? = null, localize: FLocalize = FLocalize.KOREA): String {
        val parseFormatter = if (format.isNullOrEmpty()) {
            localize.getDateTimeOffset()
        } else {
            format
        }
        return formatCustomized(dateTime, parseFormatter, localize)
    }

    fun formatCustomized(dateTime: FDateTime, format: String, localize: FLocalize): String {
        val result = StringBuilder()
        var index: Int = 0; var tokenLen: Int = 0; var hour12: Int = 0
        while (index < format.length) {
            val letter = format[index]
            val nextLetter: Int
            when (letter) {
                'h' -> {
                    tokenLen = parseRepeatPattern(format, index, letter)
                    hour12 = dateTime.hour % 12
                    if (hour12 == 0) {
                        hour12 = 12
                    }
                    formatDigits(result, hour12, tokenLen)
                }
                'H' -> {
                    tokenLen = parseRepeatPattern(format, index, letter)
                    formatDigits(result, dateTime.hour, tokenLen)
                }
                'm' -> {
                    tokenLen = parseRepeatPattern(format, index, letter)
                    formatDigits(result, dateTime.minute, tokenLen)
                }
                's' -> {
                    tokenLen = parseRepeatPattern(format, index, letter)
                    formatDigits(result, dateTime.second, tokenLen)
                }
                'f', 'F' -> {
                    tokenLen = parseRepeatPattern(format, index, letter)
                    if (tokenLen <= maxSecondsFractionDigits) {
                        var fraction = dateTime.internalTicks % dateTime._ticksPerSecond
                        fraction /= 10.0.pow((7 - tokenLen).toDouble()).toLong()
                        if (letter == 'f') {
                            formatDigits(result, fraction.toInt(), tokenLen, true)
                        } else {
                            var effectiveDigits = tokenLen
                            while (effectiveDigits > 0) {
                                if (fraction % 10 == 0L) {
                                    fraction /= 10
                                    effectiveDigits--
                                } else {
                                    break
                                }
                            }
                            if (effectiveDigits > 0) {
                                formatDigits(result, fraction.toInt(), effectiveDigits, true)
                            } else {
                                if (result.isNotEmpty() && result[result.length - 1] == '.') {
                                    result.delete(result.length - 1, 1)
                                }
                            }
                        }
                    } else {
                        throw Exception("illegal format max seconds fraction is $maxSecondsFractionDigits but $tokenLen")
                    }
                }
                't' -> {
                    tokenLen = parseRepeatPattern(format, index, letter)
                    if (tokenLen == 1) {
                        result.append(if (dateTime.hour < 12) localize.getAM()[0] else localize.getPM()[0])
                    } else {
                        result.append(if (dateTime.hour < 12) localize.getAM() else localize.getPM())
                    }
                }
                'd' -> {
                    tokenLen = parseRepeatPattern(format, index, letter)
                    if (tokenLen <= 2) {
                        formatDigits(result, localize.getDayOfMonth(dateTime), tokenLen)
                    } else {
                        result.append(localize.getDayOfWeek(dateTime, tokenLen > 3))
                    }
                }
                'M' -> {
                    tokenLen = parseRepeatPattern(format, index, letter)
                    if (tokenLen <= 2) {
                        formatDigits(result, localize.getMonth(dateTime), tokenLen)
                    } else {
                        if (tokenLen >= 4) {
                            result.append(localize.getMonth(dateTime, false))
                        } else {
                            result.append(localize.getMonth(dateTime, true))
                        }
                    }
                }
                'y' -> {
                    tokenLen = parseRepeatPattern(format, index, letter)
                    formatDigits(result, localize.getYear(dateTime), tokenLen)
                }
                ':', '/' -> {
                    tokenLen = 1
                    result.append(letter)
                }
                '\'', '\"' -> {
                    val buff = StringBuilder()
                    tokenLen = parseQuoteString(format, index, buff)
                    result.append(buff)
                }
                '%' -> {
                    nextLetter = parseNextChar(format, index)
                    if (nextLetter >= 0 && nextLetter != ('%').toInt()) {
                        result.append(dateTime, nextLetter.toChar(), localize)
                        tokenLen = 2
                    } else {
                        throw Exception("illegal format : $format")
                    }
                }
                '\\' -> {
                    nextLetter = parseNextChar(format, index)
                    if (nextLetter >= 0) {
                        result.append(nextLetter.toChar())
                    } else {
                        throw Exception("illegal format : $format")
                    }
                }
                else -> {
                    tokenLen = 1
                    result.append(letter)
                }
            }
            index += tokenLen
        }

        return result.toString()
    }

    fun parseNextChar(format: String, pos: Int): Int {
        if (pos >= format.length - 1) {
            return -1
        }
        return format[pos+1].toInt()
    }
    fun parseQuoteString(format: String, pos: Int, outputData: StringBuilder): Int {
        val formatLength = format.length
        val beginPos = pos
        var index = pos + 1
        val quoteChar = format[index]
        var foundQuote = false
        while (index < formatLength) {
            val letter = format[index++]
            if (letter == quoteChar) {
                foundQuote = true
                break
            } else if (letter == '\\') {
                if (index < formatLength) {
                    outputData.append(format[index++])
                } else {
                    throw Exception("illegal format : $format")
                }
            } else {
                outputData.append(letter)
            }
        }

        if (!foundQuote) {
            throw Exception("illegal format : $format, quoteChar : $quoteChar")
        }

        return (index - beginPos)
    }
    fun parseRepeatPattern(format: String, pos: Int, letter: Char): Int {
        val len = format.length
        var index = pos + 1
        while ((index < len) && format[index] == letter) {
            index++
        }
        return (index - pos)
    }
    fun formatDigits(outputData: StringBuilder, value: Int, tokenLen: Int) {
        formatDigits(outputData, value, tokenLen, false)
    }
    fun formatDigits(outputData: StringBuilder, value: Int, tokenLen: Int, overrideLengthLimit: Boolean) {
        if (value < 0) {
            throw Exception("illegal format value : $value")
        }

        var len = tokenLen
        if (!overrideLengthLimit && len > 2) {
            len = 2
        }

        val buff = CharArray(16)
        var p = buff.size
        var n = value
        do {
            buff[--p] = (n % 10 + '0'.toInt()).toChar()
            n /= 10
        } while (n != 0 && p > 0)

        var digits = buff.size - p
        while (digits < len && p > 0) {
            buff[--p] = '0'
            digits++
        }

        outputData.append(buff, p, digits)
    }
}