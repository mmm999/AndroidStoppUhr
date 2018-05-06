package mutz.wpf14.hs_hannover.de.hausuebungws2014;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class Stoppuhr extends Activity {

    private ToggleButton tglButton;
    private TextView tvDatumUhrzeit;
    private String date;
    private boolean stop = false;

    ToggleButton    tglStartStop;
    Button          btnClear;

    TextView        tvStoppuhr;

    boolean         startStop = false;

    long    anfang = 0;
    long    pause = 0;
    long    dauer = 0;

    Calendar        kalender = Calendar.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stoppuhr);

        tglButton       = (ToggleButton) findViewById(R.id.toggleButton);
        tvDatumUhrzeit  = (TextView)     findViewById(R.id.datumUhrzeit);
        tglStartStop    = (ToggleButton) findViewById(R.id.startStop);
        btnClear        = (Button)       findViewById(R.id.clearButton);
        tvStoppuhr      = (TextView)     findViewById(R.id.stoppuhr);
    }

    public void onToggleClicked(View view){
        boolean on = ((ToggleButton)view).isChecked();

        if (on){
            stop = false;
            Toast.makeText(getApplicationContext(), "On", Toast.LENGTH_SHORT).show();
            getDateandTime();

        }
        else {
            stop = true;
            Toast.makeText(getApplicationContext(), "Off", Toast.LENGTH_SHORT).show();
            tvDatumUhrzeit.setText("");

        }

    }

    private void getDateandTime() {
        Thread dtThread = new Thread (new Runnable() {
            @Override
            public void run() {
                //Funktion ausführen bis ToggleButton wieder ausgeschaltet wird
                while (!stop) {
                    try {
                        runOnUiThread(new Runnable() {

                            //neues Datum einlesen
                            @Override
                            public void run() {
                                // date = new Date().toString();
                                // tvDatumUhrzeit.setText(date.substring(3,date.lastIndexOf("MEZ")-1));
                                String date = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss").format(new Date());
                                tvDatumUhrzeit.setText(date);
                            }
                        });

                        Thread.sleep(1000); //1s warten

                        //Fehlermeldungen abfangen
                    } catch (InterruptedException e) {
                        Log.d("getDateAndTime", "thread interrupted !");
                    }
                }
            }
        });
        dtThread.start();
    }

    public void onStartStopClicked(View view)
    {
        startStop = ((ToggleButton) view).isChecked();

        if (startStop)
        {
            anfang = new Date().getTime();
            runStoppuhr();
        }else{
            pause = dauer;
        }


    }

    public void onClearClicked(View view)
    {
        startStop = false;
        dauer = anfang = pause = 0;
        tglStartStop.setChecked(false);
        tvStoppuhr.setText("00:00:00:0");
    }

    private String getDauerFormatiert()
    {
        dauer = new Date().getTime() - anfang + pause;
        kalender.setTimeInMillis(dauer);

        return String.format("%02d:%02d:%02d:%1d",
                kalender.get(Calendar.HOUR_OF_DAY) - 1,
                kalender.get(Calendar.MINUTE),
                kalender.get(Calendar.SECOND),
                kalender.get(Calendar.MILLISECOND)/100);
    }

    private void runStoppuhr()
    {
        Thread suThread = new Thread (new Runnable() {

            @Override
            public void run() {

                while (startStop){
                    try{
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                tvStoppuhr.setText(""+getDauerFormatiert());

                            }
                        });

                        Thread.sleep(100);
                    }catch (InterruptedException e){
                        Log.d("runStoppuhr", "thread interrrupted !");
                    }
                }
            }
        });
        suThread.start();
    }

}
