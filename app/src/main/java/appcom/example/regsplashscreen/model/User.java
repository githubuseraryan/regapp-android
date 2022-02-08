package appcom.example.regsplashscreen.model;

public class User {

    private String uid;
    private String userName;
    private String emailId;
    private String password;
    private String aadharNo;
    private String dob;
    private String userId;
    private String address;
    private String country;
    private String designation;

    private User() {
    }

    public User(String uid,
                String userName,
                String emailId,
                String password,
                String aadharNo,
                String dob,
                String address,
                String country,
                String designation) {
        this.userName = userName;
        this.emailId = emailId;
        this.password = password;
        this.aadharNo = aadharNo;
        this.dob = dob;
        this.address = address;
        this.country = country;
        this.designation = designation;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAadharNo() {
        return aadharNo;
    }

    public void setAadharNo(String aadharNo) {
        this.aadharNo = aadharNo;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }
}
