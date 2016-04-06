package fr.goui.storeorganizer.listener;

/**
 * {@code OnBottomSheetItemClickListener} is an interface used to trigger bottom sheet item click events.
 */
public interface OnBottomSheetItemClickListener {

    /**
     * Callback used when an item has been clicked.
     *
     * @param position the item position
     */
    void onBottomSheetItemClick(int position);
}
