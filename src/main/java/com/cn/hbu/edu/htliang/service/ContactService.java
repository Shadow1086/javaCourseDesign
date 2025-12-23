package com.cn.hbu.edu.htliang.service;
import com.cn.hbu.edu.htliang.entityPojo.Contacts;
import com.cn.hbu.edu.htliang.entityPojo.Groups;
import com.cn.hbu.edu.htliang.entityPojo.Tags;

import java.io.File;
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
     *
     * @param name,tele1,tele2,home,email,notes
     */
    boolean addContact(String name, String tele1, String tele2, String home, String email, String notes);

    /**
     * 通过ID查询联系人
     */
    Contacts findById(int id);

    /**
    * 查询所有联系人
    */
    List<Contacts> findAll();
    /**
     * 通过姓名查询联系人
     */
    List<Contacts> findByName(String name);
    /**
    * 通过手机号查询
    */
    List<Contacts> findByTele(String tele);
    /**
     * 根据ID删除联系人
     */
    boolean deleteById(int id);

    /**
     * 根据ID来更改联系人信息
     */
    boolean updateContactInfo(int id, String newName, String newTele1, String newTele2, String newHome, String newEmail, String newNotes);

// 分组操作

    boolean addGroup(String name, String notes);

    //向分组中添加联系人
    boolean addContactInGroup(List<Contacts> list, Groups groups);

    //删除分组
    boolean deleteGroup(String name);

    //查看某分组中的联系人
    List<Contacts> findByGroup(String name);

    //查看所有分组
    List<Groups> findAllGroup();
// 标签操作

    //创建新标签
    boolean addTag(String color, String name, String notes);

    //根据标签颜色删除标签
    boolean deleteTag(String color);

    //向标签中添加联系人
    boolean addContactToTag(List<Contacts> list, Tags tag);

    //查找标签中所有联系人
    List<Contacts> findByTag(String color);

    //根据联系人ID来查找其所在的组和标签
    Contacts findGroupTagsById(int id);

    /**
     * 从 vcf 文件导入联系人到数据库，返回成功导入的数量。
     */
    int importVcfFile(File file);

    /**
     * 将数据库中的联系人导出为 vcf 文件，返回写入后的文件对象（同入参）。
     */
    File exportVcfFile(File file);
}
