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

package li.klass.fhem.appwidget.provider

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import kotlinx.coroutines.*
import li.klass.fhem.AndFHEMApplication
import li.klass.fhem.appwidget.update.AppWidgetInstanceManager
import li.klass.fhem.appwidget.update.AppWidgetUpdateService
import li.klass.fhem.dagger.ApplicationComponent
import java.util.*
import java.util.logging.Logger
import javax.inject.Inject

abstract class AndFHEMAppWidgetProvider protected constructor() : AppWidgetProvider() {

    @Inject
    lateinit var appWidgetInstanceManager: AppWidgetInstanceManager

    @Inject
    lateinit var appWidgetUpdateService: AppWidgetUpdateService

    init {
        AndFHEMApplication.application
                ?.let { inject(it.daggerComponent) }
    }

    protected abstract fun inject(applicationComponent: ApplicationComponent)

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        logger.info("onUpdate - request update for ids=${Arrays.toString(appWidgetIds)}")
        GlobalScope.launch(Dispatchers.Main) {
            appWidgetIds.map {
                async(Dispatchers.IO) {
                    appWidgetUpdateService.doRemoteUpdate(it)
                }
            }.awaitAll().forEach { appWidgetUpdateService.updateWidget(it) }
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        appWidgetIds.forEach { appWidgetId -> appWidgetInstanceManager.delete(appWidgetId) }
    }

    companion object {
        val logger = Logger.getLogger(AndFHEMAppWidgetProvider::class.java.name)
    }
}
