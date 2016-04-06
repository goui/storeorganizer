package fr.goui.storeorganizer.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import fr.goui.storeorganizer.R;
import fr.goui.storeorganizer.listener.OnBottomSheetItemClickListener;

/**
 * {@code BottomSheetRecyclerAdapter} is a simple adapter for the {@code RecyclerView}
 * used to display the bottom sheet in {@code MainActivity} after a fab press.
 */
public class BottomSheetRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /**
     * The window context.
     */
    private Context mContext;

    /**
     * The layout inflater used to inflate views.
     */
    private LayoutInflater mLayoutInflater;

    /**
     * The list of texts for bottom sheet's items.
     */
    private List<String> mItemsTexts;

    /**
     * The list of icons id for bottom sheet's items.
     */
    private List<Integer> mItemsIcons;

    /**
     * The listener to trigger when an item is clicked.
     */
    private OnBottomSheetItemClickListener mOnBottomSheetItemClickListener;

    /**
     * Constructor.
     *
     * @param context                        the window context
     * @param itemsTexts                     the list of texts for items
     * @param itemsIcons                     the list of icons id for items
     * @param onBottomSheetItemClickListener the item click listener
     */
    public BottomSheetRecyclerAdapter(Context context, List<String> itemsTexts, List<Integer> itemsIcons, OnBottomSheetItemClickListener onBottomSheetItemClickListener) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mItemsTexts = itemsTexts;
        mItemsIcons = itemsIcons;
        mOnBottomSheetItemClickListener = onBottomSheetItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(mLayoutInflater.inflate(R.layout.layout_image_text, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyViewHolder myViewHolder = (MyViewHolder) holder;
        myViewHolder.position = position;
        myViewHolder.imageView.setImageDrawable(ContextCompat.getDrawable(mContext, mItemsIcons.get(position)));
        myViewHolder.textView.setText(mItemsTexts.get(position));
    }

    @Override
    public int getItemCount() {
        return mItemsTexts.size();
    }

    /**
     * The item view holder.
     */
    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        TextView textView;
        int position;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.layout_image);
            textView = (TextView) itemView.findViewById(R.id.layout_text);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOnBottomSheetItemClickListener.onBottomSheetItemClick(position);
        }
    }
}
