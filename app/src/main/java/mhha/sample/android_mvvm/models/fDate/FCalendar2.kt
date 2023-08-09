package mhha.sample.android_mvvm.models.fDate

class FCalendar2: FDateTime2() {
    private var _startDayOfWeek: DayOfTheWeek = DayOfTheWeek.SUNDAY
    private var _baseDay: DayOfTheWeek = DayOfTheWeek.WEDNESDAY

    fun setThis(data: FCalendar2): FCalendar2 {
        super.setThis(data)
        return this
    }
    override fun setThis(data: FDateTime2?): FCalendar2 {
        super.setThis(data)
        return this
    }
    override fun setThis(dateString: String, splitChar: String): FCalendar2 {
        super.setThis(dateString, splitChar)
        return this
    }
    override fun setThis(year: Int, month: Int, day: Int, splitChar: String): FCalendar2 {
        super.setThis(year, month, day, splitChar)
        return this
    }
    override fun setThis(data: Long): FCalendar2 {
        super.setThis(data)
        return this
    }
    override fun setError(msg: String?): FCalendar2 {
        super.setError(msg)
        return this
    }
    override fun setError(msg: Exception): FCalendar2 {
        super.setError(msg)
        return this
    }
    override fun setSplitChar(splitChar: String): FCalendar2 {
        super.setSplitChar(splitChar)
        return this
    }
    override fun getNextOrSameDate(dayOfTheWeek: DayOfTheWeek) = try {
        FCalendar2().setThis(super.getNextOrSameDate(dayOfTheWeek))
    } catch (e: Exception) {
        FCalendar2().setError(e)
    }
    override fun getNextOrSameDay(dayOfTheWeek: DayOfTheWeek) = try {
        FCalendar2()
            .setThis(this)
            .setDay(super.getNextOrSameDay(dayOfTheWeek).getDay())
    } catch (e: Exception) {
        FCalendar2().setError(e)
    }
    override fun getPrevOrSameDate(dayOfTheWeek: DayOfTheWeek) = try {
        FCalendar2().setThis(super.getPrevOrSameDate(dayOfTheWeek))
    } catch (e: Exception) {
        FCalendar2().setError(e)
    }
    override fun getPrevOrSameDay(dayOfTheWeek: DayOfTheWeek) = try {
        FCalendar2()
            .setThis(this)
            .setDay(super.getPrevOrSameDay(dayOfTheWeek).getDay())
    } catch (e : Exception) {
        FCalendar2().setError(e)
    }
    override fun getFirstDayOfMonth(dayOfTheWeek: DayOfTheWeek) = try {
        FCalendar2().setThis(super.getFirstDayOfMonth(dayOfTheWeek))
    } catch (e: Exception) {
        setError(e)
        this
    }

    override fun addYear(addYear: Int): FCalendar2 {
        super.addYear(addYear)
        return this
    }
    override fun addMonth(addMonth: Int): FCalendar2 {
        super.addMonth(addMonth)
        return this
    }
    override fun addWeeks(addWeek: Int): FCalendar2 {
        super.addWeeks(addWeek)
        return this
    }
    override fun addDays(addDay: Int): FCalendar2 {
        super.addDays(addDay)
        return this
    }
    override fun setYear(year: Int): FCalendar2 {
        super.setYear(year)
        return this
    }
    override fun setMonth(month: Int): FCalendar2 {
        super.setMonth(month)
        return this
    }
    override fun setDay(day: Int): FCalendar2 {
        super.setDay(day)
        return this
    }
    override fun setLastDay(): FCalendar2 {
        super.setLastDay()
        return this
    }

