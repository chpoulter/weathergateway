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
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author Christian Poulter <devel@poulter.de>
 */
public abstract class StationBinaryTools {

    private static final byte MAGIC1 = (byte) 0xFF;
    private static final byte MAGIC2 = (byte) 0xFF;
    
    public static final byte[] COMMAND_SEARCH = createCommand(0x12);
    public static final byte[] COMMAND_VERSION = createCommand(0x50);
    public static final byte[] COMMAND_DATA = createCommand(0x0b, 0x04);

    // int
    public static byte[] createCommand(int payload1) {
        byte[] dataAsArray = { (byte) payload1 };
        return createCommand(dataAsArray); 
    }

    public static byte[] createCommand(int payload1, int payload2) {
        byte[] payload2asBytearray = {(byte)payload2};
        return createCommand((byte) payload1, payload2asBytearray);
    }
    
    public static byte[] createCommand(int payload1, int[] payload2) {
        return createCommand((byte) payload1, intToByteArray(payload2)); 
    }

    // byte
    public static byte[] createCommand(byte[] payload) {
        int length = payload.length + 3;
        
        byte[] command = new byte[payload.length + 5];
        command[0] = MAGIC1;
        command[1] = MAGIC2;
        System.arraycopy(payload, 0, command, 2, payload.length);
        putInteger(command, (2 + payload.length), length);
        command[4 + payload.length] = crc(command, 2, payload.length + 2);
        
        return command;
    }
    
    public static byte[] createCommand(byte payload1, byte[] payload2) {
        int length = payload2.length + 5;
        
        byte[] command = new byte[payload2.length + 7];
        command[0] = MAGIC1;
        command[1] = MAGIC2;
        command[2] = payload1;
        putInteger(command, 3, length);
        System.arraycopy(payload2, 0, command, 5, payload2.length);
        command[5 + payload2.length] = crc(payload2, 0, payload2.length);
        command[6 + payload2.length] = crc(command, 2, command.length - 3);
        
        return command;
    }
    
    public static void putInteger(byte[] data, int pos, int value) {
        data[pos] = (byte) (value >> 8);
        data[pos + 1] = (byte) (value & 0xFF);        
    }
    
    public static byte crc(byte[] data, int start, int length) {
        byte crc = 0;
        for (int i = start; i < (start + length); i++) {
            crc += data[i];
        }       
        
        return crc;
    }
    
    public static String byteArrayToString(byte[] data) {
        String[] byteAsString = new String[data.length];
        for (int i = 0; i < data.length; i++) {
            byteAsString[i] = String.format("0x%02x", data[i]);
        }
        
        return Arrays.stream(byteAsString).collect(Collectors.joining(" "));
    }    
    
    public static byte[] stringToByteArray(String s) {
        String[] sp = s.split(" ");
        byte[] data = new byte[sp.length];
        
        for (int i = 0; i < sp.length; i++) {
            data[i] = Integer.decode(sp[i]).byteValue();
        }
        
        return data;
    }
    
    public static byte[] intToByteArray(int[] data) {
        byte[] result = new byte[data.length];
        for (int i=0; i < data.length; i++) result[i] = (byte) data[i];
        return result;
    }
    
    public static int fromByteUnsigned(byte[] data, int pos) {
        return data[pos] & 0xFF;
    }
    
    public static int fromByteSigned(byte[] data, int pos) {
        return data[pos];
    }

    public static int fromTwoBytesUnsigned(byte[] data, int pos) {
    	return (fromByteUnsigned(data, pos) << 8) | 
    			fromByteUnsigned(data, pos + 1);
    }
    
    public static int fromTwoBytesSigned(byte[] data, int pos) {
    	return (fromByteSigned(data, pos) << 8) | 
    			fromByteUnsigned(data, pos + 1);
    }

    public static int fromFourBytesSigned(byte[] data, int pos) {
    	return (fromByteSigned(data, pos) << 24) |
    	       (fromByteUnsigned(data, pos + 1) << 16) |
    	       (fromByteUnsigned(data, pos + 2) << 8) |
    		    fromByteUnsigned(data, pos + 3);
    }    
    
