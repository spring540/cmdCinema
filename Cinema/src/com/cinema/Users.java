package com.cinema;

import java.awt.print.Book;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Users {
    private String username;

    private String password;

    private String mail;

    public String getPrivilege() {
        return privilege;
    }

    public void setPrivilege(String privilege) {
        this.privilege = privilege;
    }

    private String privilege;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public Users(String username, String password, String mail, String privilege) {
        this.username = username;
        this.password = password;
        this.mail = mail;
        this.privilege = privilege;
    }
}

class CommonUser extends Users{
    private ArrayList<OneBooking> BookingSituation;
    public ArrayList<OneBooking> getBookingSituation() {
        return BookingSituation;
    }
    public void setBookingSituation(ArrayList<OneBooking> bookingSituation) {
        BookingSituation = bookingSituation;
    }

    public CommonUser(String username, String password, String mail, String privilege) {
        super(username, password, mail, privilege);
        BookingSituation = new ArrayList<>();
    }

    public int generateBookingID(){
        if(BookingSituation.isEmpty()){
            return 1;
        }
        return BookingSituation.get(BookingSituation.size()-1).getBookingID()+1;
    }

    public boolean checkConfictBooking(int screeningID, int arrID){
        for(OneBooking s: BookingSituation){
            if(screeningID == s.getRoomID() && arrID == s.getArrID()){
                return false;
            }
        }
        return true;
    }

    public OneBooking getBookingByID(int ID){
        for(OneBooking s: BookingSituation){
            if(ID == s.getBookingID()){
                return s;
            }
        }
        return null;
    }
    //
    public void addBooking(OneBooking s){
        BookingSituation.add(s);
    }

    public void cancelBooking(OneBooking s){
        BookingSituation.remove(s);
    }

}//普通用户可以添加场次预定

class AdminUser extends Users{
    private CinemaHall adminHall;
    public CinemaHall getAdminHall() {
        return adminHall;
    }

    public void setAdminHall(CinemaHall adminHall) {
        this.adminHall = adminHall;
    }

    public AdminUser(String username, String password, String mail, String privilege, CinemaHall realHall) {
        super(username, password, mail, privilege);
        adminHall = realHall;
    }

    public void addScreenHall(ScreeningHall newHall){
        ArrayList<ScreeningHall> temp =adminHall.getScreeningHalls();
        temp.add(newHall);
        adminHall.setScreeningHalls(temp);
    }

    public void addScreeningArrangement(ScreenArrangement newArrangement, int ScreeningID){
        ScreeningHall toHall = getScreenHallByID(ScreeningID);
        ArrayList<ScreenArrangement> temp = toHall.getScreenArrangements();
        temp.add(newArrangement);
        toHall.setScreenArrangements(temp);
    }

    public boolean delScreeningArrangement(int arrID, int ScreeningID){
        ScreeningHall toHall = getScreenHallByID(ScreeningID);
        ArrayList<ScreenArrangement> temp = toHall.getScreenArrangements();
        ScreenArrangement temp2;
        for(int i=0; i<temp.size(); i++){
            temp2 = temp.get(i);
            if(temp2.getID() == arrID){
                temp.remove(i);
                return true;
            }
        }
        return false;
    }

    //这里存在重复功能的函数
    private ScreeningHall getScreenHallByID(int ScreeningID){
        ArrayList<ScreeningHall> ScreeningHalls = this.adminHall.getScreeningHalls();
        for(ScreeningHall s: ScreeningHalls){
            if(s.getId() == ScreeningID){
                return s;
            }
        }
        return null;
    }
}//管理员用户可以为放映厅安排放映计划

class OneBooking{
    public int getRoomID() {
        return roomID;
    }

    public int getArrID() {
        return arrID;
    }

    public int getSeatedID() {
        return seatedID;
    }

    public int getBookingID() {
        return bookingID;
    }

    //放映厅
    private int roomID;
    //场次ID
    private int arrID;
    //座位序号
    private int seatedID;
    private int bookingID;

    public OneBooking(int roomID, int arrID, int seatedID, int bookingID) {
        this.roomID = roomID;
        this.arrID = arrID;
        this.seatedID = seatedID;
        this.bookingID = bookingID;
    }
    //    @Override
//    public String toString() {
//        return "OneBooking{" +
//                "roomID=" + roomID +
//                ", arrID=" + arrID +
//                ", seatedID=" + seatedID +
//                ", bookingID=" + bookingID +
//                '}';
//    }
}
