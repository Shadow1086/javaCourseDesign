package com.cn.hbu.edu.htliang.dao;

import com.cn.hbu.edu.htliang.entityPojo.Contacts;

import java.util.List;

/**
 * 类名: ContactsDao
 * 创建时间: 2025/12/12 20:36
 * 项目描述:
 *
 * @author htLiang
 */
public interface ContactsDao {
    //Insert : 插入操作
    void insert(Contacts contacts);
    void batchInsert(List<Contacts> contactsList);

    // Read ： 查询数据
    Contacts findById(int id);
    List<Contacts> findAll();
    List<Contacts> findByName(String name);
    List<Contacts> findByTele(String tele);

    // Update : 更新数据
    void update(Contacts contact);
    void updateInfo(int id ,String newName,String newTele1,String newTele2,String newHome,String newEmail,String newNotes);

    //delete : 删除数据
    void deleteById(int id);
    //其他操作
    boolean exists(int id);     //判断是否存在此对象
//    int returnId(Contacts con);
}
