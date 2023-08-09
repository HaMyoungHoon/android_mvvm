package mhha.sample.android_mvvm.models.fDate

import android.os.Build
import androidx.annotation.RequiresApi
import java.math.BigInteger
import java.time.LocalDate
import java.time.YearMonth
import java.util.TimeZone
import kotlin.math.absoluteValue

/**
 * FDateTime
 * - .net 배끼는 중
 * @constructor Create empty F date time
 */
open class FDateTime {
    internal val _ticksMask: BigInteger = BigInteger.valueOf(0x3FFFFFFFFFFFFFFF)
    internal val _flagsMask: BigInteger = BigInteger("C000000000000000", 16)
    internal val _ticksPerMillisecond = 10000
    internal val _ticksPerSecond = _ticksPerMillisecond * 1000
    internal val _ticksPerMinute = _ticksPerSecond * 60
    internal val _ticksPerHour = _ticksPerMinute.toLong() * 60
    internal val _ticksPerDay = _ticksPerHour * 24
    internal val _millisPerSecond = 1000
    internal val _millisPerMinute = _millisPerSecond * 60
    internal val _millisPerHour = _millisPerMinute * 60
    internal val _millisPerDay = _millisPerHour * 24
    internal val _daysPerYear = 365
    internal val _daysPer4Years = _daysPerYear * 4 + 1
    internal val _daysPer100Years = _daysPer4Years * 25 - 1
    internal val _daysPer400Years = _daysPer100Years * 4 + 1
    internal val _daysTo1601 = _daysPer400Years * 4
    internal val _daysTo1899 = _daysPer400Years * 4 + _daysPer100Years * 3 - 367
    internal val _daysTo1970 = _daysPer400Years * 4 + _daysPer100Years * 3 + _daysPer4Years * 17 + _daysPerYear
    internal val _daysTo10000 = _daysPer400Years * 25 - 366
    internal val _minTicks = 0
    internal val _maxTicks = _daysTo10000 * _ticksPerDay - 1
    internal val _maxMillis = (_daysTo10000.toLong() * _millisPerDay)
    internal val _fileTimeOffset = _daysTo1601 * _ticksPerDay
    internal val _doubleDateOffset = _daysTo1899 * _ticksPerDay
    internal val _oADateMinAsTicks = (_daysPer100Years - _daysPerYear) * _ticksPerDay
    internal val _oADateMinAsDouble = -657435.0
    internal val _oADateMaxAsDouble = 2958466.0
    internal val _datePartYear = 0
    internal val _datePartDayOfYear = 1
    internal val _datePartMonth = 2
    internal val _datePartDay = 3

    private var _dateData = BigInteger.ZERO
    private var _localize: FLocalize = FLocalize.KOREA
    val localize get() = _localize
    internal val internalTicks get() = (_dateData and _ticksMask).toLong()
    internal val internalKind get() = (_dateData and _flagsMask).toLong()
    val millisecond get() = ((internalTicks / _ticksPerMillisecond) % 1000).toInt()
    val second get() = ((internalTicks / _ticksPerSecond) % 60).toInt()
    val minute get() = ((internalTicks / _ticksPerMinute) % 60).toInt()
    val hour get() = ((internalTicks / _ticksPerHour) % 24).toInt()
    val day get() = getDatePart(_datePartDay)
    val dayOfWeek get() = FDayOfWeek.fromInt(((internalTicks / _ticksPerDay + 1) % 7).toInt())
    val dayOfYear get() = getDatePart(_datePartDayOfYear)
    val month get() = getDatePart(_datePartMonth)
    val year get() = getDatePart(_datePartYear)

