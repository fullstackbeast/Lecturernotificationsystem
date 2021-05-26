package com.ultraplex.lecturernotificationsystem;

public class StringUtils {


    public static String capitalizeText(String text) {

        String capitalizedWord = "";
        String[] words = text.split(" ");

        for (String word : words) {
            String firstLetter = word.substring(0, 1);
            firstLetter = firstLetter.toUpperCase();
            String newWord = firstLetter + word.substring(1);
            capitalizedWord = capitalizedWord + " " + newWord;
        }

        return capitalizedWord.trim();
    }

    public static String convertTo12Hr(String time) {
        String meridian = "AM";

        String[] times = time.split(":");

        String hour = times[0];
        String minute = times[1];

        if (Integer.parseInt(hour) > 12) {
            meridian = "PM";
            hour = String.valueOf(Integer.parseInt(hour) % 12);
        }

        if (Integer.parseInt(hour) == 12 && Integer.parseInt(minute) > 0) meridian = "PM";

        if (hour.length() == 1) hour = "0" + hour;

        if (minute.length() == 1) minute = "0" + minute;

        return (hour + ":" + minute + " " + meridian);
    }

    public static String convertTo24Hr(String time) {
        String[] timesWithMeridian = time.split(" ");

        String[] times = timesWithMeridian[0].split(":");

        String hour = times[0];
        String minute = times[1];

        if (timesWithMeridian[1].equals("PM")) hour = String.valueOf(Integer.parseInt(hour) + 12);


        return (hour + ":" + minute);
    }
}
