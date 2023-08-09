package mhha.sample.android_mvvm

import mhha.sample.android_mvvm.interfaces.command.ICommand

data class MainMenuListModel(
    var index: Int = 0,
    var icon: Int = 0,
    var name: String = "",
) {
    var relayCommand: ICommand? = null
    fun onClick(eventName: ClickEvent) {
        relayCommand?.execute(arrayListOf(eventName, this))
    }
    enum class ClickEvent {
        SELECT
    }
}