    companion object {
        val _daysToMonth365 = arrayListOf(0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334, 365)
        val _daysToMonth366 = arrayListOf(0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335, 366)
        fun daysInMonth(year: Int, month: Int): Int {
            if (month < 1 || month > 12) return -1
            val days = if (isLeapYear(year)) _daysToMonth366 else _daysToMonth365
            return days[month] - days[month - 1]
        }

        private fun isLeapYear(year: Int): Boolean {
            if (year % 400 == 0) return true
            if (year % 100 == 0) return false
            if (year % 4 == 0) return true
            return false
        }
        fun getMaxDayOfMonth(year: Int, month: Int): Int {
            return when (month) {
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
        }
    }

    fun setThis(year: Int, month: Int, day: Int): FDateTime {
        this._dateData = dateToTicks(year, month, day)
        return this
    }
    fun setThis(data: Long): FDateTime {
        val timeZoneOffset = TimeZone.getDefault().rawOffset / 60 / 60 / 1000
        _dateData = (data * _ticksPerMillisecond).toBigInteger() + dateToTicks(1970, 1, 1) + (timeZoneOffset.absoluteValue * _ticksPerHour).toBigInteger()
        return this
    }
    fun setThis(data: BigInteger): FDateTime {
        _dateData = data
        return this
    }
    fun setThis(data: String?, localize: FLocalize = FLocalize.KOREA): FDateTime {
        if (data == null) {
            return this
        }
        this._localize = localize

        this._dateData = FDateTimeParse.parse(data, this._localize)._dateData
        return this
    }
    fun setLocalize(data: FLocalize): FDateTime {
        this._localize = data
        return this
    }

    fun dateToTicks(year: Int, month: Int, day: Int): BigInteger {
        if (year < 1 || year > 9999 || month < 1 || month > 12 || day < 1) {
            throw Exception("illegal format year : $year, month : $month, day : $day")
        }

        val days = if (isLeapYear(year)) _daysToMonth366 else _daysToMonth365
        if (day > days[month] - days[month - 1]) {
            throw Exception("illegal format year : $year, month : $month, day : $day")
        }

        return BigInteger.valueOf((daysToYear(year) + days[month - 1] + day - 1) * _ticksPerDay)
    }

    //    override fun toString(): String {
//        return FDateTimeFormat.format(this, null, _localize)
//    }
    fun toString(format: String): String {
        return FDateTimeFormat.format(this, format, _localize)
    }
    fun toString(format: String, localize: FLocalize): String {
        return FDateTimeFormat.format(this, format, localize)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun toLocalDate(): LocalDate {
        return LocalDate.of(year, month, day)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun toYearMonth(): YearMonth {
        return YearMonth.of(year, month)
    }
    fun getMonthOfFirstDay(): FDateTime {
        return FDateTime().setThis(year, month, 1)
    }
    fun getMonthOfLastDay(): FDateTime {
        return FDateTime().setThis(year, month, getMaxDayOfMonth(year, month))
    }
    fun getLocalizeYear(withString: Boolean) = _localize.getYear(this, withString)
    fun getLocalizeMonth(isDDD: Boolean) = _localize.getMonth(this, isDDD)
    fun getLocalizeMonth(month: Int, isDDD: Boolean) = _localize.getMonth(month, isDDD)
    fun getLocalizeDay(isDDD: Boolean) = _localize.getDay(this, isDDD)
    fun getLocalizeDay(day: Int, isDDD: Boolean) = _localize.getDay(day, isDDD)
    fun getLocalizeDayOfWeek(isDDD: Boolean) = _localize.getDayOfWeek(this, isDDD)
    fun getLocalizeDayOfWeek(dayOfWeek: Int, isDDD: Boolean) = _localize.getDayOfWeek(dayOfWeek, isDDD)
    fun addTicks(value: Long): FDateTime {
        val ticks = internalTicks
        if (value > _maxTicks - ticks || value < _minTicks - ticks) {
            return this
        }

        return FDateTime().setThis(((ticks + value) or internalKind).toBigInteger())
    }
    fun addMonth(value: Int): FDateTime {
        if (value < -120000 || value > 120000) return this
        val yearMonthDay = getDatePart()
        var year = yearMonthDay.first
        var month = yearMonthDay.second
        var day = yearMonthDay.third

        val i = month - 1 + value
        if (i >= 0) {
            month = i % 12 + 1
            year = year + i / 12
        }
        else {
            month = 12 + (i + 1) % 12
            year = year + (i - 11) / 12
        }
        if (year < 1 || year > 9999) {
            return this
        }
        val days = daysInMonth(year, month)
        if (day > days) day = days

        return FDateTime().setThis(((dateToTicks(year, month, day).toLong() + internalTicks % _ticksPerDay) or internalKind).toBigInteger())
    }
    fun addMinutes(value: Double): FDateTime {
        return add(value, _millisPerMinute)
    }

    private fun getDatePart(part: Int): Int {
        val ticks = internalTicks
        var n = (ticks / _ticksPerDay).toInt()
        val y400: Int = n / _daysPer400Years
        n -= y400 * _daysPer400Years
        var y100: Int = n / _daysPer100Years
        if (y100 == 4) y100 = 3
        n -= y100 * _daysPer100Years
        val y4: Int = n / _daysPer4Years
        n -= y4 * _daysPer4Years
        var y1: Int = n / _daysPerYear
        if (y1 == 4) y1 = 3
        if (part == _datePartYear) {
            return y400 * 400 + y100 * 100 + y4 * 4 + y1 + 1
        }
        n -= y1 * _daysPerYear
        if (part == _datePartDayOfYear) return n + 1
        val leapYear: Boolean = y1 == 3 && (y4 != 24 || y100 == 3)
        val days = if (leapYear) _daysToMonth366 else _daysToMonth365
        var m = n shr 5 + 1
        while (n >= days[m]) m++
        return if (part == _datePartMonth) m else n - days[m - 1] + 1
    }
    fun getDatePart(): Triple<Int, Int, Int> {
        val ticks = internalTicks
        var n = ticks / _ticksPerDay
        val y400 = (n / _daysPer400Years).toInt()
        n -= y400 * _daysPer400Years
        var y100 = (n / _daysPer100Years).toInt()
        if (y100 == 4) y100 = 3
        n -= y100 * _daysPer100Years
        val y4 = (n / _daysPer4Years).toInt()
        n -= y4 * _daysPer4Years
        var y1 = (n / _daysPerYear).toInt()
        if (y1 == 4) y1 = 3
        val year = y400 * 400 + y100 * 100 + y4 * 4 + y1 + 1
        n -= y1 * _daysPerYear
        val leapYear = y1 == 3 && (y4 != 24 || y100 == 3)
        val days = if (leapYear) _daysToMonth366 else _daysToMonth365
        var m = ((n shr 5) + 1).toInt()
        while (n >= days[m]) m++
        val month = m
        val day = (n - days[m - 1] + 1).toInt()
        return Triple(year, month, day)
    }
    private fun daysToYear(year: Int): Int {
        val lastYear = year - 1
        val leapYear = 365 * 4 + 1
        val cent = lastYear / 100
        return lastYear * leapYear / 4 - cent + cent / 4
    }
    private fun isLeapYear(year: Int): Boolean {
        if (year % 400 == 0) return true
        if (year % 100 == 0) return false
        if (year % 4 == 0) return true
        return false
    }
    private fun add(value: Double, scale: Int): FDateTime {
        val millis = (value * scale + if (value >=0) 0.5 else -0.5).toLong()
        if (millis <= -_maxMillis || millis >= _maxMillis) {
            return this
        }
        return addTicks(millis * _ticksPerMillisecond)
    }
}