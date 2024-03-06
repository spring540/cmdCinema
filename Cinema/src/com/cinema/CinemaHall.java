package com.cinema;

import java.awt.print.Book;
import java.util.ArrayList;

public class CinemaHall{


    public String getCinemaName() {
        return CinemaName;
    }

    private String CinemaName;
    private ArrayList<ScreeningHall> screeningHalls;

    public ArrayList<ScreeningHall> getScreeningHalls() {
        return screeningHalls;
    }

    public void setScreeningHalls(ArrayList<ScreeningHall> screeningHalls) {
        this.screeningHalls = screeningHalls;
    }

    public CinemaHall(String cinemaName) {
        CinemaName = cinemaName;
        this.screeningHalls = new ArrayList<>();
    }
}
class ScreeningHall {
    public String getHallName() {
        return hallName;
    }

    public void setHallName(String hallName) {
        this.hallName = hallName;
    }

    private String hallName;
    private int rows;
    private int columns;

    private ArrayList<Integer> occupied_seats;
    private ArrayList<ScreenArrangement> screenArrangements;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public ArrayList<ScreenArrangement> getScreenArrangements() {
        return screenArrangements;
    }

    public void setScreenArrangements(ArrayList<ScreenArrangement> screenArrangements) {
        this.screenArrangements = screenArrangements;
    }

    public String getStrBySeatID(int seatedID){
        return (seatedID/columns+1)+"排"+(seatedID%columns)+"列";
    }

    public String getStrByArrID(int arrID){
        for(ScreenArrangement s:screenArrangements){
            if(s.getID() == arrID){
                return s.toString();
            }
        }
        return null;
    }


    public ScreenArrangement getArrByID(int arrID){
        for(ScreenArrangement s:screenArrangements){
            if(s.getID() == arrID){
                return s;
            }
        }
        return null;
    }
    public ScreeningHall(int rows, int columns, int id, String hallName) {
        this.rows = rows;
        this.columns = columns;
        this.id = id;
        this.hallName = hallName;
        this.screenArrangements = new ArrayList<>();
        this.occupied_seats = new ArrayList<>();
    }
}

class ScreenArrangement{
    public String getFilmName() {
        return filmName;
    }

    public long getTimeStart() {
        return timeStart;
    }

    public long getTimeEnd() {
        return timeEnd;
    }

    public ScreenArrangement(String filmName, long timeStart, long timeEnd, int ID) {
        this.filmName = filmName;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.ID = ID;
    }

    public int getID() {
        return ID;
    }

    private int ID;
    private String filmName;
    private long timeStart;
    private long timeEnd;

    @Override
    public String toString() {

        return "{" +
                "电影名:'" + filmName + '\'' +
                BookingSystem.TimeStampToYMD(timeStart) +"-"+BookingSystem.TimeStampToYMD(timeEnd) + "}";

    }
}