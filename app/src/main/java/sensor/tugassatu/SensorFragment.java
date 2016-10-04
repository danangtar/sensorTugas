package sensor.tugassatu;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * A placeholder fragment containing a simple view.
 */
public class SensorFragment extends Fragment implements SensorEventListener{
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String TAG = "Sensor";

//    private recAccmeter AccmeterTask = null;

    private File file;
    private File f;
    private String time;
    private Date date;

    private boolean ifrec = false;

    private SensorManager sm;
    private Sensor light;
    private Sensor accmeter;

    private TextView li;
    private TextView Tview_x;
    private TextView Tview_y;
    private TextView Tview_z;
    private TextView recStat;

    private Button btnRec;

    private float li_val;

    public SensorFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static SensorFragment newInstance(int sectionNumber) {
        SensorFragment fragment = new SensorFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sm = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        accmeter = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        light = sm.getDefaultSensor(Sensor.TYPE_LIGHT);
        isStoragePermissionGranted();
        f= new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "Sensor");
        f.mkdirs();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sensor, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        li = (TextView)view.findViewById(R.id.light_output);
        Tview_x = (TextView)view.findViewById(R.id.output_x);
        Tview_y = (TextView)view.findViewById(R.id.output_y);
        Tview_z = (TextView)view.findViewById(R.id.output_z);
        recStat = (TextView)view.findViewById(R.id.rec_stat);

        btnRec = (Button) view.findViewById(R.id.start_rec);
        btnRec.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(ifrec) {
                    recStat.setVisibility(View.INVISIBLE);
                    btnRec.setText(R.string.start_rec);
                    ifrec = false;
                }
                else {
                    date=new Date();
                    time=date.toString();
                    file=new File(f,time+".csv");

                    recStat.setVisibility(View.VISIBLE);
                    btnRec.setText(R.string.stop_rec);
                    ifrec = true;
                }

//                try {
//                    AccmeterTask = new recAccmeter();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                AccmeterTask.execute((Void) null);
            }

        });
    }

    @Override
    public void onSensorChanged(SensorEvent event){
        if(event.sensor.getType() == Sensor.TYPE_LIGHT){
            li_val = event.values[0];
            String li_str = String.valueOf(li_val);
            li.setText(li_str);
//            Log.d(TAG, "Light: " + li_str);
        }
        else if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            String val_x = String.format("%.03f",event.values[0]);
            String val_y = String.format("%.03f",event.values[1]);
            String val_z = String.format("%.03f",event.values[2]);
            Tview_x.setText(val_x);
            Tview_y.setText(val_y);
            Tview_z.setText(val_z);
//            Log.d(TAG, "x: " + val_x + " y: " + val_y + " z: " + val_z);

            if(ifrec) {
                if(li_val == 0) {
                    Log.d(TAG, "Light: " + String.valueOf(li_val) +
                            " x: " + val_x + " y: " + val_y + " z: " + val_z);
                    String dat = val_x+","+val_y+","+val_z+"\n";
                    try {
                        FileWriter fw=new FileWriter(file,true);
                        fw.append(dat);
                        fw.flush();
                        fw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onResume() {
        super.onResume();
        if(!ifrec) {
            sm.registerListener(this, light, SensorManager.SENSOR_DELAY_NORMAL);
            sm.registerListener(this, accmeter, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(!ifrec) {
            sm.unregisterListener(this);
        }
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getActivity().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }
//
//    public class recAccMeter extends AsyncTask<Void, Void, Boolean> {
//        @Override
//        protected Boolean doInBackground(Void... voids) {
//            try {
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return false;
//        }
//
//        @Override
//        protected void onPostExecute(final Boolean success) {
//            AccmeterTask = null;
//
//            if (!success) {
//
//            }
//        }
//    }
}