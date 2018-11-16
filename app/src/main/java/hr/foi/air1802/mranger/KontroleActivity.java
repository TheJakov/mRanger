package hr.foi.air1802.mranger;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.UUID;

public class KontroleActivity extends AppCompatActivity {

    String address = null;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    BluetoothAdapter myBluetoothAdapter = null;
    BluetoothSocket bluetoothSocket = null;

    private boolean isBluetoothConnected = false;

    Button gumbDisconnect;
    Button gumbIdiNaprijed;


    InputStream inputStream = null;
    String incomingMessage;
    StringBuilder messages;

    //za kretanje podaci
    byte[] cmd = new byte[13];
    public static final int WRITEMODULE = 2;
    public static final int type=5;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Disconnect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kontrole);
        Intent newint = getIntent();
        messages = new StringBuilder();
        address = newint.getStringExtra(MainActivity.EXTRA_ADDRESS);
        gumbDisconnect = findViewById(R.id.gumbDisconnect);
        gumbIdiNaprijed = findViewById(R.id.gumbIdiNaprijed);

        new ConnectBT().execute();

        gumbDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Disconnect();
            }
        });

        gumbIdiNaprijed.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Toast.makeText(getApplicationContext(),"Stisnuto",Toast.LENGTH_SHORT).show();

                    Kretanje(-180,180);

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    Toast.makeText(getApplicationContext(),"Pusteno",Toast.LENGTH_SHORT).show();

                    Kretanje(0,0);

                }
                return true;
            }


        });
    }

    private void Kretanje(int lijeviMotor,int desniMotor)
    {
        cmd[0]=(byte) 0xff;
        cmd[1]=(byte) 0x55;
        cmd[2]=(byte) 8;
        cmd[3]=(byte) 0;
        cmd[4]=(byte) WRITEMODULE;
        cmd[5]=(byte) type;
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
        cmd[10]=(byte) '\n';

        try{
            bluetoothSocket.getOutputStream().write(cmd);
        }catch(IOException e){

        }
    }

    //metoda za odspajanje
    private void Disconnect(){
        if (bluetoothSocket!=null) //ako smo spojeni
        {
            try
            {
                bluetoothSocket.close(); //prekini vezu
            }
            catch (IOException e)
            { }
        }
        finish();
    }

    //OVO NAS ZANIMA -> VIDLI NA INTERNETU (KINEZ NA git/makeblock-official.cc)
    Thread ListenInput=new Thread(){
        @Override
        public void run() {
            try {
                inputStream = bluetoothSocket.getInputStream();
                byte[] buffer = new byte[1024];
                int bytes;
                while (true) {
                    // Read from the InputStream
                    try {
                        if(inputStream ==null)
                        {
                            Log.d("","InputStream is null");
                        }
                        bytes = inputStream.read(buffer);
                        incomingMessage = new String(buffer, 0, bytes);
                        messages.append(incomingMessage);
                    } catch (IOException e) {
                        Log.d("","Oh no, silent crash !");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };


    public class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //ako smo došli do ovdje, skoro smo se spojili

        @Override
        protected void onPreExecute()
        {
            Toast.makeText(getApplicationContext(),"Povezivanje....",Toast.LENGTH_LONG).show(); //dok traje spajanje
        }

        @Override
        protected Void doInBackground(Void... devices) //varijabilan broj parametara, dobro je to tako
        {
            try
            {
                if (bluetoothSocket == null || !isBluetoothConnected) //ako nismo spojeni
                {
                    ActivityCompat.requestPermissions(KontroleActivity.this,new String[]{Manifest.permission.BLUETOOTH},1);
                    ActivityCompat.requestPermissions(KontroleActivity.this,new String[]{Manifest.permission.BLUETOOTH_ADMIN},1);
                    ActivityCompat.requestPermissions(KontroleActivity.this,new String[]{Manifest.permission.BLUETOOTH_PRIVILEGED},1);

                    myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();//dohvati naš bluetooth uređaj

                    BluetoothDevice bluetoothRobot = myBluetoothAdapter.getRemoteDevice(address);

                    //spaja se na adresu uređaja i provjerava da li je slobodna
                    bluetoothSocket = bluetoothRobot.createInsecureRfcommSocketToServiceRecord(myUUID);//stvara RFCOMM (SPP) vezu
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    bluetoothSocket.connect();//počinje spajanje
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//ako se nismo uspjeli spojiti
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //nakon pokušaja spajanja, provjeravamo da li je sve u redu
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                Toast.makeText(getApplicationContext(),"Povezivanje neuspješno",Toast.LENGTH_SHORT).show();
                finish();
            }
            else
            {
                Toast.makeText(getApplicationContext(),"Povezivanje uspješno",Toast.LENGTH_SHORT).show();
                isBluetoothConnected = true;
            }

        }
    }
}
