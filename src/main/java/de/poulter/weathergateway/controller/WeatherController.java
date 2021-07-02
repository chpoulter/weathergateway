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

package de.poulter.weathergateway.controller;

import java.util.Map;
import java.util.TreeMap;

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

/**
 * @author Christian Poulter <devel@poulter.de>
 */
@RestController
public class WeatherController {

    private static final Logger log = LogManager.getLogger(WeatherController.class);
    
    @Autowired
    private DataService dataService;
    
    @Autowired
    MessageSource messageSource;
    
    @RequestMapping(value = "/weather", method = RequestMethod.GET)
    public Map<String, SensorData> weather() {
        log.info("REST: /weather");
        
        Map<Sensor, Double> currentValues = dataService.getCurrentValues();
        
        Map<String, SensorData> data = new TreeMap<>();
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