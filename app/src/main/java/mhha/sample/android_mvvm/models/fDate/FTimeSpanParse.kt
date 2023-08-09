package mhha.sample.android_mvvm.models.fDate

import java.util.EnumSet
import kotlin.math.pow

/**
 * FTimeSpanParse
 * - .net 배끼는 중
 * @constructor Create empty F time span parse
 */
internal object FTimeSpanParse {
    val unlimitedDigits: Int = -1
    val maxfractionDigits = 7
    val maxDays: Int = 10675199
    val maxHours: Int = 23
    val maxMinutes: Int = 59
    val maxSeconds: Int = 59
    val maxFraction: Int = 9999999

    enum class TimeSpanStyles {
        None,
        AssumeNegative
    }
    enum class TimeSpanThrowStyle {
        None,
        All
    }
    enum class ParseFailureKind {
        None,
        ArgumentNull,
        Format,
        FormatWithParameter,
        OverFlow
    }
    enum class TimeSpanStandardStyle(val flags: Int) {
        None(0x00000000),
        Invariant(0x00000001),
        Localized(0x00000002),
        RequireFull(0x00000004),
        Any(Invariant.flags or Localized.flags);
        infix fun and(rhs: TimeSpanStandardStyle) = EnumSet.of(this, rhs)
    }
    infix fun TimeSpanStandardStyles.allOf(rhs: TimeSpanStandardStyles) = this.containsAll(rhs)
    infix fun TimeSpanStandardStyles.and(rhs: TimeSpanStandardStyle) = EnumSet.of(rhs, *this.toTypedArray())
    infix fun TimeSpanStandardStyles.flag(rhs: TimeSpanStandardStyle): Int {
        val buff = this.toTypedArray()
        var ret = 0
        for (i in buff) {
            ret = ret or i.flags
        }
        ret = ret and rhs.flags

        return ret
    }
    enum class TimeSpanTokenTypes {
        None,
        End,
        Num,
        Sep,
        NumOverFlow
    }

