/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.systemui.screenshot;

import android.graphics.Bitmap;
import android.graphics.HardwareRenderer;
import android.graphics.RecordingCanvas;
import android.graphics.Rect;
import android.graphics.RenderNode;
import android.graphics.drawable.Drawable;

import androidx.annotation.UiThread;

import java.util.ArrayList;
import java.util.List;

/**
 * Owns a series of partial screen captures (tiles).
 * <p>
 * To display on-screen, use {@link #getDrawable()}.
 */
@UiThread
class ImageTileSet {

    private static final String TAG = "ImageTileSet";

    interface OnBoundsChangedListener {
        /**
         * Reports an update to the bounding box that contains all active tiles. These are virtual
         * (capture) coordinates which can be either negative or positive.
         */
        void onBoundsChanged(int left, int top, int right, int bottom);
    }

    interface OnContentChangedListener {
        /**
         * Mark as dirty and rebuild display list.
         */
        void onContentChanged();
    }

    private final List<ImageTile> mTiles = new ArrayList<>();
    private final Rect mBounds = new Rect();

    private OnContentChangedListener mOnContentChangedListener;
    private OnBoundsChangedListener mOnBoundsChangedListener;

    void setOnBoundsChangedListener(OnBoundsChangedListener listener) {
        mOnBoundsChangedListener = listener;
    }

    void setOnContentChangedListener(OnContentChangedListener listener) {
        mOnContentChangedListener = listener;
    }

    void addTile(ImageTile tile) {
        final Rect newBounds = new Rect(mBounds);
        final Rect newRect = tile.getLocation();
        mTiles.add(tile);
        newBounds.union(newRect);
        if (!newBounds.equals(mBounds)) {
            mBounds.set(newBounds);
            if (mOnBoundsChangedListener != null) {
                mOnBoundsChangedListener.onBoundsChanged(
                        newBounds.left, newBounds.top, newBounds.right, newBounds.bottom);
            }
        }
        if (mOnContentChangedListener != null) {
            mOnContentChangedListener.onContentChanged();
        }
    }

    /**
     * Returns a drawable to paint the combined contents of the tiles. Drawable dimensions are
     * zero-based and map directly to {@link #getLeft()}, {@link #getTop()}, {@link #getRight()},
     * and {@link #getBottom()} which are dimensions relative to the capture start position
     * (positive or negative).
     *
     * @return a drawable to display the image content
     */
    Drawable getDrawable() {
        return new TiledImageDrawable(this);
    }

    boolean  isEmpty() {
        return mTiles.isEmpty();
    }

    int size() {
        return mTiles.size();
    }

    ImageTile get(int i) {
        return mTiles.get(i);
    }

    Bitmap toBitmap() {
        if (mTiles.isEmpty()) {
            return null;
        }
        final RenderNode output = new RenderNode("Bitmap Export");
        output.setPosition(0, 0, getWidth(), getHeight());
        RecordingCanvas canvas = output.beginRecording();
        canvas.translate(-getLeft(), -getTop());
        for (ImageTile tile : mTiles) {
            canvas.save();
            canvas.translate(tile.getLeft(), tile.getTop());
            canvas.drawRenderNode(tile.getDisplayList());
            canvas.restore();
        }
        output.endRecording();
        return HardwareRenderer.createHardwareBitmap(output, getWidth(), getHeight());
    }

    int getLeft() {
        return mBounds.left;
    }

    int getTop() {
        return mBounds.top;
    }

    int getRight() {
        return mBounds.right;
    }

    int getBottom() {
        return mBounds.bottom;
    }

    int getWidth() {
        return mBounds.width();
    }

    int getHeight() {
        return mBounds.height();
    }

    void clear() {
        mBounds.set(0, 0, 0, 0);
        mTiles.forEach(ImageTile::close);
        mTiles.clear();
        if (mOnBoundsChangedListener != null) {
            mOnBoundsChangedListener.onBoundsChanged(0, 0, 0, 0);
        }
        if (mOnContentChangedListener != null) {
            mOnContentChangedListener.onContentChanged();
        }
    }
}
