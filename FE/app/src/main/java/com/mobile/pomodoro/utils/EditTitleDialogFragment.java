package com.mobile.pomodoro.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class EditTitleDialogFragment extends DialogFragment {

    public interface OnTitleEditListener {
        void onTitleChanged(String newTitle);
    }

    private OnTitleEditListener listener;
    private String currentTitle;

    public EditTitleDialogFragment(String currentTitle, OnTitleEditListener listener) {
        this.currentTitle = currentTitle;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        EditText editText = new EditText(getContext());
        editText.setText(currentTitle);
        editText.setSelectAllOnFocus(true);

        return new AlertDialog.Builder(requireContext())
                .setTitle("Chỉnh sửa tiêu đề task")
                .setView(editText)
                .setPositiveButton("OK", (dialog, which) -> {
                    String newTitle = editText.getText().toString().trim();
                    if (!newTitle.isEmpty() && listener != null) {
                        listener.onTitleChanged(newTitle);
                    }
                })
                .setNegativeButton("Hủy", null)
                .create();
    }
}