package com.kong.alex.sharkfeed.ui;

import android.view.View;

import com.kong.alex.sharkfeed.api.search.Photo;

public interface SharkClickListener {
    public void onClick(View view, Photo photo, int position);
}
