package com.cn.hbu.edu.htliang.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class ValidationUtilTest {

    @Test
    public void isValidName_rejectsBlankAndBadFormat() {
        assertFalse(ValidationUtil.isValidName(null));
        assertFalse(ValidationUtil.isValidName(""));
        assertFalse(ValidationUtil.isValidName(" "));
        assertFalse(ValidationUtil.isValidName("A"));
        assertFalse(ValidationUtil.isValidName("John@Doe"));
    }

    @Test
    public void isValidName_acceptsChineseAndEnglish() {
        assertTrue(ValidationUtil.isValidName("张三"));
        assertTrue(ValidationUtil.isValidName("Alice Zhang"));
        assertTrue(ValidationUtil.isValidName("王五 Smith"));
    }

    @Test
    public void isValidTele_validatesMainlandNumber() {
        assertTrue(ValidationUtil.isValidTele("13800138000"));
        assertFalse(ValidationUtil.isValidTele("123456"));
        assertFalse(ValidationUtil.isValidTele("23800138000"));
    }

    @Test
    public void isValidEmail_checksFormat() {
        assertTrue(ValidationUtil.isValidEmail("user@example.com"));
        assertTrue(ValidationUtil.isValidEmail("user.name+tag@example.com"));
        assertFalse(ValidationUtil.isValidEmail("user@example"));
        assertFalse(ValidationUtil.isValidEmail("@example.com"));
    }

    @Test
    public void validateContact_checksAllFields() {
        ValidationUtil.ValidationResult result;

        result = ValidationUtil.validateContact(null, "13800138000", null, null);
        assertFalse(result.isValid());
        assertEquals("联系人姓名不能为空", result.getMessage());

        result = ValidationUtil.validateContact("A", "13800138000", null, null);
        assertFalse(result.isValid());
        assertEquals("联系人姓名格式不正确，请重新检查", result.getMessage());

        result = ValidationUtil.validateContact("张三", null, null, null);
        assertFalse(result.isValid());
        assertEquals("联系人电话不能为空", result.getMessage());

        result = ValidationUtil.validateContact("张三", "123", null, null);
        assertFalse(result.isValid());
        assertEquals("联系人电话号格式不正确，请重新检查", result.getMessage());

        result = ValidationUtil.validateContact("张三", "13800138000", "123", null);
        assertFalse(result.isValid());
        assertEquals("联系人电话格式不正确，请重新检查", result.getMessage());

        result = ValidationUtil.validateContact("张三", "13800138000", null, "bad@") ;
        assertFalse(result.isValid());
        assertEquals("联系人邮箱格式不正确，请中心检查", result.getMessage());

        result = ValidationUtil.validateContact("张三", "13800138000", "13900139000", "user@example.com");
        assertTrue(result.isValid());
        assertNull(result.getMessage());
    }
}
