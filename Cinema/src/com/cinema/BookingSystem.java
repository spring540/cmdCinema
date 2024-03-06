package com.cinema;

import com.sun.jdi.VMOutOfMemoryException;

import java.net.IDN;
import java.sql.SQLOutput;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//import java.text.SimpleDateFormat;
import java.util.Date;

public class BookingSystem {
    private ArrayList<Users> accounts;
    private Scanner sc = new Scanner(System.in);
    private Users loginUser;

    private CinemaHall hall;
    //
    public BookingSystem(CinemaHall hall) {
        this.hall = hall;
        this.accounts = new ArrayList<>();
        AdminUser newAdmin = new AdminUser("admin", "123", "123@123.com", "admin", hall);
        accounts.add(newAdmin);
        CommonUser newUser = new CommonUser("user", "123", "123@123.com", "user");
        accounts.add(newUser);
        ScreeningHall newHall = new ScreeningHall(10,10,1,"testHall");
        newAdmin.addScreenHall(newHall);
    }

    public void start(){
        while(true){
            System.out.println("---欢迎进入" + hall.getCinemaName() + "订票系统---");
            System.out.println("1、用户登录");
            System.out.println("2、用户注册");
            System.out.println("3、退出系统");
            System.out.println("请选择:");
            int command = this.sc.nextInt();
            switch (command){
                case 1:
                    this.login();
                    break;
                case 2:
                    this.createAccount();
                    break;
                case 3:
                    return;
                default:
                    System.out.println("请输入正确的操作序号");
            }
        }
    }

    private void login(){
        System.out.println("正在进行用户登录操作：");
        while (true) {
            System.out.println("请输入邮箱地址/账户名称：");
            String mail_id = sc.next();
            Users acc = getAccountByMailID(mail_id);
            if(acc == null){
                System.out.println("不存在该用户，请重新输入");
            }else{
                while(true){
                    System.out.println("请输入账户密码");
                    String password = sc.next();
                    if (!acc.getPassword().equals(password)){
                        System.out.println("账户密码输入错误");
                    }else{
                        this.loginUser = acc;
                        System.out.println("登录成功");
                        if("admin".equals(acc.getPrivilege())){
                            AdminUser adminLoggedUser = (AdminUser) acc;
                            LoggedInAdminView(adminLoggedUser);
                        }else if ("user".equals(acc.getPrivilege())){
                            CommonUser commonLoggedUser = (CommonUser) acc;
                            LoggedInUserView(commonLoggedUser);
                        }
                        return;
                    }
                }

            }
        }
    }
    private void logOut(Users user){
        loginUser = null;
        start();
    }

    private void LoggedInAdminView(AdminUser user){
        System.out.println("您已成功登录，账户名:"+user.getUsername()+";邮箱:"+user.getMail()+";权限:"+user.getPrivilege()+".");
        while (true){
            System.out.println("请选择操作:\n1.添加新的放映厅\n2.获取放映厅排片情况\n3.排片\n4.退出登录");
            int command = sc.nextInt();
            if(command == 1){
                System.out.println("-----添加放映厅-----");
                addScreenHall(user);
            }else if(command == 2){
                System.out.println("-----获取放映厅排片情况-----");
                showFilmManage();
            }else if(command == 3){
                System.out.println("-----排片-----");
                System.out.println("请选择操作:\n1.新增排片\n2.取消排片");
                command = sc.nextInt();
                if(command == 1){
                    System.out.println("----新增排片-----");
                    addScreening(user);
                }else if(command == 2){
                    System.out.println("----取消排片-----");
                    delScreening(user);
                }
            }else if(command == 4){
                logOut(user);
            }else{
                System.out.println("请输入正确的操作序号");
            }
        }
    }

    private int GenerateHallID(){
        ArrayList<ScreeningHall> temp = this.hall.getScreeningHalls();
        if(temp.isEmpty()){
            return 1;
        }
        return temp.get(temp.size()-1).getId()+1;
    }

    private void LoggedInUserView(CommonUser user){
        while(true){
            System.out.println("您已成功登录，账户名:"+user.getUsername()+";邮箱:"+user.getMail()+";权限:"+user.getPrivilege()+".");
            showFilmManage();
            showBooking(user);
            System.out.println("请选择操作:\n1.订票\n2.取消订票\n3.登出");
            int command = sc.nextInt();
            if(command == 1){
                System.out.println("-----订票-----");
                if(booking(user)){
                    System.out.println("订票成功！");
                }else{
                    System.out.println("订票失败!");
                }
            }else if(command == 2){
                System.out.println("-----取消订票-----");
                if(cancel_booking(user)){
                    System.out.println("取消订票成功！");
                }else{
                    System.out.println("取消订票失败！");
                }
            }else if(command == 3){
                logOut(user);
            }
            else{
                System.out.println("请输入正确的操作序号");
            }
        }
    }

