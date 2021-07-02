/*
 * Weathergateway
 *
 * Copyright (C) 2019 Christian Poulter
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package de.poulter.weathergateway.station;

import static de.poulter.weathergateway.station.StationBinaryTools.stringToByteArray;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

/**
 * @author Christian Poulter <devel@poulter.de>
 */
public class SensorsTest {

    @Test
    public void testHumidity() throws IOException {
        assertEquals(  0.0, Sensor.OutsideHumidity.convertValue(stringToByteArray("0x00"), 0).doubleValue(), 0.0000001);
        assertEquals(  1.0, Sensor.OutsideHumidity.convertValue(stringToByteArray("0x01"), 0).doubleValue(), 0.0000001);
        assertEquals(100.0, Sensor.OutsideHumidity.convertValue(stringToByteArray("0x64"), 0).doubleValue(), 0.0000001);
        assertEquals(  0.0, Sensor.OutsideHumidity.convertValue(stringToByteArray("0x65"), 0).doubleValue(), 0.0000001);
        assertEquals(  0.0, Sensor.OutsideHumidity.convertValue(stringToByteArray("0x80"), 0).doubleValue(), 0.0000001);
        assertEquals(  0.0, Sensor.OutsideHumidity.convertValue(stringToByteArray("0xFB"), 0).doubleValue(), 0.0000001);
        assertEquals(  0.0, Sensor.OutsideHumidity.convertValue(stringToByteArray("0xFF"), 0).doubleValue(), 0.0000001);
    }
    
    @Test
    public void testTemperature() throws IOException {
        assertEquals(    0.0, Sensor.OutsideTemperature.convertValue(stringToByteArray("0x00 0x00"), 0).doubleValue(), 0.0000001);
        assertEquals(    0.1, Sensor.OutsideTemperature.convertValue(stringToByteArray("0x00 0x01"), 0).doubleValue(), 0.0000001);
        assertEquals(   21.8, Sensor.OutsideTemperature.convertValue(stringToByteArray("0x00 0xda"), 0).doubleValue(), 0.0000001);
        assertEquals(   51.1, Sensor.OutsideTemperature.convertValue(stringToByteArray("0x01 0xFF"), 0).doubleValue(), 0.0000001);
        assertEquals(    0.0, Sensor.OutsideTemperature.convertValue(stringToByteArray("0x7F 0xFF"), 0).doubleValue(), 0.0000001);
        assertEquals(   -0.1, Sensor.OutsideTemperature.convertValue(stringToByteArray("0xFF 0xFF"), 0).doubleValue(), 0.0000001);
        assertEquals( -153.6, Sensor.OutsideTemperature.convertValue(stringToByteArray("0xFA 0x00"), 0).doubleValue(), 0.0000001);
        assertEquals(-3276.7, Sensor.OutsideTemperature.convertValue(stringToByteArray("0x80 0x01"), 0).doubleValue(), 0.0000001);
        assertEquals(-3276.8, Sensor.OutsideTemperature.convertValue(stringToByteArray("0x80 0x00"), 0).doubleValue(), 0.0000001);
    }
    
    @Test
    public void testPressure() throws IOException {
        assertEquals( 0.0, Sensor.BarometricPressureAbsolute.convertValue(stringToByteArray("0x00 0x00"), 0).doubleValue(), 0.0000001);
        assertEquals( 0.1, Sensor.BarometricPressureAbsolute.convertValue(stringToByteArray("0x00 0x01"), 0).doubleValue(), 0.0000001);
        assertEquals(21.8, Sensor.BarometricPressureAbsolute.convertValue(stringToByteArray("0x00 0xda"), 0).doubleValue(), 0.0000001);
        assertEquals(51.1, Sensor.BarometricPressureAbsolute.convertValue(stringToByteArray("0x01 0xFF"), 0).doubleValue(), 0.0000001);
        assertEquals( 0.0, Sensor.BarometricPressureAbsolute.convertValue(stringToByteArray("0x7F 0xFF"), 0).doubleValue(), 0.0000001);
        assertEquals( 0.0, Sensor.BarometricPressureAbsolute.convertValue(stringToByteArray("0xFF 0xFF"), 0).doubleValue(), 0.0000001);
        assertEquals( 0.0, Sensor.BarometricPressureAbsolute.convertValue(stringToByteArray("0xFA 0x00"), 0).doubleValue(), 0.0000001);
        assertEquals( 0.0, Sensor.BarometricPressureAbsolute.convertValue(stringToByteArray("0x80 0x01"), 0).doubleValue(), 0.0000001);
        assertEquals( 0.0, Sensor.BarometricPressureAbsolute.convertValue(stringToByteArray("0x80 0x00"), 0).doubleValue(), 0.0000001);
    }
    