    private val zero: TimeSpanToken = TimeSpanToken(0)
    data class TimeSpanToken(
        var ttt: TimeSpanTokenTypes,
        var num: Int,
        var zeroes: Int,
        var sep: String?
    ) {
        constructor(): this(TimeSpanTokenTypes.Num, 0, 0, null)
        constructor(number: Int) : this(TimeSpanTokenTypes.Num, number, 0, null)
        constructor(number: Int, leadingZeroes: Int): this(TimeSpanTokenTypes.Num, number, leadingZeroes, null)
        fun isInvalidNumber(maxValue: Int, maxPrecision: Int): Boolean {
            if (num > maxValue) return true
            if (maxPrecision == unlimitedDigits) return false
            if (zeroes > maxPrecision) return true
            if (num == 0 || zeroes == 0) return false
            return (num >= (maxValue / (10.0).pow(zeroes - 1)))
        }
    }
    data class TimeSpanTokenizer(
        private var _pos: Int = 0,
        private var _value: String? = null,
    ) {

        val eol: Boolean get() {
            return _pos >= ((_value?.length ?: 0) - 1)
        }
        val currentChar: Char get() {
            if (_value.isNullOrEmpty()) {
                return (0).toChar()
            }
            return if (_pos > -1 && _pos < (_value?.length ?: 0)) {
                _value?.get(_pos) ?: (0).toChar()
            } else {
                (0).toChar()
            }
        }
        fun backOne() {
            if (_pos > 0) {
                --_pos
            }
        }
        val nextChar: Char get() {
            _pos++
            return currentChar
        }

        fun init(value: String): TimeSpanTokenizer {
            init(0, value)
            return this
        }
        fun init(pos: Int, value: String): TimeSpanTokenizer {
            _pos = pos
            _value = value
            return this
        }
        fun getNextToken(): TimeSpanToken {
            val ret = TimeSpanToken()
            var char = currentChar
            if (char == (0).toChar()) {
                ret.ttt = TimeSpanTokenTypes.End
                return ret
            }

            if (char in '0'..'9') {
                ret.ttt = TimeSpanTokenTypes.Num
                ret.num = 0
                ret.zeroes = 0
                do {
                    if ((ret.num and 0xF0000000.toInt()) != 0) {
                        ret.ttt = TimeSpanTokenTypes.NumOverFlow
                        return ret
                    }
                    ret.num = ret.num * 10 + char.toInt() - '0'.toInt()
                    if (ret.num == 0) ret.zeroes++
                    if (ret.num < 0) {
                        ret.ttt = TimeSpanTokenTypes.NumOverFlow
                        return ret
                    }
                    char = nextChar
                } while (char in '0'..'9')
                return ret
            } else {
                ret.ttt = TimeSpanTokenTypes.Sep
                val startIndex = _pos
                var length = 0
                while (char != (0).toChar() && (char < '0' || char > '9')) {
                    char = nextChar
                    length++
                }
                ret.sep = _value?.substring(startIndex, length)
                return ret
            }
        }
    }
    data class TimeSpanRawInfo(
        var lastSeenTTT: TimeSpanTokenTypes = TimeSpanTokenTypes.None,
        var tokenCount: Int = 0,
        var sepCount: Int = 0,
        var numCount: Int = 0,
        var literals: MutableList<String> = arrayListOf("", "", "", "", "", ""),
        var numbers: MutableList<TimeSpanToken> = arrayListOf(TimeSpanToken(), TimeSpanToken(), TimeSpanToken(), TimeSpanToken(), TimeSpanToken()),
        private var _posLoc: FTimeSpanFormat.FormatLiterals? = null,
        private var _negLoc: FTimeSpanFormat.FormatLiterals? = null,
        private var _fullPosPattern: String = "",
        private var _fullNegPattern: String = "",
    ) {
        private val _maxTokens = 11
        private val _maxLiteralTokens = 6
        private val _maxNumericTokens = 5
        val positiveInvariant: FTimeSpanFormat.FormatLiterals get() = FTimeSpanFormat.positiveInvariantFormatLiterals
        val negativeInvariant: FTimeSpanFormat.FormatLiterals get() = FTimeSpanFormat.negativeInvariantFormatLiterals
        val positiveLocalized: FTimeSpanFormat.FormatLiterals get() {
            if (_posLoc == null) {
                _posLoc = FTimeSpanFormat.FormatLiterals().init(_fullPosPattern, false)
            }
            return _posLoc!!
        }
        val negativeLocalized: FTimeSpanFormat.FormatLiterals get() {
            if (_negLoc == null) {
                _negLoc = FTimeSpanFormat.FormatLiterals().init(_fullNegPattern, false)
            }
            return _negLoc!!
        }

        fun init(localize: FLocalize): TimeSpanRawInfo {
            lastSeenTTT = TimeSpanTokenTypes.None
            tokenCount = 0
            sepCount = 0
            numCount = 0
            literals = arrayListOf("", "", "", "", "", "")
            numbers = arrayListOf(TimeSpanToken(), TimeSpanToken(), TimeSpanToken(), TimeSpanToken(), TimeSpanToken())
            _fullPosPattern = localize.fullTimeSpanPositivePattern
            _fullNegPattern = localize.fullTimeSpanNegativePattern
            return this
        }
        fun processToken(token: TimeSpanToken, result: TimeSpanResult): Boolean {
            if (token.ttt == TimeSpanTokenTypes.NumOverFlow) {
                result.setFailure(ParseFailureKind.OverFlow, "token is overflow")
                return false
            }
            if (token.ttt != TimeSpanTokenTypes.Sep && token.ttt != TimeSpanTokenTypes.Num) {
                result.setFailure(ParseFailureKind.Format, "token is bad format")
            }
            when (token.ttt) {
                TimeSpanTokenTypes.Sep -> if (!addSep(token.sep, result)) return false
                TimeSpanTokenTypes.Num -> {
                    if (tokenCount == 0) {
                        if (!addSep("", result)) return false
                    }
                    if (!addNum(token, result)) return false
                }
                else -> {}
            }

            lastSeenTTT = token.ttt
            return true
        }
        fun addSep(sep: String?, result: TimeSpanResult): Boolean {
            if (sepCount >= _maxLiteralTokens || tokenCount >= _maxTokens) {
                result.setFailure(ParseFailureKind.Format, "token is overflow")
                return false
            }
            if (sep == null) {
                result.setFailure(ParseFailureKind.ArgumentNull, "token is null")
                return false
            }
            literals[sepCount++] = sep
            tokenCount++
            return true
        }
        fun addNum(num: TimeSpanToken, result: TimeSpanResult): Boolean {
            if (numCount >= _maxNumericTokens || tokenCount >= _maxTokens) {
                result.setFailure(ParseFailureKind.Format, "token is overflow")
                return false
            }
            numbers[numCount++] = num
            tokenCount++
            return true
        }

        fun fullAppCompatMatch(pattern: FTimeSpanFormat.FormatLiterals): Boolean {
            return sepCount == 5 &&
                    numCount == 4 &&
                    pattern.start == literals[0] &&
                    pattern.dayHourSep == literals[1] &&
                    pattern.hourMinuteSep == literals[2] &&
                    pattern.appCompatLiteral == literals[3] &&
                    pattern.end == literals[4]
        }
        fun partialAppCompatMatch(pattern: FTimeSpanFormat.FormatLiterals): Boolean {
            return sepCount == 4 &&
                    numCount == 3 &&
                    pattern.start == literals[0] &&
                    pattern.hourMinuteSep == literals[1] &&
                    pattern.appCompatLiteral == literals[2] &&
                    pattern.end == literals[3]
        }
        fun fullMatch(pattern: FTimeSpanFormat.FormatLiterals): Boolean {
            return sepCount == _maxLiteralTokens &&
                    numCount == _maxNumericTokens &&
                    pattern.start == literals[0] &&
                    pattern.dayHourSep == literals[1] &&
                    pattern.hourMinuteSep == literals[2] &&
                    pattern.minuteSecondSep == literals[3] &&
                    pattern.secondFractionSep == literals[4] &&
                    pattern.end == literals[5]
        }
        fun fullDHMSMatch(pattern: FTimeSpanFormat.FormatLiterals): Boolean {
            return sepCount == 5 &&
                    numCount == 4 &&
                    pattern.start == literals[0] &&
                    pattern.dayHourSep == literals[1] &&
                    pattern.hourMinuteSep == literals[2] &&
                    pattern.minuteSecondSep == literals[3] &&
                    pattern.end == literals[4]
        }
        fun fullHMSFMatch(pattern: FTimeSpanFormat.FormatLiterals): Boolean {
            return sepCount == 5 &&
                    numCount == 4 &&
                    pattern.start == literals[0] &&
                    pattern.hourMinuteSep == literals[1] &&
                    pattern.minuteSecondSep == literals[2] &&
                    pattern.secondFractionSep == literals[3] &&
                    pattern.end == literals[4]
        }
        fun fullDHMMatch(pattern: FTimeSpanFormat.FormatLiterals): Boolean {
            return sepCount == 4 &&
                    numCount == 3 &&
                    pattern.start == literals[0] &&
                    pattern.dayHourSep == literals[1] &&
                    pattern.hourMinuteSep == literals[2] &&
                    pattern.end == literals[3]
        }
        fun fullHMSMatch(pattern: FTimeSpanFormat.FormatLiterals): Boolean {
            return sepCount == 4 &&
                    numCount == 3 &&
                    pattern.start == literals[0] &&
                    pattern.hourMinuteSep == literals[1] &&
                    pattern.minuteSecondSep == literals[2] &&
                    pattern.end == literals[3]
        }
        fun fullHMMatch(pattern: FTimeSpanFormat.FormatLiterals): Boolean {
            return sepCount == 3 &&
                    numCount == 2 &&
                    pattern.start == literals[0] &&
                    pattern.hourMinuteSep == literals[1] &&
                    pattern.end == literals[2]
        }
        fun fullDMatch(pattern: FTimeSpanFormat.FormatLiterals): Boolean {
            return sepCount == 2 &&
                    numCount == 1 &&
                    pattern.start == literals[0] &&
                    pattern.end == literals[1]
        }
    }
    data class TimeSpanResult(
        var parsedTimeSpan: FTimeSpan = FTimeSpan(0),
        var throwStyle: TimeSpanThrowStyle = TimeSpanThrowStyle.None,
        var failure: ParseFailureKind = ParseFailureKind.None,
        var failureMessage: String = ""
    ) {
        fun init(canThrow: TimeSpanThrowStyle): TimeSpanResult {
            parsedTimeSpan = FTimeSpan(0)
            throwStyle = canThrow
            return this
        }
        fun setFailure(failure: ParseFailureKind, failureMessage: String) {
            this.failure = failure
            this.failureMessage = failureMessage
            if (throwStyle != TimeSpanThrowStyle.None) {
                throw getTimeSpanParseException()
            }
        }
        fun getTimeSpanParseException(): Exception {
            return when (failure) {
                ParseFailureKind.ArgumentNull -> Exception(failureMessage) // NullPointerException()
                ParseFailureKind.Format -> Exception(failureMessage) // FormatException()
                ParseFailureKind.FormatWithParameter -> Exception(failureMessage) // FormatException()
                ParseFailureKind.OverFlow -> Exception(failureMessage) // OverFlowException()
                else -> Exception(failureMessage)
            }
        }
    }
    fun tryTimeToTicks(positive: Boolean, days: TimeSpanToken, hours: TimeSpanToken, minutes: TimeSpanToken, seconds: TimeSpanToken, fraction: TimeSpanToken): Pair<Boolean, Long> {
        var ret = 0L
        if (days.isInvalidNumber(maxDays, unlimitedDigits) ||
            hours.isInvalidNumber(maxHours, unlimitedDigits) ||
            minutes.isInvalidNumber(maxMinutes, unlimitedDigits) ||
            seconds.isInvalidNumber(maxSeconds, unlimitedDigits) ||
            fraction.isInvalidNumber(maxFraction, unlimitedDigits)
        ) {
            return Pair(false, ret)
        }

        val ticks = (days.num * 3600 * 24 + hours.num * 3600 + minutes.num * 60 + seconds.num) * 1000
        if (ticks > FTimeSpan.maxMilliSeconds || ticks < FTimeSpan.minMilliSeconds) {
            return Pair(false, ret)
        }

        var fractionBuff = fraction.num
        if (fractionBuff != 0) {
            var lowerLimit = FTimeSpan.ticksPerTenthSecond
            if (fraction.zeroes > 0) {
                val divisor = (10.0).pow(fraction.zeroes)
                lowerLimit = (lowerLimit / divisor).toInt()
            }
            while (fractionBuff < lowerLimit) {
                fractionBuff *= 10
            }
        }
        ret = ticks.toLong() * FTimeSpan.ticksPerMillisecond + fractionBuff
        if (positive && ret < 0) {
            ret = 0
            return Pair(false, ret)
        }

        return Pair(true, ret)
    }
    fun tryParse(input: String?, localize: FLocalize, result: FTimeSpan): Boolean {
        val parseResult = TimeSpanResult().init(TimeSpanThrowStyle.None)
        return if (tryParseTimeSpan(input,
                EnumSet.of(TimeSpanStandardStyle.Any), localize, parseResult)) {
            result._ticks = parseResult.parsedTimeSpan.ticks
            true
        } else {
            result._ticks = 0
            false
        }
    }
    fun parseExact(input: String, format: String, localize: FLocalize, styles: TimeSpanStyles): FTimeSpan {
        val parseResult = TimeSpanResult().init(TimeSpanThrowStyle.All)
        return if (tryParseExactTimeSpan(input, format, localize, styles, parseResult)) {
            parseResult.parsedTimeSpan
        } else {
            throw parseResult.getTimeSpanParseException()
        }
    }
    fun parseExactDigits(tokenizer: TimeSpanTokenizer, minDigitLength: Int): Pair<Boolean, Int> {
        val maxDigitLength = if (minDigitLength == 1) 2 else minDigitLength
        val doublePair = parseExactDigits(tokenizer, minDigitLength, maxDigitLength)
        return Pair(doublePair.first, doublePair.second.second)
    }
    fun parseExactDigits(tokenizer: TimeSpanTokenizer, minDigitLength: Int, maxDigitLength: Int): Pair<Boolean, Pair<Int, Int>> {
        var first = false
        var second = 0
        var third = 0

        var tokenLength = 0
        while (tokenLength < maxDigitLength) {
            val ch = tokenizer.nextChar
            if (ch < '0' || ch > '9') {
                tokenizer.backOne()
                break
            }
            third = third * 10 + (ch - '0')
            if (third == 0) {
                second++
            }
            tokenLength++
        }
        first = tokenLength >= minDigitLength

        return Pair(first, Pair(second, third))
    }
    fun tryParseExact(input: String, format: String, localize: FLocalize, styles: TimeSpanStyles, result: FTimeSpan): Boolean {
        val parseResult = TimeSpanResult().init(TimeSpanThrowStyle.None)
        return if (tryParseExactTimeSpan(input, format, localize, styles, parseResult)) {
            result._ticks = parseResult.parsedTimeSpan.ticks
            true
        } else {
            result._ticks = 0
            false
        }
    }
    fun parseExactMultiple(input: String, formats: List<String>, localize: FLocalize, styles: TimeSpanStyles): FTimeSpan {
        val parseResult = TimeSpanResult().init(TimeSpanThrowStyle.All)
        return if (tryParseExactMultipleTimeSpan(input, formats, localize, styles, parseResult)) {
            parseResult.parsedTimeSpan
        } else {
            throw parseResult.getTimeSpanParseException()
        }
    }
    fun tryParseExactMultiple(input: String, formats: List<String>, localize: FLocalize, styles: TimeSpanStyles, result: FTimeSpan): Boolean {
        val parseResult = TimeSpanResult().init(TimeSpanThrowStyle.None)
        return if (tryParseExactMultipleTimeSpan(input, formats, localize, styles, parseResult)) {
            result._ticks = parseResult.parsedTimeSpan.ticks
            true
        } else {
            result._ticks = 0
            false
        }
    }