    private void showBooking(CommonUser user){
        System.out.println("当前订票情况如下：");
        if(user.getBookingSituation().isEmpty()){
            System.out.println("空\n");
            return;
        }
        for(OneBooking s:user.getBookingSituation()){
            ScreeningHall hall = getScreenHallByID(s.getRoomID());
            System.out.println("订票序号:"+s.getBookingID()+" "+s.getRoomID()+"号放映厅 "+hall.getStrByArrID(s.getArrID())+" "+hall.getStrBySeatID(s.getSeatedID()));
        }
    }

    private boolean booking(CommonUser user){
        System.out.println("请输入放映厅ID");
        int screeningID = sc.nextInt();
        ScreeningHall hall = getScreenHallByID(screeningID);
        if(hall == null){
            System.out.println("不存在的放映厅！");
            return false;
        }

        System.out.println("请输入电影场次");
        int arrID = sc.nextInt();
        ScreenArrangement screeenArr = hall.getArrByID(arrID);
        if(screeenArr == null){
            System.out.println("不存在的电影场次！");
            return false;
        }

        if(!user.checkConfictBooking(screeningID, arrID)){
            System.out.println("存在冲突的订票计划");
            return false;
        }

        System.out.println("请输入座位序号");
        int seatID = sc.nextInt();
        if(seatID > hall.getRows()*hall.getColumns() || seatID <= 0){
            System.out.println("不存在的座位序号");
            return false;
        }

        int newBookingID = user.generateBookingID();
        OneBooking newBooking = new OneBooking(screeningID, arrID, seatID, newBookingID);
        user.addBooking(newBooking);
        return true;
    }

    private boolean cancel_booking(CommonUser user){
        System.out.println("请输入要取消订票的编号");
        showBooking(user);
        int cancelBookingID = sc.nextInt();
        OneBooking cancelBooking = user.getBookingByID(cancelBookingID);
        if(cancelBooking == null){
            System.out.println("不存在的订票编号！");
            return false;
        }

        user.cancelBooking(cancelBooking);
        return true;
    }
    private void showFilmManage(){
        System.out.println("当前放映厅排片情况为：");
        ArrayList<ScreeningHall> nowHalls = hall.getScreeningHalls();
        if(nowHalls.isEmpty()){
            System.out.println("空\n");
            return;
        }
        for(int i = 0; i<nowHalls.size(); i++){
            System.out.println(nowHalls.get(i).getId()+"号放映厅："+nowHalls.get(i).getHallName());
            if(nowHalls.get(i).getScreenArrangements().isEmpty()){
                System.out.println("当前无排片安排\n");
            }
            for(int j = 0; j<nowHalls.get(i).getScreenArrangements().size(); j++){
                String filmName = nowHalls.get(i).getScreenArrangements().get(j).getFilmName();
                String time_start = TimeStampToYMD(nowHalls.get(i).getScreenArrangements().get(j).getTimeStart());
                String time_end = TimeStampToYMD(nowHalls.get(i).getScreenArrangements().get(j).getTimeEnd());
                int arrID = nowHalls.get(i).getScreenArrangements().get(j).getID();
                System.out.println("" + arrID +" || "+ filmName+" || "+time_start+"至"+time_end);
            }
        }
    }

    private void addScreenHall(AdminUser user){
        System.out.println("输入放映厅名字:");
        String hallName = sc.next();
        System.out.println("输入行数和列数:");
        int rowNum = sc.nextInt();
        int columnNum = sc.nextInt();
//            System.out.println("请输入放映厅ID:");
        int IDNum = GenerateHallID();
        ScreeningHall newHall = new ScreeningHall(rowNum, columnNum, IDNum, hallName);
        user.addScreenHall(newHall);
        System.out.println("添加放映厅完成。");
    }

