package appcom.example.regsplashscreen.model;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class User {

    private String uid;
    private String userName;
    private String emailId;
    private String dob;
    private String encodedImage;
    private String userActive;
    private String userAdmin;
    private List<DocumentDetails> documentDetailsList;

    private User() {
    }

    public User(Builder builder) {
        this.uid = builder.uid;
        this.userName = builder.userName;
        this.emailId = builder.emailId;
        this.dob = builder.dob;
        this.encodedImage = builder.encodedImage;
        this.userActive = builder.userActive;
        this.userAdmin = builder.userAdmin;
        this.documentDetailsList = builder.documentDetailsList;
    }

    public static class Builder {
        private String uid;
        private String userName;
        private String emailId;
        private String dob;
        private String encodedImage;
        private String userActive;
        private String userAdmin;
        private List<DocumentDetails> documentDetailsList;

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
                    ", dob='" + dob + '\'' +
                    ", encodedImage='" + encodedImage + '\'' +
                    ", userActive='" + userActive + '\'' +
                    ", userAdmin='" + userAdmin + '\'' +
                    ", documentDetails='" + documentDetailsList + '\'' +
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
        public Builder setDob(String dob) {
            this.dob = dob;
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
        public Builder setUserAdmin(String userAdmin) {
            this.userAdmin = userAdmin;
            return this;
        }
        public Builder setDocumentDetailsList(DocumentDetails documentDetails) {
            if(this.documentDetailsList == null) {
                this.documentDetailsList = new ArrayList<>();
            }
            this.documentDetailsList.add(documentDetails);
            return this;
        }
        public Builder setDocumentDetailsList(Collection<? extends DocumentDetails> documentDetailsList) {
            if(this.documentDetailsList == null) {
                this.documentDetailsList = new ArrayList<>();
            }
            this.documentDetailsList.addAll(documentDetailsList);
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

    public String getDob() {
        return dob;
    }

    public String getEncodedImage() { return encodedImage; }

    public String getUserActive() { return userActive; }

    public String getUserAdmin() { return userAdmin; }

    public List<DocumentDetails> getDocumentDetailsList() { return documentDetailsList; }

}
