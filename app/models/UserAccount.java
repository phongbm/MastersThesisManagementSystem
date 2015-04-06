package models;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class UserAccount extends Model {
    @Id
    public Long id;

    @Constraints.Required
    public String email;

    @Constraints.Required
    public String password;

    public String permission;

    public static Finder<Long, UserAccount> finder = new Finder<Long, UserAccount>(Long.class, UserAccount.class);

    public UserAccount() {
    }

    public UserAccount(String email, String password, String permission) {
        this.email = email;
        this.password = password;
        this.permission = permission;
    }

    public static UserAccount authenticate(String email, String password) {
        return finder.where().eq("email", email).eq("password", password).findUnique();

    }

    public static UserAccount findByEmail(String email) {
        return finder.where().eq("email", email).findUnique();

    }

    public boolean isAdministrator() {
        if (permission.equals("Administrator")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isMastersStudent() {
        if (permission.equals("MastersStudent")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isFaculty() {
        if (permission.equals("Faculty")) {
            return true;
        } else {
            return false;
        }
    }

    public String getPermission(){
        return permission;
    }

    public String getEmail() {
        return email;
    }
}