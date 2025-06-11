package com.mobile.pomodoro.utils.Settings;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

// các cài đặt về lựa chọn thời gian của các mode
public class TimeSelection {

    private Context context;
    private OnTimeSelectedListener listener;
    private final Integer[] focusTimeOptions = {5, 10, 25, 30, 40, 50, 60};
    private final Integer[] shortBreakTimeOptions = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    private final Integer[] longBreakTimeOptions = {10, 15, 20, 25, 30};

    public interface OnTimeSelectedListener {
        void onFocusTimeSelected(int minutes);

        void onShortBreakTimeSelected(int minutes);

        void onLongBreakTimeSelected(int minutes);
    }

    public TimeSelection(Context context) {
        this.context = context;
    }

    public void setOnTimeSelectedListener(OnTimeSelectedListener listener) {
        this.listener = listener;
    }

    public void showFocusTimeDialog(int currentValue) {
        showTimeSelectionDialog(
                "Select Focus Time",
                "Choose your focus work duration. Recommended: 25-45 minutes per session.",
                focusTimeOptions,
                currentValue,
                selectedTime -> {
                    if (listener != null) {
                        listener.onFocusTimeSelected(selectedTime);
                    }
                }
        );
    }

    public void showShortBreakTimeDialog(int currentValue) {
        showTimeSelectionDialog(
                "Select Short Break Time",
                "Choose short break duration between work sessions. Recommended: 5-10 minutes.",
                shortBreakTimeOptions,
                currentValue,
                selectedTime -> {
                    if (listener != null) {
                        listener.onShortBreakTimeSelected(selectedTime);
                    }
                }
        );
    }

    public void showLongBreakTimeDialog(int currentValue) {
        showTimeSelectionDialog(
                "Select Long Break Time",
                "Choose long break duration after 4 work sessions. Recommended: 15-30 minutes.",
                longBreakTimeOptions,
                currentValue,
                selectedTime -> {
                    if (listener != null) {
                        listener.onLongBreakTimeSelected(selectedTime);
                    }
                }
        );
    }

    private void showTimeSelectionDialog(String title, String message, Integer[] timeOptions,
                                         int currentValue, TimeSelectedCallback callback) {

        ListView listView = new ListView(context);
        TimeOptionAdapter adapter = new TimeOptionAdapter(timeOptions, currentValue);
        listView.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setView(listView)
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            int selectedTime = timeOptions[position];
            callback.onTimeSelected(selectedTime);
            dialog.dismiss();
        });

        dialog.show();
    }

    private class TimeOptionAdapter extends BaseAdapter {
        private Integer[] timeOptions;
        private int currentValue;

        public TimeOptionAdapter(Integer[] timeOptions, int currentValue) {
            this.timeOptions = timeOptions;
            this.currentValue = currentValue;
        }

        @Override
        public int getCount() {
            return timeOptions.length;
        }

        @Override
        public Object getItem(int position) {
            return timeOptions[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView;

            if (convertView == null) {
                textView = new TextView(context);
                textView.setPadding(40, 30, 40, 30);
                textView.setTextSize(16);
            } else {
                textView = (TextView) convertView;
            }

            int timeValue = timeOptions[position];
            textView.setText(timeValue + " minutes");

            if (timeValue == currentValue) {
                textView.setBackgroundColor(Color.parseColor("#E3F2FD"));
                textView.setTextColor(Color.parseColor("#1976D2"));
                textView.setText("✓ " + timeValue + " minutes (current");
            } else {
                textView.setBackgroundColor(Color.TRANSPARENT);
                textView.setTextColor(Color.parseColor("#333333"));
            }

            return textView;
        }
    }

    private interface TimeSelectedCallback {
        void onTimeSelected(int time);
    }
}