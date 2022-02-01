package appcom.example.regsplashscreen.models;

public class Users {


    public Users(String profilePic, String userName, String mail, String password, String aadharNo, String DOB, String userId, String address, String Age ,String country) {
        ProfilePic = profilePic;
        UserName = userName;
        Mail = mail;
        Password = password;
        AadharNo = aadharNo;
        this.DOB = DOB;
        UserId = userId;
        Address = address;
        this. Age = Age;
        Country=country;


    }
public Users(){}
    public Users( String userName,
                  String mail,
                  String password,
                  String aadharNo,
                  String DOB,
                  String address,
                  String country,
    String designation) {
        UserName = userName;
        Mail = mail;
        Password = password;
        AadharNo = aadharNo;
        this.DOB = DOB;
        Address = address;
        Country=country;
        Designation=designation;


    }


    public String getProfilePic() {
        return ProfilePic;
    }

    public void setProfilePic(String profilePic) {
        ProfilePic = profilePic;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getMail() {
        return Mail;
    }

    public void setMail(String mail) {
        Mail = mail;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getAadharNo() {
        return AadharNo;
    }

    public void setAadharNo(String aadharNo) {
        AadharNo = aadharNo;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }
    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }
    public String getDesignation() {
        return Designation;
    }

    public void setDesignation(String designation) {
        Designation = designation;
    }
    String ProfilePic;
    String UserName;
    String Mail;
    String Password;
    String AadharNo;
    String DOB;
    String UserId;
    String Address;
    String Age;
    String Country;
    String Designation;
}
