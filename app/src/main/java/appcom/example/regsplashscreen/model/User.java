package appcom.example.regsplashscreen.model;

import androidx.annotation.NonNull;

public class User {

    private String uid;
    private String userName;
    private String emailId;
    private String aadharNo;
    private String dob;
    private String panNo;
    private String voterIdNo;
    private String drivingLicenseNo;
    private String encodedImage;
    private String userActive;

    private User() {
    }

    public User(Builder builder) {
        this.uid = builder.uid;
        this.userName = builder.userName;
        this.emailId = builder.emailId;
        this.aadharNo = builder.aadharNo;
        this.dob = builder.dob;
        this.panNo = builder.panNo;
        this.voterIdNo = builder.voterIdNo;
        this.drivingLicenseNo = builder.drivingLicenseNo;
        this.encodedImage = builder.encodedImage;
        this.userActive = builder.userActive;
    }

    public static class Builder {
        private String uid;
        private String userName;
        private String emailId;
        private String aadharNo;
        private String dob;
        private String panNo;
        private String voterIdNo;
        private String drivingLicenseNo;
        private String encodedImage;
        private String userActive;

        public static Builder newInstance()
        {
            return new Builder();
        }

        private Builder() {}

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
                    ", aadharNo='" + aadharNo + '\'' +
                    ", dob='" + dob + '\'' +
                    ", panNo='" + panNo + '\'' +
                    ", voterIdNo='" + voterIdNo + '\'' +
                    ", drivingLicenseNo='" + drivingLicenseNo + '\'' +
                    ", encodedImage='" + encodedImage + '\'' +
                    ", userActive='" + userActive + '\'' +
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

        public Builder setAadharNo(String aadharNo) {
            this.aadharNo = aadharNo;
            return this;
        }
        public Builder setDob(String dob) {
            this.dob = dob;
            return this;
        }
        public Builder setPanNo(String panNo) {
            this.panNo = panNo;
            return this;
        }
        public Builder setVoterIdNo(String voterIdNo) {
            this.voterIdNo = voterIdNo;
            return this;
        }
        public Builder setDrivingLicenseNo(String drivingLicenseNo) {
            this.drivingLicenseNo = drivingLicenseNo;
            return this;
        }
        public Builder setEncodedImage(String encodedImage) {
            this.encodedImage = encodedImage;
            return this;
        }
        public Builder setUserActive(String userActive) {
            this.userActive = userActive;
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

    public String getAadharNo() {
        return aadharNo;
    }

    public String getDob() {
        return dob;
    }

    public String getPanNo() {
        return panNo;
    }

    public String getVoterIdNo() {
        return voterIdNo;
    }

    public String getDrivingLicenseNo() {
        return drivingLicenseNo;
    }

    public String getEncodedImage() { return encodedImage; }

    public String getUserActive() { return userActive; }

}
