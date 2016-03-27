package fr.goui.storeorganizer.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import fr.goui.storeorganizer.R;
import fr.goui.storeorganizer.listener.OnAppointmentClickListener;
import fr.goui.storeorganizer.model.NullStoreAppointment;
import fr.goui.storeorganizer.model.StoreAppointment;
import fr.goui.storeorganizer.model.StoreModel;
import fr.goui.storeorganizer.model.StoreWorkerModel;

/**
 * {@code OverallView} is a custom {@code View} used to display all the workers and all their appointments.
 * It displays a grid similar to a calendar.
 * Users can scroll, tap on appointments to see the details and zoom in or out.
 */
public class OverallView extends View {

    /**
     * The number of pixels to detecting a scroll action.
     */
    private static final int SCROLL_DETECTION_OFFSET_VALUE = 20;

    /**
     * The painter used for the text and the outer grid.
     */
    private final Paint mBlackPaint = new Paint();

    /**
     * The painter used for the past appointments and the inner grid.
     */
    private final Paint mGreyPaint = new Paint();

    /**
     * The painter used for current and future appointments.
     */
    private final Paint mAppointmentPaint = new Paint();

    /**
     * The painter used for the now line.
     */
    private final Paint mNowPaint = new Paint();

    /**
     * The listener we notify when user taps on an appointment.
     */
    private OnAppointmentClickListener mOnAppointmentClickListener;

    /**
     * The map storing the appointments location.
     */
    private Map<Rect, StoreAppointment> items = new HashMap<>();

    /**
     * Boolean used to know if we are touching the view.
     */
    private boolean mTouched;

    /**
     * The x coordinate of the latest touch event.
     */
    private int mTouchedPositionX;

    /**
     * The y coordinate of the latest touch event.
     */
    private int mTouchedPositionY;

    /**
     * The store's starting hour.
     */
    private int mHourMin;

    /**
     * The store's ending hour.
     */
    private int mHourMax;

    /**
     * All the hours strings. From starting hour to ending hour.
     */
    private String[] mHoursStrings;

    /**
     * The number of columns of the grid.
     */
    private int mNumberOfColumns;

    /**
     * The number of rows of the grid.
     */
    private int mNumberOfRows;

    /**
     * The width of each cell.
     */
    private int mCellWidth;

    /**
     * The width of each cell at 1x zoom.
     */
    private int mDefaultCellHeight;

    /**
     * The default zoom factor.
     */
    private float mScaleFactor = 1.0f;

    /**
     * The height of each cell.
     */
    private int mCellHeight;

    /**
     * The dimensions of the screen.
     */
    private Point mScreenSize = new Point();

    /**
     * The size of all the texts.
     */
    private int mTextSize;

    /**
     * The height of the margin between the names and the grid.
     */
    private int mTopMargin;

    /**
     * The width of the margin between the hours strings and the grid.
     */
    private int mLeftMargin;

    /**
     * The initial x position.
     */
    private int mInitialX;

    /**
     * The initial y position.
     */
    private int mInitialY;

    /**
     * The final x position.
     */
    private int mFinalX;

    /**
     * The workers model.
     */
    private StoreWorkerModel mStoreWorkerModel = StoreWorkerModel.getInstance();

    /**
     * The store model.
     */
    private StoreModel mStoreModel = StoreModel.getInstance();

    /**
     * Default constructor.
     *
     * @param context the context
     */
    public OverallView(Context context) {
        super(context);
        init(context);
    }

    /**
     * Constructor used by xml.
     *
     * @param context the context
     * @param attrs   layout attributes
     */
    public OverallView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * Initialization method used to get all the information we need to draw everything.
     *
     * @param context the context
     */
    private void init(Context context) {

        // getting the dimensions of the screen
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(mScreenSize);

        // getting project's resources
        Resources resources = context.getResources();

        // getting all values in resources and models
        mHourMin = mStoreModel.getStartingHour();
        mHourMax = mStoreModel.getEndingMinute() > 0 ? mStoreModel.getEndingHour() + 1 : mStoreModel.getEndingHour();
        mNumberOfRows = mHourMax - mHourMin;
        mHoursStrings = new String[mNumberOfRows + 1];
        generateHoursStrings();
        mNumberOfColumns = mStoreWorkerModel.getStoreWorkersNumber();
        mTopMargin = (int) resources.getDimension(R.dimen.hour_top_margin);
        mLeftMargin = (int) resources.getDimension(R.dimen.hour_left_margin);
        mTextSize = (int) resources.getDimension(R.dimen.hour_text_size);
        mDefaultCellHeight = (int) resources.getDimension(R.dimen.hour_cell_height);
        mCellHeight = mDefaultCellHeight;
        mInitialX = 2 * mLeftMargin + mTextSize;
        mInitialY = mTextSize + mTopMargin;
        mFinalX = mScreenSize.x - mTextSize;
        mCellWidth = (mFinalX - mInitialX) / mNumberOfColumns;

        // initializing the painters
        int blackColor = ContextCompat.getColor(context, R.color.black);
        mBlackPaint.setColor(blackColor);
        mBlackPaint.setTextSize(mTextSize);
        mBlackPaint.setAntiAlias(true);
        int greyColor = ContextCompat.getColor(context, R.color.grey_overlay);
        mGreyPaint.setColor(greyColor);
        mGreyPaint.setTextSize(mTextSize);
        mGreyPaint.setAntiAlias(true);
        int mainColor = ContextCompat.getColor(context, R.color.colorPrimary);
        mAppointmentPaint.setColor(mainColor);
        mAppointmentPaint.setAntiAlias(true);
        int accentColor = ContextCompat.getColor(context, R.color.colorAccent);
        mNowPaint.setColor(accentColor);
        mNowPaint.setStrokeWidth(3);
        mNowPaint.setAntiAlias(true);
    }

