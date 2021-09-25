package com.example.lonely;

public class UserHelperClass {
    String name, username, userage, password, phoneNo, userCash, lastPayment;


    public UserHelperClass() {
    }

    public UserHelperClass(String name, String username, String userage, String password, String phoneNo, String userCash, String lastPayment) {
        this.name = name;
        this.username = username;
        this.userage = userage;
        this.password = password;
        this.phoneNo = phoneNo;
        ///
        this.userCash = userCash;
        this.lastPayment = lastPayment;
        ///
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserage() {
        return userage;
    }

    public void setUserage(String userage) {
        this.userage = userage;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }
    ///
    public String getuserCash() {
        return userCash;
    }

    public void setuserCash(String userCash) {
        this.userCash = userCash;
    }

    public String getlastPayment() {
        return lastPayment;
    }

    public void setlastPayment(String lastPayment) {
        this.lastPayment = lastPayment;
    }
    ///
}
