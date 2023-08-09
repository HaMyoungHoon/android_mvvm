package mhha.sample.android_mvvm.models.fDate

import kotlin.math.pow

/**
 * FTimeSpanFormat
 * - .net 배끼는 중
 * @constructor Create empty F time span format
 */
internal object FTimeSpanFormat {
    val positiveInvariantFormatLiterals = FormatLiterals().initInvariant(false)
    val negativeInvariantFormatLiterals = FormatLiterals().initInvariant(true)
    enum class Pattern {
        None,
        Minimum,
        Full,
    }

    fun intToString(n: Int, digits: Int): String {
        val ret = StringBuilder()
        val buff = CharArray(16)
        var p = buff.size
        var value = n
        do {
            buff[--p] = (value % 10 + '0'.code).toChar()
            value /= 10
        } while (value != 0 && p > 0)

        var digitCount = buff.size - p
        while (digitCount < digits && p > 0) {
            buff[--p] = '0'
            digitCount++
        }

        ret.append(buff, p, digitCount)
        return ret.toString()
    }

    fun format(timeSpan: FTimeSpan, format: String? = null, localize: FLocalize): String {
        var formatBuff = format
        if (format.isNullOrEmpty()) {
            formatBuff = "c"
        }

        if (formatBuff?.length == 1) {
            if (formatBuff[0] == 'c' || formatBuff[0] == 't' || formatBuff[0] == 'T') {
                return formatStandard(timeSpan, true, format, Pattern.Minimum)
            }
            if (formatBuff[0] == 'g' || formatBuff[0] == 'G') {
                val pattern = if (formatBuff[0] == 'g') Pattern.Minimum else Pattern.Full
                formatBuff = if (timeSpan.ticks < 0) localize.fullTimeSpanNegativePattern else localize.fullTimeSpanPositivePattern
                return formatStandard(timeSpan, false, formatBuff, pattern)
            }
        }

        return formatCustomized(timeSpan, format, localize)
    }
    private fun formatStandard(timeSpan: FTimeSpan, isInvariant: Boolean, format: String? = null, pattern: Pattern): String {
        var ret = ""
        var day = (timeSpan.ticks / FTimeSpan.ticksPerDay).toInt()
        var time = timeSpan.ticks % FTimeSpan.ticksPerDay
        if (timeSpan.ticks < 0) {
            day = -day
            time = -time
        }
        val hours = (time / FTimeSpan.ticksPerHour % 24).toInt()
        val minutes = (time / FTimeSpan.ticksPerMinute % 60).toInt()
        val seconds = (time / FTimeSpan.ticksPerSecond % 60).toInt()
        var fraction = (time % FTimeSpan.ticksPerSecond).toInt()

        val literal = if (isInvariant) {
            if (timeSpan.ticks < 0) { negativeInvariantFormatLiterals } else { positiveInvariantFormatLiterals }
        } else {
            FormatLiterals().init(format!!, pattern == Pattern.Full)
        }
        if (fraction != 0) {
            fraction = (fraction / (10.0).pow(FDateTimeFormat.maxSecondsFractionDigits - literal.ff)).toInt()
        }
        // Pattern.Full: [-]dd.hh:mm:ss.fffffff
        // Pattern.Minimum: [-][d.]hh:mm:ss[.fffffff]

        ret += literal.start
        if (pattern == Pattern.Full || day != 0) {
            ret += day
            ret += literal.dayHourSep
        }
        ret += intToString(hours, literal.hh)
        ret += literal.hourMinuteSep
        ret += intToString(minutes, literal.mm)
        ret += literal.minuteSecondSep
        ret += intToString(seconds, literal.ss)
        if (!isInvariant && pattern == Pattern.Minimum) {
            var effectiveDigits = literal.ff
            while (effectiveDigits > 0) {
                if (fraction % 10 == 0) {
                    fraction /= 10
                    effectiveDigits--
                } else {
                    break
                }
            }
            if (effectiveDigits > 0) {
                ret += literal.secondFractionSep
                ret += intToString(fraction, effectiveDigits)
            }
        } else if (pattern == Pattern.Full || fraction != 0) {
            ret += literal.secondFractionSep
            ret += intToString(fraction, literal.ff)
        }
        ret += literal.end


        return ret
    }
    fun formatCustomized(timeSpan: FTimeSpan, format: String? = null, localize: FLocalize): String {
        val ret = StringBuilder()
        var day = (timeSpan.ticks / FTimeSpan.ticksPerDay).toInt()
        var time = timeSpan.ticks % FTimeSpan.ticksPerDay
        if (timeSpan.ticks < 0) {
            day = -day
            time = -time
        }
        val hours = (time / FTimeSpan.ticksPerHour % 24).toInt()
        val minutes = (time / FTimeSpan.ticksPerMinute % 60).toInt()
        val seconds = (time / FTimeSpan.ticksPerSecond % 60).toInt()
        val fraction = (time % FTimeSpan.ticksPerSecond).toInt()

        var temp = 0
        var index = 0
        var tokenLen = 0
        while (index < format?.length!!) {
            when (format[index]) {
                'h' -> {
                    tokenLen = FDateTimeFormat.parseRepeatPattern(format, index, format[index])
                    if (tokenLen > 2) {
                        throw Exception("illegal format data format : $format, format[$index] : ${format[index]}")
                    }
                    FDateTimeFormat.formatDigits(ret, hours, tokenLen)
                }
                'm' -> {
                    tokenLen = FDateTimeFormat.parseRepeatPattern(format, index, format[index])
                    if (tokenLen > 2) {
                        throw Exception("illegal format data format : $format, format[$index] : ${format[index]}")
                    }
                    FDateTimeFormat.formatDigits(ret, minutes, tokenLen)
                }
                's' -> {
                    tokenLen = FDateTimeFormat.parseRepeatPattern(format, index, format[index])
                    if (tokenLen > 2) {
                        throw Exception("illegal format data format : $format, format[$index] : ${format[index]}")
                    }
                    FDateTimeFormat.formatDigits(ret, seconds, tokenLen)
                }
                'f' -> {
                    tokenLen = FDateTimeFormat.parseRepeatPattern(format, index, format[index])
                    if (tokenLen > FDateTimeFormat.maxSecondsFractionDigits) {
                        throw Exception("illegal format data format : $format, format[$index] : ${format[index]}")
                    }
                    temp = fraction
                    temp /= (10.0).pow(FDateTimeFormat.maxSecondsFractionDigits - tokenLen).toInt()
                    FDateTimeFormat.formatDigits(ret, temp, tokenLen, true)
                }
                'F' -> {
                    tokenLen = FDateTimeFormat.parseRepeatPattern(format, index, format[index])
                    if (tokenLen > FDateTimeFormat.maxSecondsFractionDigits) {
                        throw Exception("illegal format data format : $format, format[$index] : ${format[index]}")
                    }
                    temp = fraction
                    temp /= (10.0).pow(FDateTimeFormat.maxSecondsFractionDigits - tokenLen).toInt()
                    var effectiveDigits = tokenLen
                    while (effectiveDigits > 0) {
                        if (temp % 10 == 0) {
                            temp /= 10
                            effectiveDigits--
                        } else {
                            break
                        }
                    }
                    if (effectiveDigits > 0) {
                        FDateTimeFormat.formatDigits(ret, temp, effectiveDigits, true)
                    }
                }
                'd' -> {
                    tokenLen = FDateTimeFormat.parseRepeatPattern(format, index, format[index])
                    if (tokenLen > 8) {
                        throw Exception("illegal format data format : $format, format[$index] : ${format[index]}")
                    }
                    FDateTimeFormat.formatDigits(ret, day, tokenLen, true)
                }
                '\'', '\"' -> {
                    val buff = StringBuilder()
                    tokenLen = FDateTimeFormat.parseQuoteString(format, index, buff)
                    ret.append(buff)
                }
                '%'-> {
                    val nextChar = FDateTimeFormat.parseNextChar(format, index)
                    if (nextChar >= 0 && nextChar != '%'.toInt()) {
                        ret.append(formatCustomized(timeSpan, nextChar.toChar().toString(), localize))
                        tokenLen = 2
                    } else {
                        throw Exception("illegal format data format : $format, format[$index] : ${format[index]}")
                    }
                }
                '\\' -> {
                    val nextChar = FDateTimeFormat.parseNextChar(format, index)
                    if (nextChar >= 0) {
                        ret.append(nextChar.toChar())
                        tokenLen = 2
                    } else {
                        throw Exception("illegal format data format : $format, format[$index] : ${format[index]}")
                    }
                }
                else -> throw Exception("illegal format data format : $format, format[$index] : ${format[index]}")
            }
            index += tokenLen
        }

        return ret.toString()
    }

