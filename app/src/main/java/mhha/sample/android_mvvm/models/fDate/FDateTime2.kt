package mhha.sample.android_mvvm.models.fDate

import java.util.TimeZone

/**
 * F date time2
 *
 * 시간도 넣었어야 했었는데
 * @constructor Create empty F date time2
 */
open class FDateTime2 {
    private var _thisDate: String = "9999-12-31"
    private var _splitChar: String = "-"
    private var _error: String = ""
    open fun setThis(data: FDateTime2?): FDateTime2 {
        if (data == null) {
            return this
        }
        this._thisDate = data._thisDate
        this._splitChar = data._splitChar
        return this
    }
    open fun setThis(dateString: String, splitChar: String = "-"): FDateTime2 {
        _thisDate = dateString
        _splitChar = splitChar
        return this
    }
    open fun setThis(year: Int, month: Int, day: Int, splitChar: String = "-"): FDateTime2 {
        this.setYear(year)
        this.setMonth(month)
        this.setDay(day)
        this.setSplitChar(splitChar)
        return this
    }
    open fun setThis(data: Long): FDateTime2 {
        val timeZoneOffset = (TimeZone.getDefault().rawOffset / 1000 / 60 / 60) + (data / 1000 / 60 / 60)
        val elapseDay = timeZoneOffset / 24
        this.setThis(1970, 1, 1)
        this.addDays(elapseDay)
        return this
    }
    fun getDate(): String {
        return _thisDate
    }
    fun getSplitChar(): String {
        return _splitChar
    }
    open fun setError(msg: String?): FDateTime2 {
        this._error = msg ?: "null"
        return this
    }
    open fun setError(msg: Exception): FDateTime2 {
        this._error = msg.message ?: "null"
        return this
    }
    fun getError(): String {
        return _error
    }
    open fun setSplitChar(splitChar: String): FDateTime2 {
        this._thisDate = this._thisDate.replace(this._splitChar, splitChar)
        this._splitChar = splitChar
        return this
    }
    fun getDoomsDay() = try {
        getDoomsDay(getYear())
    } catch (e: Exception) {
        setError(e)
        null
    }
    fun getDoomsDay(year: String) = getDoomsDay(year.toInt())
    fun getDoomsDay(year: Int): DayOfTheWeek {
        val century = year / 100
        val yearInCentury = year % 100

        val dayOfTheWeek = when (century % 4) {
            0 -> DayOfTheWeek.TUESDAY
            1 -> DayOfTheWeek.SUNDAY
            2 -> DayOfTheWeek.FRIDAY
            3 -> DayOfTheWeek.WEDNESDAY
            else -> throw Exception("getDoomsDay error year : $year")
        }

        val yearAnchor = yearInCentury / 12
        val yearRemainder = yearInCentury % 12
        val remainderAnchor = yearRemainder / 4
        return when ((dayOfTheWeek.dayOfIndex + yearAnchor + yearRemainder + remainderAnchor) % 7) {
            0 -> DayOfTheWeek.SUNDAY
            1 -> DayOfTheWeek.MONDAY
            2 -> DayOfTheWeek.TUESDAY
            3 -> DayOfTheWeek.WEDNESDAY
            4 -> DayOfTheWeek.THURSDAY
            5 -> DayOfTheWeek.FRIDAY
            6 -> DayOfTheWeek.SATURDAY
            else -> throw Exception("getDoomsDay error year: $year")
        }
    }
    fun isBefore(dateTime: FDateTime2) = isBefore(dateTime.getDate(), dateTime.getSplitChar())
    /**
     * FDateTime().isBefore
     * - 자신이 dateString 보다 작냐고 묻는 거.
     * @param dateString 대상 날짜
     * @param splitChar
     * @return Boolean
     */
    fun isBefore(dateString: String, splitChar: String = "-"): Boolean {
        val target = FDateTime2().setThis(dateString, splitChar)
        if (this.getYear() < target.getYear()) return true
        if (this.getMonth() < target.getMonth()) return true
        if (this.getDay() < target.getDay()) return true

        return false
    }
    fun isLeapYear() = try {
        isLeapYear(getYear())
    } catch (e: Exception) {
        setError(e)
        null
    }
    fun isLeapYear(year: String) = isLeapYear(year.toInt())
    fun isLeapYear(year: Int): Boolean {
        if (year % 400 == 0) return true
        if (year % 100 == 0) return false
        if (year % 4 == 0) return true
        return false
    }
    fun getDayOfWeek() = try {
        getDayOfWeek(this._thisDate, this._splitChar)
    } catch (e: Exception) {
        setError(e)
        null
    }
    fun getDayOfWeek(dateString: String, splitChar: String = "-"): DayOfTheWeek {
        val year = getYear(dateString, splitChar)
        val month = getMonth(dateString, splitChar)
        val day = getDay(dateString, splitChar)

        val doomsSameDay = getDoomsSameDay(year, month)

        val plusDay = (day + 35 - doomsSameDay) % 7
        return DayOfTheWeek.fromInt((getDoomsDay(year).dayOfIndex + plusDay) % 7) ?: throw Exception ()
    }

