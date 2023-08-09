package mhha.sample.android_mvvm.models.fDate

/**
 * FDateTimeParse
 *
 * 아직 못 함.
 * @constructor Create empty F date time parse
 */
object FDateTimeParse {
    // 일단 yyyy-MM-dd 한정
    fun parse(data: String, localize: FLocalize): FDateTime {
        val fDateTime2 = FDateTime2().setThis(data)
        return FDateTime().setThis(fDateTime2.getYear(), fDateTime2.getMonth(), fDateTime2.getDay())
    }
}