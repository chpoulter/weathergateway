package de.poulter.weathergateway.station;

import java.util.EnumSet;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

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
    WindDirection(10, 2, Unit.DEGREE, (data, pos) -> (double) StationBinaryTools.fromTwoBytes(data, pos)),
    WindSpeed(11, 2, Unit.SPEED, SensorFunctions.WINDSPEED),
    GustyWindSpeed(12, 2, Unit.SPEED, SensorFunctions.WINDSPEED),
    RainHour(14, 4, Unit.MM, SensorFunctions.RAIN),
    RainDay(16, 4, Unit.MM, SensorFunctions.RAIN),
    RainWeek(17, 4, Unit.MM, SensorFunctions.RAIN),
    RainMonth(18, 4, Unit.MM, SensorFunctions.RAIN),
    RainYear(19, 4, Unit.MM, SensorFunctions.RAIN),
    RainAll(20, 4, Unit.MM, SensorFunctions.RAIN),
    Lightness(21, 4, Unit.LUX, (data, pos) -> StationBinaryTools.fromFourBytes(data, pos) / 10.0),
    UvRaw(22, 2, Unit.UWM2, (data, pos) -> (double) StationBinaryTools.fromTwoBytes(data, pos)),
    UvIdxRaw(23, 1, Unit.NONE, (data, pos) -> (double) StationBinaryTools.fromByte(data, pos)),
    ;
    
    private static final Map<Integer, Sensor> SENSORBYID = EnumSet.allOf(Sensor.class).stream().collect(Collectors.toMap(s -> s.id, s -> s));
    
    private int id;
    private Unit unit;
    private int size;
    BiFunction<byte[], Integer, Double> function;

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
            double value = (double) StationBinaryTools.fromTwoBytes(data, pos);
            
            return value / 10.0;
        };
        
        private static final BiFunction<byte[], Integer, Double> HUMIDITY = (data, pos) -> { 
            double value = (double) StationBinaryTools.fromByte(data, pos);
            
            return value;
        };
        
        private static final BiFunction<byte[], Integer, Double> PRESSURE = (data, pos) -> { 
            double value = (double) StationBinaryTools.fromTwoBytes(data, pos);
            
            return value / 10.0;
        };
        
        private static final BiFunction<byte[], Integer, Double> WINDSPEED = (data, pos) -> { 
            double value = (double) StationBinaryTools.fromTwoBytes(data, pos);
            
            // this is m/s, but we prefer km/h
            
            return Math.round(value * 3.6) / 10.0;
        };         

        private static final BiFunction<byte[], Integer, Double> RAIN = (data, pos) -> { 
            double value = (double) StationBinaryTools.fromFourBytes(data, pos);
            
            return value / 10.0;
        };
        
    }
}
