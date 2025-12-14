package com.cn.hbu.edu.htliang.service;

import com.cn.hbu.edu.htliang.entityPojo.Contacts;

import java.util.List;

/**
 * 类名: ContactService
 * 创建时间: 2025/12/14 12:13
 * 项目描述:
 *
 * @author htLiang
 */
public interface ContactService {
    /**
    * 添加联系人，调用DAO中的insert方法
     * @param name,tele1,tele2,home,email,notes
    */
    void addContact(String name, String tele1, String tele2, String home, String email, String notes);

    /**
    * 通过ID查询联系人
    */
    Contacts findId(int id);

    /**
    * 通过姓名查询联系人
    */
    List<Contacts> findName(String name);

    /**
     * 根据ID删除联系人
     */
    void deleteId(int id);

    /**
     * 根据ID来更改联系人信息
     */
    boolean updateContactInfo(int id, String newName, String newTele1, String newTele2, String newHome, String newEmail, String newNotes);
}
