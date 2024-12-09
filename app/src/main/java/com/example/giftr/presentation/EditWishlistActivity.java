package com.example.giftr.presentation;

import static com.example.giftr.business.entities.Wishlist.WISHLIST_TIMESTAMP;
import static com.example.giftr.presentation.CreateWishlistActivity.REQUEST_CODE_CALENDAR;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;
import com.example.giftr.R;
import com.example.giftr.business.SharedUser;
import com.example.giftr.business.entities.User;
import com.example.giftr.business.entities.Wishlist;
import com.example.giftr.persistence.UserDAO;
import com.example.giftr.persistence.WishlistDAO;
import com.example.giftr.persistence.swaggerApi.SwaggerUserDAO;
import com.example.giftr.persistence.swaggerApi.SwaggerWishlistDAO;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import io.github.muddz.styleabletoast.StyleableToast;

public class EditWishlistActivity extends AppCompatActivity {

    private ImageView ivImage;
    private EditText etName;
    private EditText etDescription;
    private EditText etDate;
    private Button bEdit;
    private Wishlist originalWishlist;
    private String datePicked;
    private SharedUser sharedUser;
    private String[] friendEmails;
    private Wishlist newWishlist;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_wishlist);

        sharedUser = SharedUser.getInstance(EditWishlistActivity.this);
        originalWishlist = (Wishlist) getIntent().getSerializableExtra(Wishlist.WISHLIST_TAG);

        setupView();
    }

    private void setupView() {
        ivImage = findViewById(R.id.editwishlist_ivImage);
        etName = findViewById(R.id.editwishlist_etName);
        etDescription = findViewById(R.id.editwishlist_etDescription);
        etDate = findViewById(R.id.editwishlist_etDate);
        bEdit = findViewById(R.id.editwishlist_bEdit);
        etName.setText(originalWishlist.getName());
        etDescription.setText(originalWishlist.getDescription());
        datePicked = originalWishlist.getEndDate();

        String endDateString = originalWishlist.getEndDate();
        String outputFormatPattern = "MMMM d, yyyy";

        try {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat inputFormat = new SimpleDateFormat(WISHLIST_TIMESTAMP);

            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat outputFormat = new SimpleDateFormat(outputFormatPattern);

            Date endDate = inputFormat.parse(endDateString);

            assert endDate != null;
            String formattedEndDate = outputFormat.format(endDate);

            etDate.setText(formattedEndDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        bEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etName.getText().toString().isEmpty() || etDescription.getText().toString().isEmpty() || datePicked == null) {
                    if (etName.getText().toString().isEmpty())
                        etName.setError(getString(R.string.field_required));
                    if (etDescription.getText().toString().isEmpty())
                        etDescription.setError(getString(R.string.field_required));
                } else {
                    Wishlist wishlist = new Wishlist(etName.getText().toString(), etDescription.getText().toString(), sharedUser.getLoggedUser().getId(), new LinkedList<>(), null, datePicked);
                    wishlist.setWishlistID(originalWishlist.getID());

                    WishlistDAO wishlistDAO = new SwaggerWishlistDAO();
                    wishlistDAO.editWishlistByID(wishlist, sharedUser.getUserAccessToken(), EditWishlistActivity.this, new WishlistDAO.WishlistResponseCallback() {
                        @Override
                        public void onSuccess(Wishlist response) {
                            newWishlist = response;
                            StyleableToast.makeText(EditWishlistActivity.this, getString(R.string.toast_changed_applied), R.style.changesApplied).show();

                            showCalendarDialog();
                        }

                        @Override
                        public void onError(VolleyError error) {
                            error.printStackTrace();
                        }
                    });
                }
            }
        });

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Parse the timestamp into date components
        SimpleDateFormat dateFormat = new SimpleDateFormat(WISHLIST_TIMESTAMP, Locale.getDefault());
        Date parsedDate;
        try {
            parsedDate = dateFormat.parse(originalWishlist.getEndDate());
            Calendar originalCalendar = Calendar.getInstance();
            originalCalendar.setTime(parsedDate);

            year = originalCalendar.get(Calendar.YEAR);
            month = originalCalendar.get(Calendar.MONTH);
            day = originalCalendar.get(Calendar.DAY_OF_MONTH);
        } catch (ParseException e) {
            e.printStackTrace();
            // Handle the error if the timestamp is not in the correct format
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.CustomDatePickerDialogTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar selectedCalendar = Calendar.getInstance();
                selectedCalendar.set(Calendar.YEAR, year);
                selectedCalendar.set(Calendar.MONTH, monthOfYear);
                selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                selectedCalendar.set(Calendar.HOUR_OF_DAY, 23);
                selectedCalendar.set(Calendar.MINUTE, 59);
                selectedCalendar.set(Calendar.SECOND, 59);

                TimeZone timeZone = TimeZone.getTimeZone("GMT+2:00");

                SimpleDateFormat inputFormat = new SimpleDateFormat(WISHLIST_TIMESTAMP);
                SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM d, yyyy");

                inputFormat.setTimeZone(timeZone);
                outputFormat.setTimeZone(timeZone);

                datePicked = inputFormat.format(selectedCalendar.getTime());
                String formattedDate = outputFormat.format(selectedCalendar.getTime());

                etDate.setText(formattedDate);
            }
        }, year, month, day);

        // Set the minimum date to today to restrict selection to future dates
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());

        datePickerDialog.show();
    }

    private void loadFriends() {
        UserDAO userDAO = new SwaggerUserDAO();
        userDAO.getUserFriendsById(sharedUser.getLoggedUser().getId(), sharedUser.getUserAccessToken(), EditWishlistActivity.this, new UserDAO.UserListResponseCallback() {
            @Override
            public void onSuccess(List<User> response) {
                friendEmails = new String[response.size()];

                for (int i = 0; i < response.size(); i++) {
                    friendEmails[i] = response.get(i).getEmail();
                }

                addSpecialDateToCalendar();
            }

            @Override
            public void onError(VolleyError error) {

            }
        });
    }

    private void addSpecialDateToCalendar() {
        // Parse the timestamp into date and time components
        SimpleDateFormat dateFormat = new SimpleDateFormat(WISHLIST_TIMESTAMP, Locale.getDefault());
        Date parsedDate;
        try {
            parsedDate = dateFormat.parse(newWishlist.getEndDate());
        } catch (ParseException e) {
            e.printStackTrace();
            return; // Return or handle the error if the timestamp is not in the correct format
        }

        // Create a Calendar instance and set the desired date and time
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(parsedDate);

        // Create a new event
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setData(CalendarContract.Events.CONTENT_URI);
        intent.putExtra(CalendarContract.Events.TITLE, newWishlist.getName());
        intent.putExtra(CalendarContract.Events.DESCRIPTION, newWishlist.getDescription());
        intent.putExtra(CalendarContract.Events.ALL_DAY, true);
        intent.putExtra(Intent.EXTRA_EMAIL, friendEmails);

        // Set the flag to ensure the calendar activity is started in a new task
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // Check if the action can be done
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CODE_CALENDAR);
        }
    }

    private void showCalendarDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.calendar_saveWishlist));
        builder.setMessage(getString(R.string.calendar_askForConfirmation));
        builder.setPositiveButton(getString(R.string.calendar_accept), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked "Yes"
                loadFriends();
            }
        });
        builder.setNegativeButton(getString(R.string.calendar_deny), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked "No"
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CALENDAR && resultCode == Activity.RESULT_OK) {
            // Handle the result when the user returns from the calendar activity
            finish();
        }
    }

}

