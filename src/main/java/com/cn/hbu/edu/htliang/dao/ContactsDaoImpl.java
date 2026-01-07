package com.cn.hbu.edu.htliang.dao;

import com.cn.hbu.edu.htliang.entityPojo.Contacts;
import com.cn.hbu.edu.htliang.entityPojo.Groups;
import com.cn.hbu.edu.htliang.entityPojo.Tags;
import com.cn.hbu.edu.htliang.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 类名: ContactsDaoImpl
 * 创建时间: 2025/12/12 20:41
 * 项目描述:
 *
 * @author htLiang
 */
public class ContactsDaoImpl implements ContactsDao {
    private static final Logger logger = LoggerFactory.getLogger(ContactsDaoImpl.class);

    /**
     * 插入单个联系人方法
     */
    @Override
    public void insert(Contacts con) {
        String sql = "INSERT INTO contacts (name, tele1, tele2, home, email, notes) VALUES (?,?,?,?,?,?)";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, con.getName());
            ps.setString(2, con.getTele1());
            ps.setString(3, con.getTele2());
            ps.setString(4, con.getHome());
            ps.setString(5, con.getEmail());
            ps.setString(6, con.getNotes());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    con.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            logger.error("插入联系人失败: {}", con.getName(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 插入多个联系人方法,可以应用在从文件导入中
     */
    @Override
    public void batchInsert(List<Contacts> contacts) {
        String sql = "INSERT INTO contacts (name, tele1, tele2, home, email, notes) VALUES (?,?,?,?,?,?)";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Contacts con : contacts) {
                ps.setString(1, con.getName());
                ps.setString(2, con.getTele1());
                ps.setString(3, con.getTele2());
                ps.setString(4, con.getHome());
                ps.setString(5, con.getEmail());
                ps.setString(6, con.getNotes());
                ps.addBatch();
            }
            ps.executeBatch();
            logger.debug("批量插入{}条联系人成功", contacts.size());
        } catch (SQLException e) {
            logger.error("批量插入联系人失败", e);
        }
    }

