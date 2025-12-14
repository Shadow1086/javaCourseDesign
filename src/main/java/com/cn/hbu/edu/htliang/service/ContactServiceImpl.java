package com.cn.hbu.edu.htliang.service;

import com.cn.hbu.edu.htliang.dao.ContactsDaoImpl;
import com.cn.hbu.edu.htliang.entityPojo.Contacts;

import java.util.List;

/**
 * 类名: ContactsService
 * 创建时间: 2025/12/12 20:31
 * 项目描述:
 *
 * @author htLiang
 */
public class ContactServiceImpl implements ContactService{
    private ContactsDaoImpl dao = new ContactsDaoImpl();
    @Override
    public void addContact(String name,String tele1,String tele2,String home ,String email,String notes){
        Contacts con = new Contacts(name,tele1,tele2,home,email,notes);
        dao.insert(con);
        con.setId(dao.returnId(con));
    }
    @Override
    public Contacts findId(int id){
        return dao.findById(id);
    }

    @Override
    public List<Contacts> findName(String name){
        return dao.findByName(name);
    }
}