    //排片，可以选择指定放映厅，给定电影名、起始结束时间来新增一个
    private void addScreening(AdminUser user){
        System.out.println("输入放映厅ID:");
        long startTimeStamp;
        long endTimeStamp;
        while(true){
            int ScreenHallID = sc.nextInt();
            ScreeningHall hall =  getScreenHallByID(ScreenHallID);
            if(hall == null){
                System.out.println("放映厅不存在，请重新输入");
            }else{
                System.out.println("请输入排片电影名称：");
                String filmName = sc.next();
                while(true){
                    System.out.println("请输入电影起始放映时间：");
                    while(true){
                        String bin = sc.nextLine();
                        String startTime = sc.nextLine();
                        try{
                            startTimeStamp = YMDToTimeStamp(startTime);
                            break;
                        }catch (Exception e){
                            System.out.println("输入时间格式不正确,请重新输入");
                        }
                    }
                    System.out.println("请输入电影结束放映时间：");
                    while(true){
                        String endTime = sc.nextLine();
                        try{
                            endTimeStamp = YMDToTimeStamp(endTime);
                            break;
                        }catch (Exception e){
                            System.out.println("输入时间格式不正确,请重新输入");
                        }
                    }
                    if(CheckConflict(startTimeStamp, endTimeStamp, hall)){
                        int arrID = generateArrID(hall);
                        ScreenArrangement newArrangement = new ScreenArrangement(filmName, startTimeStamp, endTimeStamp, arrID);
                        user.addScreeningArrangement(newArrangement, ScreenHallID);
                        System.out.println("新增排片成功！");
                        return;
                    }else{
                        System.out.println("排片时间冲突，请重新输入");
                    }
                }
            }
        }

    }

    private void delScreening(AdminUser user){
        System.out.println("输入放映厅ID:");
        while(true){
            int ScreenHallID = sc.nextInt();
            ScreeningHall hall =  getScreenHallByID(ScreenHallID);
            if(hall == null) {
                System.out.println("放映厅不存在，请重新输入");
            }else{
                System.out.println("请输入取消排片的ID编号：");
                int arrID = sc.nextInt();
                if(user.delScreeningArrangement(arrID, ScreenHallID)){
                    System.out.println("排片已删除");
                    return;
                }else{
                    System.out.println("排片的ID编号有误，请检查");
                }
            }
        }
    }

    private int generateArrID(ScreeningHall hall){
        ArrayList<ScreenArrangement> temp = hall.getScreenArrangements();
        if(temp.isEmpty()){
            return 1;
        }
        return temp.get(temp.size()-1).getID()+1;
    }

    //检查新增排片时间是否与当前时间安排冲突
    private boolean CheckConflict(long startTime, long endTime, ScreeningHall hall){
        ArrayList<ScreenArrangement> screenArrangements = hall.getScreenArrangements();
        if(startTime>endTime){
            return false;
        }
        for(ScreenArrangement s : screenArrangements){
            if(endTime <= s.getTimeStart() || startTime >= s.getTimeEnd()){
                continue;
            }else{
                return false;
            }
        }
        return true;
    }
    private ScreeningHall getScreenHallByID(int ID){
        ArrayList<ScreeningHall> hallA = hall.getScreeningHalls();
        for(ScreeningHall s : hallA){
            if(s.getId() == ID){
                return s;
            }
        }
        return null;
    }


    private void createAccount(){
        System.out.println("正在进行用户注册操作：");
//        Users newUser = new Users();
        System.out.println("请输入用户权限\n1.系统管理员\n2.普通用户");
        int privilege = sc.nextInt();

        System.out.println("请输入账户名称：");
        String name = sc.next();
//        newUser.setUsername(name);

        String mail;
        System.out.println("请输入您的电子邮箱地址：");
        while(true){
            mail = sc.next();
            if(isVaildMail(mail)){
//                newUser.setMail(mail);
                break;
            }else{
                System.out.println("您输入的电子邮箱格式不正确，请重新输入");
            }
        }

        String pass;
        String okPass;
        while(true){
            System.out.println("请输入您的账户密码：");
            pass = sc.next();
            System.out.println("请重新输入密码确认：");
            okPass = sc.next();
            if(pass.equals(okPass)){
//                newUser.setPassword(pass);
                break;
            }else{
                System.out.println("两次输入的密码不一致，请确认并重新输入：");
            }
        }

//        newUser.setBookingSituation();
        if(privilege == 1){
            AdminUser newUser = new AdminUser(name, pass, mail, "admin", hall);
            accounts.add(newUser);
        }else{
            CommonUser newUser = new CommonUser(name, pass, mail, "user");
            accounts.add(newUser);
        }
        System.out.println("注册成功！请登录账户进行操作。");
    }

    private Users getAccountByMailID(String mail_id){
        for (int i = 0; i<accounts.size(); i++){
            Users acc = accounts.get(i);
            if(acc.getMail().equals(mail_id) || acc.getUsername().equals(mail_id)){
                return acc;
            }
        }
        return null;
    }

    public static boolean isVaildMail(String str){
        String pattern = "^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z0-9]{2,6}$";
        Pattern r = Pattern.compile(pattern);
        Matcher matcher = r.matcher(str);
        return matcher.find();
    }

    public static String TimeStampToYMD(long timeStamp){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.format(new Date(timeStamp));
    }

    public static long YMDToTimeStamp(String YMD)throws Exception{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = sdf.parse(YMD);
        return date.getTime();
    }

}