    private fun tryParseTimeSpan(input: String?, style: TimeSpanStandardStyles, localize: FLocalize, result: TimeSpanResult): Boolean {
        if (input == null) {
            result.setFailure(ParseFailureKind.ArgumentNull, "input argument is null string")
            return false
        }

        val inputBuff = input.trim()
        if (inputBuff.isEmpty()) {
            result.setFailure(ParseFailureKind.Format, "input data is empty")
            return false
        }

        val tokenizer = TimeSpanTokenizer().init(inputBuff)
        val raw = TimeSpanRawInfo().init(localize)
        var token = tokenizer.getNextToken()
        while (token.ttt != TimeSpanTokenTypes.End) {
            if (!raw.processToken(token, result)) {
                result.setFailure(ParseFailureKind.Format, "format data is bad input : $input, token: $token")
                return false
            }
            token = tokenizer.getNextToken()
        }
        if (!tokenizer.eol) {
            result.setFailure(ParseFailureKind.Format, "input data is bad format input : $input")
            return false
        }
        if (!processTerminalState(raw, style, result)) {
            result.setFailure(ParseFailureKind.Format, "format data is bad input : $input, raw : $raw")
            return false
        }
        return true
    }

    private fun processTerminalState(raw: TimeSpanRawInfo, style: TimeSpanStandardStyles, result: TimeSpanResult): Boolean {
        if (raw.lastSeenTTT == TimeSpanTokenTypes.Num) {
            val token = TimeSpanToken()
            token.ttt = TimeSpanTokenTypes.Sep
            token.sep = ""
            if (raw.processToken(token, result)) {
                result.setFailure(ParseFailureKind.Format, "format data is bad token : $token")
                return false
            }
        }

        return when (raw.numCount) {
            1 -> processTerminal_D(raw, style, result)
            2 -> processTerminal_HM(raw, style, result)
            3 -> processTerminal_HMSD(raw, style, result)
            4 -> processTerminal_HMSFD(raw, style, result)
            5 -> processTerminal_DHMSF(raw, style, result)
            else -> {
                result.setFailure(ParseFailureKind.Format, "format data is bad raw : $raw")
                false
            }
        }
    }
    fun processTerminal_D(raw: TimeSpanRawInfo, style: TimeSpanStandardStyles, result: TimeSpanResult): Boolean {
        if (raw.sepCount != 2 || raw.numCount != 1 || style.flag(TimeSpanStandardStyle.RequireFull) != 0) {
            result.setFailure(ParseFailureKind.Format, "format data is bad")
            return false
        }

        val inv = style.flag(TimeSpanStandardStyle.Invariant) != 0
        val loc = style.flag(TimeSpanStandardStyle.Localized) != 0
        var positive = false
        var match = false
        if (inv) {
            if (raw.fullDMatch(raw.positiveInvariant)) {
                match = true
                positive = true
            }
            if (!match && raw.fullDMatch(raw.negativeInvariant)) {
                match = true
                positive = false
            }
        }
        if (loc) {
            if (!match && raw.fullDMatch(raw.positiveLocalized)) {
                match = true
                positive = true
            }
            if (!match && raw.fullDMatch(raw.negativeLocalized)) {
                match = true
                positive = false
            }
        }

        var ticks = 0L
        if (match) {
            val pair = tryTimeToTicks(positive, raw.numbers[0], zero, zero, zero, zero)
            if (!pair.first) {
                result.setFailure(ParseFailureKind.OverFlow, "data is overflow")
                return false
            }
            if (!positive) {
                ticks = -pair.second
                if (ticks > 0) {
                    result.setFailure(ParseFailureKind.OverFlow, "data is overflow")
                    return false
                }
            }
            result.parsedTimeSpan._ticks = ticks
            return false
        }

        result.setFailure(ParseFailureKind.Format, "format data is bad")
        return false
    }
    fun processTerminal_HM(raw: TimeSpanRawInfo, style: TimeSpanStandardStyles, result: TimeSpanResult): Boolean {
        if (raw.sepCount != 3 || raw.numCount != 2 || style.flag(TimeSpanStandardStyle.RequireFull) != 0) {
            result.setFailure(ParseFailureKind.Format, "format data is bad")
            return false
        }

        val inv = style.flag(TimeSpanStandardStyle.Invariant) != 0
        val loc = style.flag(TimeSpanStandardStyle.Localized) != 0
        var positive = false
        var match = false
        if (inv) {
            if (raw.fullHMMatch(raw.positiveInvariant)) {
                match = true
                positive = true
            }
            if (!match && raw.fullHMMatch(raw.negativeInvariant)) {
                match = true
                positive = false
            }
        }
        if (loc) {
            if (!match && raw.fullHMMatch(raw.positiveLocalized)) {
                match = true
                positive = true
            }
            if (!match && raw.fullHMMatch(raw.negativeLocalized)) {
                match = true
                positive = false
            }
        }

        var ticks = 0L
        if (match) {
            val pair = tryTimeToTicks(positive, zero, raw.numbers[0], raw.numbers[1], zero, zero)
            if (!pair.first) {
                result.setFailure(ParseFailureKind.OverFlow, "data is overflow")
                return false
            }
            ticks = pair.second
            if (!positive) {
                ticks = -ticks
                if (ticks > 0) {
                    result.setFailure(ParseFailureKind.OverFlow, "data is overflow")
                    return false
                }
            }
            result.parsedTimeSpan._ticks = ticks
            return false
        }

        result.setFailure(ParseFailureKind.Format, "format data is bad")
        return false
    }
    fun processTerminal_HMSD(raw: TimeSpanRawInfo, style: TimeSpanStandardStyles, result: TimeSpanResult): Boolean {
        if (raw.sepCount != 4 || raw.numCount != 3 || style.flag(TimeSpanStandardStyle.RequireFull) != 0) {
            result.setFailure(ParseFailureKind.Format, "format data is bad")
            return false
        }

        val inv = style.flag(TimeSpanStandardStyle.Invariant) != 0
        val loc = style.flag(TimeSpanStandardStyle.Localized) != 0
        var positive = false
        var match = false
        var ticks = 0L
        var overflow = false
        if (inv) {
            if (raw.fullHMSMatch(raw.positiveInvariant)) {
                positive = true
                val pair = tryTimeToTicks(positive, zero, raw.numbers[0], raw.numbers[1], raw.numbers[2], zero)
                match = pair.first
                ticks = pair.second
                overflow = overflow || !match
            }
            if (!match && raw.fullDHMMatch(raw.positiveInvariant)) {
                positive = true
                val pair = tryTimeToTicks(positive, raw.numbers[0], raw.numbers[1], raw.numbers[2], zero, zero)
                match = pair.first
                ticks = pair.second
                overflow = overflow || !match
            }
            if (!match && raw.partialAppCompatMatch(raw.positiveInvariant)) {
                positive = true
                val pair = tryTimeToTicks(positive, zero, raw.numbers[0], raw.numbers[1], zero, raw.numbers[2])
                match = pair.first
                ticks = pair.second
                overflow = overflow || !match
            }
            if (!match && raw.fullHMSMatch(raw.negativeInvariant)) {
                positive = false
                val pair = tryTimeToTicks(positive, zero, raw.numbers[0], raw.numbers[1], raw.numbers[2], zero)
                match = pair.first
                ticks = pair.second
                overflow = overflow || !match
            }
            if (!match && raw.fullDHMMatch(raw.negativeInvariant)) {
                positive = false
                val pair = tryTimeToTicks(positive, raw.numbers[0], raw.numbers[1], raw.numbers[2], zero, zero)
                match = pair.first
                ticks = pair.second
                overflow = overflow || !match
            }
            if (!match && raw.partialAppCompatMatch(raw.negativeInvariant)) {
                positive = false
                val pair = tryTimeToTicks(positive, zero, raw.numbers[0], raw.numbers[1], zero, raw.numbers[2])
                match = pair.first
                ticks = pair.second
                overflow = overflow || !match
            }
        }
        if (loc) {
            if (!match && raw.fullHMSMatch(raw.positiveLocalized)) {
                positive = true
                val pair = tryTimeToTicks(positive, zero, raw.numbers[0], raw.numbers[1], raw.numbers[2], zero)
                match = pair.first
                ticks = pair.second
                overflow = overflow || !match
            }
            if (!match && raw.fullDHMMatch(raw.positiveLocalized)) {
                positive = true
                val pair = tryTimeToTicks(positive, raw.numbers[0], raw.numbers[1], raw.numbers[2], zero, zero)
                match = pair.first
                ticks = pair.second
                overflow = overflow || !match
            }
            if (!match && raw.partialAppCompatMatch(raw.positiveLocalized)) {
                positive = true
                val pair = tryTimeToTicks(positive, zero, raw.numbers[0], raw.numbers[1], zero, raw.numbers[2])
                match = pair.first
                ticks = pair.second
                overflow = overflow || !match
            }
            if (!match && raw.fullHMSMatch(raw.negativeLocalized)) {
                positive = false
                val pair = tryTimeToTicks(positive, zero, raw.numbers[0], raw.numbers[1], raw.numbers[2], zero)
                match = pair.first
                ticks = pair.second
                overflow = overflow || !match
            }
            if (!match && raw.fullDHMMatch(raw.negativeLocalized)) {
                positive = false
                val pair = tryTimeToTicks(positive, raw.numbers[0], raw.numbers[1], raw.numbers[2], zero, zero)
                match = pair.first
                ticks = pair.second
                overflow = overflow || !match
            }
            if (!match && raw.partialAppCompatMatch(raw.negativeLocalized)) {
                positive = false
                val pair = tryTimeToTicks(positive, zero, raw.numbers[0], raw.numbers[1], zero, raw.numbers[2])
                match = pair.first
                ticks = pair.second
                overflow = overflow || !match
            }
        }

        if (match) {
            if (!positive) {
                ticks = -ticks
                if (ticks > 0) {
                    result.setFailure(ParseFailureKind.OverFlow, "data is overflow")
                    return false
                }
            }
            result.parsedTimeSpan._ticks = ticks
            return false
        }
        return if (overflow) {
            result.setFailure(ParseFailureKind.OverFlow, "data is overflow")
            false
        } else {
            result.setFailure(ParseFailureKind.Format, "format data is bad")
            false
        }
    }
    fun processTerminal_HMSFD(raw: TimeSpanRawInfo, style: TimeSpanStandardStyles, result: TimeSpanResult): Boolean {
        if (raw.sepCount != 5 || raw.numCount != 4 || style.flag(TimeSpanStandardStyle.RequireFull) != 0) {
            result.setFailure(ParseFailureKind.Format, "format data is bad")
            return false
        }

        val inv = style.flag(TimeSpanStandardStyle.Invariant) != 0
        val loc = style.flag(TimeSpanStandardStyle.Localized) != 0
        var positive = false
        var match = false
        var ticks = 0L
        var overflow = false
        if (inv) {
            if (raw.fullHMSFMatch(raw.positiveInvariant)) {
                positive = true
                val pair = tryTimeToTicks(positive, zero, raw.numbers[0], raw.numbers[1], raw.numbers[2], raw.numbers[3])
                match = pair.first
                ticks = pair.second
                overflow = overflow || !match
            }
            if (!match && raw.fullDHMSMatch(raw.positiveInvariant)) {
                positive = true
                val pair = tryTimeToTicks(positive, raw.numbers[0], raw.numbers[1], raw.numbers[2], raw.numbers[3], zero)
                match = pair.first
                ticks = pair.second
                overflow = overflow || !match
            }
            if (!match && raw.fullAppCompatMatch(raw.positiveInvariant)) {
                positive = true
                val pair = tryTimeToTicks(positive, raw.numbers[0], raw.numbers[1], raw.numbers[2], zero, raw.numbers[3])
                match = pair.first
                ticks = pair.second
                overflow = overflow || !match
            }
            if (!match && raw.fullHMSFMatch(raw.negativeInvariant)) {
                positive = false
                val pair = tryTimeToTicks(positive, zero, raw.numbers[0], raw.numbers[1], raw.numbers[2], raw.numbers[3])
                match = pair.first
                ticks = pair.second
                overflow = overflow || !match
            }
            if (!match && raw.fullDHMSMatch(raw.negativeInvariant)) {
                positive = false
                val pair = tryTimeToTicks(positive, raw.numbers[0], raw.numbers[1], raw.numbers[2], raw.numbers[3], zero)
                match = pair.first
                ticks = pair.second
                overflow = overflow || !match
            }
            if (!match && raw.fullAppCompatMatch(raw.negativeInvariant)) {
                positive = false
                val pair = tryTimeToTicks(positive, raw.numbers[0], raw.numbers[1], raw.numbers[2], zero, raw.numbers[3])
                match = pair.first
                ticks = pair.second
                overflow = overflow || !match
            }
        }
        if (loc) {
            if (raw.fullHMSFMatch(raw.positiveLocalized)) {
                positive = true
                val pair = tryTimeToTicks(positive, zero, raw.numbers[0], raw.numbers[1], raw.numbers[2], raw.numbers[3])
                match = pair.first
                ticks = pair.second
                overflow = overflow || !match
            }
            if (!match && raw.fullDHMSMatch(raw.positiveLocalized)) {
                positive = true
                val pair = tryTimeToTicks(positive, raw.numbers[0], raw.numbers[1], raw.numbers[2], raw.numbers[3], zero)
                match = pair.first
                ticks = pair.second
                overflow = overflow || !match
            }
            if (!match && raw.fullAppCompatMatch(raw.positiveLocalized)) {
                positive = true
                val pair = tryTimeToTicks(positive, raw.numbers[0], raw.numbers[1], raw.numbers[2], zero, raw.numbers[3])
                match = pair.first
                ticks = pair.second
                overflow = overflow || !match
            }
            if (!match && raw.fullHMSFMatch(raw.negativeLocalized)) {
                positive = false
                val pair = tryTimeToTicks(positive, zero, raw.numbers[0], raw.numbers[1], raw.numbers[2], raw.numbers[3])
                match = pair.first
                ticks = pair.second
                overflow = overflow || !match
            }
            if (!match && raw.fullDHMSMatch(raw.negativeLocalized)) {
                positive = false
                val pair = tryTimeToTicks(positive, raw.numbers[0], raw.numbers[1], raw.numbers[2], raw.numbers[3], zero)
                match = pair.first
                ticks = pair.second
                overflow = overflow || !match
            }
            if (!match && raw.fullAppCompatMatch(raw.negativeLocalized)) {
                positive = false
                val pair = tryTimeToTicks(positive, raw.numbers[0], raw.numbers[1], raw.numbers[2], zero, raw.numbers[3])
                match = pair.first
                ticks = pair.second
                overflow = overflow || !match
            }
        }

        if (match) {
            if (!positive) {
                ticks = -ticks
                if (ticks > 0) {
                    result.setFailure(ParseFailureKind.OverFlow, "data is overflow")
                    return false
                }
            }
            result.parsedTimeSpan._ticks = ticks
            return false
        }
        return if (overflow) {
            result.setFailure(ParseFailureKind.OverFlow, "data is overflow")
            false
        } else {
            result.setFailure(ParseFailureKind.Format, "format data is bad")
            false
        }
    }
    fun processTerminal_DHMSF(raw: TimeSpanRawInfo, style: TimeSpanStandardStyles, result: TimeSpanResult): Boolean {
        if (raw.sepCount != 6 || raw.numCount != 5) {
            result.setFailure(ParseFailureKind.Format, "format data is bad")
            return false
        }

        val inv = style.flag(TimeSpanStandardStyle.Invariant) != 0
        val loc = style.flag(TimeSpanStandardStyle.Localized) != 0
        var positive = false
        var match = false
        if (inv) {
            if (raw.fullMatch(raw.positiveInvariant)) {
                match = true
                positive = true
            }
            if (!match && raw.fullMatch(raw.negativeInvariant)) {
                match = true
                positive = false
            }
        }
        if (loc) {
            if (raw.fullMatch(raw.positiveLocalized)) {
                match = true
                positive = true
            }
            if (!match && raw.fullMatch(raw.negativeLocalized)) {
                match = true
                positive = false
            }
        }

        var ticks = 0L
        if (match) {
            val pair = tryTimeToTicks(positive, raw.numbers[0], raw.numbers[1], raw.numbers[2], raw.numbers[3], raw.numbers[4])
            if (!pair.first) {
                result.setFailure(ParseFailureKind.OverFlow, "daa is overflow")
                return false
            }
            ticks = pair.second
            if (!positive) {
                ticks = -ticks
                if (ticks > 0) {
                    result.setFailure(ParseFailureKind.OverFlow, "data is overflow")
                    return false
                }
            }
            result.parsedTimeSpan._ticks = ticks
            return false
        }

        result.setFailure(ParseFailureKind.Format, "format data is bad")
        return false
    }

