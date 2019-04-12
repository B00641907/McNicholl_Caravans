package com.example.b00641907.mcnicholl_caravans.core;

import android.graphics.Rect;

public interface IViewFinder {

    void setLaserColor(int laserColor);
    void setMaskColor(int maskColor);
    void setBorderColor(int borderColor);
    void setBorderStrokeWidth(int borderStrokeWidth);
    void setBorderLineLength(int borderLineLength);
    void setLaserEnabled(boolean isLaserEnabled);

    void setBorderCornerRounded(boolean isBorderCornersRounded);
    void setBorderAlpha(float alpha);
    void setBorderCornerRadius(int borderCornersRadius);
    void setViewFinderOffset(int offset);
    void setSquareViewFinder(boolean isSquareViewFinder);

    void setupViewFinder();


    Rect getFramingRect();


    int getWidth();


    int getHeight();
}
