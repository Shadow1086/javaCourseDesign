package com.cn.hub.edu.htliang;

import com.cn.hbu.edu.htliang.service.ContactServiceImpl;
import org.junit.Test;

/**
 * 类名: ContactServeceImplTest
 * 创建时间: 2025/12/14 13:17
 * 项目描述:
 *
 * @author htLiang
 */
public class ContactServeceImplTest {
    @Test
    public void addContactTest(){
        ContactServiceImpl serviceImpl = new ContactServiceImpl();
        serviceImpl.addContact("name","134983925",null,"河北",null,null);
    }
}
