package com.example.startopenapp.display_manager;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Build;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

public class TimeHelper {
    public static String getFormattedTime() {
        // Lấy thời gian hiện tại
        LocalDateTime currentTime = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            currentTime = LocalDateTime.now();
        }

        // Định dạng thời gian theo định dạng mong muốn
        DateTimeFormatter formatter = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");
        }

        String formattedTime = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            formattedTime = currentTime.format(formatter);
        }

        return formattedTime;
    }

    public static String pickTime(Context context, TextView selectedTimeTextView) {
        Calendar currentTime = Calendar.getInstance();
        currentTime.add(Calendar.MINUTE, 30);

        // Thời gian kết thúc là 21:30 của ngày hiện tại
        Calendar endTime = Calendar.getInstance();
        endTime.set(Calendar.HOUR_OF_DAY, 21);
        endTime.set(Calendar.MINUTE, 30);

        TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                (view, hourOfDay, minute) -> {
                    Calendar selectedTime = Calendar.getInstance();
                    selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedTime.set(Calendar.MINUTE, minute);

                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    if (selectedTime.after(currentTime) && selectedTime.before(endTime)) {
                        String selectedTimeString = sdf.format(selectedTime.getTime());
                        selectedTimeTextView.setText(selectedTimeString);
                    } else {
                        String time = sdf.format(currentTime.getTime());
                        Toast.makeText(context, "Thời gian không hợp lệ. Vui lòng chọn lại thời gian từ " + time + " đến 21:30.",
                                Toast.LENGTH_SHORT).show();
                        // Nếu thời gian không hợp lệ, trả về null
                        selectedTimeTextView.setText("");
                    }
                }, currentTime.get(Calendar.HOUR_OF_DAY), currentTime.get(Calendar.MINUTE), true);

        timePickerDialog.setOnDismissListener(dialog -> {
            // Trả về giá trị thời gian đã chọn nếu không rỗng
            if (!selectedTimeTextView.getText().toString().isEmpty()) {
                Toast.makeText(context, "Thời gian đã chọn: " + selectedTimeTextView.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        timePickerDialog.show();

        // Trả về giá trị mặc định của selectedTimeTextView
        return selectedTimeTextView.getText().toString().isEmpty() ? null : selectedTimeTextView.getText().toString();
    }
    public static boolean isWithinValidTime() {
        Calendar currentTime = Calendar.getInstance();
        int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
        int currentMinute = currentTime.get(Calendar.MINUTE);

        // Kiểm tra nếu thời gian hiện tại từ 7 giờ sáng đến 21 giờ 29 phút
        if ((currentHour >= 7 && currentHour < 21) || (currentHour == 21 && currentMinute <= 30)) {
            return true;
        } else {
            return false;
        }
    }
}