    data class FormatLiterals(
        var appCompatLiteral: String = ":.",
        var dd: Int = 2,
        var hh: Int = 2,
        var mm: Int = 2,
        var ss: Int = 2,
        var ff: Int = FDateTimeFormat.maxSecondsFractionDigits,
        private var _literals: MutableList<String> = arrayListOf(
            "",
            ".",
            ":",
            ":",
            ".",
            ""
        )
    ) {
        val start: String get() = _literals[0]
        val dayHourSep: String get() = _literals[1]
        val hourMinuteSep: String get() = _literals[2]
        val minuteSecondSep: String get() = _literals[3]
        val secondFractionSep: String get() = _literals[4]
        val end: String get() = _literals[5]

        fun initInvariant(isNegative: Boolean): FormatLiterals {
            val ret = FormatLiterals()
            ret._literals[0] = if (isNegative) "-" else ""
            return ret
        }
        fun init(format: String, useInvariantFieldLengths: Boolean): FormatLiterals {
            _literals = arrayListOf("", "", "", "", "", "")
            dd = 0
            hh = 0
            mm = 0
            ss = 0
            ff = 0

            var buff = ""
            var inQuote = false
            var quote = '\''
            var field = 0
            var skipOnce = false
            for (i in format.indices) {
                if (skipOnce) {
                    skipOnce = false
                    continue
                }
                when (format[i]) {
                    '\'', '\"' -> {
                        if (inQuote && (quote == format[i])) {
                            _literals[field] = buff
                            buff = ""
                            inQuote = false
                        } else if (!inQuote) {
                            quote = format[i]
                            inQuote = true
                        }
                    }
                    '\\' -> {
                        if (!inQuote) {
                            skipOnce = true
                            buff += format[i]
                        }
                    }
                    'd' -> {
                        if (!inQuote) {
//                            if (!((field == 0 && buff.isEmpty()) || field == 1)) {
//                                throw Exception("illegal range is not support, TimeSpan Pattern Error format : $format, format[$i] : ${format[i]}")
//                            }
                            field = 1
                            dd++
                        }
                    }
                    'h' -> {
                        if (!inQuote) {
//                            if (!((field == 1 && buff.isEmpty()) || field == 2)) {
//                                throw Exception("illegal range is not support, TimeSpan Pattern Error format : $format, format[$i] : ${format[i]}")
//                            }
                            field = 2
                            hh++
                        }
                    }
                    'm' -> {
                        if (!inQuote) {
//                            if (!((field == 2 && buff.isEmpty()) || field == 3)) {
//                                throw Exception("illegal range is not support, TimeSpan Pattern Error format : $format, format[$i] : ${format[i]}")
//                            }
                            field = 3
                            mm++
                        }
                    }
                    's' -> {
                        if (!inQuote) {
//                            if (!((field == 3 && buff.isEmpty()) || field == 4)) {
//                                throw Exception("illegal range is not support, TimeSpan Pattern Error format : $format, format[$i] : ${format[i]}")
//                            }
                            field = 4
                            ss++
                        }
                    }
                    'f', 'F' -> {
                        if (!inQuote) {
//                            if (!((field == 4 && buff.isEmpty()) || field == 5)) {
//                                throw Exception("illegal range is not support, TimeSpan Pattern Error format : $format, format[$i] : ${format[i]}")
//                            }
                            field = 5
                            ff++
                        }
                    }
                    /*'%',*/ else -> {
                    buff += format[i]
                }
                }
            }

//            if (field != 5) {
//                throw Exception("illegal not captured end pos")
//            }
            appCompatLiteral = minuteSecondSep + secondFractionSep

//            if (dd !in 1..2) throw Exception("illegal day range is not support day : $dd")
//            if (hh !in 1..2) throw Exception("illegal hour range is not support day : $hh")
//            if (mm !in 1..2) throw Exception("illegal minute range is not support day : $mm")
//            if (ss !in 1..2) throw Exception("illegal second range is not support day : $ss")
//            if (ff !in 1..9) throw Exception("illegal fraction range is not support day : $ff")

            if (useInvariantFieldLengths) {
                dd = 2
                hh = 2
                mm = 2
                ss = 2
                ff = FDateTimeFormat.maxSecondsFractionDigits
            } else {
                if (dd < 1 || dd > 2) dd = 2
                if (hh < 1 || hh > 2) hh = 2
                if (mm < 1 || mm > 2) mm = 2
                if (ss < 1 || ss > 2) ss = 2
                if (ff < 1 || ff > 7) ff = 2
            }

            return this
        }
    }
}