    /**
     * FDateTime().getDaysBetween()
     * - 두 날짜 간의 차이,
     *      - 자신보다 대상이 크면 양수를 리턴한다.
     * @param dateString - 대상
     * @param splitChar
     * @return int 형 day 데이터
     */
    fun getDaysBetween(dateString: String, splitChar: String = "-"): Int {
        val target = FDateTime2().setThis(dateString, splitChar)

        var targetYearDay = 0
        var thisYearDay = 0
        var diffYear = target.getYear() - this.getYear()
        if (diffYear > 0) {
            for (i in 0 .. diffYear) {
                targetYearDay += if (isLeapYear(target.getYear() - i)) 366 else 365
            }
            thisYearDay = if (isLeapYear(this.getYear())) 366 else 365
        } else if (diffYear < 0) {
            diffYear = -diffYear
            for (i in 0 .. diffYear) {
                thisYearDay += if (isLeapYear(this.getYear() - i)) 366 else 365
            }
            targetYearDay = if (isLeapYear(target.getYear())) 366 else 365
        }

        var targetMonthDay = 0
        var thisMonthDay = 0
        var diffMonth = target.getMonth() - this.getMonth()
        if (diffMonth > 0) {
            for (i in 1 .. diffMonth) {
                targetMonthDay += getMaxDayOfMonth(this.getYear(), target.getMonth() - i)
            }
        } else if (diffMonth < 0) {
            diffMonth = -diffMonth
            for (i in 1 .. diffMonth) {
                thisMonthDay += getMaxDayOfMonth(this.getYear(), this.getMonth() - i)
            }
        }
        val targetDay = target.getDay()
        val thisDay = getDay()

        return targetYearDay + targetMonthDay + targetDay - thisYearDay - thisMonthDay - thisDay
    }
    open fun getNextOrSameDate(dayOfTheWeek: DayOfTheWeek) = try {
        FDateTime2().setThis(getNextOrSameDate(this._thisDate, dayOfTheWeek, this._splitChar), this._splitChar)
    } catch (e: Exception) {
        FDateTime2().setError(e)
    }
    open fun getNextOrSameDay(dayOfTheWeek: DayOfTheWeek) = try {
        FDateTime2()
            .setThis(this._thisDate, this._splitChar)
            .setDay(getNextOrSameDay(this._thisDate, dayOfTheWeek, this._splitChar))
    } catch (e: Exception) {
        FDateTime2().setError(e)
    }
    fun getNextOrSameDate(dateString: String, dayOfTheWeek: DayOfTheWeek, splitChar: String = "-"): String {
        val getDayOfTheWeek = getDayOfWeek(dateString, splitChar)
        var gapBuffer = dayOfTheWeek.dayOfIndex - getDayOfTheWeek.dayOfIndex
        if (gapBuffer < 0) {
            gapBuffer += 7
        }
        return FDateTime2().setThis(dateString, splitChar).addDays(gapBuffer).getDate()
    }
    fun getNextOrSameDay(dateString: String, dayOfTheWeek: DayOfTheWeek, splitChar: String = "-"): Int {
        val getDayOfTheWeek = getDayOfWeek(dateString, splitChar)
        var gapBuffer = dayOfTheWeek.dayOfIndex - getDayOfTheWeek.dayOfIndex
        if (gapBuffer < 0) {
            gapBuffer += 7
        }

        return FDateTime2().setThis(dateString, splitChar).addDays(gapBuffer).getDay()
    }
    open fun getPrevOrSameDate(dayOfTheWeek: DayOfTheWeek) = try {
        FDateTime2().setThis(getPrevOrSameDate(this._thisDate, dayOfTheWeek, this._splitChar), this._splitChar)
    } catch (e: Exception) {
        FDateTime2().setError(e)
    }
    open fun getPrevOrSameDay(dayOfTheWeek: DayOfTheWeek) = try {
        FDateTime2()
            .setThis(this._thisDate, this._splitChar)
            .setDay(getPrevOrSameDay(this._thisDate, dayOfTheWeek, this._splitChar))
    } catch (e : Exception) {
        FDateTime2().setError(e)
    }
    fun getPrevOrSameDate(dateString: String, dayOfTheWeek: DayOfTheWeek, splitChar: String = "-"): String {
        val getDayOfTheWeek = getDayOfWeek(dateString, splitChar)
        var gapBuffer = getDayOfTheWeek.dayOfIndex - dayOfTheWeek.dayOfIndex
        if (gapBuffer < 0) {
            gapBuffer += 7
        }
        return FDateTime2().setThis(dateString, splitChar).addDays(-gapBuffer).getDate()
    }
    fun getPrevOrSameDay(dateString: String, dayOfTheWeek: DayOfTheWeek, splitChar: String = "-"): Int {
        val getDayOfTheWeek = getDayOfWeek(dateString, splitChar)
        var gapBuffer = getDayOfTheWeek.dayOfIndex - dayOfTheWeek.dayOfIndex
        if (gapBuffer < 0) {
            gapBuffer += 7
        }
        return FDateTime2().setThis(dateString, splitChar).addDays(-gapBuffer).getDay()
    }
    open fun getFirstDayOfMonth(dayOfTheWeek: DayOfTheWeek) = try {
        FDateTime2().setThis(getFirstDayOfMonth(this._thisDate, dayOfTheWeek, this._splitChar), this._splitChar)
    } catch (e: Exception) {
        setError(e)
    }
    fun getFirstDayOfMonth(dateString: String, dayOfTheWeek: DayOfTheWeek, splitChar: String = "-"): String {
        val buff = FDateTime2().setThis(dateString, splitChar).setDay(1)
        val getDayOfTheWeek = buff.getDayOfWeek() ?: return buff.getDate()
        val dayGap = getDayOfTheWeek.dayOfIndex - dayOfTheWeek.dayOfIndex
        val firstOfTheDay = if (dayGap > 0) {
            7 - dayGap
        } else if (dayGap == 0) {
            0
        } else {
            -dayGap
        }

        return buff.setDay(firstOfTheDay + 1).getDate()
    }
    open fun addYear(addYear: Int) = try {
        this._thisDate = addYear(this._thisDate, addYear, this._splitChar)
        this
    } catch (e: Exception) {
        setError(e)
    }
    fun addYear(dateString: String, addYear: Int, splitChar: String = "-"): String {
        var currentYear = getYear(dateString, splitChar) + addYear
        var currentMonth = getMonth(dateString, splitChar)
        var currentDay = getDay(dateString, splitChar)

        if (currentYear <= 0) {
            currentYear = 9999
        }
        val maxDay = getMaxDayOfMonth(currentYear, currentMonth)
        if (currentDay > maxDay) {
            currentDay -= maxDay
            currentMonth += 1
        }
        if (currentMonth > 12) {
            currentYear += 1
            currentMonth = 1
        }

        return FDateTime2().setThis(currentYear, currentMonth, currentDay).getDate()
    }
    open fun addMonth(addMonth: Int) = try {
        this._thisDate = addMonth(this._thisDate, addMonth, this._splitChar)
        this
    } catch (e: Exception) {
        setError(e)
    }
    fun addMonth(dateString: String, addMonth: Int, splitChar: String = "-"): String {
        var currentYear = getYear(dateString, splitChar)
        var currentMonth = getMonth(dateString, splitChar) + addMonth
        var currentDay = getDay(dateString, splitChar)
        while (true) {
            if (currentMonth > 12) {
                currentMonth -= 12
                currentYear += 1
            } else if (currentMonth <= 0) {
                currentMonth = 12
                currentYear -= 1
                if (currentYear <= 0) {
                    currentYear = 9999
                }
            } else {
                break
            }
        }

        val maxDay = getMaxDayOfMonth(currentYear, currentMonth)
        if (currentDay > maxDay) {
            currentDay -= maxDay
            currentMonth += 1
        }
        if (currentMonth > 12) {
            currentYear += 1
            currentMonth = 1
        }

        return FDateTime2().setThis(currentYear, currentMonth, currentDay, splitChar).getDate()
    }
    open fun addWeeks(addWeek: Int) = try {
        this._thisDate = addWeeks(this._thisDate, addWeek, this._splitChar)
        this
    } catch (e: Exception) {
        setError(e)
    }
    fun addWeeks(dateString: String, addWeek: Int, splitChar: String = "-") =  addDays(dateString, addWeek * 7, splitChar)
    open fun addDays(addDay: Long) = try {
        this._thisDate = addDays(this._thisDate, addDay, this._splitChar)
        this
    } catch (e: Exception) {
        setError(e)
    }
    open fun addDays(addDay: Int) = try {
        this._thisDate = addDays(this._thisDate, addDay, this._splitChar)
        this
    } catch (e: Exception) {
        setError(e)
    }
    fun addDays(dateString: String, addDay: Long, splitChar: String = "-"): String {
        var currentYear = getYear(dateString, splitChar)
        var currentMonth = getMonth(dateString, splitChar)
        var currentDay = (getDay(dateString, splitChar) + addDay).toInt()
        while (true) {
            val maxDay = getMaxDayOfYear(currentYear)
            if (currentDay > maxDay) {
                currentDay -= maxDay
                currentYear += 1
            } else {
                break
            }
        }
        while (true) {
            val maxDay = getMaxDayOfMonth(currentYear, currentMonth)
            if (currentDay > maxDay) {
                currentDay -= maxDay
                currentMonth += 1
            } else if (currentDay <= 0){
                currentMonth -= 1
                if (currentMonth == 0) {
                    currentMonth = 12
                    currentYear -= 1
                    if (currentYear <= 0) {
                        currentYear = 9999
                    }
                }
                currentDay += getMaxDayOfMonth(currentYear, currentMonth)
            } else {
                break
            }
            if (currentMonth > 12) {
                currentYear += 1
                currentMonth = 1
            }
        }

        return FDateTime2().setThis(currentYear, currentMonth, currentDay, splitChar).getDate()
    }
    fun addDays(dateString: String, addDay: Int, splitChar: String = "-") = addDays(dateString, addDay.toLong(), splitChar)