    /**
     * FCalendar().setStartDayOfWeek
     * - 한 주의 시작을 무슨 요일로 할 거냐는 거
     * @param dayOfTheWeek - 요일
     * @return
     */
    fun setStartDayOfWeek(dayOfTheWeek: DayOfTheWeek): FCalendar2 {
        this._startDayOfWeek = dayOfTheWeek
        return this
    }
    /**
     * FCalendar().setBaseDay()
     * - 하나의 week 를 봤을 때, 그 달의 base week를 뭘로 할 거냐는 거
     *      - ex: week에 2월 말과 3월 초가 동시에 보일 때, 해당 주를 2월로 볼건지 3월로 볼건지
     *
     * @param dayOfTheWeek
     * @return
     */
    fun setBaseDay(dayOfTheWeek: DayOfTheWeek): FCalendar2 {
        this._baseDay = dayOfTheWeek
        return this
    }

//    /**
//     * FCalendar().getWeekNumberInDate()
//     * - 해당 일이 몇 년도 몇 월 몇 주차냐
//     * @return yyyy 년 MM 월 numberOfWeek 주차
//     */
//    fun getWeekNumberInDay(): String {
//
//    }

    /**
     * FCalendar().getWeekNumberInWeek
     * - 해당 주가 몇 년도 몇 월 몇 주차냐
     * @return yyyy 년 MM 월 numberOfWeek 주차
     */
    fun getWeekNumberInWeek(): String {
        val startDate = getPrevOrSameDate(_startDayOfWeek)
        val endDate = FCalendar2().setThis(startDate).addDays(6)
        val startMonth = startDate.getMonth()
        val endMonth = endDate.getMonth()
        val month = if (endMonth == startMonth) endMonth else {
            val baseDay = startDate.getNextOrSameDate(_baseDay)
            if (baseDay.getMonth() == startMonth) startMonth else endMonth
        }
        val weekNumberOfBaseDay = getWeekNumberOfBaseDay(startDate)
        return if (weekNumberOfBaseDay == 1) {
            "${endDate.getYear()}년 ${"%02d".format(month)}월 ${weekNumberOfBaseDay}주차"
        } else {
            "${startDate.getYear()}년 ${"%02d".format(month)}월 ${weekNumberOfBaseDay}주차"
        }
    }
    fun getWeekNumberOfBaseDay(startDate: FCalendar2): Int {
        val baseDay = startDate.getNextOrSameDate(_baseDay)
        val current = baseDay.getFirstDayOfMonth(_baseDay)
        var count = 1
        while (current.isBefore(baseDay)) {
            current.addWeeks(1)
            count++
        }
        return count
    }
    fun getYearOfWeekList(): List<List<FDateTime2>> {
        val ret = mutableListOf<MutableList<FDateTime2>>()
        val buff = this.clone().setMonth(1).setDay(1)
        val year = buff.getYear()
        while (year == buff.getYear()) {
            ret.add(buff.getWeekList().toMutableList())
            buff.addWeeks(1)
        }
        return ret
    }
    /**
     * getYearOfWeekListMid
     * 오늘을 기준으로 count - n, 이번주, count + n
     * @return
     */
    fun getYearOfWeekListMid(count: Int = 1): List<List<FDateTime2>> {
        val ret = mutableListOf<MutableList<FDateTime2>>()
        val buff = this.clone()
        buff.addWeeks(-(count / 2))
        for (i in 0 until count) {
            ret.add(buff.getWeekList().toMutableList())
            buff.addWeeks(1)
        }
        return ret
    }
    fun getYearOfMonthList(): List<List<FDateTime2>> {
        val ret = mutableListOf<MutableList<FDateTime2>>()
        val buff = this.clone().setMonth(1).setDay(1)
        val year = buff.getYear()
        while (year == buff.getYear()) {
            ret.add(buff.getMonthList().toMutableList())
            buff.addMonth(1)
        }
        return ret
    }
    fun getYearOfDayList(): List<FDateTime2> {
        val ret = mutableListOf<FDateTime2>()
        val buff = this.clone().setMonth(1).setDay(1)
        val year = buff.getYear()
        while (year == buff.getYear()) {
            buff.getMonthList().forEach {
                ret.add(it)
            }
            buff.addMonth(1)
        }
        return ret
    }
    fun getYearOfCalendarMonthList(): List<List<FDateTime2>> {
        val ret = mutableListOf<MutableList<FDateTime2>>()
        val buff = this.clone().setMonth(1).setDay(1)
        val year = buff.getYear()
        while (year == buff.getYear()) {
            val month = buff.getMonth()
            val retBuff = mutableListOf<FDateTime2>()
            var weekList = buff.getWeekList()
            while (weekList.find { it.getMonth() == month } != null) {
                retBuff.addAll(weekList)
                buff.addWeeks(1)
                weekList = buff.getWeekList()
            }
            buff.setDay(1)
            ret.add(retBuff)
        }
        return ret
    }
    /**
     * getYearOfCalendarMonthListMid
     * 오늘을 기준으로 count - n, 이번달, count + n
     * @return
     */
    fun getYearOfCalendarMonthListMid(count: Int = 1): List<List<FDateTime2>> {
        val ret = mutableListOf<MutableList<FDateTime2>>()
        val buff = this.clone().setDay(1)
        buff.addMonth(-(count / 2))
        for (i in 0 until count) {
            val month = buff.getMonth()
            val retBuff = mutableListOf<FDateTime2>()
            var weekList = buff.getWeekList()
            while (weekList.find { it.getMonth() == month } != null) {
                retBuff.addAll(weekList)
                buff.addWeeks(1)
                weekList = buff.getWeekList()
            }
            buff.setDay(1)
            ret.add(retBuff)
        }
        return ret
    }

