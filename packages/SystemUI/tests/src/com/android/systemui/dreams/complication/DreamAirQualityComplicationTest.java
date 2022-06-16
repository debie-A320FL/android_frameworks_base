/*
 * Copyright (C) 2022 The Android Open Source Project
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

package com.android.systemui.dreams.complication;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import android.testing.AndroidTestingRunner;
import android.widget.TextView;

import androidx.test.filters.SmallTest;

import com.android.systemui.SysuiTestCase;
import com.android.systemui.dreams.DreamOverlayStateController;
import com.android.systemui.dreams.complication.DreamAirQualityComplication.DreamAirQualityViewController;
import com.android.systemui.dreams.smartspace.DreamSmartspaceController;
import com.android.systemui.plugins.ActivityStarter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@SmallTest
@RunWith(AndroidTestingRunner.class)
public class DreamAirQualityComplicationTest extends SysuiTestCase {
    private static final String TRAMPOLINE_COMPONENT = "TestComponent";

    @Mock
    private DreamSmartspaceController mDreamSmartspaceController;

    @Mock
    private DreamOverlayStateController mDreamOverlayStateController;

    @Mock
    private DreamAirQualityComplication mComplication;

    @Mock
    private AirQualityColorPicker mColorPicker;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Ensures {@link DreamAirQualityComplication} is registered.
     */
    @Test
    public void testComplicationRegistered() {
        final DreamAirQualityComplication.Registrant registrant =
                new DreamAirQualityComplication.Registrant(
                        mContext,
                        mDreamOverlayStateController,
                        mComplication);
        registrant.start();
        verify(mDreamOverlayStateController).addComplication(eq(mComplication));
    }

    @Test
    public void testGetUnfilteredTargets() {
        final DreamAirQualityViewController controller =
                new DreamAirQualityViewController(
                        mock(TextView.class),
                        mDreamSmartspaceController,
                        TRAMPOLINE_COMPONENT,
                        mock(ActivityStarter.class),
                        mColorPicker);
        controller.onViewAttached();
        verify(mDreamSmartspaceController).addUnfilteredListener(any());
        controller.onViewDetached();
        verify(mDreamSmartspaceController).removeUnfilteredListener(any());
    }
}