    open fun setYear(year: Int) = try {
        this.setThis(setYear(this._thisDate, year, this._splitChar))
    } catch (e: Exception) {
        this.setError(e)
    }
    open fun setMonth(month: Int) = try {
        this.setThis(setMonth(this._thisDate, month, this._splitChar))
    } catch (e: Exception) {
        this.setError(e)
    }
    open fun setDay(day: Int) = try {
        this.setThis(setDay(this._thisDate, day, this._splitChar))
    } catch (e: Exception) {
        this.setError(e)
    }
    open fun setLastDay() = try {
        this.setThis(setDay(this._thisDate, getMaxDayOfMonth(), this._splitChar))
    } catch (e: Exception) {
        this.setError(e)
    }
    fun getYear() = try {
        getYear(this._thisDate, this._splitChar)
    } catch (e: Exception) {
        setError(e)
        -1
    }
    fun getMonth() = try {
        getMonth(this._thisDate, this._splitChar)
    } catch (e: Exception) {
        setError(e)
        -1
    }
    fun getDay() = try {
        getDay(this._thisDate, this._splitChar)
    } catch (e: Exception) {
        setError(e)
        -1
    }
    fun setYear(dateString: String, year: Int, splitChar: String = "-"): String {
        if (dateString.split(splitChar).size != 3) {
            throw Exception("setYear format illegal dateString : $dateString, splitChar : $splitChar")
        }
        if (year <= 0) {
            throw Exception("setYear format illegal year : $year")
        }
        val month = getMonthString(dateString, splitChar)
        val day = getDayString(dateString, splitChar)
        return "$year$splitChar$month$splitChar$day"
    }
    fun getYear(dateString: String, splitChar: String = "-"): Int {
        if (dateString.split(splitChar).size != 3) {
            throw Exception("getYear format illegal dateString : $dateString, splitChar : $splitChar")
        }
        val ret = dateString.split(splitChar)[0].toInt()
        if (ret <= 0) {
            throw Exception("getYear format illegal dateString : $dateString, splitChar : $splitChar, year : $ret")
        }
        return ret
    }
    fun getYearString(dateString: String, splitChar: String = "-") = getYear(dateString, splitChar).toString()
    fun setMonth(dateString: String, month: Int, splitChar: String = "-"): String {
        if (dateString.split(splitChar).size != 3) {
            throw Exception("setMonth format illegal dateString : $dateString, splitChar : $splitChar")
        }

        if (month <= 0 || month > 13) {
            throw Exception("setMonth format illegal month : $month")
        }
        val year = getYearString(dateString, splitChar)
        val day = getDayString(dateString, splitChar)
        return "$year$splitChar${"%02d".format(month)}$splitChar$day"
    }
    fun getMonth(dateString: String, splitChar: String = "-"): Int {
        if (dateString.split(splitChar).size != 3) {
            throw Exception("getMonth format illegal dateString : $dateString, splitChar : $splitChar")
        }
        val retBuff = dateString.split(splitChar)[1]
        val ret = if (retBuff.length >= 2) retBuff.substring(0, 2).toInt() else retBuff.toInt()
        if (ret <= 0 || ret >= 13) {
            throw Exception("getMonth format illegal dateString : $dateString, splitChar : $splitChar, month : $ret")
        }
        return ret
    }
    fun getMonthString(dateString: String, splitChar: String = "-") = "%02d".format(getMonth(dateString, splitChar))
    fun setDay(dateString: String, day: Int, splitChar: String = "-"): String {
        if (dateString.split(splitChar).size != 3) {
            throw Exception("setDay format illegal dateString : $dateString, splitChar : $splitChar")
        }

        val year = getYearString(dateString, splitChar)
        val month = getMonthString(dateString, splitChar)
        val maxDay = getMaxDayOfMonth(year.toInt(), getMonth(dateString, splitChar))
        if (day <= 0 || day > maxDay) {
            throw Exception("setDay format illegal month : $month, months MaxDay : $maxDay, day : $day")
        }
        return "$year$splitChar$month$splitChar${"%02d".format(day)}"
    }
    fun getDay(dateString: String, splitChar: String = "-"): Int {
        if (dateString.split(splitChar).size != 3) {
            throw Exception("getDay format illegal dateString : $dateString, splitChar : $splitChar")
        }
        val year = getYear(dateString, splitChar)
        val month = getMonth(dateString, splitChar)
        val retBuff = dateString.split(splitChar)[2]
        val ret = if (retBuff.length >= 2) retBuff.substring(0, 2).toInt() else retBuff.toInt()
        if (ret <= 0 || ret > getMaxDayOfMonth(year, month)) {
            throw Exception("getDay format illegal dateString : $dateString, splitChar : $splitChar, day : $ret")
        }
        return ret
    }
    fun getDayString() = getDay().toString()
    fun getDayString(dateString: String, splitChar: String = "-") = "%02d".format(getDay(dateString, splitChar))
    fun getMaxDayOfMonth() =  try {
        getMaxDayOfMonth(this.getYear(), this.getMonth())
    } catch (e: Exception) {
        setError(e)
        -1
    }
    fun getMaxDayOfYear(year: Int) = if (isLeapYear(year)) 366 else 365
    fun getMaxDayOfMonth(year: Int, month: Int) =  when (month) {
        1 -> 31
        2 -> if (isLeapYear(year)) 29 else 28
        3 -> 31
        4 -> 30
        5 -> 31
        6 -> 30
        7 -> 31
        8 -> 31
        9 -> 30
        10 -> 31
        11 -> 30
        12 -> 31
        else -> throw Exception("getMaxDayOfMonth month : $month")
    }
    private fun getDoomsSameDay(year: Int, month: Int) = when (month) {
        1 -> if (isLeapYear(year)) 4 else 3
        2 -> if (isLeapYear(year)) 29 else 28
        3 -> 0
        4 -> 4
        5 -> 9
        6 -> 6
        7 -> 11
        8 -> 8
        9 -> 5
        10 -> 10
        11 -> 7
        12 -> 12
        else -> throw Exception("getDoomsSameDay month : $month")
    }

    enum class DayOfTheWeek(val dayOfKorea: String, val dayOfIndex: Int) {
        SUNDAY("일", 0),
        MONDAY("월", 1),
        TUESDAY("화", 2),
        WEDNESDAY("수",3),
        THURSDAY("목", 4),
        FRIDAY("금", 5),
        SATURDAY("토", 6);
        companion object {
            fun fromString(value: String) = DayOfTheWeek.values().firstOrNull { it.dayOfKorea == value }
            fun fromInt(value: Int) = DayOfTheWeek.values().firstOrNull { it.dayOfIndex == value }
            fun next(value: DayOfTheWeek): DayOfTheWeek {
                return if (value.dayOfIndex + 1 > 6) {
                    SUNDAY
                } else {
                    fromInt(value.dayOfIndex + 1) ?: SUNDAY
                }
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is FDateTime2) return false
        if (this.getYear() != other.getYear()) return false
        if (this.getMonth() != other.getMonth()) return false
        if (this.getDay() != other.getDay()) return false

        return true
    }

    override fun hashCode(): Int {
        var result = _thisDate.hashCode()
        result = 31 * result + _splitChar.hashCode()
        result = 31 * result + _error.hashCode()
        return result
    }
}