package com.cn.hbu.edu.htliang.entityPojo;

import java.util.List;

/**
 * 类名: Group
 * 创建时间: 2025/12/14 16:43
 * 项目描述:
 * <p>
 * 分组
 *
 * @author htLiang
 */
public class Groups {
    private int id;
    private String group_name;
    private String group_notes;
    private List<Contacts> contactsGroupsList;

    public Groups(String group_notes, String group_name) {
        this.group_notes = group_notes;
        this.group_name = group_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getGroup_notes() {
        return group_notes;
    }

    public void setGroup_notes(String group_notes) {
        this.group_notes = group_notes;
    }

    public List<Contacts> getContactsGroupsList() {
        return contactsGroupsList;
    }

    public void setContactsGroupsList(List<Contacts> contactsGroupsList) {
        this.contactsGroupsList = contactsGroupsList;
    }
}
