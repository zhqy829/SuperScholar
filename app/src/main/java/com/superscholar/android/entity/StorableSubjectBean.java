package com.superscholar.android.entity;

import org.litepal.crud.DataSupport;

/**
 * @author zhqy
 * @date 2018/4/8
 */

public class StorableSubjectBean extends DataSupport {


    public static final int MONDAY = 1;
    public static final int TUESDAY = 2;
    public static final int WEDNESDAY = 3;
    public static final int THURSDAY = 4;
    public static final int FRIDAY = 5;
    public static final int SATURDAY = 6;
    public static final int SUNDAY = 7;

    private String name;
    private String teacher;
    private String classroom;
    private int timeStart;
    private int timeEnd;
    private int dayOfWeek;
    private int weekStart;
    private int weekEnd;

    public StorableSubjectBean() {
    }

    public StorableSubjectBean(String name, String teacher, String classroom, int timeStart, int timeEnd, int dayOfWeek, int weekStart, int weekEnd) {
        this.name = name;
        this.teacher = teacher;
        this.classroom = classroom;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.dayOfWeek = dayOfWeek;
        this.weekStart = weekStart;
        this.weekEnd = weekEnd;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public int getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(int timeStart) {
        this.timeStart = timeStart;
    }

    public int getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(int timeEnd) {
        this.timeEnd = timeEnd;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public int getWeekStart() {
        return weekStart;
    }

    public void setWeekStart(int weekStart) {
        this.weekStart = weekStart;
    }

    public int getWeekEnd() {
        return weekEnd;
    }

    public void setWeekEnd(int weekEnd) {
        this.weekEnd = weekEnd;
    }


}
