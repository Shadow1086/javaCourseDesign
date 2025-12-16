package com.cn.hbu.edu.htliang.service;

import com.cn.hbu.edu.htliang.dao.ContactsDaoImpl;
import com.cn.hbu.edu.htliang.entityPojo.Contacts;
import com.cn.hbu.edu.htliang.entityPojo.Groups;
import com.cn.hbu.edu.htliang.entityPojo.Tags;

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
    public Contacts findById(int id) {
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
    public List<Contacts> findByName(String name) {
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
    public List<Contacts> findByTele(String tele) {
        if(tele.length()>11){
            //TODO : 应该添加一个电话号格式的正则表达式
            throw new IllegalArgumentException("输入电话有误");
        }
        return dao.findByTele(tele);
    }

    /**
     * 根据ID删除联系人
     * @param id : 要删除的联系人的ID
     */
    @Override
    public void deleteById(int id) {
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
//分组操作

    /**
     * 创建新分组
     */
    public boolean addGroup(String name, String notes) {
        if (name != null) {
            dao.addGroup(name, notes);
            return true;
        }
        return false;
    }

    /**
     * 向分组中批量添加联系人，也可以单个添加
     */
    @Override
    public boolean addContactInGroup(List<Contacts> list, Groups groups) {
        if (list.isEmpty() || list == null) return false;
        for (Contacts con : list) {
            dao.addContactInGroup(con, groups);
        }
        return true;
    }

    /**
     * 根据分组来删除分组
     */
    @Override
    public boolean deleteGroup(String name) {
        if (name != null) {
            return dao.deleteGroup(name);
        }
        return false;
    }

    /**
     * 查看某分组中包含的所有联系人
     */
    @Override
    public List<Contacts> findByGroup(String name) {
        if (name != null) {
            return dao.findByGroup(name);
        }
        return null;
    }

    /**
     * 查找所有分组
     */
    @Override
    public List<Groups> findAllGroup() {
        return dao.findAllGroup();
    }

//标签操作

    /**
     * 新建标签
     */
    @Override
    public boolean addTag(String color, String name, String notes) {
        if (color == null) {
            return false;
        } else {
            dao.addTag(color, name, notes);
            return true;
        }
    }

    /**
     * 根据标签颜色 删除标签
     */
    @Override
    public boolean deleteTag(String color) {
        if (color != null) {
            return dao.deleteTag(color);
        }
        return false;
    }

    /**
     * 向标签中添加联系人
     */
    public boolean addContactToTag(List<Contacts> list, Tags tag) {
        if (list.isEmpty() || list == null) {
            return false;
        }
        for (Contacts con : list) {
            dao.addContactToTag(con, tag);
        }
        return true;
    }

    /**
     * 查找标签中的所有联系人
     */
    @Override
    public List<Contacts> findByTag(String color) {
        if (color != null) {
            return dao.findByTag(color);
        }
        return null;
    }

    /**
     * 据联系人ID来查找其所在的组和标签
     */
    public Contacts findGroupTagsById(int id) {
        if (!dao.exists(id)) return null;
        List<Groups> groups = dao.findGroupsByContact(id);
        List<Tags> tags = dao.findTagsByContact(id);
        Contacts con = dao.findById(id);
        con.setGroups(groups);
        con.setTags(tags);
        return con;
    }
}
