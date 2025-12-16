package com.cn.hbu.edu.htliang.entityPojo;

import java.util.List;

/**
 * 类名: Tags
 * 创建时间: 2025/12/14 16:43
 * 项目描述:
 *
 * 标签
 *
 * @author htLiang
 */
public class Tags {
    private int id;
    private String tag_name;
    private String tag_color;
    private String tag_notes;

    public Tags(int id, String tag_name, String tag_color, String tag_notes) {
        this.id = id;
        this.tag_name = tag_name;
        this.tag_color = tag_color;
        this.tag_notes = tag_notes;
    }

    public Tags() {
    }

    private List<Contacts> contactsTagsList;

    public String getTag_notes() {
        return tag_notes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTag_notes(String tag_notes) {
        this.tag_notes = tag_notes;
    }

    public String getTag_name() {
        return tag_name;
    }

    public void setTag_name(String tag_name) {
        this.tag_name = tag_name;
    }

    public String getTag_color() {
        return tag_color;
    }

    public void setTag_color(String tag_color) {
        this.tag_color = tag_color;
    }

    public List<Contacts> getContactsTagsList() {
        return contactsTagsList;
    }

    public void setContactsTagsList(List<Contacts> contactsTagsList) {
        this.contactsTagsList = contactsTagsList;
    }
}
