package com.newtube.mobile.ui.browse;

import android.app.Activity;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.liskovsoft.smartyoutubetv2.tv.R;

/**
 * NEWTUBE(you-subscreen): Browse's top bar. On a bottom-nav tab it is the YooToob mark and the
 * "YooToob" wordmark; a section opened from a You row (Sports, Playlists, ...) is a sub-screen of
 * You, so it gets a back arrow and the section's name - like YouTube's You sub-pages.
 * YOOTOOB(brand): the colour lives in the mark; the wordmark is set in ink (yoo_wordmark).
 */
final class BrowseTopBar {
    private static final float WORDMARK_SP = 22f;
    private static final float SUBSCREEN_TITLE_SP = 20f;

    private final TextView mTitle;
    private final View mBack;
    private final View mIcon;
    private final int mSubscreenColor;
    private final int mWordmarkColor;

    BrowseTopBar(Activity activity, Runnable onBack) {
        mTitle = activity.findViewById(R.id.mobile_title_bar);
        mBack = activity.findViewById(R.id.mobile_title_back);
        mIcon = activity.findViewById(R.id.mobile_title_icon);
        mBack.setOnClickListener(v -> onBack.run());
        com.newtube.mobile.ui.common.GlassMaterial.applyTouchScaleFeedback(mBack);
        mTitle.setLetterSpacing(-0.02f);
        mSubscreenColor = ContextCompat.getColor(activity, R.color.mobile_color_on_surface);
        mWordmarkColor = ContextCompat.getColor(activity, R.color.yoo_wordmark);
        show(false, null);
    }

    /**
     * A tab shows the YooToob mark and wordmark; a sub-screen shows the back arrow and
     * {@code title} (the wordmark text if the section came without one - the arrow is what matters).
     */
    void show(boolean subScreen, CharSequence title) {
        mBack.setVisibility(subScreen ? View.VISIBLE : View.GONE);
        if (mIcon != null) {
            mIcon.setVisibility(subScreen ? View.GONE : View.VISIBLE);
        }
        boolean isWordmark = !subScreen || title == null || title.length() == 0;
        mTitle.setText(isWordmark ? mTitle.getContext().getString(R.string.app_name) : title);
        mTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, subScreen ? SUBSCREEN_TITLE_SP : WORDMARK_SP);
        mTitle.setTextColor(isWordmark ? mWordmarkColor : mSubscreenColor);
    }
}
