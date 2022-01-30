package appcom.example.regsplashscreen.model.request;

public class LoginApiRequestModel {

    private String email_id;
    private String password;

    public LoginApiRequestModel(Builder builder) {
        this.email_id = builder.email_id;
        this.password = builder.password;
    }


    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "LoginApiRequestModel{" +
                "email_id='" + email_id + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public static class Builder {
        private String email_id;
        private String password;

        public Builder() {

        }

        public Builder email_id(String email_id) {
            this.email_id = email_id;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public LoginApiRequestModel build() {
            LoginApiRequestModel loginApiRequestModel = new LoginApiRequestModel(this);
            return loginApiRequestModel;
        }
    }
}
