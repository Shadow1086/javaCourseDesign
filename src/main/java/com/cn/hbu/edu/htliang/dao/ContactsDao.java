package com.cn.hbu.edu.htliang.dao;

import com.cn.hbu.edu.htliang.entityPojo.Contacts;
import com.cn.hbu.edu.htliang.entityPojo.Groups;
import com.cn.hbu.edu.htliang.entityPojo.Tags;

import java.io.BufferedWriter;
import java.util.List;

/**
 * 类名: ContactsDao
 * 创建时间: 2025/12/12 20:36
 * 项目描述:
 *
 * @author htLiang
 */
public interface ContactsDao {
    // 关于联系人操作
    // Insert : 插入操作
    void insert(Contacts contacts);

    void batchInsert(List<Contacts> contactsList);

    // Read ： 查询数据
    Contacts findById(int id);

    List<Contacts> findAll();

    List<Contacts> findByName(String name);

    List<Contacts> findByTele(String tele);

    // Update : 更新数据
    void update(Contacts contact);

    // 根据 id 修改信息
    void updateInfo(int id, String newName, String newTele1, String newTele2, String newHome, String newEmail,
            String newNotes);

    // delete : 删除数据
    void deleteById(int id);

    // 其他操作
    boolean exists(int id); // 判断是否存在此对象
    // int returnId(Contacts con);

    // 关于分组操作
    // 新建分组
    boolean addGroup(String name, String notes);

    // 添加联系人
    boolean addContactInGroup(Contacts con, Groups group);

    // 删除分组
    boolean deleteGroup(String name);

    // 查看分组中的联系人
    List<Contacts> findByGroup(String name);

    // 显示所有分组
    List<Groups> findAllGroup();

    // 关于标签操作
    // 添加标签
    boolean addTag(String color, String name, String notes);

    // 删除标签
    boolean deleteTag(String color);

    // 向标签中添加联系人
    boolean addContactToTag(Contacts con, Tags tag);

    // 查看标签中所有联系人
    List<Contacts> findByTag(String color);

    // 查找所有标签
    List<Tags> findAllTags();

    // 关于根据联系人查找分组和标签
    List<Groups> findGroupsByContact(int id);

    List<Tags> findTagsByContact(int id);

    // 将数据库中的所有联系人信息按行字符串的格式储存在list<String>中，便于导出联系人
    List<String> writeVcfFileInService(BufferedWriter bw);
}
