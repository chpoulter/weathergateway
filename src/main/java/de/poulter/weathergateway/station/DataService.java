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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @author Christian Poulter <devel@poulter.de>
 */
@Service
public class DataService implements InitializingBean {

    private static final Logger log = LogManager.getLogger(DataService.class);
    
    private final String SYNC_IPPORT = "SYNC_IPPORT";

    private Map<Sensor, Double> currentValues = new HashMap<>();    
    private InetAddress ip;
    private Integer port;
    
    @Value("${data.timeout}")
    private Integer socketTimeout;
        
    @Override
    public void afterPropertiesSet() throws Exception {
        this.ip = null;
        this.port = null;
        
        for (Sensor sensor : Sensor.values()) {
            currentValues.put(sensor, 0.0);
        }
    }

    public void setStation(InetAddress ip, int port) {
        synchronized(SYNC_IPPORT) {
            this.ip = ip;
            this.port = port;
        }
    }
    
    public void clearStation() {
        synchronized(SYNC_IPPORT) {
            this.ip = null;
            this.port = null;
        }
    }
    
    public Map<Sensor, Double> getCurrentValues() {
        return Collections.unmodifiableMap(currentValues);
    }
    
    @Scheduled(fixedRateString = "${data.rate}", initialDelayString = "${data.initial}")
    public void fetchData() {
        log.info("Refreshing data from station.");
        
        InetAddress ip = null;
        Integer port = null;        
        synchronized(SYNC_IPPORT) {
            ip = this.ip;
            port = this.port;
        }
        
        if ((ip != null) && (port != null)) {
            fetchData(ip, port);
        }        
    }

    private void fetchData(InetAddress ip, Integer port) {
        SocketAddress addr = new InetSocketAddress(ip, port);
        
        log.info("Connecting to station " + ip + ":" + port + ".");
        
        try (Socket socket = new Socket()) {
            socket.connect(addr, socketTimeout);
            socket.setSoTimeout(socketTimeout);
            
            try (InputStream inputStream = socket.getInputStream();
                 OutputStream outputStream = socket.getOutputStream()
            ) {                
                outputStream.write(StationBinaryTools.COMMAND_DATA);
                
                byte[] first = new byte[5];
                int read = inputStream.read(first);
                
                if (read < 5) {
                    log.warn("Not enough data: " + read + " bytes");
                    throw new IOException("Not enough data");
                }
                
                int len = StationBinaryTools.fromTwoBytesUnsigned(first, 3) - 3;
                byte[] all = new byte[len + 5];                
                read = inputStream.read(all, 5, len);                
                if (read < len) {
                    log.warn("Not enough data: " + len + "(" + StationBinaryTools.byteArrayToString(first) + ")");
                    throw new IOException("Not enough data");
                }
                
                System.arraycopy(first, 0, all, 0, first.length);
                byte[] payload = StationBinaryTools.parse(all, 0x0b, 0x04);
                
                for (int pos = 0; pos < payload.length; ) {
                    int sensorType = StationBinaryTools.fromByteUnsigned(payload, pos++);
                    Sensor sensor = Sensor.getSensor(sensorType);
                    
                    if (sensor != null) {
                        Double value = sensor.convertValue(payload, pos);
                        pos += sensor.getSize();
                        currentValues.put(sensor, value);
                        
                    } else {
                        
                        log.warn("Unknown sensor index: " + sensorType);
                    }
                }
                
            } catch (IOException ex) {
                log.warn("Error while communication with station.", ex);
            }
            
        } catch (IOException ex) {
            log.warn("Unable to create socket.", ex);
        }
    }
    
//    This would dump current data to logfile.
//    
//    @Scheduled(fixedRate=10000)
//    public void cw() {
//        log.info("--- Werte ---------");
//        for (Sensor sensor : currentValues.keySet().stream().sorted().collect(Collectors.toList())) {
//            log.info("Sensor " + sensor.name() + ": " + currentValues.get(sensor) + " " + sensor.getUnit() + ".");
//        }
//        log.info("-------------------");
//    }
}
