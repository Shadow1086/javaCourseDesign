package com.cn.hbu.edu.htliang.util;

import java.util.regex.Pattern;

/**
 * 类名: ValidationUtil
 * 创建时间: 2025/12/23 14:31
 * 项目描述:
 * <p>
 * 用来判断用户输入格式是否正确
 *
 * @author htLiang
 */
public class ValidationUtil {
    private static final Pattern TELE_PATTERN = Pattern.compile("1\\d{10}");    //格式：1xx xxxx xxxx
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{3,}$");  //格式：xxxxx@xxx.xxx
    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\u4e00-\\u9fa5a-zA-Z\\s]{2,50}$");      //格式：中英文可以混合

    //判断手机号
    public static boolean isValidName(String name) {
        if (name == null || name.isBlank()) return false;
        return NAME_PATTERN.matcher(name.trim()).matches();
    }

    //判断电话号
    public static boolean isValidTele(String tele) {
        if (tele == null || tele.isBlank()) return false;
        return TELE_PATTERN.matcher(tele.trim()).matches();
    }

    //判断邮箱格式
    public static boolean isValidEmail(String email) {
        if (email == null || email.isBlank()) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * 验证联系人信息格式是否正确
     *
     * @param name  姓名
     * @param tele  必填的电话号
     * @param tele2 选填的电话号
     * @param email 邮箱
     */
    public static ValidationResult validateContact(String name, String tele, String tele2, String email) {
        if (name == null || name.isBlank()) {
            return ValidationResult.error("联系人姓名不能为空");
        }
        if (!isValidName(name)) {
            return ValidationResult.error("联系人姓名格式不正确，请重新检查");
        }

        if (tele == null || tele.isBlank()) {
            return ValidationResult.error("联系人电话不能为空");
        }
        if (!isValidTele(tele)) {
            return ValidationResult.error("联系人电话号格式不正确，请重新检查");
        }

        if (tele2 != null && !tele2.isBlank() && !isValidTele(tele2)) {
            return ValidationResult.error("联系人电话格式不正确，请重新检查");
        }
        if (email != null && !email.isBlank() && !isValidEmail(email)) {
            return ValidationResult.error("联系人邮箱格式不正确，请中心检查");
        }
        return ValidationResult.success();
    }

    //验证结果类
        public record ValidationResult(boolean valid, String message) {

        public static ValidationResult success() {
                return new ValidationResult(true, null);
            }

            public static ValidationResult error(String message) {
                return new ValidationResult(false, message);
            }

        public boolean isValid() {
            return valid;
        }

        public String getMessage() {
            return message;
        }
        }

}
