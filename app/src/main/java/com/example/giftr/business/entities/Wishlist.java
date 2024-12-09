package com.example.giftr.business.entities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class Wishlist implements Serializable {
	private int wishlistID;
	private String name;
	private String description;
	private int userID;
	private List<Gift> gifts;
	private String creationDate;
	private String endDate;
	public final static String WISHLIST_TAG = "Wishlist_Tag";
	public final static String WISHLIST_TIMESTAMP = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	public Wishlist(String name, String description, int userID, List<Gift> gifts, String creationDate, String endDate) {
		this.name = name;
		this.description = description;
		this.userID = userID;
		this.gifts = gifts;
		this.creationDate = creationDate;
		this.endDate = endDate;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getEndDate() {
		return endDate;
	}

  	public String getCreationDate() {
		return creationDate;
	}

	public int getID() {
		return wishlistID;
	}

	public void setWishlistID(int wishlistID) {
		this.wishlistID = wishlistID;
	}

	public int getUserID() {
		return userID;
	}

	// Transform a JSONObject into a Wishlist instance.
	public static Wishlist fromJson(JSONObject jsonObject) {
		String name = null;
		String description = null;
		String creationDate = null;
		String endDate = null;
		int userID;
		List<Gift> gifts;

		try {
			name = jsonObject.getString("name");
			description = jsonObject.getString("description");
			creationDate = jsonObject.getString("creation_date");
			endDate = jsonObject.getString("end_date");
			userID = jsonObject.getInt("user_id");

			if (jsonObject.has("gifts")) {
				gifts = Gift.fromJsonArray(jsonObject.getJSONArray("gifts"));
			} else {
				gifts = new LinkedList<>();
			}

		} catch (JSONException e) {
			throw new RuntimeException(e);
		}

		Wishlist wishlist = new Wishlist(name, description, userID, gifts, creationDate, endDate);

		if (jsonObject.has("id")) {
			int id = 0;

			try {
				id = jsonObject.getInt("id");
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}

			wishlist.setWishlistID(id);
		}
		return wishlist;
	}

	public String getSentTime(boolean sendHour) {
		String formattedDate;

		// Parse the time stamp string
		LocalDateTime dateTime = LocalDateTime.parse(endDate, DateTimeFormatter.ISO_DATE_TIME);

		// Convert to the system's default time zone
		LocalDateTime localDateTime = dateTime.atZone(ZoneId.of("UTC"))
				.withZoneSameInstant(ZoneId.systemDefault())
				.toLocalDateTime();

		LocalDateTime currentDateTime = LocalDateTime.now();

		// Compare the date with the current date
		if (localDateTime.toLocalDate().isEqual(currentDateTime.toLocalDate())) {
			if (sendHour) {
				// The date is today, so display only the time in HH:mm format
				formattedDate = localDateTime.format(DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault()));
			} else {
				// The date is today
				formattedDate = "today";
			}
		} else if (localDateTime.isAfter(currentDateTime)) {
			// The date is in the future, display the date in DD-MM-YYYY format
			formattedDate = localDateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.getDefault()));
		} else {
			// Calculate the number of days between the dates
			long daysPassed = ChronoUnit.DAYS.between(localDateTime.toLocalDate(), currentDateTime.toLocalDate());

			if (daysPassed == 1) {
				// The date is yesterday
				formattedDate = "yesterday";
			} else {
				// The date is more than one day ago
				formattedDate = daysPassed + " days ago";
			}
		}

		return formattedDate;
	}


	public String getHourSent() {
		// Parse the time stamp string
		LocalDateTime dateTime = LocalDateTime.parse(endDate, DateTimeFormatter.ISO_DATE_TIME);

		// Convert to the system's default time zone
		LocalDateTime localDateTime = dateTime.atZone(ZoneId.of("UTC"))
				.withZoneSameInstant(ZoneId.systemDefault())
				.toLocalDateTime();

		return localDateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
	}

	//This function is needed because the API response has some fields that have a different name from the ones specified in the documentation
	public static Wishlist fromResponseJson(JSONObject jsonObject) {
		String name = "";
		String description = "";
		String creationDate = "";
		String endDate = "";
		int userID;
		List<Gift> gifts;

		try {
			if (jsonObject.has("name")) {
				name = jsonObject.getString("name");
			}

			if (jsonObject.has("description")) {
				description = jsonObject.getString("description");
			}

			if (jsonObject.has("creation_date")) {
				creationDate = jsonObject.getString("creation_date");
			}

			if (jsonObject.has("end_date")) {
				endDate = jsonObject.getString("end_date");
			}
			else {
				endDate = jsonObject.getString("date");
			}

			if (jsonObject.has("user_id")) {
				userID = jsonObject.getInt("user_id");
			}
			else {
				userID = jsonObject.getInt("owner_id");
			}

			if (jsonObject.has("gifts")) {
				gifts = Gift.fromJsonArray(jsonObject.getJSONArray("gifts"));
			} else {
				gifts = new LinkedList<>();
			}
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}

		Wishlist wishlist = new Wishlist(name, description, userID, gifts, creationDate, endDate);

		if (jsonObject.has("id")) {
			int id = 0;

			try {
				id = jsonObject.getInt("id");
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}

			wishlist.setWishlistID(id);
		}


		return wishlist;
	}

	public List<Gift> getGifts() {
		return gifts;
	}

	public int getWishlistID() {
		return wishlistID;
	}

	public boolean createdBy(int userID) {
		return this.userID == userID;
	}

	public JSONObject toJson() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", wishlistID);
		jsonObject.put("name", name);
		jsonObject.put("description", description);
		jsonObject.put("user_id", userID);

		JSONArray giftsArray = new JSONArray();
		for (Gift gift : gifts) {
			giftsArray.put(gift.fromWishlistToJson());
		}
		jsonObject.put("gifts", giftsArray);

		jsonObject.put("creation_date", creationDate);
		jsonObject.put("end_date", endDate);

		return jsonObject;
	}

	public boolean hasGifts() {
		return !gifts.isEmpty();
	}

	public boolean createdByUser(int userID) {
		return this.userID == userID;
	}

	public boolean hasExpired() {

		if (endDate == null || endDate.equals("null")) {
			return true;
		}

		LocalDateTime currentDateTime = LocalDateTime.now();
		String timestampString = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(timestampString);
		LocalDateTime timestampDateTime = LocalDateTime.parse(endDate, formatter);
		return currentDateTime.compareTo(timestampDateTime) >= 0;
	}
}
