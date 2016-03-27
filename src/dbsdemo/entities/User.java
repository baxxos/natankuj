package dbsdemo.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author Baxos
 */
@Entity
@Table(name="users")
public class User implements Serializable {

    @Column(length=50, unique=true)
    private String username;
    @Column(length=50)
    private String name;
    @Column(length=50)
    private String surname;
    @Column
    private int userLevel;
    @Column
    private String passwd;
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;
    
    public User(){
        // For ORM purposes
    }
    
    public User(String username, String passwd, String name, String surname){
        this.passwd = passwd;
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.userLevel = 0;
    }
    
    public User(String username, String passwd, String name, String surname, int userLevel){
        this.passwd = passwd;
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.userLevel = userLevel;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public int getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(int userLevel) {
        this.userLevel = userLevel;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
