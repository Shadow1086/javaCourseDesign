package com.cn.hub.edu.htliang;

import com.cn.hbu.edu.htliang.pojo.ContactsList;
import org.junit.Test;

/**
 * 类名: ContactsList
 * 创建时间: 2025/12/14 13:29
 * 项目描述:
 *
 * @author htLiang
 */
public class ContactsListTest {
    @Test
    public void initDatabase(){
        ContactsList conList = new ContactsList();
        conList.InitDatabase();
    }
}
