/*
 * Copyright (C) 2019 Descendant
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

package com.android.systemui.qs.tiles;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.view.View;

import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.plugins.qs.QSTile.BooleanState;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.qs.QSHost;
import com.android.systemui.qs.tileimpl.QSTileImpl;
import com.android.internal.logging.nano.MetricsProto.MetricsEvent;

import static android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE;
import static android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
import static android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL;

import javax.inject.Inject;

/** Quick settings tile: AutoBrightness **/
public class AutoBrightnessTile extends QSTileImpl<BooleanState> {

    private boolean mListening;

    private final String SYSTEM_KEY = SCREEN_BRIGHTNESS_MODE;
    private final int DEFAULT_VALUE = SCREEN_BRIGHTNESS_MODE_MANUAL;

    @Inject
    public AutoBrightnessTile(QSHost host) {
        super(host);
    }

    @Override
    public BooleanState newTileState() {
        return new BooleanState();
    }

    @Override
    public void handleSetListening(boolean listening) {
        if (mListening == listening) return;
        mListening = listening;
    }

    @Override
    protected void handleClick() {
        setEnabled(!mState.value);
        refreshState();
    }

    @Override
    public void handleLongClick() {
        setEnabled(!mState.value);
        refreshState();
    }

    @Override
    public Intent getLongClickIntent() {
        return null;
    }

    private void setEnabled(boolean enabled) {
        Settings.System.putInt(mContext.getContentResolver(), SYSTEM_KEY,
                enabled ? SCREEN_BRIGHTNESS_MODE_AUTOMATIC : DEFAULT_VALUE);
    }

    private boolean isChecked() {
        return Settings.System.getInt(mContext.getContentResolver(),
                SYSTEM_KEY, DEFAULT_VALUE) != DEFAULT_VALUE;
    }

    @Override
    protected void handleUpdateState(BooleanState state, Object arg) {
        state.value = isChecked();
        state.label = mContext.getString(R.string.quick_settings_autobrightness_label);
        state.icon = ResourceIcon.get(R.drawable.ic_qs_autobrightness);
        state.contentDescription =  mContext.getString(
                   R.string.quick_settings_autobrightness_label);
        if (state.value) {
            state.state = Tile.STATE_ACTIVE;
        } else {
            state.state = Tile.STATE_INACTIVE;
        }
    }

    @Override
    public CharSequence getTileLabel() {
        return mContext.getString(R.string.quick_settings_autobrightness_label);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.DESCENDANT_METRICS;
    }
}
