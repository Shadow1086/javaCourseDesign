package com.cn.hbu.edu.htliang.service;

import com.cn.hbu.edu.htliang.dao.ContactsDaoImpl;
import com.cn.hbu.edu.htliang.entityPojo.Contacts;
import com.cn.hbu.edu.htliang.entityPojo.Groups;
import com.cn.hbu.edu.htliang.entityPojo.Tags;

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
                if (line.equalsIgnoreCase("BEGIN:VCARD")) con = new Contacts();
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
            e.printStackTrace();
        } finally {
            try {
                if (br != null) br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 逐行处理文件中的内容
     */
    public void parseEachRowInVcf(String line, Contacts con) {
        if (line.startsWith("VERSION:")) {
        }
        else if (line.startsWith("N:") || line.startsWith("N;")) {
            String name = "";
            String[] nameList = line.split(";", -1);
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
                    if (!con.getTele1().equals(value)) con.setTele2(value);
                } else {
                    System.out.println("出错了，多余的电话号保存在了note备注中");
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
                if (i > 0) value += ",";
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
                String[] part = str.split(",");
                bw.write("\nBEGIN:VCARD\nVERSION:3.0\nFN:" + part[0] +
                        "\nTEL:" + part[1] + "\n");
                writeProp(bw, "TEL", part[2]);
                writeProp(bw, "ADR", part[3]);
                writeProp(bw, "EMAIL", part[4]);

                String notes = "";
                if (hasValue(part[5])) notes += part[5]; //备注本身
                if (hasValue(part[6])) notes += "X-GROUP : " + part[6]; //分组信息
                if (hasValue(part[7])) notes += "X-TAG : " + part[7];

                writeProp(bw, "NOTE", notes);

                bw.write("\nEND:VCARD");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    @Override
    public int importVcfFile(File file) {
        List<Contacts> contacts = readVcfFile(file);
        if (contacts == null || contacts.isEmpty()) return 0;

        List<Contacts> readyToInsert = new ArrayList<>();
        for (Contacts c : contacts) {
            if (c == null) continue;
            if ((c.getTele1() == null || c.getTele1().isBlank()) && hasValue(c.getTele2())) {
                c.setTele1(c.getTele2());
                c.setTele2(null);
            }
            if (!hasValue(c.getName()) || !hasValue(c.getTele1())) continue;
            readyToInsert.add(c);
        }
        if (readyToInsert.isEmpty()) return 0;
        dao.batchInsert(readyToInsert);
        return readyToInsert.size();
    }

    @Override
    public File exportVcfFile(File file) {
        return writeVcfFile(file);
    }
}