    @Test
    public void testWindSpeed() throws IOException {
        assertEquals(  0.0, Sensor.WindSpeed.convertValue(stringToByteArray("0x00 0x00"), 0).doubleValue(), 0.0000001);
        assertEquals(  0.4, Sensor.WindSpeed.convertValue(stringToByteArray("0x00 0x01"), 0).doubleValue(), 0.0000001);
        assertEquals( 78.5, Sensor.WindSpeed.convertValue(stringToByteArray("0x00 0xda"), 0).doubleValue(), 0.0000001);
        assertEquals(184.0, Sensor.WindSpeed.convertValue(stringToByteArray("0x01 0xFF"), 0).doubleValue(), 0.0000001);
        assertEquals(  0.0, Sensor.WindSpeed.convertValue(stringToByteArray("0x7F 0xFF"), 0).doubleValue(), 0.0000001);
        assertEquals(  0.0, Sensor.WindSpeed.convertValue(stringToByteArray("0xFF 0xFF"), 0).doubleValue(), 0.0000001);
        assertEquals(  0.0, Sensor.WindSpeed.convertValue(stringToByteArray("0xFA 0x00"), 0).doubleValue(), 0.0000001);
        assertEquals(  0.0, Sensor.WindSpeed.convertValue(stringToByteArray("0x80 0x01"), 0).doubleValue(), 0.0000001);
        assertEquals(  0.0, Sensor.WindSpeed.convertValue(stringToByteArray("0x80 0x00"), 0).doubleValue(), 0.0000001);
    }
    
    @Test
    public void testWindDirection() throws IOException {
        assertEquals(  0.0, Sensor.WindDirection.convertValue(stringToByteArray("0x00 0x00"), 0).doubleValue(), 0.0000001);
        assertEquals(  1.0, Sensor.WindDirection.convertValue(stringToByteArray("0x00 0x01"), 0).doubleValue(), 0.0000001);
        assertEquals(218.0, Sensor.WindDirection.convertValue(stringToByteArray("0x00 0xda"), 0).doubleValue(), 0.0000001);
        assertEquals(360.0, Sensor.WindDirection.convertValue(stringToByteArray("0x01 0x68"), 0).doubleValue(), 0.0000001);
        assertEquals(  0.0, Sensor.WindDirection.convertValue(stringToByteArray("0x01 0x69"), 0).doubleValue(), 0.0000001);
        assertEquals(  0.0, Sensor.WindDirection.convertValue(stringToByteArray("0x01 0xFF"), 0).doubleValue(), 0.0000001);
        assertEquals(  0.0, Sensor.WindDirection.convertValue(stringToByteArray("0x7F 0xFF"), 0).doubleValue(), 0.0000001);
        assertEquals(  0.0, Sensor.WindDirection.convertValue(stringToByteArray("0xFF 0xFF"), 0).doubleValue(), 0.0000001);
        assertEquals(  0.0, Sensor.WindDirection.convertValue(stringToByteArray("0xFA 0x00"), 0).doubleValue(), 0.0000001);
        assertEquals(  0.0, Sensor.WindDirection.convertValue(stringToByteArray("0x80 0x01"), 0).doubleValue(), 0.0000001);
        assertEquals(  0.0, Sensor.WindDirection.convertValue(stringToByteArray("0x80 0x00"), 0).doubleValue(), 0.0000001);
    }
    
