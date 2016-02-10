package fr.goui.storeorganizer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.WindowManager;

public class OverallView extends View {

    private final String[] mHoursStrings;
    private final int mNumberOfRows;
    private int mNumberOfColumns;

    private final int mDefaultCellHeight;
    private static final int HOUR_GAP = 1;
    private int mTopMargin;
    private int mLeftMargin;
    private int mTextSize;
    private int mCellHeight;
    private Point mScreenSize = new Point();
    private final Paint mPaint = new Paint();

    public OverallView(Context context) {
        super(context);
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(mScreenSize);
        mHoursStrings = context.getResources().getStringArray(R.array.working_hours_string_array);
        mNumberOfRows = mHoursStrings.length - 1;
        mNumberOfColumns = StoreWorkerModel.getInstance().getStoreWorkersNumber();
        int paintColor = ContextCompat.getColor(context, R.color.black);
        mTopMargin = (int) getResources().getDimension(R.dimen.hour_top_margin);
        mLeftMargin = (int) getResources().getDimension(R.dimen.hour_left_margin);
        mTextSize = (int) getResources().getDimension(R.dimen.hour_text_size);
        mDefaultCellHeight = (int) getResources().getDimension(R.dimen.hour_cell_height);
        mCellHeight = mDefaultCellHeight;
        mPaint.setColor(paintColor);
        mPaint.setTextSize(mTextSize);
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);
    }

    private void drawBackground(Canvas canvas) {
        int initialY = mTextSize + mTopMargin;
        int initialX = 2 * mLeftMargin + mTextSize;
        int finalX = mScreenSize.x - mTextSize;
        int cellWidth = (finalX - initialX) / mNumberOfColumns;

        int y = initialY;
        for (int i = 0; i < mNumberOfRows; i++) {
            String time = mHoursStrings[i];
            canvas.drawText(time, mLeftMargin, y + mTopMargin, mPaint);
            canvas.drawLine(initialX, y, finalX, y, mPaint);
            y += mCellHeight + HOUR_GAP;
        }
        canvas.drawText(mHoursStrings[mNumberOfRows], mLeftMargin, y + mTopMargin, mPaint);
        canvas.drawLine(initialX, y, finalX, y, mPaint);

        int x = initialX;
        for (int i = 0; i < mNumberOfColumns; i++) {
            canvas.drawLine(x, initialY, x, y, mPaint);
            x += cellWidth;
        }
        canvas.drawLine(x, initialY, x, y, mPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int maxHeight = 2 * (mTextSize + mTopMargin) + mNumberOfRows * (mCellHeight + HOUR_GAP);
        setMeasuredDimension(mScreenSize.x, maxHeight);
    }

    public void onScaleChanged(float scaleFactor_p) {
        mCellHeight = (int) (mDefaultCellHeight * scaleFactor_p);
        requestLayout();
        invalidate();
    }

    public void onWorkersChanged() {
        mNumberOfColumns = StoreWorkerModel.getInstance().getStoreWorkersNumber();
        invalidate();
    }

}
