package com.pawix25.mockvolt;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.ComponentActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.scottyab.rootbeer.RootBeer;

import java.io.IOException;

public class MainActivity extends ComponentActivity {

    private SeekBar seekBar;
    private TextView textView;
    private Button applyButton;
    private Button resetButton;
    private Button customLevelButton;
    private Switch chargingSwitch;
    private ImageView githubIcon;
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
        customLevelButton = findViewById(R.id.customLevelButton);
        chargingSwitch = findViewById(R.id.chargingSwitch);
        githubIcon = findViewById(R.id.githubIcon);

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

        // Show custom level dialog when Custom Level button is clicked
        customLevelButton.setOnClickListener(view -> showCustomLevelDialog());

        // Toggle charging state when Switch is changed
        chargingSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> toggleCharging(isChecked));

        // Open GitHub profile when GitHub icon is clicked
        githubIcon.setOnClickListener(view -> openGitHubProfile());

        // Update GitHub icon based on theme
        updateGitHubIcon();
    }

    private void showCustomLevelDialog() {
        // Create an EditText for user input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);

        new AlertDialog.Builder(this)
                .setTitle("Set Custom Battery Level")
                .setMessage("Enter a battery level (0-100):")
                .setView(input)
                .setPositiveButton("Set", (dialog, which) -> {
                    // Get user input and set battery level
                    String inputText = input.getText().toString();
                    try {
                        int level = Integer.parseInt(inputText);
                        if (level >= 0 && level <= 100) {
                            setBatteryLevel(level);
                        } else {
                            Toast.makeText(this, "Please enter a value between 0 and 100.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Invalid input. Please enter a valid number.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
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

    private void toggleCharging(boolean isCharging) {
        if (!rootBeer.isRooted()) {
            Toast.makeText(this, "Device is not rooted or root access not granted.", Toast.LENGTH_LONG).show();
            return;
        }

        String command = isCharging ? "dumpsys battery set ac 1" : "dumpsys battery unplug";
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"su", "-c", command});
            process.waitFor();
            String message = isCharging ? "Simulating charging." : "Simulating battery unplugged.";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to toggle charging state.", Toast.LENGTH_SHORT).show();
        }
    }

    private void openGitHubProfile() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/pawix25"));
        startActivity(intent);
    }

    private void updateGitHubIcon() {
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            // Dark mode
            githubIcon.setImageResource(R.drawable.github_light);
        } else {
            // Light mode
            githubIcon.setImageResource(R.drawable.github);
        }
    }
}
