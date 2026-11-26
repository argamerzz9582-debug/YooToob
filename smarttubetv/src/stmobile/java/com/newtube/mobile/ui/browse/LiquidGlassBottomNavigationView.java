package com.newtube.mobile.ui.browse;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.liskovsoft.smartyoutubetv2.tv.R;
import com.newtube.mobile.ui.common.Haptics;

/**
 * YooToob floating glass dock (the "Floating bottom bar" appearance setting, on by default).
 *
 * <p>A restrained Liquid Glass layer, drawn with plain Canvas calls so it costs nothing per frame:
 * <ul>
 *   <li>A translucent body (yoo_glass_dock_body, about 90% opaque) the feed scrolls under. No backdrop blur:
 *       it does not exist below API 31, and a live blur over a scrolling grid is a full-screen
 *       pass every frame. At that opacity the labels keep full contrast over any thumbnail.</li>
 *   <li>A hairline outline, and a lit top edge (yoo_glass_dock_highlight) that fades out toward
 *       the middle, which is what makes the surface read as a material rather than a flat pill.</li>
 *   <li>A soft ambient shadow offset downward.</li>
 *   <li>No ripple bubbles; a haptic tick on selection.</li>
 * </ul>
 * All colours are resources with a night side (values/colors_yootoob.xml), so the dock follows
 * the Theme setting. Shaders and paths are rebuilt only when the size or configuration changes.</p>
 */
public class LiquidGlassBottomNavigationView extends BottomNavigationView {

    private boolean mFloatingEnabled = false;

    // Geometry & Paths (pre-allocated to avoid GC churn)
    private final RectF mCapsuleRect = new RectF();
    private final Path mCapsulePath = new Path();

    // Paints
    private final Paint mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mDockBodyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mDockOutlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF mHighlightRect = new RectF();

    public LiquidGlassBottomNavigationView(@NonNull Context context) {
        this(context, null);
    }

    public LiquidGlassBottomNavigationView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, com.google.android.material.R.attr.bottomNavigationStyle);
    }

    public LiquidGlassBottomNavigationView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setWillNotDraw(false);
        setBackground(null);
        setBackgroundColor(Color.TRANSPARENT);

        mDockOutlinePaint.setStyle(Paint.Style.STROKE);
        mDockOutlinePaint.setStrokeWidth(dp(1f));
        mHighlightPaint.setStyle(Paint.Style.STROKE);
        mHighlightPaint.setStrokeWidth(dp(1f));
        updateColors();

        // Disable circular ripple bubbles
        setItemRippleColor(ColorStateList.valueOf(Color.TRANSPARENT));
        setItemBackground(null);
    }

    public void setFloatingEnabled(boolean enabled) {
        if (mFloatingEnabled == enabled) {
            return;
        }
        mFloatingEnabled = enabled;
        updateColors();

        if (mFloatingEnabled) {
            setBackground(null);
            setBackgroundColor(Color.TRANSPARENT);
            setItemBackground(null);
            // Disable circular ripple bubble effect when tapping
            setItemRippleColor(ColorStateList.valueOf(Color.TRANSPARENT));
        }
        invalidate();
    }

    public boolean isFloatingEnabled() {
        return mFloatingEnabled;
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateColors();
        updateGeometry(getWidth(), getHeight());
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateGeometry(w, h);
        updateColors();
    }

    private void updateGeometry(int w, int h) {
        if (w <= 0 || h <= 0) {
            return;
        }

        float pillRadius = h / 2f;
        mCapsuleRect.set(0f, 0f, (float) w, (float) h);

        mCapsulePath.reset();
        mCapsulePath.addRoundRect(mCapsuleRect, pillRadius, pillRadius, Path.Direction.CW);

        // The lit edge: the same capsule inset by half a stroke, its stroke fading from the
        // highlight colour at the top to nothing a third of the way down.
        float inset = dp(0.5f);
        mHighlightRect.set(inset, inset, w - inset, h - inset);
        int highlight = ContextCompat.getColor(getContext(), R.color.yoo_glass_dock_highlight);
        mHighlightPaint.setShader(new LinearGradient(0f, 0f, 0f, h * 0.35f,
                highlight, highlight & 0x00FFFFFF, Shader.TileMode.CLAMP));
    }

    private void updateColors() {
        Context context = getContext();
        mDockBodyPaint.setColor(ContextCompat.getColor(context, R.color.yoo_glass_dock_body));
        mDockOutlinePaint.setColor(ContextCompat.getColor(context, R.color.yoo_glass_dock_outline));
        mShadowPaint.setColor(ContextCompat.getColor(context, R.color.yoo_glass_dock_shadow));
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // Flat, clean touch dispatch without squish or liquid scale
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void setSelectedItemId(int itemId) {
        super.setSelectedItemId(itemId);
        if (mFloatingEnabled && isAttachedToWindow()) {
            Haptics.tick(this);
        }
    }

    @Override
    public void setOnItemSelectedListener(@Nullable OnItemSelectedListener listener) {
        super.setOnItemSelectedListener(item -> {
            if (mFloatingEnabled && isAttachedToWindow()) {
                Haptics.tick(this);
            }
            return listener == null || listener.onNavigationItemSelected(item);
        });
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (!mFloatingEnabled) {
            super.dispatchDraw(canvas);
            return;
        }

        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) {
            super.dispatchDraw(canvas);
            return;
        }

        float pillRadius = h / 2f;

        // 1. Ambient shadow, offset down so the dock reads as lifted off the feed
        canvas.drawRoundRect(0f, dp(2f), (float) w, (float) h + dp(3f), pillRadius, pillRadius, mShadowPaint);

        // 2. Translucent glass body
        canvas.drawRoundRect(mCapsuleRect, pillRadius, pillRadius, mDockBodyPaint);

        // 3. Hairline outline, then the lit top edge over it
        canvas.drawRoundRect(mCapsuleRect, pillRadius, pillRadius, mDockOutlinePaint);
        canvas.drawRoundRect(mHighlightRect, pillRadius, pillRadius, mHighlightPaint);

        // 4. Clip children strictly within the capsule
        canvas.save();
        canvas.clipPath(mCapsulePath);
        super.dispatchDraw(canvas);
        canvas.restore();
    }

    private float dp(float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }
}
