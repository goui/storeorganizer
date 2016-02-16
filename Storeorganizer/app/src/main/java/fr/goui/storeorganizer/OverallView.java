package fr.goui.storeorganizer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.WindowManager;

import java.util.Calendar;

public class OverallView extends View {

    private static final int HOUR_MIN = 8;
    private static final int HOUR_MAX = 20;
    private static final int TIMER_PERIOD = 1000 * 60;

    private final int mNumberOfRows;
    private final int mDefaultCellHeight;
    private final String[] mHoursStrings;
    private final Paint mBlackPaint = new Paint();
    private final Paint mGreyPaint = new Paint();
    private final Paint mAppointmentPaint = new Paint();
    private final Paint mNowPaint = new Paint();
    private final int mTextSize;
    private final int mTopMargin;
    private final int mLeftMargin;
    private final int mInitialX;
    private final int mInitialY;
    private final int mFinalX;

    private int mNumberOfColumns;
    private int mCellWidth;
    private float mScaleFactor = 1.0f;
    private int mCellHeight;
    private int mNowLineY;
    private Point mScreenSize = new Point();
    private StoreWorkerModel mStoreWorkerModel = StoreWorkerModel.getInstance();

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
        int accentColor = ContextCompat.getColor(context, R.color.colorAccent);
        mTopMargin = (int) resources.getDimension(R.dimen.hour_top_margin);
        mLeftMargin = (int) resources.getDimension(R.dimen.hour_left_margin);
        mTextSize = (int) resources.getDimension(R.dimen.hour_text_size);
        mDefaultCellHeight = (int) resources.getDimension(R.dimen.hour_cell_height);
        mCellHeight = mDefaultCellHeight;
        mInitialX = 2 * mLeftMargin + mTextSize;
        mInitialY = mTextSize + mTopMargin;
        mFinalX = mScreenSize.x - mTextSize;
        mCellWidth = (mFinalX - mInitialX) / mNumberOfColumns;

        mBlackPaint.setColor(blackColor);
        mBlackPaint.setTextSize(mTextSize);
        mBlackPaint.setAntiAlias(true);
        mGreyPaint.setColor(greyColor);
        mGreyPaint.setTextSize(mTextSize);
        mGreyPaint.setAntiAlias(true);
        mAppointmentPaint.setColor(mainColor);
        mAppointmentPaint.setAntiAlias(true);
        mNowPaint.setColor(accentColor);
        mNowPaint.setAntiAlias(true);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mNowLineY != 0) {
                    invalidate();
                    handler.postDelayed(this, TIMER_PERIOD);
                }
            }
        }, TIMER_PERIOD);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);
        drawAppointments(canvas);
        drawNowLine(canvas);
    }

    private void drawBackground(Canvas canvas) {
        mCellWidth = (mFinalX - mInitialX) / mNumberOfColumns;
        int y = mInitialY;
        canvas.drawText(mHoursStrings[0], mLeftMargin, y + mTopMargin, mBlackPaint);
        canvas.drawLine(mInitialX, y, mFinalX, y, mBlackPaint);
        y += mCellHeight;
        for (int i = 1; i < mNumberOfRows; i++) {
            String time = mHoursStrings[i];
            canvas.drawText(time, mLeftMargin, y + mTopMargin, mBlackPaint);
            canvas.drawLine(mInitialX, y, mFinalX, y, mGreyPaint);
            y += mCellHeight;
        }
        canvas.drawText(mHoursStrings[mNumberOfRows], mLeftMargin, y + mTopMargin, mBlackPaint);
        canvas.drawLine(mInitialX, y, mFinalX, y, mBlackPaint);

        int x = mInitialX;
        for (int i = 0; i < mNumberOfColumns; i++) {
            canvas.drawLine(x, mInitialY, x, y, mBlackPaint);
            x += mCellWidth;
        }
        canvas.drawLine(mFinalX, mInitialY, mFinalX, y, mBlackPaint);
    }

    private void drawAppointments(Canvas canvas) {
        int x1 = mInitialX + 1;
        for (int i = 0; i < mNumberOfColumns; i++) {
            int x2 = x1 + mCellWidth - 2;
            if (i == mNumberOfColumns - 1) {
                x2 = mFinalX - 1;
            }
            for (StoreAppointment currentAppointment : mStoreWorkerModel.getStoreWorker(i).getStoreAppointments()) {
                if (!(currentAppointment instanceof StoreAppointment.NullStoreAppointment)) {
                    int startHour = currentAppointment.getStartTime().get(Calendar.HOUR_OF_DAY);
                    int startMinute = currentAppointment.getStartTime().get(Calendar.MINUTE);
                    int endHour = currentAppointment.getEndTime().get(Calendar.HOUR_OF_DAY);
                    int endMinute = currentAppointment.getEndTime().get(Calendar.MINUTE);
                    int y1 = mInitialY + mCellHeight * (startHour - HOUR_MIN) + (mCellHeight * startMinute) / 60;
                    int y2 = mInitialY + mCellHeight * (endHour - HOUR_MIN) + (mCellHeight * endMinute) / 60;
                    if (currentAppointment.getEndTime().after(Calendar.getInstance())) {
                        canvas.drawRect(x1, y1, x2, y2, mAppointmentPaint);
                    } else {
                        canvas.drawRect(x1, y1, x2, y2, mGreyPaint);
                    }
                    canvas.drawLine(x1, y1, x2, y1, mBlackPaint);
                    canvas.drawLine(x1, y2, x2, y2, mBlackPaint);
                }
            }
            x1 += mCellWidth;
        }
    }

    private void drawNowLine(Canvas canvas) {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minute = Calendar.getInstance().get(Calendar.MINUTE);
        mNowLineY = mInitialY + mCellHeight * (hour - HOUR_MIN) + (mCellHeight * minute) / 60;
        canvas.drawLine(mInitialX, mNowLineY, mFinalX, mNowLineY, mNowPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int maxHeight = 2 * (mTextSize + mTopMargin) + mNumberOfRows * mCellHeight;
        setMeasuredDimension(mScreenSize.x, maxHeight);
    }

    public void onScaleChanged(float scaleFactor_p) {
        if (scaleFactor_p != mScaleFactor) {
            mScaleFactor = scaleFactor_p;
            mCellHeight = (int) (mDefaultCellHeight * scaleFactor_p);
            requestLayout();
            invalidate();
        }
    }

    public void onWorkersChanged() {
        mNumberOfColumns = mStoreWorkerModel.getStoreWorkersNumber();
        invalidate();
    }

}
