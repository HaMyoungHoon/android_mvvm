package mhha.sample.android_mvvm.bases

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import mhha.sample.android_mvvm.interfaces.IItemTouchHelperListener

class FItemTouchHelperCallback(private val adapter: IItemTouchHelperListener): ItemTouchHelper.Callback() {
    var fromPosition = -1
    var toPosition = -1
    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = 0 // ItemTouchHelper.START or ItemTouchHelper.END
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        if (fromPosition == -1) {
            fromPosition = viewHolder.bindingAdapterPosition
        }
        toPosition = target.bindingAdapterPosition
        adapter.onItemMove(viewHolder.bindingAdapterPosition, target.bindingAdapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//        adapter.onItemDismiss(viewHolder.bindingAdapterPosition)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        if (fromPosition != -1 && toPosition != -1) {
            adapter.onItemDrop(fromPosition, toPosition)
            fromPosition = -1
            toPosition = -1
        }
    }
}