    /**
     * 查询：通过ID查询
     */
    @Override
    public Contacts findById(int id) {
        String sql = "SELECT * FROM contacts WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return getContactsFromRsNext(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("查询联系人失败, id={}", id, e);
        }
        return null;
    }

    /**
     * 查询：查询所有数据
     */
    @Override
    public List<Contacts> findAll() {
        List<Contacts> list = new ArrayList<>();
        String sql = "SELECT * FROM contacts";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(getContactsFromRsNext(rs));
            }
        } catch (SQLException e) {
            logger.error("查询所有联系人失败", e);
        }
        return list;
    }

    /**
     * 根据姓名查询,需要在service中去调用这个方法，使用返回的列表展示在GUI上
     *
     * @return List<Contacts> 以姓名查询的联系人列表
     */
    @Override
    public List<Contacts> findByName(String name) {
        List<Contacts> list = new ArrayList<>();
        String sql = "SELECT * FROM contacts WHERE name LIKE ?";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + name + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(getContactsFromRsNext(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("按姓名查询联系人失败, name={}", name, e);
        }
        return list;
    }

    /**
     * 根据电话号码查询
     */
    @Override
    public List<Contacts> findByTele(String tele) {
        List<Contacts> list = new ArrayList<>();
        String sql = "SELECT * FROM contacts WHERE tele1 = ? OR tele2 = ?";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tele);
            ps.setString(2, tele);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(getContactsFromRsNext(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("按电话查询联系人失败, tele={}", tele, e);
        }
        return list;
    }

    /**
     * 更新数据
     */
    @Override
    public void update(Contacts con) {
        String sql = "UPDATE contacts SET name=?, tele1=?, tele2=?, home=?, email=?, notes=? WHERE id=?";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, con.getName());
            ps.setString(2, con.getTele1());
            ps.setString(3, con.getTele2());
            ps.setString(4, con.getHome());
            ps.setString(5, con.getEmail());
            ps.setString(6, con.getNotes());
            ps.setInt(7, con.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("更新联系人失败, id={}", con.getId(), e);
        }
    }

    /**
     * 根据ID更新数据
     */
    @Override
    public void updateInfo(int id, String newName, String newTele1, String newTele2, String newHome, String newEmail,
            String newNotes) {
        String sql = "UPDATE contacts SET name=?, tele1=?, tele2=?, home=?, email=?, notes=? WHERE id=?";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newName);
            ps.setString(2, newTele1);
            ps.setString(3, newTele2);
            ps.setString(4, newHome);
            ps.setString(5, newEmail);
            ps.setString(6, newNotes);
            ps.setInt(7, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("更新联系人信息失败, id={}", id, e);
        }
    }

    /**
     * 根据ID删除联系人
     */
    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM contacts WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("删除联系人失败, id={}", id, e);
        }
    }

    /**
     * 判断联系人是否存在
     */
    @Override
    public boolean exists(int id) {
        String sql = "SELECT 1 FROM contacts WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.error("检查联系人是否存在失败, id={}", id, e);
        }
        return false;
    }

    /**
     * 新建分组
     */
    @Override
    public boolean addGroup(String name, String notes) {
        String sql = "INSERT INTO groups (group_name, group_notes) VALUES (?,?)";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, notes);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            logger.error("创建分组失败, name={}", name, e);
        }
        return false;
    }

    /**
     * 向分组中添加联系人
     */
    @Override
    public boolean addContactInGroup(Contacts con, Groups group) {
        String sql = "INSERT INTO contacts_group (contacts_id, group_id) VALUES (?,?)";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, con.getId());
            ps.setInt(2, group.getId());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            logger.error("添加联系人到分组失败, contactId={}, groupId={}", con.getId(), group.getId(), e);
        }
        return false;
    }

    /**
     * 删除选择的分组
     */
    @Override
    public boolean deleteGroup(String name) {
        String sql = "DELETE FROM groups WHERE group_name = ?";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            logger.error("删除分组失败, name={}", name, e);
        }
        return false;
    }

    /**
     * 查看分组中的全部联系人
     */
    @Override
    public List<Contacts> findByGroup(String name) {
        List<Contacts> list = new ArrayList<>();
        String sql = """
                SELECT c.id, c.name, c.tele1, c.tele2, c.home, c.email, c.notes
                FROM contacts c
                JOIN contacts_group cg ON c.id = cg.contacts_id
                JOIN groups g ON g.id = cg.group_id
                WHERE g.group_name = ?
                """;
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(getContactsFromRsNext(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("查询分组联系人失败, groupName={}", name, e);
        }
        return list;
    }

    /**
     * 显示所有分组
     */
    @Override
    public List<Groups> findAllGroup() {
        List<Groups> list = new ArrayList<>();
        String sql = "SELECT * FROM groups";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Groups group = new Groups(rs.getInt("id"), rs.getString("group_name"), rs.getString("group_notes"));
                list.add(group);
            }
        } catch (SQLException e) {
            logger.error("查询所有分组失败", e);
        }
        return list;
    }

    // 标签操作

    /**
     * 新建标签
     */
    @Override
    public boolean addTag(String color, String name, String notes) {
        String sql = "INSERT INTO tags (tag_color, tag_name, tag_notes) VALUES (?,?,?)";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, color);
            ps.setString(2, name);
            ps.setString(3, notes);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            logger.error("创建标签失败, color={}", color, e);
        }
        return false;
    }

    /**
     * 根据标签颜色删除标签
     */
    @Override
    public boolean deleteTag(String color) {
        String sql = "DELETE FROM tags WHERE tag_color = ?";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, color);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            logger.error("删除标签失败, color={}", color, e);
        }
        return false;
    }

    /**
     * 向标签中添加联系人
     */
    @Override
    public boolean addContactToTag(Contacts con, Tags tag) {
        String sql = "INSERT INTO tag_contacts (contacts_id, tag_id) VALUES (?,?)";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, con.getId());
            ps.setInt(2, tag.getId());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            logger.error("添加联系人到标签失败, contactId={}, tagId={}", con.getId(), tag.getId(), e);
        }
        return false;
    }

    /**
     * 查找标签中所有联系人
     */
    @Override
    public List<Contacts> findByTag(String color) {
        List<Contacts> list = new ArrayList<>();
        String sql = """
                SELECT c.id, c.name, c.tele1, c.tele2, c.home, c.email, c.notes
                FROM contacts c
                JOIN tag_contacts tc ON c.id = tc.contacts_id
                JOIN tags t ON t.id = tc.tag_id
                WHERE t.tag_color = ?
                """;
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, color);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(getContactsFromRsNext(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("查询标签联系人失败, color={}", color, e);
        }
        return list;
    }

    /**
     * 显示所有标签
     */
    @Override
    public List<Tags> findAllTags() {
        List<Tags> list = new ArrayList<>();
        String sql = "SELECT * FROM tags";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Tags tag = new Tags(rs.getInt("id"), rs.getString("tag_name"), rs.getString("tag_color"),
                        rs.getString("tag_notes"));
                list.add(tag);
            }
        } catch (SQLException e) {
            logger.error("查询所有标签失败", e);
        }
        return list;
    }

    /**
     * 根据联系人来查找分组列表
     */
    @Override
    public List<Groups> findGroupsByContact(int id) {
        List<Groups> list = new ArrayList<>();
        String sql = """
                SELECT g.id, g.group_name, g.group_notes
                FROM groups g
                JOIN contacts_group cg ON g.id = cg.group_id
                WHERE cg.contacts_id = ?
                """;
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Groups group = new Groups(rs.getInt("id"), rs.getString("group_name"), rs.getString("group_notes"));
                    list.add(group);
                }
            }
        } catch (SQLException e) {
            logger.error("查询联系人所属分组失败, contactId={}", id, e);
        }
        return list;
    }

    /**
     * 根据联系人ID查找标签列表
     */
    @Override
    public List<Tags> findTagsByContact(int id) {
        List<Tags> list = new ArrayList<>();
        String sql = """
                SELECT t.id, t.tag_name, t.tag_color, t.tag_notes
                FROM tags t
                JOIN tag_contacts tc ON t.id = tc.tag_id
                WHERE tc.contacts_id = ?
                """;
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Tags tag = new Tags(rs.getInt("id"), rs.getString("tag_name"), rs.getString("tag_color"),
                            rs.getString("tag_notes"));
                    list.add(tag);
                }
            }
        } catch (SQLException e) {
            logger.error("查询联系人所属标签失败, contactId={}", id, e);
        }
        return list;
    }

    /**
     * 从ResultSet中获取Contact对象
     */
    private Contacts getContactsFromRsNext(ResultSet rs) throws SQLException {
        Contacts contact = new Contacts(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("tele1"),
                rs.getString("tele2"),
                rs.getString("home"),
                rs.getString("email"),
                rs.getString("notes"));

        // 尝试读取时间字段（如果SQL查询中包含这些字段）
        try {
            java.sql.Timestamp createdTs = rs.getTimestamp("created_at");
            if (createdTs != null) {
                contact.setCreatedAt(createdTs.toLocalDateTime());
            }
        } catch (SQLException e) {
            // 如果查询中没有created_at字段，忽略
        }

        try {
            java.sql.Timestamp updatedTs = rs.getTimestamp("updated_at");
            if (updatedTs != null) {
                contact.setUpdatedAt(updatedTs.toLocalDateTime());
            }
        } catch (SQLException e) {
            // 如果查询中没有updated_at字段，忽略
        }

        return contact;
    }

    /**
     * 将数据库中的联系人导出到文件中
     *
     * @param bw : 从FileVcf中被调用
     */
    @Override
    public List<String> writeVcfFileInService() {
        List<String> resultList = new ArrayList<>();
        String sqlForGroup = """
                SELECT c.name, c.tele1, c.tele2, c.home, c.email, c.notes,
                GROUP_CONCAT(DISTINCT g.group_name) as groups,
                GROUP_CONCAT(DISTINCT (t.tag_name || '(' || t.tag_color || ')')) as tags
                FROM contacts c
                LEFT JOIN contacts_group cg ON c.id = cg.contacts_id
                LEFT JOIN groups g ON g.id = cg.group_id
                LEFT JOIN tag_contacts tc ON c.id = tc.contacts_id
                LEFT JOIN tags t ON t.id = tc.tag_id
                GROUP BY c.id,c.name,c.tele1,c.tele2,c.home,c.email,c.notes;
                """;

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sqlForGroup);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                StringBuilder line = new StringBuilder();

                line.append(rs.getString("name")).append(";");
                line.append(rs.getString("tele1")).append(";");
                line.append(rs.getString("tele2")).append(";");
                line.append(rs.getString("home")).append(";");
                line.append(rs.getString("email")).append(";");
                line.append(rs.getString("notes")).append(";");

                String groups = rs.getString("groups");
                String tags = rs.getString("tags");

                line.append(groups != null ? groups : "").append(";");
                line.append(tags != null ? tags : "").append("\n");

                resultList.add(line.toString());
            }
        } catch (SQLException e) {
            logger.error("导出联系人到VCF失败:", e);
        }
        return resultList;
    }
}
