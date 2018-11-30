package hr.foi.air1802.mranger;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.view.MotionEvent;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Controls {

    public static String address = null;
    public static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static BluetoothAdapter myBluetoothAdapter = null;
    public static BluetoothSocket bluetoothSocket = null;
    public static boolean isBluetoothConnected = false;

    //podaci za kretanje
    private static byte[] cmd = new byte[13];
    public static final int WRITEMODULE = 2;
    public static final int type = 5;
    private static int DesniMotor = 180;
    private static int LijeviMotor = 180;

    private static void move(int lijeviMotor, int desniMotor) {
        cmd[0] = (byte) 0xff;
        cmd[1] = (byte) 0x55;
        cmd[2] = (byte) 8;
        cmd[3] = (byte) 0;
        cmd[4] = (byte) WRITEMODULE;
        cmd[5] = (byte) type;
        final ByteBuffer buf = ByteBuffer.allocate(4);
        buf.putShort((short) lijeviMotor);
        buf.putShort((short) desniMotor);
        buf.position(0);
        // Read back bytes
        final byte b1 = buf.get();
        final byte b2 = buf.get();
        final byte b3 = buf.get();
        final byte b4 = buf.get();
        cmd[6] = b2;
        cmd[7] = b1;
        cmd[8] = b4;
        cmd[9] = b3;
        cmd[10] = (byte) '\n';

        try {
            bluetoothSocket.getOutputStream().write(cmd);
        } catch (IOException e) {
        }
    }

    public static boolean moveForward(MotionEvent event){
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            move(-LijeviMotor, DesniMotor);

        } else if (event.getAction() == MotionEvent.ACTION_UP) {

            move(0, 0);
        }
        return true;
    }

    public static boolean moveLeft(MotionEvent event){
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            move(-LijeviMotor, -DesniMotor);

        } else if (event.getAction() == MotionEvent.ACTION_UP) {

            move(0, 0);
        }
        return true;
    }

    public static boolean moveRight(MotionEvent event){
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            move(LijeviMotor, DesniMotor);

        } else if (event.getAction() == MotionEvent.ACTION_UP) {

            move(0, 0);
        }
        return true;
    }

    public  static  boolean moveBackwards(MotionEvent event){
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            move(LijeviMotor, -DesniMotor);

        } else if (event.getAction() == MotionEvent.ACTION_UP) {

            move(0, 0);
        }
        return true;
    }

    public static void changeSpeed(int desniMotor, int lijeviMotor) {
        DesniMotor = desniMotor;
        LijeviMotor = lijeviMotor;
    }

    public static String getTemperature() {
        String test = "";

        byte[] cmd = new byte[9];
        cmd[0] = (byte) 0xff; //0xff
        cmd[1] = (byte) 0x55; //0x55
        cmd[2] = (byte) 5; //5
        cmd[3] = (byte) 7; //7 indeks na kojem se nalazi senzor
        cmd[4] = (byte) 1; //1
        cmd[5] = (byte) 27; //27 tip uređaja
        cmd[6] = (byte) (13 & 0xff); //13&0xff- mora biti port 13 za senzor
        cmd[7] = (byte) (2 & 0xff); //2&0xff- slot senzora čije uzimamo vrijednosti
        cmd[8] = (byte) '\n'; // '\n'

        try {
            bluetoothSocket.getOutputStream().write(cmd);//uključivanje moda mjerenja temperature
            int byteCount = bluetoothSocket.getInputStream().available();
            while (byteCount == 0) {
                byteCount = bluetoothSocket.getInputStream().available();
            }

            if (byteCount > 0) {
                InputStream inputStream = bluetoothSocket.getInputStream();//uzimanje podataka od robota
                byte[] podaci = new byte[1024];
                int count = inputStream.read(podaci);

                if (podaci != null) {

                    List<Byte> listaBajtova = new ArrayList<>();

                    if (count > 0) {
                        for (int i = 0; i < count; i++) {
                            Byte b = podaci[i];
                            listaBajtova.add(b);
                            Byte[] novaLista = listaBajtova.toArray(new Byte[listaBajtova.size()]);
                            int[] buffer = new int[listaBajtova.size()];
                            for (int j = 0; j < novaLista.length; j++) {
                                test += String.format("%02X ", novaLista[j]);
                                buffer[j] = novaLista[j];
                            }
                            listaBajtova.clear();
                        }
                    }
                }
            }
        } catch (IOException e) {        }

        String[] dijelovi = test.split(" ");
        test = dijelovi[6]; // dohvaćamo sa 6. indeksa buffera
        int temp = Integer.parseInt(test, 16);
        float temperatura = (float) temp / 10;
        test = String.valueOf(temperatura) + " °C";
        return test;
    }

    public static void Disconnect(Activity activity) {
        if (bluetoothSocket != null) //ako smo spojeni
        {
            try {
                bluetoothSocket.close(); //prekini vezu
            } catch (IOException e) {}
        }
        activity.finish();
    }


}
