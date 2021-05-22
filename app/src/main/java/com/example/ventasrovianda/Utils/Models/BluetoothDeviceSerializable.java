package com.example.ventasrovianda.Utils.Models;

import android.bluetooth.BluetoothDevice;

import java.io.Serializable;

public class BluetoothDeviceSerializable implements Serializable {

    private BluetoothDevice bluetoothDevice;
    private boolean printerConnected;


    public boolean isPrinterConnected() {
        return printerConnected;
    }

    public void setPrinterConnected(boolean printerConnected) {
        this.printerConnected = printerConnected;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }
}
