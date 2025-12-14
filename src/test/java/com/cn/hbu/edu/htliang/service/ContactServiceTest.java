package com.cn.hbu.edu.htliang.service;

import com.cn.hbu.edu.htliang.entityPojo.Contacts;

import java.util.List;

/**
 * 类名: ContactServiceTest
 * 创建时间: 2025/12/14
 * 项目描述: ContactService 手动测试类
 * 运行方式: 直接运行 main 方法
 *
 * @author htLiang
 */
public class ContactServiceTest {

    private static final ContactService service = new ContactServiceImpl();
    private static int passCount = 0;
    private static int failCount = 0;

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("       ContactService 测试开始");
        System.out.println("========================================\n");

        // 按顺序执行测试
        testAddContact();
        testFindAll();
        testFindById();
        testFindByName();
        testFindByTele();
        testUpdateContact();
        testDeleteById();

        // 输出测试汇总
        System.out.println("\n========================================");
        System.out.println("              测试汇总");
        System.out.println("========================================");
        System.out.println("通过: " + passCount + " 个");
        System.out.println("失败: " + failCount + " 个");
        System.out.println("总计: " + (passCount + failCount) + " 个");
        System.out.println("========================================");
    }

    // ==================== 测试方法 ====================

    /**
     * 测试添加联系人
     */
    private static void testAddContact() {
        printTestHeader("测试添加联系人");
        try {
            service.addContact("测试用户", "13800138000", "010-12345678", "北京市", "test@example.com", "这是测试备注");
            printPass("添加联系人成功");
        } catch (Exception e) {
            printFail("添加联系人失败: " + e.getMessage());
        }
    }

    /**
     * 测试查询所有联系人
     */
    private static void testFindAll() {
        printTestHeader("测试查询所有联系人");
        try {
            List<Contacts> list = service.findAll();
            if (list != null && !list.isEmpty()) {
                printPass("查询到 " + list.size() + " 条记录");
                // 打印前3条数据预览
                int showCount = Math.min(3, list.size());
                System.out.println("  [数据预览]");
                for (int i = 0; i < showCount; i++) {
                    Contacts c = list.get(i);
                    System.out.println("    ID=" + c.getId() + ", 姓名=" + c.getName() + ", 电话=" + c.getTele1());
                }
            } else {
                printPass("数据库为空，返回空列表");
            }
        } catch (Exception e) {
            printFail("查询所有联系人失败: " + e.getMessage());
        }
    }

    /**
     * 测试根据ID查询
     */
    private static void testFindById() {
        printTestHeader("测试根据ID查询");
        try {
            // 先获取一个存在的ID
            List<Contacts> all = service.findAll();
            if (all != null && !all.isEmpty()) {
                int testId = all.get(0).getId();
                Contacts result = service.findId(testId);
                if (result != null) {
                    printPass("ID=" + testId + " 查询成功，姓名=" + result.getName());
                } else {
                    printFail("ID=" + testId + " 查询返回null");
                }
            } else {
                System.out.println("  [跳过] 数据库为空，无法测试");
            }

            // 测试不存在的ID
            Contacts notExist = service.findId(-999);
            if (notExist == null) {
                printPass("不存在的ID查询正确返回null");
            }
        } catch (Exception e) {
            printFail("根据ID查询失败: " + e.getMessage());
        }
    }

    /**
     * 测试根据姓名查询
     */
    private static void testFindByName() {
        printTestHeader("测试根据姓名查询");
        try {
            List<Contacts> result = service.findName("测试用户");
            if (result != null) {
                printPass("姓名查询成功，找到 " + result.size() + " 条记录");
            } else {
                printPass("姓名查询返回null（无匹配数据）");
            }
        } catch (Exception e) {
            printFail("姓名查询失败: " + e.getMessage());
        }
    }

    /**
     * 测试根据电话查询
     */
    private static void testFindByTele() {
        printTestHeader("测试根据电话查询");
        try {
            List<Contacts> result = service.findTele("138");
            if (result != null) {
                printPass("电话查询成功，找到 " + result.size() + " 条记录");
            } else {
                printPass("电话查询返回null（无匹配数据）");
            }
        } catch (Exception e) {
            printFail("电话查询失败: " + e.getMessage());
        }
    }

    /**
     * 测试更新联系人
     */
    private static void testUpdateContact() {
        printTestHeader("测试更新联系人");
        try {
            List<Contacts> all = service.findAll();
            if (all != null && !all.isEmpty()) {
                int testId = all.get(all.size() - 1).getId(); // 取最后一条
                boolean success = service.updateContactInfo(testId, "更新后姓名", "13900139000", "", "", "", "更新测试");
                if (success) {
                    printPass("ID=" + testId + " 更新成功");
                } else {
                    printFail("ID=" + testId + " 更新返回false");
                }
            } else {
                System.out.println("  [跳过] 数据库为空，无法测试");
            }
        } catch (Exception e) {
            printFail("更新联系人失败: " + e.getMessage());
        }
    }

    /**
     * 测试删除联系人
     */
    private static void testDeleteById() {
        printTestHeader("测试删除联系人");
        try {
            // 先添加一条专门用于删除的数据
            service.addContact("待删除用户", "10000000000", "", "", "", "");

            List<Contacts> all = service.findAll();
            if (all != null && !all.isEmpty()) {
                int deleteId = all.get(all.size() - 1).getId();
                service.deleteId(deleteId);

                // 验证是否删除成功
                Contacts deleted = service.findId(deleteId);
                if (deleted == null) {
                    printPass("ID=" + deleteId + " 删除成功");
                } else {
                    printFail("ID=" + deleteId + " 删除后仍能查到");
                }
            }
        } catch (Exception e) {
            printFail("删除联系人失败: " + e.getMessage());
        }
    }

    // ==================== 辅助方法 ====================

    private static void printTestHeader(String testName) {
        System.out.println("【" + testName + "】");
    }

    private static void printPass(String msg) {
        System.out.println("  [通过] " + msg);
        passCount++;
    }

    private static void printFail(String msg) {
        System.out.println("  [失败] " + msg);
        failCount++;
    }
}

