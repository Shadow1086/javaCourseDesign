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
    private String tags_name;
    private String tags_color;
    private List<Contacts> contactsTagsList;

    public Tags(String tags_name, String tags_color) {
        this.tags_name = tags_name;
        this.tags_color = tags_color;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTags_name() {
        return tags_name;
    }

    public void setTags_name(String tags_name) {
        this.tags_name = tags_name;
    }

    public String getTags_color() {
        return tags_color;
    }

    public void setTags_color(String tags_color) {
        this.tags_color = tags_color;
    }

    public List<Contacts> getContactsTagsList() {
        return contactsTagsList;
    }

    public void setContactsTagsList(List<Contacts> contactsTagsList) {
        this.contactsTagsList = contactsTagsList;
    }
}