    fun tryParseExactTimeSpan(input: String?, format: String?, fLocalize: FLocalize, styles: TimeSpanStyles, result: TimeSpanResult): Boolean {
        if (input == null) {
            result.setFailure(ParseFailureKind.ArgumentNull, "argument is null")
            return false
        }
        if (format == null) {
            result.setFailure(ParseFailureKind.ArgumentNull, "format is null")
            return false
        }
        if (format.isEmpty()) {
            result.setFailure(ParseFailureKind.Format, "format data is bad")
            return false
        }
        if (format.length == 1) {
            var styleBuff: TimeSpanStandardStyles = EnumSet.of(TimeSpanStandardStyle.None)
            if (format[0] == 'c' || format[0] == 't' || format[0] == 'T') {
                return tryParseTimeSpanConstant(input, result)
            } else if (format[0] == 'g') {
                styleBuff = styleBuff and TimeSpanStandardStyle.Localized
            } else if (format[0] == 'G') {
                styleBuff = styleBuff and TimeSpanStandardStyle.Localized and TimeSpanStandardStyle.RequireFull
            } else {
                result.setFailure(ParseFailureKind.Format, "format data is bad")
                return false
            }
            return tryParseTimeSpan(input, styleBuff, fLocalize, result)
        }

        return tryParseByFormat(input, format, styles, result)
    }
    fun tryParseExactMultipleTimeSpan(input: String?, formats: List<String?>?, fLocalize: FLocalize, styles: TimeSpanStyles, result: TimeSpanResult): Boolean {
        if (input == null) {
            result.setFailure(ParseFailureKind.ArgumentNull, "argument is null")
            return false
        }
        if (formats == null) {
            result.setFailure(ParseFailureKind.ArgumentNull, "format is null")
            return false
        }
        if (input.isEmpty()) {
            result.setFailure(ParseFailureKind.ArgumentNull, "argument data is bad")
            return false
        }
        if (formats.isEmpty()) {
            result.setFailure(ParseFailureKind.Format, "format data is bad")
            return false
        }

        formats.forEach { x ->
            if (x.isNullOrEmpty()) {
                result.setFailure(ParseFailureKind.Format, "format data is bad")
                return false
            }

            val innerResult = TimeSpanResult()
            if (tryParseExactTimeSpan(input, x, fLocalize, styles, innerResult)) {
                result.parsedTimeSpan = innerResult.parsedTimeSpan
                return true
            }
        }

        result.setFailure(ParseFailureKind.Format, "format data is bad")
        return false
    }
    fun tryParseTimeSpanConstant(input: String?, result: TimeSpanResult): Boolean {
        return StringParser().tryParse(input, result)
    }
    fun tryParseByFormat(input: String?, format: String?, styles: TimeSpanStyles, result: TimeSpanResult): Boolean {
        if (input == null) {
            result.setFailure(ParseFailureKind.ArgumentNull, "argument is null")
            return false
        }
        if (format == null) {
            result.setFailure(ParseFailureKind.ArgumentNull, "format is null")
            return false
        }
        if (input.isEmpty()) {
            result.setFailure(ParseFailureKind.ArgumentNull, "argument data is bad")
            return false
        }
        if (format.isEmpty()) {
            result.setFailure(ParseFailureKind.Format, "format data is bad")
            return false
        }

        var seenDD = false
        var seenHH = false
        var seenMM = false
        var seenSS = false
        var seenFF = false
        var dd = 0
        var hh = 0
        var mm = 0
        var ss = 0
        var leadingZeroes = 0
        var ff = 0
        var i = 0
        var tokenLen = 0

        val tokenizer = TimeSpanTokenizer().init(-1, input)
        while (i < format.length) {
            var ch = format[i]
            var nextFormatChar = 0
            when (ch) {
                'h' -> {
                    tokenLen = FDateTimeFormat.parseRepeatPattern(format, i, ch)
                    val pair = parseExactDigits(tokenizer, tokenLen)
                    if (tokenLen > 2 || seenHH || !pair.first) {
                        result.setFailure(ParseFailureKind.Format, "format data is bad")
                        return false
                    }
                    hh = pair.second
                    seenHH = true
                }
                'm' -> {
                    tokenLen = FDateTimeFormat.parseRepeatPattern(format, i, ch)
                    val pair = parseExactDigits(tokenizer, tokenLen)
                    if (tokenLen > 2 || seenMM || !pair.first) {
                        result.setFailure(ParseFailureKind.Format, "format data is bad")
                        return false
                    }
                    mm = pair.second
                    seenMM = true
                }
                's' -> {
                    tokenLen = FDateTimeFormat.parseRepeatPattern(format, i, ch)
                    val pair = parseExactDigits(tokenizer, tokenLen)
                    if (tokenLen > 2 || seenSS || !pair.first) {
                        result.setFailure(ParseFailureKind.Format, "format data is bad")
                        return false
                    }
                    ss = pair.second
                    seenSS = true
                }
                'f' -> {
                    tokenLen = FDateTimeFormat.parseRepeatPattern(format, i, ch)
                    val pair = parseExactDigits(tokenizer, tokenLen, tokenLen)
                    if (tokenLen > FDateTimeFormat.maxSecondsFractionDigits || seenFF || !pair.first) {
                        result.setFailure(ParseFailureKind.Format, "format data is bad")
                        return false
                    }
                    ff = pair.second.second
                    seenFF = true
                }
                'F' -> {
                    tokenLen = FDateTimeFormat.parseRepeatPattern(format, i, ch)
                    if (tokenLen > FDateTimeFormat.maxSecondsFractionDigits || seenFF) {
                        result.setFailure(ParseFailureKind.Format, "format data is bad")
                        return false
                    }
                    ff = parseExactDigits(tokenizer, tokenLen, tokenLen).second.second
                    seenFF = true
                }
                'd' -> {
                    tokenLen = FDateTimeFormat.parseRepeatPattern(format, i, ch)
                    val pair = parseExactDigits(tokenizer, if (tokenLen < 2) 1 else tokenLen, if (tokenLen < 2) 8 else tokenLen)
                    if (tokenLen > 8 || seenDD || !pair.first) {
                        result.setFailure(ParseFailureKind.Format, "format data is bad")
                        return false
                    }
                    dd = pair.second.second
                    seenDD = true
                }
                '\'', '\"' -> {

                }
                '%' -> {
                    nextFormatChar = FDateTimeFormat.parseNextChar(format, i)
                    if (nextFormatChar >= 0 && nextFormatChar != '%'.toInt()) {
                        tokenLen = 1
                    } else {
                        result.setFailure(ParseFailureKind.Format, "format data is bad")
                        return false
                    }
                }
                '\\' -> {
                    nextFormatChar = FDateTimeFormat.parseNextChar(format, i)
                    if (nextFormatChar >= 0 && tokenizer.nextChar == nextFormatChar.toChar()) {
                        tokenLen = 2
                    } else {
                        result.setFailure(ParseFailureKind.Format, "format data is bad")
                        return false
                    }
                }
                else -> {
                    result.setFailure(ParseFailureKind.Format, "format data is bad")
                    return false
                }
            }
            i += tokenLen
        }

        if (!tokenizer.eol) {
            result.setFailure(ParseFailureKind.Format, "format data is bad")
            return false
        }

        var ticks = 0L
        val positive = (styles == TimeSpanStyles.None)
        val pairSub = tryTimeToTicks(positive, TimeSpanToken(dd), TimeSpanToken(hh), TimeSpanToken(mm), TimeSpanToken(ss), TimeSpanToken(leadingZeroes, ff))
        ticks = pairSub.second
        return if (pairSub.first) {
            if (!positive) {
                ticks = -ticks
            }
            result.parsedTimeSpan._ticks = ticks
            true
        } else {
            result.setFailure(ParseFailureKind.OverFlow, "data is overflow")
            false
        }
    }

