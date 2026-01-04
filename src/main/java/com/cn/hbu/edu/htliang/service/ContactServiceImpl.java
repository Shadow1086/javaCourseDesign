package com.cn.hbu.edu.htliang.service;

import com.cn.hbu.edu.htliang.dao.ContactsDao;
import com.cn.hbu.edu.htliang.entityPojo.Contacts;
import com.cn.hbu.edu.htliang.entityPojo.Groups;
import com.cn.hbu.edu.htliang.entityPojo.Tags;
import com.cn.hbu.edu.htliang.util.ValidationUtil;
import com.cn.hbu.edu.htliang.util.ValidationUtil.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
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
    private final ContactsDao dao;

    // 工厂模式，由外部决定使用什么数据库，可以通过dao层控制切换数据库
    public ContactServiceImpl(ContactsDao contactsDao) {
        this.dao = contactsDao;
    }

    private static final Logger logger = LoggerFactory.getLogger(ContactServiceImpl.class);

    /**
     * 添加单个联系人
     * 姓名等所有信息都需要添加到形参，除了ID
     */
    @Override
    public boolean addContact(Contacts con) {
        ValidationResult result = ValidationUtil.validateContact(con.getName(), con.getTele1(),con.getTele2(),con.getEmail());
        if (!result.isValid()) {
            logger.error("添加联系人失败：{}", result.getMessage());
            throw new RuntimeException(result.getMessage());
        }
        try {
            dao.insert(con);
            logger.debug("添加联系人成功: {}", con);
            return true;
        } catch (Exception e) {
            logger.error("添加联系人失败", e);
            String errorMsg = e.getMessage();
            if (errorMsg != null && errorMsg.contains("UNIQUE constraint failed")) {
                throw new RuntimeException("手机号已存在，请使用其他手机号");
            }
            throw new RuntimeException("添加联系人失败：" + (errorMsg != null ? errorMsg : "数据库操作异常"));
        }
    }

    /**
     * 根据ID查找联系人
     */
    @Override
    public Contacts findById(int id) {
        if (dao.exists(id)) {
            return dao.findById(id);
        }
        logger.warn("未找到ID为{}的联系人", id);
        return null;
    }

    /**
     * 查询所有联系人
     */
    public List<Contacts> findAll() {
        List<Contacts> result = dao.findAll();
        logger.debug("查询到{}条联系人", result.size());
        return result;
    }

    /**
     * 根据姓名查找联系人
     */
    @Override
    public List<Contacts> findByName(String name) {
        List<Contacts> result = dao.findByName(name);
        if (result == null || result.isEmpty()) {
            logger.warn("未找到姓名为{}的联系人", name);
            return null;
        }
        return result;
    }

    /**
     * 通过手机号查询
     */
    public List<Contacts> findByTele(String tele) {
        if (!ValidationUtil.isValidTele(tele)) {
            // 如果输入电话号不合法
            logger.error("通过电话号查询联系人中输入电话号不合法");
            return null;
        } else {
            return dao.findByTele(tele);
        }
    }

    /**
     * 根据ID删除联系人
     * 
     * @param id : 要删除的联系人的ID
     */
    @Override
    public boolean deleteById(int id) {
        if (dao.exists(id)) {
            dao.deleteById(id);
            logger.debug("删除联系人成功, id={}", id);
            return true;
        } else {
            logger.warn("删除失败，未找到ID为{}的联系人", id);
            return false;
        }
    }

    /**
     * 根据ID修改联系人信息
     */
    @Override
    public boolean updateContactInfo(Contacts con) {
        int id = con.getId();
        String newName = con.getName();
        String newTele1 = con.getTele1();
        String newTele2 = con.getTele2();
        String newEmail = con.getEmail();
        String newHome = con.getHome();
        String newNotes  = con.getNotes();
        try {
            if (!ValidationUtil.isValidName(newName)) {
                logger.error("修改ID为：{} 的联系人失败，原因：姓名格式不正确", id);
                throw new RuntimeException("联系人姓名格式不正确，请重新检查");
            }
            if (!ValidationUtil.isValidTele(newTele1)) {
                logger.error("修改ID为：{}的联系人信息错误：原因：电话号格式错误", id);
                throw new RuntimeException("联系人电话号格式不正确，请重新检查");
            }
            if (newTele2 != null && !newTele2.isBlank() && !ValidationUtil.isValidTele(newTele2)) {
                logger.error("修改ID为：{}的联系人信息错误：原因：备用电话号格式错误", id);
                throw new RuntimeException("备用电话号格式不正确，请重新检查");
            }

            if (newEmail != null && !newEmail.isBlank() && !ValidationUtil.isValidEmail(newEmail)) {
                logger.error("修改ID为：{}的联系人信息错误：原因：邮箱格式错误", id);
                throw new RuntimeException("联系人邮箱格式不正确，请重新检查");
            }
            dao.updateInfo(id, newName, newTele1, newTele2, newHome, newEmail, newNotes);
            logger.debug("修改联系人成功, id={}", id);
            return true;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            logger.error("修改联系人信息失败, id={}", id, e);
            String errorMsg = e.getMessage();
            if (errorMsg != null && errorMsg.contains("UNIQUE constraint failed")) {
                throw new RuntimeException("手机号已存在，请使用其他手机号");
            }
            throw new RuntimeException("修改联系人失败：" + (errorMsg != null ? errorMsg : "数据库操作异常"));
        }
    }

    // 分组操作

    /**
     * 创建新分组
     */
    public boolean addGroup(String name, String notes) {
        if (name == null || name.isEmpty()) {
            logger.warn("创建分组失败：名称不能为空");
            return false;
        }
        dao.addGroup(name, notes);
        logger.debug("创建分组成功: {}", name);
        return true;
    }

    /**
     * 向分组中批量添加联系人，也可以单个添加
     */
    @Override
    public boolean addContactInGroup(List<Contacts> list, Groups groups) {
        if (list == null || list.isEmpty()) {
            logger.warn("添加联系人到分组失败：列表为空");
            return false;
        }
        for (Contacts con : list) {
            dao.addContactInGroup(con, groups);
        }
        logger.debug("批量添加{}个联系人到分组", list.size());
        return true;
    }

    /**
     * 根据分组来删除分组
     */
    @Override
    public boolean deleteGroup(String name) {
        if (name == null || name.isEmpty()) {
            logger.warn("删除分组失败：组名为空");
            return false;
        }
        return dao.deleteGroup(name);
    }

    /**
     * 查看某分组中包含的所有联系人
     */
    @Override
    public List<Contacts> findByGroup(String name) {
        if (name == null || name.isEmpty()) {
            logger.warn("查询分组联系人失败：组名为空");
            return null;
        }
        return dao.findByGroup(name);
    }

    /**
     * 查找所有分组
     */
    @Override
    public List<Groups> findAllGroup() {
        return dao.findAllGroup();
    }

    // 标签操作

    /**
     * 新建标签
     */
    @Override
    public boolean addTag(String color, String name, String notes) {
        if (color == null || color.isEmpty()) {
            logger.warn("新建标签失败：颜色不能为空");
            return false;
        }
        dao.addTag(color, name, notes);
        logger.debug("新建标签成功: color={}, name={}", color, name);
        return true;
    }

    /**
     * 根据标签颜色 删除标签
     */
    @Override
    public boolean deleteTag(String color) {
        if (color == null || color.isEmpty()) {
            logger.warn("删除标签失败：颜色为空");
            return false;
        }
        return dao.deleteTag(color);
    }

    /**
     * 向标签中添加联系人
     */
    public boolean addContactToTag(List<Contacts> list, Tags tag) {
        if (list == null || list.isEmpty()) {
            logger.warn("添加联系人到标签失败：列表为空");
            return false;
        }
        for (Contacts con : list) {
            dao.addContactToTag(con, tag);
        }
        logger.debug("批量添加{}个联系人到标签", list.size());
        return true;
    }

    /**
     * 查找标签中的所有联系人
     */
    @Override
    public List<Contacts> findByTag(String color) {
        if (color == null || color.isEmpty()) {
            logger.warn("查询标签联系人失败：颜色为空");
            return null;
        }
        return dao.findByTag(color);
    }

    /**
     * 据联系人ID来查找其所在的组和标签
     */
    public Contacts findGroupTagsById(int id) {
        if (!dao.exists(id)) {
            logger.warn("未找到ID为{}的联系人", id);
            return null;
        }
        List<Groups> groups = dao.findGroupsByContact(id);
        List<Tags> tags = dao.findTagsByContact(id);
        Contacts con = dao.findById(id);
        con.setGroups(groups);
        con.setTags(tags);
        return con;
    }

    /**
     * 来判断联系人信息中的值值不值得写入文件中
     */
    private static boolean hasValue(String s) {
        return s != null && !s.isBlank() && !"null".equalsIgnoreCase(s);
    }

    /**
     * 写入文件
     */
    private static void writeProp(BufferedWriter bw, String key, String value) throws IOException {
        if (hasValue(value)) {
            bw.write(key + ":" + value + "\r\n");
        }
    }

    /**
     * 从文件中读取联系人信息，一列表形式返回
     */

    public List<Contacts> readVcfFile(File file) {
        List<Contacts> list = new ArrayList<>();
        if (!file.getName().endsWith(".vcf")) {
            return list;
        }
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            String line;
            Contacts con = null;
            while ((line = br.readLine()) != null) {
                if (line.equalsIgnoreCase("BEGIN:VCARD"))
                    con = new Contacts();
                else if (line.equalsIgnoreCase("END:vcard")) {
                    if (con != null) {
                        list.add(con);
                        con = null;
                    }
                } else if (con != null) {
                    parseEachRowInVcf(line, con);
                }
            }
        } catch (IOException e) {
            logger.error("读取VCF文件失败: {}", file.getName(), e);
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (IOException e) {
                logger.error("关闭文件流失败", e);
            }
        }
        return list;
    }

    /**
     * 逐行处理文件中的内容
     */
    public void parseEachRowInVcf(String line, Contacts con) {
        if (line.startsWith("VERSION:")) {
        } else if (line.startsWith("N:") || line.startsWith("N;")) {
            String name = "";
            String nameWithN = line.split(":", -1)[1];
            String[] nameList = nameWithN.split(";", -1);
            for (String part : nameList) {
                name += part;
            }
            con.setName(name);
        } else if (line.startsWith("TEL")) {
            String value = extractValue(line);
            if (line.toUpperCase().contains("CELL") || line.toUpperCase().contains("MOBILE")) {
                if (con.getTele1() == null || con.getTele1().isBlank()) {
                    con.setTele1(value);
                } else if (con.getTele2() == null || con.getTele2().isBlank()) {
                    if (!con.getTele1().equals(value))
                        con.setTele2(value);
                } else {
                    logger.debug("多余的电话号保存在备注中: {}", value);
                    if (con.getNotes() != null) {
                        con.setNotes(con.getNotes() + "\n备注电话号：" + value);
                    } else {
                        con.setNotes("备注电话号：" + value);
                    }
                }
            } else if (line.toUpperCase().contains("WORK")) {
                con.setTele1(value);
            }
        } else if (line.startsWith("EMAIL")) {
            String value = extractValue(line);
            con.setEmail(value);
        } else if (line.startsWith("ADR")) {
            String value = extractValue(line);
            String[] list = value.split(";");
            value = "";
            for (int i = list.length - 1; i >= 0; i--) {
                value += (list[i]);
                if (i > 0)
                    value += ",";
            }
            con.setHome(value);
        } else if (line.startsWith("NOTE")) {
            String value = extractValue(line);
            con.setNotes(value);
        } else {
            if (con.getNotes() != null) {
                String value = con.getNotes() + extractValue(line) + "\t";
                con.setNotes(value);
            } else {
                String value = extractValue(line) + "\t";
                con.setNotes(value);
            }
        }
    }

    /**
     * 提取一行中冒号以后的字符串
     */
    public String extractValue(String line) {
        int index = line.indexOf(':');
        if (index != -1) {
            return line.substring(index + 1);
        }
        return "";
    }

    /**
     * 导出联系人
     */
    public File writeVcfFile(File file) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            List<String> list = dao.writeVcfFileInService(bw);
            for (String str : list) {
                String[] part = str.split(";");
                bw.write("\nBEGIN:VCARD\nVERSION:3.0\nFN:" + part[0] +
                        "\nTEL:" + part[1] + "\n");
                writeProp(bw, "TEL", part[2]);
                writeProp(bw, "ADR", part[3]);
                writeProp(bw, "EMAIL", part[4]);

                String notes = "";
                if (hasValue(part[5]))
                    notes += part[5]; // 备注本身
                if (hasValue(part[6]))
                    notes += "X-GROUP : " + part[6]; // 分组信息
                if (hasValue(part[7]))
                    notes += "X-TAG : " + part[7];

                writeProp(bw, "NOTE", notes);

                bw.write("\nEND:VCARD");
            }
            logger.debug("导出联系人文件成功: {}", file.getName());
        } catch (IOException e) {
            logger.error("导出联系人文件失败: {}", file.getName(), e);
        }
        return file;
    }

    /**
     * 导入联系人列表
     */
    @Override
    public int importVcfFile(File file) {
        List<Contacts> contacts = readVcfFile(file);
        if (contacts == null || contacts.isEmpty())
            return 0;

        List<Contacts> readyToInsert = new ArrayList<>();
        for (Contacts c : contacts) {
            if (c == null)
                continue;
            if ((c.getTele1() == null || c.getTele1().isBlank()) && hasValue(c.getTele2())) {
                c.setTele1(c.getTele2());
                c.setTele2(null);
            }
            if (!hasValue(c.getName()) || !hasValue(c.getTele1()))
                continue;
            readyToInsert.add(c);
        }
        if (readyToInsert.isEmpty()) {
            return 0;
        }
        dao.batchInsert(readyToInsert);
        logger.debug("从文件导入{}条联系人", readyToInsert.size());
        return readyToInsert.size();
    }

    @Override
    public File exportVcfFile(File file) {
        return writeVcfFile(file);
    }

}
