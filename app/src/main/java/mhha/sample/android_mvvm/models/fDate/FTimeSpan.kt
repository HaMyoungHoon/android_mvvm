package mhha.sample.android_mvvm.models.fDate

/**
 * FTimeSpan
 * - .net 배끼는 중
 * @constructor Create empty F time span
 */
open class FTimeSpan {
    companion object {
        const val ticksPerMillisecond = 10000
        const val ticksPerSecond = ticksPerMillisecond * 1000   // 10,000,000
        const val ticksPerMinute = ticksPerSecond * 60         // 600,000,000
        const val ticksPerHour = ticksPerMinute.toLong() * 60        // 36,000,000,000
        const val ticksPerDay = ticksPerHour * 24          // 864,000,000,000

        private const val _millisecondsPerTick = 1.0 / ticksPerMillisecond
        private const val _secondsPerTick =  1.0 / ticksPerSecond         // 0.0001
        private const val _minutesPerTick = 1.0 / ticksPerMinute // 1.6666666666667e-9
        private const val _hoursPerTick = 1.0 / ticksPerHour // 2.77777777777777778e-11
        private const val _daysPerTick = 1.0 / ticksPerDay // 1.1574074074074074074e-12
        private const val _millisPerSecond = 1000
        private const val _millisPerMinute = _millisPerSecond * 60 //     60,000
        private const val _millisPerHour = _millisPerMinute * 60   //  3,600,000
        private const val _millisPerDay = _millisPerHour * 24      // 86,400,000

        internal const val maxSeconds = Long.MAX_VALUE / ticksPerSecond
        internal const val minSeconds = Long.MIN_VALUE / ticksPerSecond
        internal const val maxMilliSeconds = Long.MAX_VALUE / ticksPerMillisecond
        internal const val minMilliSeconds = Long.MIN_VALUE / ticksPerMillisecond
        internal const val ticksPerTenthSecond = ticksPerMillisecond * 100

        val Zero = FTimeSpan(0L)
        val MaxValue = FTimeSpan(Long.MAX_VALUE)
        val MINValue = FTimeSpan(Long.MIN_VALUE)

        fun equals(timeSpan1: FTimeSpan, timeSpan2: FTimeSpan) = timeSpan1.ticks == timeSpan2.ticks

        fun fromTicks(value: Long) = FTimeSpan(value)
        fun fromDays(value: Double) = interval(value, _millisPerDay)
        fun fromHours(value: Double) = interval(value, _millisPerHour)
        fun fromMinutes(value: Double) = interval(value, _millisPerMinute)
        fun fromSeconds(value: Double) = interval(value, _millisPerSecond)
        fun fromMilliseconds(value: Double) = interval(value, 1)

        private fun interval(value: Double, scale: Int): FTimeSpan {
            if (value.isNaN()) {
                throw Exception("illegal value cant be nan")
            }

            val temp = value * scale
            val millis = temp + if (value >= 0) 0.5 else -0.5
            if (millis > Long.MAX_VALUE / ticksPerMillisecond) {
                throw Exception("illegal value overflow value : $value, scale : $scale")
            }
            if (millis < Long.MIN_VALUE / ticksPerMillisecond) {
                throw Exception("illegal value underflow value : $value, scale : $scale")
            }

            return FTimeSpan(millis.toLong() * ticksPerMillisecond)
        }
    }

    internal var _ticks: Long
    val ticks get() = _ticks
    val days: Int get() {
        return (_ticks / ticksPerDay).toInt()
    }
    val hours: Int get() {
        return ((_ticks / ticksPerHour) % 24).toInt()
    }
    val minutes: Int get() {
        return ((_ticks / ticksPerMinute) % 60).toInt()
    }
    val seconds: Int get() {
        return ((_ticks / ticksPerSecond) % 60).toInt()
    }
    val milliseconds: Int get() {
        return ((_ticks / ticksPerMillisecond) % 1000).toInt()
    }
    val totalDays: Double get() {
        return (_ticks * _daysPerTick)
    }
    val totalHours: Double get() {
        return (_ticks * _hoursPerTick)
    }
    val totalMinutes: Double get() {
        return (_ticks * _minutesPerTick)
    }
    val totalSeconds: Double get() {
        return (_ticks * _secondsPerTick)
    }
    val totalMilliseconds: Double get() {
        val buff = _ticks * _millisecondsPerTick
        if (buff > maxMilliSeconds) {
            return maxMilliSeconds.toDouble()
        }
        if (buff < minMilliSeconds) {
            return minMilliSeconds.toDouble()
        }

        return buff
    }

