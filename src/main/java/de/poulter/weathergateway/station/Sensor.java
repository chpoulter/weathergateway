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

import java.util.EnumSet;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Christian Poulter <devel@poulter.de>
 */
public enum Sensor {
    
    RoomTemperature(1, 2, Unit.TEMPERATURE, SensorFunctions.TEMPERATURE),
    OutsideTemperature(2, 2, Unit.TEMPERATURE, SensorFunctions.TEMPERATURE),
    DewPoint(3, 2, Unit.TEMPERATURE, SensorFunctions.TEMPERATURE),
    ApparentAirTemperature(4, 2, Unit.TEMPERATURE, SensorFunctions.TEMPERATURE),
    TemperatureHumidityIndex(5, 2, Unit.TEMPERATURE, SensorFunctions.TEMPERATURE),
    RoomHumidity(6, 1, Unit.HUMIDITY, SensorFunctions.HUMIDITY),
    OutsideHumidity(7, 1, Unit.HUMIDITY, SensorFunctions.HUMIDITY),
    BarometricPressureAbsolute(8, 2, Unit.PRESSURE, SensorFunctions.PRESSURE),
    BarometricPressureRelative(9, 2, Unit.PRESSURE, SensorFunctions.PRESSURE),
    WindDirection(10, 2, Unit.DEGREE, SensorFunctions.WINDDIRECTION),
    WindSpeed(11, 2, Unit.SPEED, SensorFunctions.WINDSPEED),
    GustyWindSpeed(12, 2, Unit.SPEED, SensorFunctions.WINDSPEED),
    RainHour(14, 4, Unit.MM, SensorFunctions.RAIN),
    RainDay(16, 4, Unit.MM, SensorFunctions.RAIN),
    RainWeek(17, 4, Unit.MM, SensorFunctions.RAIN),
    RainMonth(18, 4, Unit.MM, SensorFunctions.RAIN),
    RainYear(19, 4, Unit.MM, SensorFunctions.RAIN),
    RainAll(20, 4, Unit.MM, SensorFunctions.RAIN),
    Lightness(21, 4, Unit.LUX, SensorFunctions.LIGHTNESS),
    UvRaw(22, 2, Unit.UWM2, SensorFunctions.UVRAW),
    UvIdxRaw(23, 1, Unit.NONE, SensorFunctions.UV),
    ;
    
	private static final Logger log = LogManager.getLogger(Sensor.class);
    private static final Map<Integer, Sensor> SENSORBYID = EnumSet.allOf(Sensor.class).stream().collect(Collectors.toMap(s -> s.id, s -> s));
    
    private int id;
    private Unit unit;
    private int size;
    private BiFunction<byte[], Integer, Double> function;

    private Sensor(int id, int size, Unit einheit, BiFunction<byte[], Integer, Double> function) {
        this.id = id;
        this.unit = einheit;
        this.size = size;
        this.function = function;
    }

    public static Sensor getSensor(int id) {
        return SENSORBYID.get(id);
    }
    
    public Double convertValue(byte[] data2, int pos2) {
        return function.apply(data2, pos2);
    }
    
    public int getId() {
        return id;
    }

    public Unit getUnit() {
        return unit;
    }

    public int getSize() {
        return size;
    }
 
    private static class SensorFunctions {

        private static final BiFunction<byte[], Integer, Double> TEMPERATURE = (data, pos) -> {
            int value = StationBinaryTools.fromTwoBytesSigned(data, pos);

            if (value == 32767) {
                log.warn("Invalid temperature: " + value);
                return 0.0;
            }

            return (double) value / 10.0;
        };

        private static final BiFunction<byte[], Integer, Double> HUMIDITY = (data, pos) -> {
            int value = StationBinaryTools.fromByteSigned(data, pos);

            if (value < 0 || value > 100) {
                log.warn("Invalid humidity: " + value);
                return 0.0;
            }

            return (double) value;
        };

        private static final BiFunction<byte[], Integer, Double> PRESSURE = (data, pos) -> {
            int value = StationBinaryTools.fromTwoBytesSigned(data, pos);

            if (value < 0 || value == 32767) {
                log.warn("Invalid pressure: " + value);
                return 0.0;
            }

            return (double) value / 10.0;
        };

        private static final BiFunction<byte[], Integer, Double> WINDSPEED = (data, pos) -> {
            int value = StationBinaryTools.fromTwoBytesSigned(data, pos);

            if (value < 0 || value == 32767) {
                log.warn("Invalid windspeed: " + value);
                return 0.0;
            }

            // this is m/s, but we prefer km/h
            return Math.round((double) value * 3.6) / 10.0;
        };

        private static final BiFunction<byte[], Integer, Double> WINDDIRECTION = (data, pos) -> {
            int value = StationBinaryTools.fromTwoBytesSigned(data, pos);

            if (value < 0 || value > 360) {
                log.warn("Invalid winddirection: " + value);
                return 0.0;
            }

            return (double) value;
        };

        private static final BiFunction<byte[], Integer, Double> RAIN = (data, pos) -> {
            int value = StationBinaryTools.fromFourBytesSigned(data, pos);

            if (value < 0 || value > 16777214) {
                log.warn("Invalid rain: " + value);
                return 0.0;
            }

            return (double) value / 10.0;
        };

        private static final BiFunction<byte[], Integer, Double> LIGHTNESS = (data, pos) -> {
            int value = StationBinaryTools.fromFourBytesSigned(data, pos);

            if (value < 0 || value > 16777214) {
                log.warn("Invalid lightness: " + value);
                return 0.0;
            }

            return (double) value / 10.0;
        };

        private static final BiFunction<byte[], Integer, Double> UVRAW = (data, pos) -> {
            int value = StationBinaryTools.fromTwoBytesSigned(data, pos);

            if (value < 0 || value == 32767) {
                log.warn("Invalid uv raw: " + value);
                return 0.0;
            }

            return (double) value;
        };

        private static final BiFunction<byte[], Integer, Double> UV = (data, pos) -> {
            int value = StationBinaryTools.fromByteSigned(data, pos);

            if (value < 0) {
                log.warn("Invalid uv: " + value);
                return 0.0;
            }

            return (double) value;
        };

    }
}