    public static byte[] parse(byte[] data, int expectedCommand, int expectedSubCommand) throws IOException {
        if ((data == null) || (data.length < 8)) {
            throw new IOException("Message is to short: " + data.length);
        }
        
        // magic
        int magic = StationBinaryTools.fromTwoBytesUnsigned(data, 0);            
        if (magic != 0xFFFF) {
            throw new IOException("Invalid magic values: " + Integer.toHexString(magic));
        }
        
        // command
        int command = StationBinaryTools.fromByteUnsigned(data, 2);            
        if (command != expectedCommand) {
            throw new IOException("Invalid command: " + Integer.toHexString(command) + " <-> " + Integer.toHexString(expectedCommand));
        }
        
        // message length
        int messageLength = StationBinaryTools.fromTwoBytesUnsigned(data, 3);
        if (data.length < (messageLength + 2)) {
            throw new IOException("Message to short: " + data.length + ", expected " + (messageLength + 2));
        }
        
        if (messageLength < 6) {
            throw new IOException("Declared message length is to small: " + messageLength);
        }

        // subCommand
        int subCommand = StationBinaryTools.fromByteUnsigned(data, 5);            
        if (subCommand != expectedSubCommand) {
            throw new IOException("Invalid sub command: " + Integer.toHexString(subCommand) + " <-> " + Integer.toHexString(expectedSubCommand));
        }
        
        int payloadLength = messageLength - 6; 
        byte[] payload = new byte[payloadLength];
        System.arraycopy(data, 6, payload, 0, payload.length);
        
        int crcPos = 6 + payloadLength;
        byte payloadCrc = crc(data, 5, (crcPos - 5));
        byte dataCrc = crc(data, 2, (crcPos - 1));
        byte dataPayloadCrc = data[crcPos];
        byte dataDataCrc = data[crcPos + 1];
        
        if (payloadCrc != dataPayloadCrc) {
            throw new IOException("Payload crc does not match: " + Integer.toHexString(payloadCrc & 0xFF) + " <-> " + Integer.toHexString(dataPayloadCrc & 0xFF));
        }
        
        if (dataCrc != dataDataCrc) {
            throw new IOException("Data crc does not match: " + Integer.toHexString(dataCrc & 0xFF) + " <-> " + Integer.toHexString(dataDataCrc & 0xFF));
        }
     
        return payload;
    }
    
    public static byte[] parse(byte[] data, int expectedCommand) throws IOException {
        if ((data == null) || (data.length < 6)) {
            throw new IOException("Message is to short: " + data.length);
        }
        
        // magic
        int magic = StationBinaryTools.fromTwoBytesUnsigned(data, 0);            
        if (magic != 0xFFFF) {
            throw new IOException("Invalid magic values: " + Integer.toHexString(magic));
        }
        
        // command
        int command = StationBinaryTools.fromByteUnsigned(data, 2);            
        if (command != expectedCommand) {
            throw new IOException("Invalid command: " + Integer.toHexString(command) + " <-> " + Integer.toHexString(expectedCommand));
        }
        
        // message length
        int messageLength = StationBinaryTools.fromTwoBytesUnsigned(data, 3);
        if (data.length < messageLength) {
            throw new IOException("Message to short: " + data.length + ", expected " + (messageLength + 2));
        }
        
        if (messageLength < 4) {
            throw new IOException("Declared message length is to small: " + messageLength);
        }
        
        int payloadLength = messageLength - 7; 
        byte[] payload = new byte[payloadLength];
        System.arraycopy(data, 5, payload, 0, payload.length);
        
        int crcPos = 6 + payloadLength;
        byte dataCrc = crc(data, 2, crcPos - 2);
        byte dataDataCrc = data[crcPos];
        
        if (dataCrc != dataDataCrc) {
            throw new IOException("Data crc does not match: " + Integer.toHexString(dataCrc & 0xFF) + " <-> " + Integer.toHexString(dataDataCrc & 0xFF));
        }
     
        return payload;
    }
    
}
