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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @author Christian Poulter <devel@poulter.de>
 */
@Service
public class SeekerService  {

    private static final Logger log = LogManager.getLogger(SeekerService.class);

    private static final Charset CHARSET = StandardCharsets.ISO_8859_1;
    
    private static final int BROADCAST_TIMEOUT = 2000;
    private static final int POS_HEADER_MESSAGELENGTH = 3;
    
    @Value("${broadcast.port}")
    private Integer broadcastPort;
    
    @Autowired
    private DataService dataService;
    
    @Scheduled(fixedRateString = "${broadcast.rate}", initialDelayString = "${broadcast.initial}")
    public void seekStations() {
        log.info("Looking for stations on local network.");
        
        List<InetAddress> broadcastAddresses = getAllBroadcastAddresses();            
        for (InetAddress broadcastAddress : broadcastAddresses) {
            broadcastToNetwork(broadcastAddress);
        }                   
    }
    
    private void broadcastToNetwork(InetAddress broadcast) {
        log.info("Looking for a weather station on network " + broadcast + " port " + broadcastPort + " for a station.");
        
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setBroadcast(true);
            socket.setSoTimeout(BROADCAST_TIMEOUT);
        
            // request
            DatagramPacket packet = new DatagramPacket(
                StationBinaryTools.COMMAND_SEARCH,
                StationBinaryTools.COMMAND_SEARCH.length,
                broadcast,
                broadcastPort
            );
            socket.send(packet);
            
            // response
            byte[] buf = new byte[256];
            packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            
            int packetLength = packet.getLength();
            byte[] response = packet.getData();
                    
            if (packetLength < (POS_HEADER_MESSAGELENGTH + 1)) {
                log.warn("Reponse to short: " + packetLength + " bytes");
                dataService.clearStation();
                return;
            }
            
            byte[] payload = StationBinaryTools.parse(response, 0x12);
            
            // MAC
            String stationMac = "";
            byte[] stationMacBytes = Arrays.copyOfRange(payload, 0, 6);
            for (int i = 0; i < stationMacBytes.length; i++) stationMac += String.format( (i==0?"":"-")+"%02X", stationMacBytes[i] );

            // IP, Port
            byte[] stationAddressBytes = Arrays.copyOfRange(payload, 6, 10);
            InetAddress stationAddress = InetAddress.getByAddress(stationAddressBytes);
            int port = StationBinaryTools.fromTwoBytesUnsigned(payload, 10);
            
            // name            
            int nameLength = StationBinaryTools.fromByteUnsigned(payload, 12) - 1;
            int nameLengthFromStart = 13 + nameLength;
            
            if (payload.length < nameLengthFromStart) {
                log.warn("Reponse was to short: " + payload.length + " bytes, expected " + nameLengthFromStart + " bytes.");
                dataService.clearStation();
                return;
            }
            String name = new String(Arrays.copyOfRange(payload, 13, nameLengthFromStart), CHARSET);
            
            log.info("Found station \"" + name + "\" at MAC " + stationMac + ", IP " + stationAddress + ":" + port);
            
            dataService.setStation(stationAddress, port);
                
        } catch (SocketException ex) {
            log.warn("Could not create UDP socket.", ex);
            dataService.clearStation();
            return;
            
        } catch (SocketTimeoutException ex) {
            log.warn("No response for request.");
            dataService.clearStation();
            return;

        } catch (IOException ex) {
            log.warn("Error on receiving the response.", ex);
            dataService.clearStation();
            return;
        }       
    }
    
    private List<InetAddress> getAllBroadcastAddresses() {
        List<InetAddress> broadcastList = new ArrayList<>();
        
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();

                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue;
                }

                networkInterface.getInterfaceAddresses()
                    .stream()
                    .map(a -> a.getBroadcast())
                    .filter(Objects::nonNull)
                    .forEach(broadcastList::add);
            }
            
        } catch (SocketException ex) {
            log.error("Could not create a list of possible broadcast addresses.", ex);
        }

        broadcastList = broadcastList.stream().distinct().collect(Collectors.toList());

        return broadcastList;
    }
    
}
