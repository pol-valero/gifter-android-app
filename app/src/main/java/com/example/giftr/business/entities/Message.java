package com.example.giftr.business.entities;

import android.annotation.SuppressLint;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class Message{
    private int id;
    private String content;
    private int senderID;
    private int receiverID;
    private String timeStamp;

    // Constructor to get the message from the API.
    public Message(int id, String content, int senderID, int receiverID, String timeStamp) {
        this.id = id;
        this.content = content;
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.timeStamp = timeStamp;
    }

    public Message(String content, int senderID, int receiverID) {
        this.content = content;
        this.senderID = senderID;
        this.receiverID = receiverID;
    }

    public String getContent() {
        return content;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getSentTime(boolean sendHour) {
        String formattedDate;

        // Parse the time stamp string
        LocalDateTime dateTime = LocalDateTime.parse(timeStamp, DateTimeFormatter.ISO_DATE_TIME);

        // Convert to the system's default time zone
        LocalDateTime localDateTime = dateTime.atZone(ZoneId.of("UTC"))
                .withZoneSameInstant(ZoneId.systemDefault())
                .toLocalDateTime();

        LocalDate currentDate = LocalDate.now();

        // Compare the date with the current date
        if (localDateTime.toLocalDate().isEqual(currentDate)) {
            if (sendHour) {
                // The date is today, so display only the time in HH:mm:ss format
                formattedDate = localDateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
            } else {
                // The date is today, so display only the time in HH:mm:ss format
                formattedDate = "today";
            }
        } else {
            // Calculate the number of days between the dates
            long daysPassed = ChronoUnit.DAYS.between(localDateTime.toLocalDate(), currentDate);

            // Display the number of days ago
            if (daysPassed == 1) {
                formattedDate = "yesterday";
            }
            else {
                formattedDate = (daysPassed + " days ago");
            }
        }

        return formattedDate;
    }

    public String getHourSent() {
        // Parse the time stamp string
        LocalDateTime dateTime = LocalDateTime.parse(timeStamp, DateTimeFormatter.ISO_DATE_TIME);

        // Convert to the system's default time zone
        LocalDateTime localDateTime = dateTime.atZone(ZoneId.of("UTC"))
                .withZoneSameInstant(ZoneId.systemDefault())
                .toLocalDateTime();

        return localDateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public JSONObject toJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("content", content);
        jsonObject.put("user_id_send", senderID);
        jsonObject.put("user_id_recived", receiverID);

        return jsonObject;
    }

    // Transform a JSONObject into a Message instance.
    public static Message fromJson(JSONObject jsonObject) {
        int id;
        String content;
        int senderID;
        int receiverID;
        String timeStamp;

        try {
            id = jsonObject.getInt("id");
            content = jsonObject.getString("content");
            senderID = jsonObject.getInt("user_id_send");
            receiverID = jsonObject.getInt("user_id_recived");
            timeStamp = jsonObject.getString("timeStamp");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return new Message(id, content, senderID, receiverID, timeStamp);
    }

    public boolean sentBy(int userID) {
        return this.senderID == userID;
    }

    public boolean isOlderThan(Message otherMessage) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        try {
            Date currentMessage = dateFormat.parse(timeStamp);
            Date newMessage = dateFormat.parse(otherMessage.getTimeStamp());

            int comparisonResult = currentMessage.compareTo(newMessage);

            if (comparisonResult < 0) {
                System.out.println("timestamp1 is before timestamp2");
                return false;
            } else if (comparisonResult > 0) {
                System.out.println("timestamp1 is after timestamp2");
                return true;
            } else {
                System.out.println("timestamp1 is equal to timestamp2");
                return true;
            }

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
