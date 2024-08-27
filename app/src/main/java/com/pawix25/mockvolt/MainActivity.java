package com.pawix25.mockvolt;

import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.ComponentActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.scottyab.rootbeer.RootBeer;

import java.io.IOException;

public class MainActivity extends ComponentActivity {

    private SeekBar seekBar;
    private TextView textView;
    private Button applyButton;
    private Button resetButton;
    private RootBeer rootBeer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        ConstraintLayout mainLayout = findViewById(R.id.main);
        seekBar = findViewById(R.id.seekBar);
        textView = findViewById(R.id.textView);
        applyButton = findViewById(R.id.button);
        resetButton = findViewById(R.id.resetButton);

        // Initialize RootBeer
        rootBeer = new RootBeer(this);

        // Adjust padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set initial value for text view
        textView.setText("Battery Level: " + seekBar.getProgress() + "%");

        // Update text view as SeekBar is changed
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textView.setText("Battery Level: " + progress + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Apply battery level when Apply button is clicked
        applyButton.setOnClickListener(view -> setBatteryLevel(seekBar.getProgress()));

        // Reset battery level to default value when Reset button is clicked
        resetButton.setOnClickListener(view -> resetBatteryLevel());
    }

    private void setBatteryLevel(int level) {
        if (!rootBeer.isRooted()) {
            Toast.makeText(this, "Device is not rooted or root access not granted.", Toast.LENGTH_LONG).show();
            return;
        }

        String command = "dumpsys battery set level " + level;
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"su", "-c", command});
            process.waitFor();
            Toast.makeText(this, "Battery level set to " + level + "%", Toast.LENGTH_SHORT).show();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to set battery level.", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetBatteryLevel() {
        if (!rootBeer.isRooted()) {
            Toast.makeText(this, "Device is not rooted or root access not granted.", Toast.LENGTH_LONG).show();
            return;
        }

        // Reset battery to default values
        String command = "dumpsys battery reset";
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"su", "-c", command});
            process.waitFor();
            seekBar.setProgress(50);
            textView.setText("Battery Level: 50%");
            Toast.makeText(this, "Battery level reset to default.", Toast.LENGTH_SHORT).show();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to reset battery level.", Toast.LENGTH_SHORT).show();
        }
    }
}
