package fr.goui.storeorganizer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.WindowManager;

import java.util.Calendar;

public class OverallView extends View {

    private static final int HOUR_GAP = 1;
    private static final int HOUR_MIN = 8;
    private static final int HOUR_MAX = 20;

    private final int mNumberOfRows;
    private final int mDefaultCellHeight;
    private final String[] mHoursStrings;
    private final Paint mBlackPaint = new Paint();
    private final Paint mGreyPaint = new Paint();
    private final Paint mAppointmentPaint = new Paint();
    private final int mTextSize;
    private final int mTopMargin;
    private final int mLeftMargin;
    private final int mInitialX;
    private final int mInitialY;
    private final int mFinalX;

    private int mNumberOfColumns;
    private int mFinalY;
    private float mScaleFactor;
    private int mCellHeight;
    private Point mScreenSize = new Point();
    private StoreWorkerModel mStoreWorkerModel = StoreWorkerModel.getInstance();
    private Calendar mNow = Calendar.getInstance();

    public OverallView(Context context) {
        super(context);
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(mScreenSize);
        Resources resources = context.getResources();
        mHoursStrings = resources.getStringArray(R.array.working_hours_string_array);
        mNumberOfRows = mHoursStrings.length - 1;
        mNumberOfColumns = mStoreWorkerModel.getStoreWorkersNumber();
        int blackColor = ContextCompat.getColor(context, R.color.black);
        int greyColor = ContextCompat.getColor(context, R.color.grey_overlay);
        int mainColor = ContextCompat.getColor(context, R.color.colorPrimary);
        mTopMargin = (int) resources.getDimension(R.dimen.hour_top_margin);
        mLeftMargin = (int) resources.getDimension(R.dimen.hour_left_margin);
        mTextSize = (int) resources.getDimension(R.dimen.hour_text_size);
        mDefaultCellHeight = (int) resources.getDimension(R.dimen.hour_cell_height);
        mCellHeight = mDefaultCellHeight;
        mInitialX = 2 * mLeftMargin + mTextSize;
        mInitialY = mTextSize + mTopMargin;
        mFinalX = mScreenSize.x - mTextSize;

        mBlackPaint.setColor(blackColor);
        mBlackPaint.setTextSize(mTextSize);
        mBlackPaint.setAntiAlias(true);
        mGreyPaint.setColor(greyColor);
        mGreyPaint.setTextSize(mTextSize);
        mGreyPaint.setAntiAlias(true);
        mAppointmentPaint.setColor(mainColor);
        mAppointmentPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);
        drawAppointments(canvas);
    }

    private void drawBackground(Canvas canvas) {
        int cellWidth = (mFinalX - mInitialX) / mNumberOfColumns;

        mFinalY = mInitialY;
        canvas.drawText(mHoursStrings[0], mLeftMargin, mFinalY + mTopMargin, mBlackPaint);
        canvas.drawLine(mInitialX, mFinalY, mFinalX, mFinalY, mBlackPaint);
        mFinalY += mCellHeight + HOUR_GAP;
        for (int i = 1; i < mNumberOfRows; i++) {
            String time = mHoursStrings[i];
            canvas.drawText(time, mLeftMargin, mFinalY + mTopMargin, mBlackPaint);
            canvas.drawLine(mInitialX, mFinalY, mFinalX, mFinalY, mGreyPaint);
            mFinalY += mCellHeight + HOUR_GAP;
        }
        canvas.drawText(mHoursStrings[mNumberOfRows], mLeftMargin, mFinalY + mTopMargin, mBlackPaint);
        canvas.drawLine(mInitialX, mFinalY, mFinalX, mFinalY, mBlackPaint);

        int x = mInitialX;
        for (int i = 0; i < mNumberOfColumns; i++) {
            canvas.drawLine(x, mInitialY, x, mFinalY, mBlackPaint);
            x += cellWidth;
        }
        canvas.drawLine(mFinalX, mInitialY, mFinalX, mFinalY, mBlackPaint);
    }

    private void drawAppointments(Canvas canvas) {
        int cellWidth = (mFinalX - mInitialX) / mNumberOfColumns;
        int x1 = mInitialX + 1;
        for (int i = 0; i < mNumberOfColumns; i++) {
            int x2 = x1 + cellWidth - 1;
            if (i == mNumberOfColumns - 1) {
                x2 = mFinalX - 1;
            }
            for (StoreAppointment currentAppointment : mStoreWorkerModel.getStoreWorker(i).getStoreAppointments()) {
                int startHour = currentAppointment.getStartTime().get(Calendar.HOUR_OF_DAY);
                int startMinute = currentAppointment.getStartTime().get(Calendar.MINUTE);
                int endHour = currentAppointment.getEndTime().get(Calendar.HOUR_OF_DAY);
                int endMinute = currentAppointment.getEndTime().get(Calendar.MINUTE);
                int y1 = (int) (mInitialY + mCellHeight * (startHour - HOUR_MIN) + startMinute * mScaleFactor);
                int y2 = (int) (mInitialY + mCellHeight * (endHour - HOUR_MIN) + endMinute * mScaleFactor);
                canvas.drawRect(x1, y1, x2, y2, mAppointmentPaint);
            }
            x1 += cellWidth + 1;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int maxHeight = 2 * (mTextSize + mTopMargin) + mNumberOfRows * (mCellHeight + HOUR_GAP);
        setMeasuredDimension(mScreenSize.x, maxHeight);
    }

    public void onScaleChanged(float scaleFactor_p) {
        mScaleFactor = scaleFactor_p;
        mCellHeight = (int) (mDefaultCellHeight * scaleFactor_p);
        requestLayout();
        invalidate();
    }

    public void onWorkersChanged() {
        mNumberOfColumns = mStoreWorkerModel.getStoreWorkersNumber();
        invalidate();
    }

}
