package mhha.sample.android_mvvm.interfaces

interface IItemTouchHelperListener {
    fun onItemMove(fromPosition: Int, toPosition: Int): Boolean
    fun onItemDrop(fromPosition: Int, toPosition: Int)
    fun onItemDismiss(position: Int)
}