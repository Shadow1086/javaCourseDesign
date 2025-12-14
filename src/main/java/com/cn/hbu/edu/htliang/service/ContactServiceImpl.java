package com.cn.hbu.edu.htliang.service;

import com.cn.hbu.edu.htliang.dao.ContactsDaoImpl;
import com.cn.hbu.edu.htliang.entityPojo.Contacts;

import java.util.ArrayList;
import java.util.List;

/**
 * 类名: ContactsService
 * 创建时间: 2025/12/12 20:31
 * 项目描述:
 * 业务层，向外暴露的方法
 *
 * @author htLiang
 */
public class ContactServiceImpl implements ContactService {
    private final ContactsDaoImpl dao = new ContactsDaoImpl();

    /**
     * 添加单个联系人
     * 姓名等所有信息都需要添加到形参，除了ID
     */
    @Override
    public void addContact(String name, String tele1, String tele2, String home, String email, String notes) {
        if (name == null) {
            System.err.println("姓名不能为空");
        } else if (tele1 == null) {
            System.err.println("电话不能为空");
        } else {
            Contacts con = new Contacts(name, tele1, tele2, home, email, notes);
            dao.insert(con);
            System.out.println(con); //TODO:以后可以删除，只是验证ID返回是否正确
        }
    }

    /**
     * 根据ID查找联系人
     */
    @Override
    public Contacts findId(int id) {
        if (dao.exists(id)) {
            return dao.findById(id);
        } else {
            System.err.println("没有ID为：" + id + "的用户");
        }
        return null;
    }
    /**
    * 查询所有联系人
    */
    public List<Contacts> findAll(){
        return dao.findAll();
    }
    /**
     * 根据姓名查找联系人
     */
    @Override
    public List<Contacts> findName(String name) {
        List<Contacts> result = dao.findByName(name);
        if (result == null) {
            System.out.println("无此用户");
            return null;
        }
        return result;

    }

    /**
    * 通过手机号查询
    */
    public List<Contacts> findTele(String tele){
        if(tele.length()>11){
            //TODO : 应该添加一个电话号格式的正则表达式
            try {
                throw new Exception("输入电话有误");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return dao.findByTele(tele);
    }

    /**
     * 根据ID删除联系人
     * @param id : 要删除的联系人的ID
     */
    @Override
    public void deleteId(int id) {
        if (dao.exists(id)) {
            dao.deleteById(id);
        } else {
            System.err.println("没有ID为：" + id + "的用户");
        }
    }

    /**
     * 根据ID修改联系人信息
     */
    @Override
    public boolean updateContactInfo(int id, String newName, String newTele1, String newTele2, String newHome, String newEmail, String newNotes) {
        if (!dao.exists(id)) return false;
        try {
            Contacts con = dao.findById(id);
            if (newName == null) newName = con.getName();
            if (newTele1 == null) newTele1 = con.getTele1();
            if (newTele2 == null) newTele2 = con.getTele2();
            if (newHome == null) newHome = con.getHome();
            if (newEmail == null) newEmail = con.getEmail();
            if (newNotes == null) newNotes = con.getNotes();
            dao.updateInfo(id, newName, newTele1, newTele2, newHome, newEmail, newNotes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
