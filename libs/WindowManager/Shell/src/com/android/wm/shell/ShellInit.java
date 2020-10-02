/*
 * Copyright (C) 2019 The Android Open Source Project
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

package com.android.wm.shell;

import com.android.wm.shell.common.DisplayImeController;
import com.android.wm.shell.draganddrop.DragAndDropController;
import com.android.wm.shell.splitscreen.SplitScreen;

import java.util.Optional;

/**
 * An entry point into the shell for initializing shell internal state.
 */
public class ShellInit {

    private final DisplayImeController mDisplayImeController;
    private final DragAndDropController mDragAndDropController;
    private final ShellTaskOrganizer mShellTaskOrganizer;
    private final Optional<SplitScreen> mSplitScreenOptional;

    public ShellInit(DisplayImeController displayImeController,
            DragAndDropController dragAndDropController,
            ShellTaskOrganizer shellTaskOrganizer,
            Optional<SplitScreen> splitScreenOptional) {
        mDisplayImeController = displayImeController;
        mDragAndDropController = dragAndDropController;
        mShellTaskOrganizer = shellTaskOrganizer;
        mSplitScreenOptional = splitScreenOptional;
    }

    public void init() {
        // Start listening for display changes
        mDisplayImeController.startMonitorDisplays();
        // Register the shell organizer
        mShellTaskOrganizer.registerOrganizer();
        // Bind the splitscreen impl to the drag drop controller
        mDragAndDropController.setSplitScreenController(mSplitScreenOptional);
    }
}
