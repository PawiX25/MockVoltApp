# MockVolt Android Application

MockVolt is an Android application designed for developers and testers who need to simulate various battery levels and charging states on a rooted Android device. This app provides an easy-to-use interface for setting custom battery levels, toggling charging states, and viewing the current battery status, all of which can be especially useful for testing apps under different power conditions.

## Features

- **Custom Battery Level Simulation**: Set the battery level to any value between 0% and 100% using a SeekBar or a custom input dialog.
- **Charging State Simulation**: Toggle between charging and unplugged states to test how your app behaves under different power conditions.
- **Battery Status Display**: View real-time battery status, including current level, charging status, and charging source.
- **Root Detection**: The app uses RootBeer to ensure that the device is rooted, which is required for simulating battery levels and charging states.

## How It Works

MockVolt utilizes the `dumpsys` command to manipulate battery settings on your Android device. This command is part of the Android Debug Bridge (ADB) toolset, which allows developers to interact with the system at a low level. Below are the specific commands the app uses:

- **Set Battery Level**:
  ```shell
  dumpsys battery set level <level>
  ```
  This command sets the battery level to a specific value (where `<level>` is a number between 0 and 100).

- **Reset Battery Level**:
  ```shell
  dumpsys battery reset
  ```
  This command resets the battery level and charging state to the system's default values.

- **Toggle Charging State**:
  - To simulate the device being plugged in (charging):
    ```shell
    dumpsys battery set ac 1
    ```

  - To simulate the device being unplugged (not charging):
    ```shell
    dumpsys battery unplug
    ```

These commands require root access, which is why the app checks for root permissions using the RootBeer library before attempting to execute them.

## Installation

This app requires a rooted Android device. You can download and install the APK directly or clone the project and build it using Android Studio.

## Usage

1. **Setting Battery Level**:
   - Use the SeekBar to adjust the battery level. The current level will be displayed in the text view.
   - Press the "Apply" button to set the battery level to the selected value.

2. **Resetting Battery Level**:
   - Press the "Reset" button to restore the battery level to its default state.

3. **Toggling Charging State**:
   - Use the charging switch to simulate charging or unplugged states. The app will display the corresponding charging status.

4. **Viewing Battery Status**:
   - The battery status text view provides real-time updates on the battery's charge level and charging source.

## Requirements

- **Root Access**: This app requires root access to manipulate system-level battery settings. If the device is not rooted, the app will not be able to perform these actions and will notify the user.
- **Android 5.0 (Lollipop) or higher**: The app is compatible with devices running Android Lollipop or later.

## Screenshot

Below is a screenshot of the MockVolt application:

![MockVolt Screenshot](https://github.com/user-attachments/assets/2832311c-962c-43a8-8309-e777574bd5fe)
