package com.example.ventasrovianda.Utils;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.dantsu.escposprinter.exceptions.EscPosBarcodeException;
import com.dantsu.escposprinter.exceptions.EscPosConnectionException;
import com.dantsu.escposprinter.exceptions.EscPosEncodingException;
import com.dantsu.escposprinter.exceptions.EscPosParserException;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Set;
import java.util.UUID;

public class PrinterUtil {

    BluetoothDevice bluetoothDevice = null;
    BluetoothSocket socket;
    OutputStream outputStream;
    InputStream inputStream;

    BluetoothAdapter bluetoothAdapter;
    Context context;

    public PrinterUtil(Context context) {
        this.context = context;
    }

    public Set<BluetoothDevice> findDevices() {

        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        return pairedDevices;
    }

    public boolean connectWithPrinter(BluetoothDevice bluetoothDevice) {

        if (this.bluetoothDevice != null && socket.isConnected()) {
            return true;
        }
        /*this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.bluetoothAdapter.startDiscovery();
        Set<BluetoothDevice> devices= bluetoothAdapter.getBondedDevices();
        for(BluetoothDevice ecmp : devices){
                if(ecmp.getName().indexOf("EC MP-2")!=-1){
                    this.bluetoothDevice=ecmp;
                }
        }*/
        this.bluetoothDevice = bluetoothDevice;
        if(this.bluetoothDevice!=null) {
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // uuig generico para servicios de conexion y transmision de datos (estandar)
            try {
                //Method m = bluetoothDevice.getClass().getMethod("createRfcommSocket", new Class<?>[] {Integer.TYPE}); // prueba de obtencion de metodos de socket de dispositivo
                socket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
                // obtencion del socket
                if (bluetoothAdapter != null && bluetoothAdapter.isDiscovering()) {
                    bluetoothAdapter.cancelDiscovery();
                }
                // inicio de conexion al socket
                //Toast.makeText(this.context,"CONECTADO",Toast.LENGTH_SHORT).show();
                //outputStream = socket.getOutputStream(); // setteo del output stream
                //inputStream = socket.getInputStream();   // setteo del input stream
                //beginListenForData();
                return true;
            } catch (IOException e) {
                desconect();
                return false;
            }
        }else{

            return false;
        }
        //return  true;
    }


    String value = "";
    public void IntentPrint(String txtValue)  { // metodo que inicia la conexion con la impresora modelo EC MP-2

        //Boolean connected = connectWithPrinter();
        /*if(!socket.isConnected() && this.bluetoothDevice!=null){
            try {
                socket.connect();
                outputStream = socket.getOutputStream();
            }catch (IOException e){

            }
           //System.out.println("conectado: "+connectWithPrinter(this.bluetoothDevice));
        }*/

        /*EscPosPrinter printer = null;
        try {
            BluetoothConnection bluetoothConnection = new BluetoothConnection(this.bluetoothDevice);
            printer = new EscPosPrinter(bluetoothConnection.connect(), 203, 96f, 32);
        } catch (EscPosConnectionException e) {
            e.printStackTrace();
        }*/

        //byte[] buffer = txtValue.getBytes();

        //PrintHeader[3] = (byte) buffer.length; // se crea un header para la impresora , aqui se especifica el valor esperado en bytes
       /* try {
            printer.printFormattedText(txtValue);
            printer.printFormattedText(txtValue);

        } catch (EscPosConnectionException e) {
            e.printStackTrace();
        } catch (EscPosParserException e) {
            e.printStackTrace();
        } catch (EscPosEncodingException e) {
            e.printStackTrace();
        } catch (EscPosBarcodeException e) {
            e.printStackTrace();
        }*/

/*       if(PrintHeader.length>128){ // si el valor es mayor a 128 bytes, solo se imprime en toast (esto es de pruebas), ya que los valores
            //reales varian de tamaño
            value+="\n Value is more than 128 size\n";

        }else{*/

        if(this.bluetoothDevice!=null) {
            try {
                byte[] format = {27, 33, 0};

                socket.connect();
                // format[2] = ((byte)(0x10));
                outputStream = socket.getOutputStream();

                outputStream.write(format);
                outputStream.write(txtValue.getBytes(Charset.forName("UTF-8")));
                // escribe datos en el outputs stream del socket de la impresora
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            sleep(5000);
                            outputStream.flush();
                            outputStream.close();                    // una vez escritos los datos se cierra el output stream
                            socket.close();
                        } catch (InterruptedException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                // se cierra el socket
            } catch (Exception ex) {
                value += ex.toString() + "\n" + "Except Intent print \n";
                System.out.println("ERROR: " + ex.getMessage());

            }
        }
     //   }
    }

    public void desconect(){
        this.bluetoothDevice=null;
        try {
            if(this.socket!=null && this.socket.isConnected()) {
                this.socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected(){
        return this.socket.isConnected();
    }
}

