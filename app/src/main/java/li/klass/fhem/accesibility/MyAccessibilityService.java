/*
 * AndFHEM - Open Source Android application to control a FHEM home automation
 * server.
 *
 * Copyright (c) 2011, Matthias Klass or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU GENERAL PUBLIC LICENSE, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU GENERAL PUBLIC LICENSE
 * for more details.
 *
 * You should have received a copy of the GNU GENERAL PUBLIC LICENSE
 * along with this distribution; if not, write to:
 *   Free Software Foundation, Inc.
 *   51 Franklin Street, Fifth Floor
 *   Boston, MA  02110-1301  USA
 */

package li.klass.fhem.accesibility;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.util.List;

import li.klass.fhem.activities.CommandIndicatorActivity;
import li.klass.fhem.constants.Actions;
import li.klass.fhem.constants.BundleExtraKeys;
import li.klass.fhem.constants.ResultCodes;
import li.klass.fhem.service.intent.VoiceCommandIntentService;
import li.klass.fhem.util.FhemResultReceiver;

public class MyAccessibilityService extends AccessibilityService {
    private DateTime lastCommandTime = new DateTime();

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        DateTime now = new DateTime();
        Interval interval = new Interval(lastCommandTime, now);
        long millis = interval.toDurationMillis();
        if (millis < 3000) return;

        lastCommandTime = now;

        List<CharSequence> texts = accessibilityEvent.getText();
        if (texts.isEmpty()) return;

        String command = texts.get(0).toString();
        command = command.toLowerCase();
        startService(new Intent(Actions.RECOGNIZE_VOICE_COMMAND)
                .setClass(this, VoiceCommandIntentService.class)
                .putExtra(BundleExtraKeys.COMMAND, command));
        Log.d(MyAccessibilityService.class.getName(), command);
    }

    @Override
    public void onInterrupt() {
    }
}