    @Test
    public void testRain() throws IOException {
        assertEquals(      0.0, Sensor.RainAll.convertValue(stringToByteArray("0x00 0x00 0x00 0x00"), 0).doubleValue(), 0.0000001);
        assertEquals(      0.1, Sensor.RainAll.convertValue(stringToByteArray("0x00 0x00 0x00 0x01"), 0).doubleValue(), 0.0000001);
        assertEquals(     25.5, Sensor.RainAll.convertValue(stringToByteArray("0x00 0x00 0x00 0xFF"), 0).doubleValue(), 0.0000001);
        assertEquals(     25.6, Sensor.RainAll.convertValue(stringToByteArray("0x00 0x00 0x01 0x00"), 0).doubleValue(), 0.0000001);
        assertEquals(   6553.5, Sensor.RainAll.convertValue(stringToByteArray("0x00 0x00 0xFF 0xFF"), 0).doubleValue(), 0.0000001);
        assertEquals(   6553.6, Sensor.RainAll.convertValue(stringToByteArray("0x00 0x01 0x00 0x00"), 0).doubleValue(), 0.0000001);
        assertEquals(1677721.4, Sensor.RainAll.convertValue(stringToByteArray("0x00 0xFF 0xFF 0xFE"), 0).doubleValue(), 0.0000001);
        assertEquals(      0.0, Sensor.RainAll.convertValue(stringToByteArray("0x00 0xFF 0xFF 0xFF"), 0).doubleValue(), 0.0000001);
        assertEquals(      0.0, Sensor.RainAll.convertValue(stringToByteArray("0x01 0x00 0x00 0x00"), 0).doubleValue(), 0.0000001);
        assertEquals(      0.0, Sensor.RainAll.convertValue(stringToByteArray("0x7F 0xFF 0xFF 0xFF"), 0).doubleValue(), 0.0000001);

        assertEquals(0.0, Sensor.RainAll.convertValue(stringToByteArray("0xFF 0xFF 0xFF 0xFF"), 0).doubleValue(), 0.0000001);
        assertEquals(0.0, Sensor.RainAll.convertValue(stringToByteArray("0xFF 0xFF 0xFF 0xFE"), 0).doubleValue(), 0.0000001);
        assertEquals(0.0, Sensor.RainAll.convertValue(stringToByteArray("0xFF 0xFF 0xFF 0x00"), 0).doubleValue(), 0.0000001);
        assertEquals(0.0, Sensor.RainAll.convertValue(stringToByteArray("0xFF 0xFF 0xFE 0xFF"), 0).doubleValue(), 0.0000001);
        assertEquals(0.0, Sensor.RainAll.convertValue(stringToByteArray("0xFF 0xFF 0x00 0x00"), 0).doubleValue(), 0.0000001);
        assertEquals(0.0, Sensor.RainAll.convertValue(stringToByteArray("0xFF 0xFE 0xFF 0xFF"), 0).doubleValue(), 0.0000001);
        assertEquals(0.0, Sensor.RainAll.convertValue(stringToByteArray("0xFF 0x00 0x00 0x00"), 0).doubleValue(), 0.0000001);
        assertEquals(0.0, Sensor.RainAll.convertValue(stringToByteArray("0xFE 0xFF 0xFF 0xFF"), 0).doubleValue(), 0.0000001);
        assertEquals(0.0, Sensor.RainAll.convertValue(stringToByteArray("0x80 0x00 0x00 0x00"), 0).doubleValue(), 0.0000001);
    }