    class StringParser {
        private var _str: String = ""
        private var _ch: Char = Char.MIN_VALUE
        private var _pos: Int = -1
        private var _len = 0

        fun nextChar() {
            if (_pos < _len) _pos++
            _ch = if (_pos < _len) _str[_pos] else Char.MIN_VALUE
        }
        fun nextNonDigit(): Char {
            var index = _pos
            while (index < _len) {
                val ch = _str[index]
                if (ch < '0' || ch > '9') {
                    return ch
                }
                index++
            }

            return Char.MIN_VALUE
        }
        fun tryParse(input: String?, result: TimeSpanResult): Boolean {
            result.parsedTimeSpan._ticks = 0
            if (input == null) {
                result.setFailure(ParseFailureKind.ArgumentNull, "argument is null")
                return false
            }
            _str = input
            _len = input.length
            _pos = -1
            nextChar()
            skipBlanks()
            var negative = false
            if (_ch == '-') {
                negative = true
                nextChar()
            }
            var time = 0L
            if (nextNonDigit() == ':') {
                val pair = parseTime(result)
                if (!pair.first) {
                    return false
                }
                time = pair.second
            } else {
                var days = 0
                val pair = parseInt((0x7FFFFFFFFFFFFFFFL / FTimeSpan.ticksPerDay).toInt(), result)
                if (!pair.first) {
                    return false
                }
                days = pair.second
                time = days * FTimeSpan.ticksPerDay
                if (_ch == '.') {
                    nextChar()
                    val pairSub = parseTime(result)
                    if (!pairSub.first) {
                        return false
                    }

                    time += pairSub.second
                }
            }
            if (negative) {
                time = -time
                if (time > 0) {
                    result.setFailure(ParseFailureKind.OverFlow, "data is overflow")
                    return false
                }
            } else {
                if (time < 0) {
                    result.setFailure(ParseFailureKind.OverFlow, "data is overflow")
                    return false
                }
            }
            skipBlanks()
            if (_pos < _len) {
                result.setFailure(ParseFailureKind.Format, "format data is bad")
                return false
            }
            result.parsedTimeSpan._ticks = time
            return true
        }
        fun parseInt(max: Int, result: TimeSpanResult): Pair<Boolean, Int> {
            var second = 0
            val pos = _pos
            while (_ch in '0'..'9') {
                if ((second and (0xF0000000).toInt()) != 0) {
                    result.setFailure(ParseFailureKind.OverFlow, "data is overflow")
                    return Pair(false, second)
                }
                second = second * 10 + (_ch.toInt() - '0'.toInt())
                if (second < 0) {
                    result.setFailure(ParseFailureKind.OverFlow, "data is overflow")
                    return Pair(false, second)
                }
                nextChar()
            }
            if (pos == _pos) {
                result.setFailure(ParseFailureKind.Format, "format data is bad")
                return Pair(false, second)
            }
            if (second > max) {
                result.setFailure(ParseFailureKind.OverFlow, "data is overflow")
                return Pair(false, second)
            }

            return Pair(true, second)
        }
        fun parseTime(result: TimeSpanResult): Pair<Boolean, Long> {
            var second = 0L
            var pair = parseInt(23, result)
            if (!pair.first) {
                return Pair(false, second)
            }
            second = pair.second * FTimeSpan.ticksPerHour
            if (_ch != ':') {
                result.setFailure(ParseFailureKind.Format, "format data is bad")
                return Pair(false, second)
            }
            nextChar()
            pair = parseInt(59, result)
            if (!pair.first) {
                return Pair(false, second)
            }
            second += pair.second * FTimeSpan.ticksPerMinute
            if (_ch == ':') {
                nextChar()
                if (_ch != '.') {
                    pair = parseInt(59, result)
                    if (!pair.first) {
                        return Pair(false, second)
                    }
                    second += pair.second * FTimeSpan.ticksPerSecond
                }
                if (_ch == '.') {
                    nextChar()
                    var ticksPerSecond = FTimeSpan.ticksPerSecond
                    while (ticksPerSecond > 1 && _ch >= '0' && _ch <= '9') {
                        ticksPerSecond /= 10
                        second += (_ch - '0') * ticksPerSecond
                        nextChar()
                    }
                }
            }
            return Pair(true, second)
        }
        fun skipBlanks() {
            while (_ch == ' ' || _ch == '\t') {
                nextChar()
            }
        }
    }
}
internal typealias TimeSpanStandardStyles = EnumSet<FTimeSpanParse.TimeSpanStandardStyle>