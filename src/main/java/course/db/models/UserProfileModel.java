package course.db.models;

import course.db.views.UserProfileView;

public class UserProfileModel {
    private String nickname;
    private String fullname;
    private String about;
    private String email;

    public UserProfileModel(String nickname,
                           String fullname,
                           String about,
                           String email) {
        this.nickname = nickname;
        this.fullname = fullname;
        this.about = about;
        this.email = email;
    }

    public UserProfileModel() {}

    public UserProfileModel(UserProfileView userProfileView) {
        this.nickname = userProfileView.getNickname();
        this.fullname = userProfileView.getFullname();
        this.about = userProfileView.getAbout();
        this.email = userProfileView.getEmail();
    }

    public void copy(UserProfileModel other) {
        this.nickname = other.getNickname();
        this.fullname = other.getFullname();
        this.about = other.getAbout();
        this.email = other.getEmail();
    }

    public UserProfileView toView(){
        final UserProfileView userProfileView = new UserProfileView(nickname, fullname, about, email);
        return userProfileView;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public String getFullname() {
        return fullname;
    }

    public String getAbout() {
        return about;
    }

    public String getEmail() {
        return email;
    }
}
