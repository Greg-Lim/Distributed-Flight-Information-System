/**
 * Represents a date and time value.
 */
package com.sc4051.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class DateTime {
    /**
     * The date and time value represented by this object.
     */
    Date date;

    /**
     * Constructs a new DateTime object with the specified year, month, day, hours, and minutes.
     * @param year the year.
     * @param month the month (1-12).
     * @param day the day of the month (1-31).
     * @param hrs the hours (0-23).
     * @param min the minutes (0-59).
     */
    public DateTime(int year,int month, int day, int hrs,int min){
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day, hrs, min, 0);
        cal.set(Calendar.MILLISECOND, 0);
        this.date = cal.getTime();
    }

    /**
     * Constructs a new DateTime object with the specified Date object.
     * @param date the Date object.
     */
    public DateTime(Date date){
        this.date = date;
    }

    /**
     * Returns a nicely formatted string representation of the date and time value.
     * @return a nicely formatted string representation of the date and time value.
     */
    public String toNiceString(){
        DateFormat df = new SimpleDateFormat("dd/mm/yyyy hh:mm");
        return df.format(this.date);
    }
}
