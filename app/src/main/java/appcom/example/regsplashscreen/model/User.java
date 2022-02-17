package appcom.example.regsplashscreen.model;

import androidx.annotation.NonNull;

public class User {

    private String uid;
    private String userName;
    private String emailId;
    private String password;
    private String aadharNo;
    private String dob;
    private String address;
    private String country;
    private String designation;

    private User() {
    }

    public User(Builder builder) {
        this.uid = builder.uid;
        this.userName = builder.userName;
        this.emailId = builder.emailId;
        this.password = builder.password;
        this.aadharNo = builder.aadharNo;
        this.dob = builder.dob;
        this.address = builder.address;
        this.country = builder.country;
        this.designation = builder.designation;
    }

    public static class Builder {
        private String uid;
        private String userName;
        private String emailId;
        private String password;
        private String aadharNo;
        private String dob;
        private String address;
        private String country;
        private String designation;

        public static Builder newInstance()
        {
            return new Builder();
        }

        private Builder() {};

        public User build() {
            return new User(this);
        }

        @NonNull
        @Override
        public String toString() {
            return "Builder{" +
                    "uid='" + uid + '\'' +
                    ", userName='" + userName + '\'' +
                    ", emailId='" + emailId + '\'' +
                    ", password='" + password + '\'' +
                    ", aadharNo='" + aadharNo + '\'' +
                    ", dob='" + dob + '\'' +
                    ", address='" + address + '\'' +
                    ", country='" + country + '\'' +
                    ", designation='" + designation + '\'' +
                    '}';
        }

        // SETTERS
        public Builder setUid(String uid) {
            this.uid = uid;
            return this;
        }
        public Builder setUserName(String userName) {
            this.userName = userName;
            return this;
        }
        public Builder setEmailId(String emailId) {
            this.emailId = emailId;
            return this;
        }
        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }
        public Builder setAadharNo(String aadharNo) {
            this.aadharNo = aadharNo;
            return this;
        }
        public Builder setDob(String dob) {
            this.dob = dob;
            return this;
        }
        public Builder setAddress(String address) {
            this.address = address;
            return this;
        }
        public Builder setCountry(String country) {
            this.country = country;
            return this;
        }
        public Builder setDesignation(String designation) {
            this.designation = designation;
            return this;
        }
    }

    public String getUid() {
        return uid;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmailId() {
        return emailId;
    }

    public String getPassword() {
        return password;
    }

    public String getAadharNo() {
        return aadharNo;
    }

    public String getDob() {
        return dob;
    }

    public String getAddress() {
        return address;
    }

    public String getCountry() {
        return country;
    }

    public String getDesignation() {
        return designation;
    }

}
