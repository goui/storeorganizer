package fr.goui.storeorganizer.listener;

/**
 * {@code OnCategoryClickListener} is an interface used to trigger click on a category events.
 */
public interface OnCategoryClickListener {

    /**
     * Callback used when a category has been clicked.
     *
     * @param position the position of the clicked category
     */
    void onCategoryClicked(int position);

}
