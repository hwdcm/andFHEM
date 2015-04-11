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

package li.klass.fhem.domain;

import android.content.Context;

import java.util.List;

import li.klass.fhem.R;
import li.klass.fhem.appwidget.annotation.SupportsWidget;
import li.klass.fhem.appwidget.annotation.WidgetTemperatureField;
import li.klass.fhem.appwidget.view.widget.medium.TemperatureWidgetView;
import li.klass.fhem.domain.core.DeviceChart;
import li.klass.fhem.domain.core.DeviceFunctionality;
import li.klass.fhem.domain.core.FhemDevice;
import li.klass.fhem.domain.core.XmllistAttribute;
import li.klass.fhem.domain.genericview.ShowField;
import li.klass.fhem.domain.heating.TemperatureDevice;
import li.klass.fhem.resources.ResourceIdMapper;
import li.klass.fhem.service.graph.description.ChartSeriesDescription;

import static li.klass.fhem.service.graph.description.SeriesType.HUMIDITY;
import static li.klass.fhem.service.graph.description.SeriesType.PRESSURE;
import static li.klass.fhem.service.graph.description.SeriesType.RAIN_RATE;
import static li.klass.fhem.service.graph.description.SeriesType.RAIN_TOTAL;
import static li.klass.fhem.service.graph.description.SeriesType.TEMPERATURE;
import static li.klass.fhem.service.graph.description.SeriesType.WIND;

@SupportsWidget(TemperatureWidgetView.class)
@SuppressWarnings("unused")
public class OregonDevice extends FhemDevice<OregonDevice> implements TemperatureDevice {

    @ShowField(description = ResourceIdMapper.humidity, showInOverview = true)
    @XmllistAttribute("humidity")
    private String humidity;

    @WidgetTemperatureField
    @ShowField(description = ResourceIdMapper.temperature, showInOverview = true)
    @XmllistAttribute("temperature")
    private String temperature;

    @ShowField(description = ResourceIdMapper.forecast, showInOverview = true)
    @XmllistAttribute("forecast")
    private String forecast;

    @ShowField(description = ResourceIdMapper.dewpoint)
    @XmllistAttribute("dewpoint")
    private String dewpoint;

    @ShowField(description = ResourceIdMapper.pressure)
    @XmllistAttribute("pressure")
    private String pressure;

    @ShowField(description = ResourceIdMapper.battery)
    @XmllistAttribute("battery")
    private String battery;

    @ShowField(description = ResourceIdMapper.rainRate, showInOverview = true)
    @XmllistAttribute("rain_rate")
    private String rainRate;

    @ShowField(description = ResourceIdMapper.rainTotal, showInOverview = true)
    @XmllistAttribute("rain_total")
    private String rainTotal;

    @ShowField(description = ResourceIdMapper.windAvgSpeed, showInOverview = true)
    @XmllistAttribute("wind_avspeed")
    private String windAvgSpeed;

    @ShowField(description = ResourceIdMapper.windDirection, showInOverview = true)
    @XmllistAttribute("wind_dir")
    private String windDirection;

    @ShowField(description = ResourceIdMapper.windSpeed, showInOverview = true)
    @XmllistAttribute("wind_speed")
    private String windSpeed;

    @ShowField(description = ResourceIdMapper.uvValue, showInOverview = true)
    @XmllistAttribute("uv_val")
    private String uvValue;

    @ShowField(description = ResourceIdMapper.uvRisk, showInOverview = true)
    @XmllistAttribute("uv_risk")
    private String uvRisk;

    @XmllistAttribute("time")
    public void setTime(String value) {
        setMeasured(value);
    }

    public String getHumidity() {
        return humidity;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getForecast() {
        return forecast;
    }

    public String getDewpoint() {
        return dewpoint;
    }

    public String getPressure() {
        return pressure;
    }

    public String getBattery() {
        return battery;
    }

    public String getRainRate() {
        return rainRate;
    }

    public String getRainTotal() {
        return rainTotal;
    }

    public String getWindAvgSpeed() {
        return windAvgSpeed;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public String getUvValue() {
        return uvValue;
    }

    public String getUvRisk() {
        return uvRisk;
    }

    @Override
    protected void fillDeviceCharts(List<DeviceChart> chartSeries, Context context) {
        super.fillDeviceCharts(chartSeries, context);

        addDeviceChartIfNotNull(new DeviceChart(R.string.temperatureGraph,
                new ChartSeriesDescription.Builder()
                        .withColumnName(R.string.temperature, context)
                        .withFileLogSpec("4:temperature:0:")
                        .withDbLogSpec("temperature::int1")
                        .withSeriesType(TEMPERATURE)
                        .withShowRegression(true)
                        .build()
        ), temperature);

        addDeviceChartIfNotNull(new DeviceChart(R.string.humidityGraph,
                new ChartSeriesDescription.Builder()
                        .withColumnName(R.string.humidity, context).withFileLogSpec("4:humidity:0:")
                        .withDbLogSpec("humidity::int")
                        .withSeriesType(HUMIDITY)
                        .withYAxisMinMaxValue(getLogDevices().get(0).getYAxisMinMaxValueFor("humidity", 0, 100))
                        .build()
        ), humidity);

        addDeviceChartIfNotNull(new DeviceChart(R.string.pressureGraph,
                new ChartSeriesDescription.Builder()
                        .withColumnName(R.string.pressure, context).withFileLogSpec("4:pressure:0:")
                        .withDbLogSpec("pressure::int")
                        .withSeriesType(PRESSURE)
                        .withYAxisMinMaxValue(getLogDevices().get(0).getYAxisMinMaxValueFor("pressure", 700, 1200))
                        .build()
        ), pressure);

        addDeviceChartIfNotNull(new DeviceChart(R.string.rainRate,
                new ChartSeriesDescription.Builder()
                        .withColumnName(R.string.rainRate, context).withFileLogSpec("4:rain_rate:0:")
                        .withDbLogSpec("rain_rate::int2")
                        .withSeriesType(RAIN_RATE)
                        .withYAxisMinMaxValue(getLogDevices().get(0).getYAxisMinMaxValueFor("rain_rate", 0, 0))
                        .build()
        ), rainRate);

        addDeviceChartIfNotNull(new DeviceChart(R.string.rainTotal,
                new ChartSeriesDescription.Builder()
                        .withColumnName(R.string.rainRate, context).withFileLogSpec("4:rain_total:0:")
                        .withDbLogSpec("rain_total::int2")
                        .withSeriesType(RAIN_TOTAL)
                        .withYAxisMinMaxValue(getLogDevices().get(0).getYAxisMinMaxValueFor("rain_total", 0, 0))
                        .build()
        ), rainTotal);

        addDeviceChartIfNotNull(new DeviceChart(R.string.windSpeed,
                new ChartSeriesDescription.Builder()
                        .withColumnName(R.string.rainRate, context).withFileLogSpec("4:wind_speed:0:")
                        .withDbLogSpec("wind_speed::int2")
                        .withSeriesType(WIND)
                        .withYAxisMinMaxValue(getLogDevices().get(0).getYAxisMinMaxValueFor("wind_speed", 0, 0))
                        .build()
        ), windSpeed);
    }

    @Override
    public DeviceFunctionality getDeviceGroup() {
        return DeviceFunctionality.WEATHER;
    }

    @Override
    public boolean isSensorDevice() {
        return true;
    }

    @Override
    public long getTimeRequiredForStateError() {
        return OUTDATED_DATA_MS_DEFAULT;
    }
}