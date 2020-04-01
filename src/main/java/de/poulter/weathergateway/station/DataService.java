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
                    throw new IOException("Not enough data");
                }
                
                int len = StationBinaryTools.fromTwoBytes(first, 3) - 3;
                byte[] all = new byte[len + 5];                
                read = inputStream.read(all, 5, len);                
                if (read < len) {
                    throw new IOException("Not enough data: " + len + "(" + StationBinaryTools.byteArrayToString(first) + ")");
                }
                
                System.arraycopy(first, 0, all, 0, first.length);
                byte[] payload = StationBinaryTools.parse(all, 0x0b, 0x04);
                
                for (int pos = 0; pos < payload.length; ) {
                    int sensorType = StationBinaryTools.fromByte(payload, pos++);
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

    
    
    
    
//    @Scheduled(fixedRate=10000)
//    public void cw() {
//        log.info("--- Werte ---------");
//        for (Sensor sensor : currentValues.keySet().stream().sorted().collect(Collectors.toList())) {
//            log.info("Sensor " + sensor.getName() + ": " + currentValues.get(sensor) + " " + sensor.getEinheit().getSymbol() + ".");
//        }
//        log.info("-------------------");
//    }
}
