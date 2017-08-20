package project.fertilizerandwatercontrol;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;


public class SetTimeFragment extends Fragment {
    private TimePicker timePicker;
    private Button morning, evening;
    private EditText volume;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_set_time, container, false);
        timePicker = (TimePicker) view.findViewById(R.id.timePicker);
        morning = (Button) view.findViewById(R.id.morning);
        evening = (Button) view.findViewById(R.id.evening);
        volume = (EditText) view.findViewById(R.id.volume);
        getActivity().setTitle("Auto Fertilization");
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        morning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SetTimeFragment.this.getActivity(), "Morning Set: " + timePicker.getCurrentHour() + ":" +
                                timePicker.getCurrentMinute() + System.getProperty("line.separator") + " Volume: " + volume.getText() + " mL",
                        Toast.LENGTH_SHORT).show();
            }
        });

        evening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SetTimeFragment.this.getActivity(), "Evening Set: " + timePicker.getCurrentHour() + ":" +
                                timePicker.getCurrentMinute() + System.getProperty("line.separator") + " Volume: " + volume.getText() + " mL",
                        Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