    fun getMonthList(): List<FDateTime2> {
        val ret: MutableList<FDateTime2> = arrayListOf()
        val startDate = FDateTime2().setThis(this).setDay(1)
        for (i in 1 .. startDate.getMaxDayOfMonth()) {
            ret.add(FDateTime2().setThis(startDate).setDay(i))
        }
        return ret
    }
    fun getMonthListWithDayOfWeek(): List<Pair<String, String>> {
        val ret: MutableList<Pair<String, String>> = arrayListOf()
        val startDate = FDateTime2().setThis(this).setDay(1)
        for (i in 1 .. startDate.getMaxDayOfMonth()) {
            val buff = FDateTime2().setThis(startDate).setDay(i)
            ret.add(Pair(buff.getDate(), buff.getDayOfWeek()?.dayOfKorea!!))
        }
        return ret
    }
    fun getMonthList(year: Int, month: Int, splitChar: String = "-"): List<String> {
        val ret: MutableList<String> = arrayListOf()
        val startDate = FDateTime2().setThis(year, month, 1, splitChar)
        for (i in 1 .. startDate.getMaxDayOfMonth()) {
            ret.add(FDateTime2().setThis(startDate).setDay(i).getDate())
        }
        return ret
    }
    fun getMonthListWithDayOfWeek(year: Int, month: Int, splitChar: String = "-"): List<Pair<String, String>> {
        val ret: MutableList<Pair<String, String>> = arrayListOf()
        val startDate = FDateTime2().setThis(year, month, 1, splitChar)
        for (i in 1 .. startDate.getMaxDayOfMonth()) {
            val buff = FDateTime2().setThis(startDate).setDay(i)
            ret.add(Pair(buff.getDate(), buff.getDayOfWeek()?.dayOfKorea!!))
        }
        return ret
    }
    fun getMonthList(dateString: String, splitChar: String = "-"): List<String> {
        val ret: MutableList<String> = arrayListOf()
        val startDate = FDateTime2().setThis(dateString, splitChar).setDay(1)
        for (i in 1 .. startDate.getMaxDayOfMonth()) {
            ret.add(FDateTime2().setThis(startDate).setDay(i).getDate())
        }
        return ret
    }
    fun getMonthListWithDayOfWeek(dateString: String, splitChar: String = "-"): List<Pair<String, String>> {
        val ret: MutableList<Pair<String, String>> = arrayListOf()
        val startDate = FDateTime2().setThis(dateString, splitChar).setDay(1)
        for (i in 1 .. startDate.getMaxDayOfMonth()) {
            val buff = FDateTime2().setThis(startDate).setDay(i)
            ret.add(Pair(buff.getDate(), buff.getDayOfWeek()?.dayOfKorea!!))
        }
        return ret
    }
    /**
     * FCalendar().getWeekList()
     * - 기준일을 기점으로 시작요일부터 그 주의 모든 일자를 리턴
     *      - ex: 2023년 2월 26일이 일요일이고, 시작 기준요일이 일요일이었을 때, dateString이 2월 28일 이라면 2023년 2월 26일부터 2023년 3월 4일을 리턴 함.
     *
     * @return
     */
    fun getWeekList(): List<FDateTime2> {
        val ret: MutableList<FDateTime2> = arrayListOf()
        val startDate = getPrevOrSameDate(_startDayOfWeek)
        for (i in 0 until 7) {
            ret.add(FDateTime2().setThis(startDate).addDays(i))
        }

        return ret
    }
    fun getDayOfWeekList(): List<DayOfTheWeek> {
        val ret = mutableListOf<DayOfTheWeek>()
        var lastDayOfWeek = _startDayOfWeek
        ret.add(lastDayOfWeek)
        for (i in 1 until 7) {
            lastDayOfWeek = DayOfTheWeek.next(lastDayOfWeek)
            ret.add(lastDayOfWeek)
        }
        return ret
    }
    fun getWeekListWithDayOfWeek(): List<Pair<String, String>> {
        val ret: MutableList<Pair<String, String>> = arrayListOf()
        val startDate = getPrevOrSameDate(_startDayOfWeek)
        for (i in 0 until 7) {
            val buff = FDateTime2().setThis(startDate).addDays(i)
            ret.add(Pair(buff.getDate(), buff.getDayOfWeek()?.dayOfKorea!!))
        }

        return ret
    }
    /**
     * FCalendar().getWeekList(dateString)
     * - 기준일을 기점으로 시작요일부터 그 주의 모든 일자를 리턴
     *      - ex: 2023년 2월 26일이 일요일이고, 시작 기준요일이 일요일이었을 때, dateString이 2월 28일 이라면 2023년 2월 26일부터 2023년 3월 4일을 리턴 함.
     *
     * @param dateString
     * @return
     */
    fun getWeekList(dateString: String, splitChar: String = "-"): List<String> {
        val ret: MutableList<String> = arrayListOf()
        val startDate = getPrevOrSameDate(dateString, _startDayOfWeek, splitChar)
        for (i in 0 until 7) {
            ret.add(FDateTime2().setThis(startDate, splitChar).addDays(i).getDate())
        }

        return ret
    }
    fun getWeekListWithDayOfWeek(dateString: String, splitChar: String = "-"): List<Pair<String, String>> {
        val ret: MutableList<Pair<String, String>> = arrayListOf()
        val startDate = getPrevOrSameDate(dateString, _startDayOfWeek, splitChar)
        for (i in 0 until 7) {
            val buff = FDateTime2().setThis(startDate, splitChar).addDays(i)
            ret.add(Pair(buff.getDate(), buff.getDayOfWeek()?.dayOfKorea!!))
        }

        return ret
    }

    override fun equals(other: Any?): Boolean {
        if (other !is FCalendar2) return false
        if (this._baseDay != other._baseDay) return false
        if (this._startDayOfWeek != other._startDayOfWeek) return false
        if (this.getDate() != other.getDate()) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + _startDayOfWeek.hashCode()
        result = 31 * result + _baseDay.hashCode()
        return result
    }
    fun clone(): FCalendar2 {
        return FCalendar2().setThis(this.getDate(), this.getSplitChar()).setStartDayOfWeek(this._startDayOfWeek).setBaseDay(this._baseDay)
    }
}