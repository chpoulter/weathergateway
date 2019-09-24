package de.poulter.weathergateway.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.poulter.weathergateway.station.DataService;
import de.poulter.weathergateway.station.Sensor;
import de.poulter.weathergateway.station.Unit;

@RestController
public class WeatherController {

    private static final Logger log = LogManager.getLogger(WeatherController.class);
    
    @Autowired
    private DataService dataService;
    
    @Autowired
    MessageSource messageSource;
    
    @RequestMapping(value = "/weather", method = RequestMethod.GET)
    public Map<String, SensorData> weather() {
        log.info("weather was called.");
        
        Map<Sensor, Double> currentValues = dataService.getCurrentValues();
        
        Map<String, SensorData> data = new HashMap<>();
        for (Sensor sensor : currentValues.keySet()) {
            SensorData sensorData = new SensorData();
            sensorData.setId(sensor.getId());
            sensorData.setName(getName(sensor));
            sensorData.setUnit(getSymbol(sensor.getUnit()));
            sensorData.setValue(currentValues.get(sensor));
            
            data.put(sensor.name(), sensorData);
        }
        
        return data;
    }
    
    private String getName(Sensor sensor) {
        String key = Sensor.class.getName() + "." + sensor.name();
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }
    
    private String getSymbol(Unit unit) {
        String key = Unit.class.getName() + "." + unit.name();
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }
    
}