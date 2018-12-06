package hr.foi.air1802.mranger;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class FancyKontroleActivity extends AppCompatActivity {

    ImageButton gumbDisconnect;
    ImageButton gumbForward;
    ImageButton gumbLeft;
    ImageButton gumbRight;
    ImageButton gumbBackwards;
    ImageButton gumbSporo;
    ImageButton gumbNormalno;
    ImageButton gumbBrzo;

    //temperatura
    ImageButton gumbTemperatura;
    ImageButton gumbPohraniTemperaturu;
    TextView textViewTemperatura;

    StringBuilder messages;
    /**
     * Metoda koja čeka pritisak na gumb za povratak na prethodni zaslon.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Controls.Disconnect(FancyKontroleActivity.this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fancy_kontrole);

        Intent newint = getIntent();
        messages = new StringBuilder();
        Controls.address = newint.getStringExtra(Bluetooth.EXTRA_ADDRESS);
        new ConnectBT(getApplicationContext(),FancyKontroleActivity.this).execute(); //konekcija
        gumbDisconnect = findViewById(R.id.imageButtonOdspoji);

        //kretanje
        gumbForward = findViewById(R.id.imageButtonGore);
        gumbLeft = findViewById(R.id.imageButtonLijevo);
        gumbRight = findViewById(R.id.imageButtonDesno);
        gumbBackwards = findViewById(R.id.imageButtonDolje);

        //brzine
        gumbSporo = findViewById(R.id.imageButtonSporo);
        gumbNormalno = findViewById(R.id.imageButtonNormalno);
        gumbBrzo = findViewById(R.id.imageButtonBrzo);

        //boje gumbova brzina
        gumbNormalno.setImageResource(R.drawable.normalno_stisnuto);

        //temperatura
        gumbTemperatura = findViewById(R.id.imageButtonTemperatura);
        gumbPohraniTemperaturu=findViewById(R.id.imageButtonDodajTemp);
        textViewTemperatura = findViewById(R.id.textViewTemperatura);
        textViewTemperatura.setTextColor(Color.WHITE);

        /**
         * Metoda koja se aktivira na pritisak gumba za prekidanje bluetooth veze.
         */
        gumbDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Controls.Disconnect(FancyKontroleActivity.this);
            }
        });
        /**
         *  Metoda koja se aktivira na pritisak gumba za kretanje unaprijed.
         */
        gumbForward.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return Controls.moveForward(event);
            }


        });

        /**
         * Metoda koja se aktivira na pritisak gumba za kretanje ulijevo.
         */
        gumbLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return Controls.moveLeft(event);
            }
        });

        /**
         * Metoda koja se aktivira na pritisak gumba za kretanje udesno.
         */
        gumbRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return Controls.moveRight(event);
            }
        });

        /**
         * Metoda koja se aktivira na pritisak gumba za kretanje unazad.
         */
        gumbBackwards.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return  Controls.moveBackwards(event);
            }
        });

        /**
         * Metoda koja se aktivira na pritisak gumba za promijenu brzine robota na sporo.
         */
        gumbSporo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Controls.changeSpeed(120, 120);

                gumbSporo.setImageResource(R.drawable.sporo_stisnuto);
                gumbNormalno.setImageResource(R.drawable.normalno);
                gumbBrzo.setImageResource(R.drawable.brzo);

            }
        });

        /**
         * Metoda koja se aktivira na pritisak gumba za promijenu brzine robota na normalno.
         */
        gumbNormalno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Controls.changeSpeed(180, 180);

                gumbSporo.setImageResource(R.drawable.sporo);
                gumbNormalno.setImageResource(R.drawable.normalno_stisnuto);
                gumbBrzo.setImageResource(R.drawable.brzo);
            }
        });

        /**
         * Metoda koja se aktivira na pritisak gumba za promijenu brzine robota na brzo.
         */
        gumbBrzo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Controls.changeSpeed(240, 240);

                gumbSporo.setImageResource(R.drawable.sporo);
                gumbNormalno.setImageResource(R.drawable.normalno);
                gumbBrzo.setImageResource(R.drawable.brzo_stisnuto);
            }
        });

        /**
         * Metoda koja se aktivira na pritisak gumba za očitanje temperature.
         */
        gumbTemperatura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewTemperatura.setText(Controls.getTemperature());
            }
        });

        /**
         * Metoda koja se aktivira na pritisak gumba za pohranjivanje temperature u bazu.
         */
        gumbPohraniTemperaturu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Controls.insertTemperatueToDB(getApplicationContext());
            }
        });
    }
}
