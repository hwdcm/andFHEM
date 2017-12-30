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

package li.klass.fhem.appwidget.ui.widget.medium

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import li.klass.fhem.R
import li.klass.fhem.adapter.devices.hook.ButtonHook
import li.klass.fhem.adapter.devices.hook.DeviceHookProvider
import li.klass.fhem.adapter.devices.toggle.OnOffBehavior
import li.klass.fhem.appwidget.action.AppWidgetBroadcastReceiver
import li.klass.fhem.appwidget.ui.widget.base.DeviceAppWidgetView
import li.klass.fhem.appwidget.update.WidgetConfiguration
import li.klass.fhem.constants.Actions
import li.klass.fhem.constants.BundleExtraKeys.*
import li.klass.fhem.dagger.ApplicationComponent
import li.klass.fhem.domain.core.FhemDevice
import javax.inject.Inject

open class ToggleWidgetView : DeviceAppWidgetView() {
    @Inject
    lateinit var deviceHookProvider: DeviceHookProvider
    @Inject
    lateinit var onOffBehavior: OnOffBehavior

    override fun getWidgetName(): Int = R.string.widget_toggle

    override fun getContentView(): Int = R.layout.appwidget_toggle

    override fun fillWidgetView(context: Context, view: RemoteViews, device: FhemDevice, widgetConfiguration: WidgetConfiguration) {
        val isOn = onOffBehavior.isOnConsideringHooks(device)
        val actionIntent = actionIntentFor(device, widgetConfiguration, context)

        if (isOn) {
            view.setViewVisibility(R.id.toggleOff, View.GONE)
            view.setViewVisibility(R.id.toggleOn, View.VISIBLE)
            view.setTextViewText(R.id.toggleOn, device.getEventMapStateFor(onOffBehavior.getOnStateName(device)!!))
        } else {
            view.setViewVisibility(R.id.toggleOff, View.VISIBLE)
            view.setViewVisibility(R.id.toggleOn, View.GONE)
            view.setTextViewText(R.id.toggleOff, device.getEventMapStateFor(onOffBehavior.getOffStateName(device)!!))
        }

        val pendingIntent = PendingIntent.getBroadcast(context, widgetConfiguration.widgetId, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        view.setOnClickPendingIntent(R.id.toggleOff, pendingIntent)
        view.setOnClickPendingIntent(R.id.toggleOn, pendingIntent)

        openDeviceDetailPageWhenClicking(R.id.main, view, device, widgetConfiguration, context)
    }

    private fun actionIntentFor(device: FhemDevice, widgetConfiguration: WidgetConfiguration, context: Context): Intent {
        val hook = deviceHookProvider.buttonHookFor(device)

        return Intent(context, AppWidgetBroadcastReceiver::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(APP_WIDGET_ID, widgetConfiguration.widgetId)
                .putExtra(DEVICE_NAME, device.name)
                .putExtra(CONNECTION_ID, widgetConfiguration.connectionId)
                .apply {
                    when (hook) {
                        ButtonHook.ON_DEVICE -> setAction(Actions.DEVICE_WIDGET_TARGET_STATE)
                                .putExtra(DEVICE_TARGET_STATE, onOffBehavior.getOnStateName(device))
                        ButtonHook.OFF_DEVICE -> setAction(Actions.DEVICE_WIDGET_TARGET_STATE)
                                .putExtra(DEVICE_TARGET_STATE, onOffBehavior.getOffStateName(device))
                        else -> setAction(Actions.DEVICE_WIDGET_TOGGLE)
                    }
                }
    }

    override fun supports(device: FhemDevice, context: Context): Boolean =
            onOffBehavior.supports(device)

    override fun inject(applicationComponent: ApplicationComponent) {
        applicationComponent.inject(this)
    }
}