    constructor(ticks: Long) {
        _ticks = ticks
    }
    constructor(hours: Int, minutes: Int, seconds: Int) {
        _ticks = timeToTicks(hours, minutes, seconds)
    }
    constructor(days: Int, hours: Int, minutes: Int, seconds: Int) {
        _ticks = timeToTicks(days, hours, minutes, seconds, 0)
    }
    constructor(days: Int, hours: Int, minutes: Int, seconds: Int, milliseconds: Int) {
        _ticks = timeToTicks(days, hours, minutes, seconds, milliseconds)
    }

    fun negate(): FTimeSpan {
        if (ticks == MINValue.ticks) {
            throw Exception("illegal underflow negate; twos complement number is invalid.")
        }
        return FTimeSpan(-ticks)
    }
    fun add(timeSpan: FTimeSpan): FTimeSpan {
        val ret = ticks + timeSpan.ticks
        if ((ticks shr 63 == timeSpan.ticks shr 63) && (ticks shr 63 != ret shr 63)) {
            throw Exception("illegal overflow this ticks : $ticks, add ticks : ${timeSpan.ticks}")
        }
        return FTimeSpan(ret)
    }
    fun subtract(timeSpan: FTimeSpan): FTimeSpan {
        val ret = ticks - timeSpan.ticks
        if ((ticks shr 63 != timeSpan.ticks) && (ticks shr 63 != ret shr 63)) {
            throw Exception("illegal overflow this ticks : $ticks, subtract ticks : ${timeSpan.ticks}")
        }
        return FTimeSpan(ret)
    }
    fun duration(): FTimeSpan {
        if (ticks == MINValue.ticks) {
            throw Exception("illegal underflow ticks is MinValue")
        }
        return FTimeSpan(if (ticks >= 0) ticks else -ticks)
    }
    fun timeToTicks(hours: Int, minutes: Int, seconds: Int): Long {
        val totalSeconds = hours * 3600 + minutes * 60 + seconds
        if (totalSeconds > maxSeconds) {
            throw Exception("illegal overflow data hours: $hours, minutes: $minutes, seconds: $seconds")
        }
        if (totalSeconds < minSeconds) {
            throw Exception("illegal underflow data hours: $hours, minutes: $minutes, seconds: $seconds")
        }
        return (totalSeconds * ticksPerSecond).toLong()
    }
    fun timeToTicks(days: Int, hours: Int, minutes: Int, seconds: Int, milliseconds: Int): Long {
        val totalMilliSeconds: Long = (days * 3600 * 24 + hours * 3600 + minutes * 60 + seconds).toLong() * 1000 + milliseconds
        if (totalMilliSeconds > maxMilliSeconds) {
            throw Exception("illegal overflow data days : $days, hours: $hours, minutes: $minutes, seconds: $seconds, milliseconds: $milliseconds")
        }
        if (totalMilliSeconds < minMilliSeconds) {
            throw Exception("illegal underflow data days : $days, hours: $hours, minutes: $minutes, seconds: $seconds, milliseconds: $milliseconds")
        }
        return totalMilliSeconds * ticksPerMillisecond
    }

    operator fun unaryMinus(): FTimeSpan {
        if (ticks == MINValue.ticks) {
            throw Exception("illegal underflow rhs is minvalue")
        }
        return negate()
    }
    operator fun plus(rhs: FTimeSpan) = add(rhs)
    operator fun minus(rhs: FTimeSpan) = subtract(rhs)
    operator fun compareTo(rhs : FTimeSpan): Int {
        if (ticks > rhs.ticks) return 1
        if (ticks < rhs.ticks) return -1
        return 0
    }
    override fun hashCode(): Int {
        return ticks.hashCode()
    }
    override fun equals(other: Any?): Boolean {
        if (other !is FTimeSpan) return false

        return ticks == other.ticks
    }
    fun equals(rhs: FTimeSpan) = ticks == rhs.ticks
}