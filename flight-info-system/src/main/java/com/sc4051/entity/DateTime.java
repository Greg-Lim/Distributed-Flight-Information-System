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
    Date date;

    public DateTime(int year,int month, int day, int hrs,int min){
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day, hrs, min, 0);
        cal.set(Calendar.MILLISECOND, 0);
        this.date = cal.getTime();
    }

    public DateTime(Date date){
        this.date = date;
    }

    public String toNiceString(){
        DateFormat df = new SimpleDateFormat("dd/mm/yyyy hh:mm");
        return df.format(this.date);
    }
}