    @Test
    public void testLightness() throws IOException {
        assertEquals(      0.0, Sensor.Lightness.convertValue(stringToByteArray("0x00 0x00 0x00 0x00"), 0).doubleValue(), 0.0000001);
        assertEquals(      0.1, Sensor.Lightness.convertValue(stringToByteArray("0x00 0x00 0x00 0x01"), 0).doubleValue(), 0.0000001);
        assertEquals(     25.5, Sensor.Lightness.convertValue(stringToByteArray("0x00 0x00 0x00 0xFF"), 0).doubleValue(), 0.0000001);
        assertEquals(     25.6, Sensor.Lightness.convertValue(stringToByteArray("0x00 0x00 0x01 0x00"), 0).doubleValue(), 0.0000001);
        assertEquals(   6553.5, Sensor.Lightness.convertValue(stringToByteArray("0x00 0x00 0xFF 0xFF"), 0).doubleValue(), 0.0000001);
        assertEquals(   6553.6, Sensor.Lightness.convertValue(stringToByteArray("0x00 0x01 0x00 0x00"), 0).doubleValue(), 0.0000001);
        assertEquals(1677721.4, Sensor.Lightness.convertValue(stringToByteArray("0x00 0xFF 0xFF 0xFE"), 0).doubleValue(), 0.0000001);
        assertEquals(      0.0, Sensor.Lightness.convertValue(stringToByteArray("0x00 0xFF 0xFF 0xFF"), 0).doubleValue(), 0.0000001);
        assertEquals(      0.0, Sensor.Lightness.convertValue(stringToByteArray("0x01 0x00 0x00 0x00"), 0).doubleValue(), 0.0000001);
        assertEquals(      0.0, Sensor.Lightness.convertValue(stringToByteArray("0x7F 0xFF 0xFF 0xFF"), 0).doubleValue(), 0.0000001);

        assertEquals(0.0, Sensor.Lightness.convertValue(stringToByteArray("0xFF 0xFF 0xFF 0xFF"), 0).doubleValue(), 0.0000001);
        assertEquals(0.0, Sensor.Lightness.convertValue(stringToByteArray("0xFF 0xFF 0xFF 0xFE"), 0).doubleValue(), 0.0000001);
        assertEquals(0.0, Sensor.Lightness.convertValue(stringToByteArray("0xFF 0xFF 0xFF 0x00"), 0).doubleValue(), 0.0000001);
        assertEquals(0.0, Sensor.Lightness.convertValue(stringToByteArray("0xFF 0xFF 0xFE 0xFF"), 0).doubleValue(), 0.0000001);
        assertEquals(0.0, Sensor.Lightness.convertValue(stringToByteArray("0xFF 0xFF 0x00 0x00"), 0).doubleValue(), 0.0000001);
        assertEquals(0.0, Sensor.Lightness.convertValue(stringToByteArray("0xFF 0xFE 0xFF 0xFF"), 0).doubleValue(), 0.0000001);
        assertEquals(0.0, Sensor.Lightness.convertValue(stringToByteArray("0xFF 0x00 0x00 0x00"), 0).doubleValue(), 0.0000001);
        assertEquals(0.0, Sensor.Lightness.convertValue(stringToByteArray("0xFE 0xFF 0xFF 0xFF"), 0).doubleValue(), 0.0000001);
        assertEquals(0.0, Sensor.Lightness.convertValue(stringToByteArray("0x80 0x00 0x00 0x00"), 0).doubleValue(), 0.0000001);
    }
    
    @Test
    public void testUvRaw() throws IOException {
        assertEquals(    0.0, Sensor.UvRaw.convertValue(stringToByteArray("0x00 0x00"), 0).doubleValue(), 0.0000001);
        assertEquals(    1.0, Sensor.UvRaw.convertValue(stringToByteArray("0x00 0x01"), 0).doubleValue(), 0.0000001);
        assertEquals(  218.0, Sensor.UvRaw.convertValue(stringToByteArray("0x00 0xda"), 0).doubleValue(), 0.0000001);
        assertEquals(  511.0, Sensor.UvRaw.convertValue(stringToByteArray("0x01 0xFF"), 0).doubleValue(), 0.0000001);
        assertEquals(32766.0, Sensor.UvRaw.convertValue(stringToByteArray("0x7F 0xFE"), 0).doubleValue(), 0.0000001);
        assertEquals(    0.0, Sensor.UvRaw.convertValue(stringToByteArray("0x7F 0xFF"), 0).doubleValue(), 0.0000001);
        assertEquals(    0.0, Sensor.UvRaw.convertValue(stringToByteArray("0xFF 0xFF"), 0).doubleValue(), 0.0000001);
        assertEquals(    0.0, Sensor.UvRaw.convertValue(stringToByteArray("0xFA 0x00"), 0).doubleValue(), 0.0000001);
        assertEquals(    0.0, Sensor.UvRaw.convertValue(stringToByteArray("0x80 0x01"), 0).doubleValue(), 0.0000001);
        assertEquals(    0.0, Sensor.UvRaw.convertValue(stringToByteArray("0x80 0x00"), 0).doubleValue(), 0.0000001);
    }
    
    @Test
    public void testUv() throws IOException {
        assertEquals(  0.0, Sensor.UvIdxRaw.convertValue(stringToByteArray("0x00"), 0).doubleValue(), 0.0000001);
        assertEquals(  1.0, Sensor.UvIdxRaw.convertValue(stringToByteArray("0x01"), 0).doubleValue(), 0.0000001);
        assertEquals(127.0, Sensor.UvIdxRaw.convertValue(stringToByteArray("0x7F"), 0).doubleValue(), 0.0000001);
        assertEquals(  0.0, Sensor.UvIdxRaw.convertValue(stringToByteArray("0x80"), 0).doubleValue(), 0.0000001);
        assertEquals(  0.0, Sensor.UvIdxRaw.convertValue(stringToByteArray("0xFB"), 0).doubleValue(), 0.0000001);
        assertEquals(  0.0, Sensor.UvIdxRaw.convertValue(stringToByteArray("0xFF"), 0).doubleValue(), 0.0000001);
    }
}
