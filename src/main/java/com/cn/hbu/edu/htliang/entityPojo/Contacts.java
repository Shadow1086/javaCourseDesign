package com.cn.hbu.edu.htliang.entityPojo;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ClassName: Contacts
 * Description:
 * 
 * 通讯录中联系人对象
 * 
 * {@code @Author} Liang-ht
 * {@code @Create} 2025-12-09 19:53:55
 */
public class Contacts {
    private Integer id;
    private String name;
    private String tele1;
    private String tele2;
    private String home;
    private String email;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<Groups> groups;
    private List<Tags> tags;

    public Contacts() {
    }

    public List<Groups> getGroups() {
        return groups;
    }

    public void setGroups(List<Groups> groups) {
        this.groups = groups;
    }

    public List<Tags> getTags() {
        return tags;
    }

    public void setTags(List<Tags> tags) {
        this.tags = tags;
    }

    public Contacts(String name, String tele1, String tele2, String home, String email, String notes) {
        this.name = name;
        this.tele1 = tele1;
        this.tele2 = tele2;
        this.home = home;
        this.email = email;
        this.notes = notes;
    }

    public Contacts(Integer id, String name, String tele1, String tele2, String home, String email, String notes) {
        this.id = id;
        this.name = name;
        this.tele1 = tele1;
        this.tele2 = tele2;
        this.home = home;
        this.email = email;
        this.notes = notes;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTele1() {
        return tele1;
    }

    public void setTele1(String tele1) {
        this.tele1 = tele1;
    }

    public String getTele2() {
        return tele2;
    }

    public void setTele2(String tele2) {
        this.tele2 = tele2;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Contacts{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", tele1='" + tele1 + '\'' +
                ", tele2='" + tele2 + '\'' +
                ", home='" + home + '\'' +
                ", email='" + email + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }
}
