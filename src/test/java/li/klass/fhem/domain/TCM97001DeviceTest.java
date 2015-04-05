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

import org.junit.Test;

import li.klass.fhem.domain.core.DeviceXMLParsingBase;

import static org.assertj.core.api.Assertions.assertThat;

public class TCM97001DeviceTest extends DeviceXMLParsingBase {

    @Test
    public void should_read_device_attributes() throws Exception {
        TCM97001Device device = getDeviceFor("CUL_TCM97001_102", TCM97001Device.class);
        assertThat(device).isNotNull();
        assertThat(device.getTemperature()).isEqualTo("4.1 (°C)");
        assertThat(device.getBattery()).isEqualTo("Low");

        assertThat(device.getLogDevices()).isNotEmpty();
        assertThat(device.getDeviceCharts()).hasSize(1);
    }

    @Test
    public void should_ignore_devices_without_temperature() {
        TCM97001Device device = getDeviceFor("CUL_TCM97001_13", TCM97001Device.class);
        assertThat(device).isNull();
    }

    @Override
    protected String getFileName() {
        return "tcm97001.xml";
    }
}