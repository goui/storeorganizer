package fr.goui.storeorganizer;

/**
 * {@code OnCategoryClickListener} is an interface used to trigger click on a category events.
 */
public interface OnCategoryClickListener {

    /**
     * Callback used when a category has been clicked.
     *
     * @param position_p the position of the clicked category
     */
    void onCategoryClicked(int position_p);

}
