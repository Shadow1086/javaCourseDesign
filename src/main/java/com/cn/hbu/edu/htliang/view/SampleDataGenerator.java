//package com.cn.hbu.edu.htliang.view;
//
//import com.cn.hbu.edu.htliang.entityPojo.Contacts;
//import com.cn.hbu.edu.htliang.entityPojo.Groups;
//import com.cn.hbu.edu.htliang.entityPojo.Tags;
//import com.cn.hbu.edu.htliang.service.ContactService;
//import com.cn.hbu.edu.htliang.service.ContactServiceImpl;
//import com.cn.hbu.edu.htliang.util.DBUtil;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Objects;
//
///**
// * 一键插入测试数据：20 个联系人，部分有分组/标签，部分没有。
// * 仅写在 view 层，调用现有 Service/DAO，不改其他层。
// */
//public class SampleDataGenerator {
//    private final ContactService contactService = new ContactServiceImpl();
//
//    public static void main(String[] args) {
//        new SampleDataGenerator().seed();
//        System.out.println("测试数据插入完毕");
//    }
//
//    public void seed() {
//        // 准备分组与标签
//        ensureGroups();
//        ensureTags();
//
//        // 分组名 -> Groups 映射（含 id）
//        Map<String, Groups> groupMap = loadGroupMap();
//
//        // 20 条联系人样本
//        List<ContactSeed> seeds = buildSeeds();
//        for (ContactSeed seed : seeds) {
//            if (!contactExists(seed.name, seed.tele1)) {
//                contactService.addContact(seed.name, seed.tele1, seed.tele2, seed.home, seed.email, seed.notes);
//            }
//            Contacts inserted = getContactByNameTele(seed.name, seed.tele1);
//            if (inserted == null) {
//                continue; // 未找到就跳过，避免空指针
//            }
//
//            // 分组
//            if (seed.groupName != null && groupMap.containsKey(seed.groupName)) {
//                Groups g = groupMap.get(seed.groupName);
//                List<Contacts> one = new ArrayList<>();
//                one.add(inserted);
//                contactService.addContactInGroup(one, g);
//            }
//
//            // 标签
//            if (seed.tags != null) {
//                for (String color : seed.tags) {
//                    Integer tagId = findTagIdByColor(color);
//                    if (tagId == null) {
//                        continue;
//                    }
//                    Tags t = new Tags();
//                    t.setId(tagId);
//                    t.setTag_color(color);
//                    List<Contacts> one = new ArrayList<>();
//                    one.add(inserted);
//                    contactService.addContactToTag(one, t);
//                }
//            }
//        }
//    }
//
//    private void ensureGroups() {
//        String[][] groups = {
//                {"家人", "家里联系人"},
//                {"朋友", "朋友分组"},
//                {"同事", "同事分组"},
//                {"同学", "同学分组"}
//        };
//        Map<String, Groups> existing = loadGroupMap();
//        for (String[] g : groups) {
//            if (!existing.containsKey(g[0])) {
//                contactService.addGroup(g[0], g[1]);
//            }
//        }
//    }
//
//    private void ensureTags() {
//        String[][] tags = {
//                {"#FF6666", "紧急", "紧急联系"},
//                {"#4A90E2", "工作", "工作相关"},
//                {"#4CAF50", "朋友", "朋友标签"},
//                {"#F5A623", "同学", "同学标签"},
//                {"#9B59B6", "重要", "重要标记"},
//                {"#FFB74D", "备用", "备用联系方式"}
//        };
//        for (String[] t : tags) {
//            if (findTagIdByColor(t[0]) == null) {
//                contactService.addTag(t[0], t[1], t[2]);
//            }
//        }
//    }
//
//    private Map<String, Groups> loadGroupMap() {
//        Map<String, Groups> map = new HashMap<>();
//        List<Groups> groups = contactService.findAllGroup();
//        if (groups != null) {
//            for (Groups g : groups) {
//                map.put(g.getGroup_name(), g);
//            }
//        }
//        return map;
//    }
//
//    private boolean contactExists(String name, String tele1) {
//        List<Contacts> list = contactService.findByName(name);
//        if (list == null) return false;
//        for (Contacts c : list) {
//            if (Objects.equals(tele1, c.getTele1())) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    private Contacts getContactByNameTele(String name, String tele1) {
//        List<Contacts> list = contactService.findByName(name);
//        if (list == null) return null;
//        for (Contacts c : list) {
//            if (Objects.equals(tele1, c.getTele1())) {
//                return c;
//            }
//        }
//        return null;
//    }
//
//    private Integer findTagIdByColor(String color) {
//        String sql = "select id from tags where tag_color = ? limit 1";
//        try (Connection conn = DBUtil.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//            ps.setString(1, color);
//            try (ResultSet rs = ps.executeQuery()) {
//                if (rs.next()) {
//                    return rs.getInt("id");
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    private List<ContactSeed> buildSeeds() {
//        List<ContactSeed> list = new ArrayList<>();
//        list.add(new ContactSeed("李雷", "13800000001", "13800001001", "北京海淀", "lilei@example.com", "家人联系", "家人", new String[]{"#FF6666"}));
//        list.add(new ContactSeed("韩梅梅", "13800000002", "", "上海浦东", "han@example.com", "高中同学", "同学", new String[]{"#F5A623"}));
//        list.add(new ContactSeed("王强", "13800000003", "", "深圳南山", "wang@example.com", "项目同事", "同事", new String[]{"#4A90E2"}));
//        list.add(new ContactSeed("张三", "13800000004", "", "广州天河", "zhangsan@example.com", "朋友聚会", "朋友", new String[]{"#4CAF50"}));
//        list.add(new ContactSeed("李四", "13800000005", "", "杭州西湖", "lisi@example.com", "重要合作方", "同事", new String[]{"#9B59B6"}));
//        list.add(new ContactSeed("小红", "13800000006", "", "成都武侯", "hong@example.com", "大学同学", "同学", new String[]{"#F5A623"}));
//        list.add(new ContactSeed("小刚", "13800000007", "", "武汉汉口", "gang@example.com", "朋友", "朋友", new String[]{"#4CAF50"}));
//        list.add(new ContactSeed("Alice", "13800000008", "", "北京朝阳", "alice@example.com", "无分组示例", null, null));
//        list.add(new ContactSeed("Bob", "13800000009", "", "上海杨浦", "bob@example.com", "无标签示例", "同事", null));
//        list.add(new ContactSeed("Carol", "13800000010", "", "南京鼓楼", "carol@example.com", "双标签示例", "朋友", new String[]{"#4CAF50", "#FFB74D"}));
//        list.add(new ContactSeed("Dave", "13800000011", "", "苏州工业园", "dave@example.com", "备用号", "家人", new String[]{"#FFB74D"}));
//        list.add(new ContactSeed("Eve", "13800000012", "", "长沙岳麓", "eve@example.com", "工作同事", "同事", new String[]{"#4A90E2"}));
//        list.add(new ContactSeed("Frank", "13800000013", "", "重庆渝中", "frank@example.com", "仅标签", null, new String[]{"#9B59B6"}));
//        list.add(new ContactSeed("Grace", "13800000014", "", "合肥高新", "grace@example.com", "仅分组", "朋友", null));
//        list.add(new ContactSeed("Heidi", "13800000015", "", "厦门思明", "heidi@example.com", "同事+重要", "同事", new String[]{"#9B59B6", "#4A90E2"}));
//        list.add(new ContactSeed("Ivan", "13800000016", "", "青岛市南", "ivan@example.com", "无分组标签", null, null));
//        list.add(new ContactSeed("Judy", "13800000017", "", "天津和平", "judy@example.com", "同学+备用", "同学", new String[]{"#F5A623", "#FFB74D"}));
//        list.add(new ContactSeed("Kevin", "13800000018", "", "郑州金水", "kevin@example.com", "朋友+紧急", "朋友", new String[]{"#FF6666"}));
//        list.add(new ContactSeed("Lucy", "13800000019", "", "佛山南海", "lucy@example.com", "家人+备用", "家人", new String[]{"#FFB74D"}));
//        list.add(new ContactSeed("Mike", "13800000020", "", "大连中山", "mike@example.com", "工作+同学", "同事", new String[]{"#4A90E2", "#F5A623"}));
//        return list;
//    }
//
//    private static class ContactSeed {
//        String name;
//        String tele1;
//        String tele2;
//        String home;
//        String email;
//        String notes;
//        String groupName;
//        String[] tags;
//
//        ContactSeed(String name, String tele1, String tele2, String home, String email, String notes, String groupName, String[] tags) {
//            this.name = name;
//            this.tele1 = tele1;
//            this.tele2 = tele2;
//            this.home = home;
//            this.email = email;
//            this.notes = notes;
//            this.groupName = groupName;
//            this.tags = tags;
//        }
//    }
//}
