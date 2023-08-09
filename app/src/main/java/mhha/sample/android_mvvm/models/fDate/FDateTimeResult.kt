package mhha.sample.android_mvvm.models.fDate

import java.util.EnumSet

/**
 * FDateTimeResult
 * - .net 배끼는 중
 * @property year
 * @property month
 * @property day
 * @property hour
 * @property minute
 * @property second
 * @property fraction
 * @property era
 * @property flags
 * @constructor Create empty F date time result
 */
data class FDateTimeResult(
    internal var year: Int = 9999,
    internal var month: Int = 12,
    internal var day: Int = 31,
    internal var hour: Int = 0,
    internal var minute: Int = 0,
    internal var second: Int = 0,
    internal var fraction: Double = 0.0,
    internal var era: Int = 0,
    internal var flags: ParseFlags = EnumSet.of(ParseFlag.HaveDate),
) {

    enum class ParseFlag(val flag: Int) {
        HaveYear(0x00000001),
        HaveMonth(0x00000002),
        HaveDay(0x00000004),
        HaveHour(0x00000008),
        HaveMinute(0x00000010),
        HaveSecond(0x00000020),
        HaveTime(0x00000040),
        HaveDate(0x00000080),
        TimeZoneUsed(0x00000100),
        TimeZoneUtc(0x00000200),
        ParsedMonthName(0x00000400),
        CaptureOffset(0x00000800),
        YearDefault(0x00001000),
        Rfc1123Pattern(0x00002000),
        UtcSortPattern(0x00004000);

        infix fun and(rhs: ParseFlag) = EnumSet.of(this, rhs)
    }
    infix fun ParseFlags.allOf(rhs: ParseFlags) = this.containsAll(rhs)
    infix fun ParseFlags.and(rhs: ParseFlag) = EnumSet.of(rhs, *this.toTypedArray())
}

typealias ParseFlags = EnumSet<FDateTimeResult.ParseFlag>