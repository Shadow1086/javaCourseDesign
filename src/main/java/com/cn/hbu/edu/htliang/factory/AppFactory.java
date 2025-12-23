package com.cn.hbu.edu.htliang.factory;

import com.cn.hbu.edu.htliang.dao.ContactsDao;
import com.cn.hbu.edu.htliang.dao.ContactsDaoImpl;
import com.cn.hbu.edu.htliang.service.ContactService;
import com.cn.hbu.edu.htliang.service.ContactServiceImpl;

/**
 * 类名: AppFactory
 * 创建时间: 2025/12/23 14:12
 * 项目描述:
 *
 * @author htLiang
 */
public class AppFactory {
    //Dao层的工厂
    public static ContactsDao createContactsDao() {
        return new ContactsDaoImpl();
    }

    //Service
    public static ContactService createContactService() {
        return new ContactServiceImpl(createContactsDao());
    }
}
