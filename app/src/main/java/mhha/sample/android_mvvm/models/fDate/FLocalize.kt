package mhha.sample.android_mvvm.models.fDate

/**
 * FLocalize
 * - .net 배끼는 중
 * @constructor Create empty F localize
 */
open class FLocalize {
    object KOREA: FLocalize()
    object KOREA_LUNA: FLocalize()
    object USA: FLocalize()

    companion object {
        fun parseThis(lang: String) = when (lang) {
            "en" -> USA
            else -> KOREA
        }
    }

    private var _fullTimeSpanPositivePattern: String? = null
    val fullTimeSpanPositivePattern: String get() {
        if (_fullTimeSpanPositivePattern == null) {
            val decimalSeparator = "."
            _fullTimeSpanPositivePattern = "d':'h':'mm':'ss'$decimalSeparator'FFFFFFF"
        }
        return _fullTimeSpanPositivePattern!!
    }
    private var _fullTimeSpanNegativePattern: String? = null
    val fullTimeSpanNegativePattern: String get() {
        if (_fullTimeSpanNegativePattern == null) {
            _fullTimeSpanNegativePattern = "'-'" + fullTimeSpanPositivePattern
        }
        return _fullTimeSpanNegativePattern!!
    }

    fun getCultureInfo(): String {
        return when (this) {
            is KOREA -> "ko-kr"
            is KOREA_LUNA -> "ko-kr-luna"
            else -> "en-us"
        }
    }
    fun getCultureName(): String {
        return when (this) {
            is KOREA -> "한국어"
            is KOREA_LUNA -> "한국어 (음력)"
            else -> "English (US)"
        }
    }
    fun getDateTimeOffset(): String {
        return when (this) {
            is KOREA, is KOREA_LUNA -> "yyyy-MM-dd hh:mm:ss (ddd)"
            else -> "MM-dd-yyyy hh:mm:ss (ddd)"
        }
    }
    fun getYear(solarDate: FDateTime, withString: Boolean): String {
        return when (this) {
            is KOREA -> if (withString) "${solarDate.year}년" else "${solarDate.year}"
            is KOREA_LUNA -> if (withString) "${cvtLunarDay(solarDate).year}년" else "${cvtLunarDay(solarDate).year}"
            else -> "${solarDate.year},"
        }
    }
    fun getYear(solarDate: FDateTime): Int {
        return when (this) {
            is KOREA_LUNA -> cvtLunarDay(solarDate).year
            else -> solarDate.year
        }
    }
    fun getMonth(solarDate: FDateTime): Int {
        return when (this) {
            is KOREA_LUNA -> cvtLunarDay(solarDate).month
            else -> solarDate.month
        }
    }
    fun getMonth(month: Int, isDDD: Boolean): String {
        return when (this) {
            is KOREA, is KOREA_LUNA -> if (isDDD) "${"%02d".format(month)}월" else "${month}월"
            else -> {
                when (month) {
                    1 -> if (isDDD) "Jan" else "January"
                    2 -> if (isDDD) "Feb" else "February"
                    3 -> if (isDDD) "Mar" else "March"
                    4 -> if (isDDD) "Apr" else "April"
                    5 -> if (isDDD) "May" else "May"
                    6 -> if (isDDD) "Jun" else "June"
                    7 -> if (isDDD) "Jul" else "July"
                    8 -> if (isDDD) "Aug" else "August"
                    9 -> if (isDDD) "Sep" else "September"
                    10 -> if (isDDD) "Oct" else "October"
                    11 -> if (isDDD) "Nov" else "November"
                    12 -> if (isDDD) "Dec" else "December"
                    else -> throw Exception("illegal data month : $month")
                }
            }
        }
    }
    fun getMonth(solarDate: FDateTime, isDDD: Boolean): String {
        return when (this) {
            is KOREA -> if (isDDD) "${"%02d".format(solarDate.month)}월" else "${solarDate.month}월"
            is KOREA_LUNA -> if (isDDD) "${"%02d".format(cvtLunarDay(solarDate).month)}월" else "${cvtLunarDay(solarDate).month}월"
            else -> {
                when (solarDate.month) {
                    1 -> if (isDDD) "Jan" else "January"
                    2 -> if (isDDD) "Feb" else "February"
                    3 -> if (isDDD) "Mar" else "March"
                    4 -> if (isDDD) "Apr" else "April"
                    5 -> if (isDDD) "May" else "May"
                    6 -> if (isDDD) "Jun" else "June"
                    7 -> if (isDDD) "Jul" else "July"
                    8 -> if (isDDD) "Aug" else "August"
                    9 -> if (isDDD) "Sep" else "September"
                    10 -> if (isDDD) "Oct" else "October"
                    11 -> if (isDDD) "Nov" else "November"
                    12 -> if (isDDD) "Dec" else "December"
                    else -> throw Exception("illegal data month : ${solarDate.month}")
                }
            }
        }
    }
    fun getDay(day: Int, isDDD: Boolean): String {
        return when (this) {
            is KOREA -> if (isDDD) "${"%02d".format(day)}일" else "${day}일"
            is KOREA_LUNA -> if (isDDD) "${"%02d".format(day)}일" else "${day}일"
            else -> if (isDDD) {
                "%02d".format(day) + when (day % 10) {
                    1 -> "st"
                    2 -> "nd"
                    3 -> "rd"
                    else -> "th"
                }
            } else "$day"
        }
    }
    fun getDay(solarDate: FDateTime, isDDD: Boolean): String {
        return when (this) {
            is KOREA -> if (isDDD) "${"%02d".format(solarDate.day)}일" else "${solarDate.day}일"
            is KOREA_LUNA -> if (isDDD) "${"%02d".format(cvtLunarDay(solarDate).day)}일" else "${cvtLunarDay(solarDate).day}일"
            else -> "${solarDate.day}"
        }
    }
    fun getDayOfMonth(solarDate: FDateTime): Int {
        return when (this) {
            is KOREA_LUNA -> cvtLunarDay(solarDate).day
            else -> solarDate.day
        }
    }
    fun getDayOfWeek(dayOfWeek: Int, isDDD: Boolean = false): String {
        val fDayOfWeek = FDayOfWeek.fromInt(dayOfWeek)
        return when (this) {
            is KOREA -> {
                when (fDayOfWeek) {
                    FDayOfWeek.SUNDAY -> if (isDDD) "일요일" else "일"
                    FDayOfWeek.MONDAY -> if (isDDD) "월요일" else "월"
                    FDayOfWeek.TUESDAY -> if (isDDD) "화요일" else "화"
                    FDayOfWeek.WEDNESDAY -> if (isDDD) "수요일" else "수"
                    FDayOfWeek.THURSDAY -> if (isDDD) "목요일" else "목"
                    FDayOfWeek.FRIDAY -> if (isDDD) "금요일" else "금"
                    FDayOfWeek.SATURDAY -> if (isDDD) "토요일" else "토"
                    else -> throw Exception("illegal data day of week : $dayOfWeek")
                }
            }
            is KOREA_LUNA -> {
                when (fDayOfWeek) {
                    FDayOfWeek.SUNDAY -> if (isDDD) "일요일" else "일"
                    FDayOfWeek.MONDAY -> if (isDDD) "월요일" else "월"
                    FDayOfWeek.TUESDAY -> if (isDDD) "화요일" else "화"
                    FDayOfWeek.WEDNESDAY -> if (isDDD) "수요일" else "수"
                    FDayOfWeek.THURSDAY -> if (isDDD) "목요일" else "목"
                    FDayOfWeek.FRIDAY -> if (isDDD) "금요일" else "금"
                    FDayOfWeek.SATURDAY -> if (isDDD) "토요일" else "토"
                    else -> throw Exception("illegal data day of week : $fDayOfWeek")
                }
            }
            else -> when (fDayOfWeek) {
                FDayOfWeek.SUNDAY -> if (isDDD) "Sunday" else "Sun"
                FDayOfWeek.MONDAY -> if (isDDD) "Monday" else "Mon"
                FDayOfWeek.TUESDAY -> if (isDDD) "Tuesday" else "Tues"
                FDayOfWeek.WEDNESDAY -> if (isDDD) "Wednesday" else "Wed"
                FDayOfWeek.THURSDAY -> if (isDDD) "Thursday" else "Thur"
                FDayOfWeek.FRIDAY -> if (isDDD) "Friday" else "Fri"
                FDayOfWeek.SATURDAY -> if (isDDD) "Saturday" else "Sat"
                else -> throw Exception("illegal data day of week : $dayOfWeek")
            }
        }
    }
    fun getDayOfWeek(fDayOfWeek: FDayOfWeek, isDDD: Boolean = false): String {
        return when (this) {
            is KOREA, is KOREA_LUNA -> {
                when (fDayOfWeek) {
                    FDayOfWeek.SUNDAY -> if (isDDD) "일요일" else "일"
                    FDayOfWeek.MONDAY -> if (isDDD) "월요일" else "월"
                    FDayOfWeek.TUESDAY -> if (isDDD) "화요일" else "화"
                    FDayOfWeek.WEDNESDAY -> if (isDDD) "수요일" else "수"
                    FDayOfWeek.THURSDAY -> if (isDDD) "목요일" else "목"
                    FDayOfWeek.FRIDAY -> if (isDDD) "금요일" else "금"
                    FDayOfWeek.SATURDAY -> if (isDDD) "토요일" else "토"
                    else -> throw Exception("illegal data day of week : $fDayOfWeek")
                }
            }
            else -> when (fDayOfWeek) {
                FDayOfWeek.SUNDAY -> if (isDDD) "Sunday" else "Sun"
                FDayOfWeek.MONDAY -> if (isDDD) "Monday" else "Mon"
                FDayOfWeek.TUESDAY -> if (isDDD) "Tuesday" else "Tues"
                FDayOfWeek.WEDNESDAY -> if (isDDD) "Wednesday" else "Wed"
                FDayOfWeek.THURSDAY -> if (isDDD) "Thursday" else "Thur"
                FDayOfWeek.FRIDAY -> if (isDDD) "Friday" else "Fri"
                FDayOfWeek.SATURDAY -> if (isDDD) "Saturday" else "Sat"
                else -> throw Exception("illegal data day of week : $fDayOfWeek")
            }
        }
    }
    fun getDayOfWeek(solarDate: FDateTime, isDDD: Boolean = false): String {
        return when (this) {
            is KOREA -> {
                when (solarDate.dayOfWeek) {
                    FDayOfWeek.SUNDAY -> if (isDDD) "일요일" else "일"
                    FDayOfWeek.MONDAY -> if (isDDD) "월요일" else "월"
                    FDayOfWeek.TUESDAY -> if (isDDD) "화요일" else "화"
                    FDayOfWeek.WEDNESDAY -> if (isDDD) "수요일" else "수"
                    FDayOfWeek.THURSDAY -> if (isDDD) "목요일" else "목"
                    FDayOfWeek.FRIDAY -> if (isDDD) "금요일" else "금"
                    FDayOfWeek.SATURDAY -> if (isDDD) "토요일" else "토"
                    else -> throw Exception("illegal data day of week : ${solarDate.dayOfWeek}")
                }
            }
            is KOREA_LUNA -> {
                val fDayOfWeek = cvtLunarDay(solarDate).dayOfWeek
                when (fDayOfWeek) {
                    FDayOfWeek.SUNDAY -> if (isDDD) "일요일" else "일"
                    FDayOfWeek.MONDAY -> if (isDDD) "월요일" else "월"
                    FDayOfWeek.TUESDAY -> if (isDDD) "화요일" else "화"
                    FDayOfWeek.WEDNESDAY -> if (isDDD) "수요일" else "수"
                    FDayOfWeek.THURSDAY -> if (isDDD) "목요일" else "목"
                    FDayOfWeek.FRIDAY -> if (isDDD) "금요일" else "금"
                    FDayOfWeek.SATURDAY -> if (isDDD) "토요일" else "토"
                    else -> throw Exception("illegal data day of week : $fDayOfWeek")
                }
            }
            else -> when (solarDate.dayOfWeek) {
                FDayOfWeek.SUNDAY -> if (isDDD) "Sunday" else "Sun"
                FDayOfWeek.MONDAY -> if (isDDD) "Monday" else "Mon"
                FDayOfWeek.TUESDAY -> if (isDDD) "Tuesday" else "Tues"
                FDayOfWeek.WEDNESDAY -> if (isDDD) "Wednesday" else "Wed"
                FDayOfWeek.THURSDAY -> if (isDDD) "Thursday" else "Thur"
                FDayOfWeek.FRIDAY -> if (isDDD) "Friday" else "Fri"
                FDayOfWeek.SATURDAY -> if (isDDD) "Saturday" else "Sat"
                else -> throw Exception("illegal data day of week : ${solarDate.dayOfWeek}")
            }
        }
    }
    fun getAM(): String {
        return when (this) {
            is KOREA, is KOREA_LUNA -> "오전"
            else -> "AM"
        }
    }
    fun getPM(): String {
        return when (this) {
            is KOREA, is KOREA_LUNA -> "오후"
            else -> "PM"
        }
    }
    private fun getMaxCalendarYear(): Int {
        return when(this) {
            is KOREA, is KOREA_LUNA -> FKoreanLunaModel.maxLunaYear
            else -> FKoreanLunaModel.maxGregorianYear
        }
    }
    private fun cvtLunarDay(solarDate: FDateTime): FDateTime {
        val isLeap = isLeapYear(solarDate.year)
        val solarDay = if (isLeap) FDateTime._daysToMonth366[solarDate.month - 1] + solarDate.day else FDateTime._daysToMonth365[solarDate.month - 1] + solarDate.day
        var jan1Month = 0
        var jan1Date = 0
        var lunarDay = solarDay
        var lunarYear = solarDate.year
        if (lunarYear == getMaxCalendarYear() + 1) {
            lunarYear--
            lunarDay += if (isLeapYear(lunarYear)) 366 else 365
            jan1Month = FKoreanLunaModel.getYearInfo(lunarYear, FKoreanLunaModel.jan1Month)
            jan1Date = FKoreanLunaModel.getYearInfo(lunarYear, FKoreanLunaModel.jan1Date)
        } else {
            jan1Month = FKoreanLunaModel.getYearInfo(lunarYear, FKoreanLunaModel.jan1Month)
            jan1Date = FKoreanLunaModel.getYearInfo(lunarYear, FKoreanLunaModel.jan1Date)
            if (solarDate.month < jan1Month || (solarDate.month == jan1Month && solarDate.day < jan1Date)) {
                lunarYear--
                lunarDay += if (isLeapYear(lunarYear)) 366 else 365
                jan1Month = FKoreanLunaModel.getYearInfo(lunarYear, FKoreanLunaModel.jan1Month)
                jan1Date = FKoreanLunaModel.getYearInfo(lunarYear, FKoreanLunaModel.jan1Date)
            }
        }

        lunarDay -= FDateTime._daysToMonth365[jan1Month - 1]
        lunarDay -= (jan1Date - 1)

        var mask = 0x8000
        val yearInfo = FKoreanLunaModel.getYearInfo(lunarYear, FKoreanLunaModel.daysPerMonth)
        var days = if ((yearInfo and mask) != 0) 30 else 29
        var lunarMonth = 1
        while (lunarDay > days) {
            lunarDay -= days
            lunarMonth++
            mask = mask shr 1
            days = if ((yearInfo and mask) != 0) 30 else 29
        }

        return FDateTime().setThis(lunarYear, lunarMonth, lunarDay)
    }
    private fun isLeapYear(year: Int): Boolean {
        if (year % 400 == 0) return true
        if (year % 100 == 0) return false
        if (year % 4 == 0) return true
        return false
    }
}