    /**
     * Method used to fill the hours strings array.
     */
    private void generateHoursStrings() {
        int counter = 0;
        for (int i = mHourMin; i < mHourMax + 1; i++) {
            mHoursStrings[counter] = i < 10 ? "0" + i : "" + i;
            counter++;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // drawing the grid, the texts, the appointments and the now line
        drawBackground(canvas);
        drawAppointments(canvas);
        drawNowLine(canvas);
    }

    /**
     * Draws the texts and the grid.
     *
     * @param canvas the canvas
     */
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

    /**
     * Draws the appointments
     *
     * @param canvas the canvas
     */
    private void drawAppointments(Canvas canvas) {
        int x1 = mInitialX + 2;
        for (int i = 0; i < mNumberOfColumns; i++) {
            int x2 = x1 + mCellWidth - 4;
            if (i == mNumberOfColumns - 1) {
                x2 = mFinalX - 2;
            }
            for (StoreAppointment currentAppointment : mStoreWorkerModel.getStoreWorker(i).getStoreAppointments()) {
                if (!(currentAppointment instanceof NullStoreAppointment)
                        && !currentAppointment.getStartTime().before(mStoreModel.getStartingTime())
                        && !currentAppointment.getEndTime().after(mStoreModel.getEndingTime())) {
                    int startHour = currentAppointment.getStartTime().get(Calendar.HOUR_OF_DAY);
                    int startMinute = currentAppointment.getStartTime().get(Calendar.MINUTE);
                    int endHour = currentAppointment.getEndTime().get(Calendar.HOUR_OF_DAY);
                    int endMinute = currentAppointment.getEndTime().get(Calendar.MINUTE);
                    int y1 = mInitialY + mCellHeight * (startHour - mHourMin) + (mCellHeight * startMinute) / 60;
                    int y2 = mInitialY + mCellHeight * (endHour - mHourMin) + (mCellHeight * endMinute) / 60;
                    Calendar calendar = Calendar.getInstance();
                    // we don't want to consider seconds and milliseconds
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    Paint p = currentAppointment.getEndTime().after(calendar) ? mAppointmentPaint : mGreyPaint;
                    Rect r = new Rect(x1, y1 + 1, x2, y2);
                    items.put(r, currentAppointment);
                    canvas.drawRect(r, p);
                    canvas.drawLine(x1, y1 + 1, x2, y1 + 1, mGreyPaint);
                    canvas.drawLine(x1, y2, x2, y2, mGreyPaint);
                }
            }
            x1 += mCellWidth;
        }
    }

    /**
     * Draws the now line.
     *
     * @param canvas the canvas
     */
    private void drawNowLine(Canvas canvas) {
        Calendar calendar = Calendar.getInstance();
        // we don't want to consider seconds and milliseconds
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int nowLineY = mInitialY + mCellHeight * (hour - mHourMin) + (mCellHeight * minute) / 60;
        canvas.drawLine(mInitialX, nowLineY, mFinalX, nowLineY, mNowPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int maxHeight = 2 * (mTextSize + mTopMargin) + mNumberOfRows * mCellHeight;
        setMeasuredDimension(mScreenSize.x, maxHeight);
    }

    /**
     * When the user has pinched the view we zoom in or out.
     *
     * @param scaleFactor the zoom factor
     */
    public void onScaleChanged(float scaleFactor) {
        if (scaleFactor != mScaleFactor) {
            mScaleFactor = scaleFactor;
            mCellHeight = (int) (mDefaultCellHeight * scaleFactor);
            requestLayout();
            invalidate();
        }
    }

    /**
     * When something about the workers has changed we update our information and redraw everything.
     */
    public void onWorkersChanged() {
        mNumberOfColumns = mStoreWorkerModel.getStoreWorkersNumber();
        invalidate();
    }

    /**
     * When working times have changed we update our information and redraw everything.
     */
    public void onWorkingTimesChanged() {
        mHourMin = mStoreModel.getStartingHour();
        mHourMax = mStoreModel.getEndingMinute() > 0 ? mStoreModel.getEndingHour() + 1 : mStoreModel.getEndingHour();
        mNumberOfRows = mHourMax - mHourMin;
        mHoursStrings = new String[mNumberOfRows + 1];
        generateHoursStrings();
        requestLayout();
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN: // touch
                mTouchedPositionX = (int) event.getX();
                mTouchedPositionY = (int) event.getY();
                mTouched = true;
                break;
            case MotionEvent.ACTION_MOVE: // scroll action
                int x = (int) event.getX();
                int y = (int) event.getY();
                if (Math.abs(x - mTouchedPositionX) > SCROLL_DETECTION_OFFSET_VALUE || Math.abs(y - mTouchedPositionY) > SCROLL_DETECTION_OFFSET_VALUE) {
                    mTouched = false;
                }
                break;
            case MotionEvent.ACTION_UP: // maybe a tap
                if (mTouched) {
                    onClick();
                    mTouched = false;
                }
                break;
        }
        return true;
    }

    /**
     * Sets the listener for appointment tap events.
     *
     * @param onAppointmentClickListener the listener
     */
    public void setOnAppointmentClickListener(OnAppointmentClickListener onAppointmentClickListener) {
        mOnAppointmentClickListener = onAppointmentClickListener;
    }

    /**
     * When the user taps on the screen we check if it was on an appointment. If so we trigger the listener.
     */
    private void onClick() {
        // getting the appointment clicked based on the touched position
        for (Rect r : items.keySet()) {
            if (r.contains(mTouchedPositionX, mTouchedPositionY)) {
                // displaying a dialog about appointment's details
                mOnAppointmentClickListener.onAppointmentClicked(items.get(r));
                break;
            }
        }
    }

}
