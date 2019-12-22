package com.bobisonfire.lab4;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id @GeneratedValue
    private long id;

    @Column(unique = true)
    private String userName;
    private String password;

    private String lastAddress;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id")
    private List<HistoryNode> userHistory = new ArrayList<>();

    public User() {
    }

    public User(String userName, String password, String lastAddress) {
        this.userName = userName;
        this.password = password;
        this.lastAddress = lastAddress;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<HistoryNode> getUserHistory() {
        return userHistory;
    }

    public void setUserHistory(List<HistoryNode> userHistory) {
        this.userHistory = userHistory;
    }

    public String getLastAddress() {
        return lastAddress;
    }

    public void setLastAddress(String lastAddress) {
        this.lastAddress = lastAddress;
    }
}
