package com.example.yusuf.beaconcheck.util;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.example.yusuf.beaconcheck.Constants.CHARACTERISTIC_ECHO_STRING;
import static com.example.yusuf.beaconcheck.Constants.CHARACTERISTIC_TIME_STRING;
import static com.example.yusuf.beaconcheck.Constants.CLIENT_CONFIGURATION_DESCRIPTOR_SHORT_ID;
import static com.example.yusuf.beaconcheck.Constants.SERVICE_STRING;

public class BluetoothUtils {

    // Characteristics

    public static List<BluetoothGattCharacteristic> findCharacteristics(BluetoothGatt bluetoothGatt) {
        List<BluetoothGattCharacteristic> matchingCharacteristics = new ArrayList<>();

        List<BluetoothGattService> serviceList = bluetoothGatt.getServices();
        BluetoothGattService service = BluetoothUtils.findService(serviceList);
        if (service == null) {
            return matchingCharacteristics;
        }

        List<BluetoothGattCharacteristic> characteristicList = service.getCharacteristics();
        for (BluetoothGattCharacteristic characteristic : characteristicList) {
            if (isMatchingCharacteristic(characteristic)) {
                matchingCharacteristics.add(characteristic);
            }
        }

        return matchingCharacteristics;
    }

    @Nullable
    public static BluetoothGattCharacteristic findEchoCharacteristic(BluetoothGatt bluetoothGatt) {
        return findCharacteristic(bluetoothGatt, CHARACTERISTIC_ECHO_STRING);
    }

    @Nullable
    public static BluetoothGattCharacteristic findTimeCharacteristic(BluetoothGatt bluetoothGatt) {
        return findCharacteristic(bluetoothGatt, CHARACTERISTIC_TIME_STRING);
    }

    @Nullable
    private static BluetoothGattCharacteristic findCharacteristic(BluetoothGatt bluetoothGatt, String uuidString) {
        List<BluetoothGattService> serviceList = bluetoothGatt.getServices();
        BluetoothGattService service = BluetoothUtils.findService(serviceList);
        if (service == null) {
            return null;
        }

        List<BluetoothGattCharacteristic> characteristicList = service.getCharacteristics();
        for (BluetoothGattCharacteristic characteristic : characteristicList) {
            if (characteristicMatches(characteristic, uuidString)) {
                return characteristic;
            }
        }

        return null;
    }

    public static boolean isEchoCharacteristic(BluetoothGattCharacteristic characteristic) {
        return characteristicMatches(characteristic, CHARACTERISTIC_ECHO_STRING);
    }

    public static boolean isTimeCharacteristic(BluetoothGattCharacteristic characteristic) {
        return characteristicMatches(characteristic, CHARACTERISTIC_TIME_STRING);
    }

    private static boolean characteristicMatches(BluetoothGattCharacteristic characteristic, String uuidString) {
        if (characteristic == null) {
            return false;
        }
        UUID uuid = characteristic.getUuid();
        return uuidMatches(uuid.toString(), uuidString);
    }

    private static boolean isMatchingCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (characteristic == null) {
            return false;
        }
        UUID uuid = characteristic.getUuid();
        return matchesCharacteristicUuidString(uuid.toString());
    }

    private static boolean matchesCharacteristicUuidString(String characteristicIdString) {
        return uuidMatches(characteristicIdString, CHARACTERISTIC_ECHO_STRING, CHARACTERISTIC_TIME_STRING);
    }

    public static boolean requiresResponse(BluetoothGattCharacteristic characteristic) {
        return (characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)
                != BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE;
    }

    public static boolean requiresConfirmation(BluetoothGattCharacteristic characteristic) {
        return (characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_INDICATE)
                == BluetoothGattCharacteristic.PROPERTY_INDICATE;
    }

    // Descriptor

    @Nullable
    public static BluetoothGattDescriptor findClientConfigurationDescriptor(List<BluetoothGattDescriptor> descriptorList) {
        for(BluetoothGattDescriptor descriptor : descriptorList) {
            if (isClientConfigurationDescriptor(descriptor)) {
                return descriptor;
            }
        }

        return null;
    }

    private static boolean isClientConfigurationDescriptor(BluetoothGattDescriptor descriptor) {
        if (descriptor == null) {
            return false;
        }
        UUID uuid = descriptor.getUuid();
        String uuidSubstring = uuid.toString().substring(4, 8);
        return uuidMatches(uuidSubstring, CLIENT_CONFIGURATION_DESCRIPTOR_SHORT_ID);
    }

    // Service

    private static boolean matchesServiceUuidString(String serviceIdString) {
        return uuidMatches(serviceIdString, SERVICE_STRING);
    }

    @Nullable
    private static BluetoothGattService findService(List<BluetoothGattService> serviceList) {
        for (BluetoothGattService service : serviceList) {
            String serviceIdString = service.getUuid()
                    .toString();
            if (matchesServiceUuidString(serviceIdString)) {
                return service;
            }
        }
        return null;
    }

    // String matching

    // If manually filtering, substring to match:
    // 0000XXXX-0000-0000-0000-000000000000
    private static boolean uuidMatches(String uuidString, String... matches) {
        for (String match : matches) {
            if (uuidString.equalsIgnoreCase(match)) {
                return true;
            }
        }

        return false;
    }
}
