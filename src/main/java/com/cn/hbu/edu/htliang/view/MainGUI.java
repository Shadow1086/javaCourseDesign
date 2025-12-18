package com.cn.hbu.edu.htliang.view;

import com.cn.hbu.edu.htliang.entityPojo.Contacts;
import com.cn.hbu.edu.htliang.entityPojo.Groups;
import com.cn.hbu.edu.htliang.entityPojo.Tags;
import com.cn.hbu.edu.htliang.service.ContactService;
import com.cn.hbu.edu.htliang.service.ContactServiceImpl;
import com.cn.hbu.edu.htliang.util.DBUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Swing 前端：联系人增删改查 + 分组/标签筛选与管理。
 * 仅修改 view 层，后端调用直接使用 ContactService。
 */
public class MainGUI extends JFrame {
    private final ContactService contactService = new ContactServiceImpl();

    // 联系人表格
    private JTable contactTable;
    private DefaultTableModel contactTableModel;
    private JPopupMenu rightClickMenu;

    // 筛选/搜索输入
    private JTextField searchField;
    private JComboBox<String> groupFilterCombo;
    private JComboBox<TagOption> tagFilterCombo;

    /**
     * 主窗口构造器：初始化界面，并加载分组/标签下拉框与联系人表格数据。
     * 调用：{@link #initUI()}、{@link #refreshGroupFilter()}、{@link #refreshTagFilter()}、{@link #refreshContactTable()}。
     */
    public MainGUI() {
        initUI();
        refreshGroupFilter();
        refreshTagFilter();
        refreshContactTable();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainGUI app = new MainGUI();
            app.setVisible(true);
        });
    }

    // ============ 界面构建 ============

    /**
     * 初始化主窗口 UI：设置窗口大小/布局，并装配顶部工具栏、表格区域、底部状态栏。
     * 调用：{@link #buildToolbar()}、{@link #buildContactTablePanel()}、{@link #buildStatusBar()}。
     */
    private void initUI() {
        setTitle("手机通讯录");
        setSize(1100, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(buildToolbar(), BorderLayout.NORTH);
        add(buildContactTablePanel(), BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);
    }

    /**
     * 构建顶部工具栏：包含“管理/文件”菜单、分组/标签筛选、搜索框。
     * 触发：导入/导出回调 {@link #onImportButtonClick()} / {@link #onExportButtonClick()}；
     * 筛选回调 {@link #onGroupFilterChange()} / {@link #onTagFilterChange()}；
     * 搜索回调 {@link #onSearchButtonClick()}。
     */
    private JPanel buildToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        toolbar.setBackground(new Color(244, 247, 252));
        toolbar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 226, 236)),
                new EmptyBorder(8, 12, 8, 12)
        ));

        Dimension btnSize = new Dimension(92, 34);
        Font btnFont = new Font("微软雅黑", Font.PLAIN, 13);

        JButton manageBtn = createToolButton("管理", btnSize, btnFont, null);
        manageBtn.addActionListener(e -> showManageMenu(manageBtn));
        JButton fileBtn = createToolButton("文件", btnSize, btnFont, null);
        fileBtn.addActionListener(e -> showFileMenu(fileBtn));
        toolbar.add(manageBtn);
        toolbar.add(fileBtn);

        toolbar.add(new JLabel("按组筛选:"));
        groupFilterCombo = new JComboBox<>();
        groupFilterCombo.setPreferredSize(new Dimension(150, 32));
        groupFilterCombo.addActionListener(e -> onGroupFilterChange());
        toolbar.add(groupFilterCombo);

        toolbar.add(new JLabel("按标签筛选:"));
        tagFilterCombo = new JComboBox<>();
        tagFilterCombo.setPreferredSize(new Dimension(170, 32));
        tagFilterCombo.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            String text = value == null ? "" : value.displayText();
            JLabel lbl = new JLabel(text);
            lbl.setOpaque(true);
            lbl.setBorder(new EmptyBorder(2, 6, 2, 6));
            if (isSelected) {
                lbl.setBackground(new Color(225, 236, 255));
            } else {
                lbl.setBackground(Color.WHITE);
            }
            return lbl;
        });
        tagFilterCombo.addActionListener(e -> onTagFilterChange());
        toolbar.add(tagFilterCombo);
        // 标签筛选改为下拉自动选择，无需按钮

        toolbar.add(new JLabel("搜索:"));
        searchField = new JTextField(16);
        searchField.setPreferredSize(new Dimension(170, 32));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 215), 1, true),
                new EmptyBorder(4, 8, 4, 8)
        ));
        searchField.setToolTipText("输入姓名或电话，回车立即搜索");
        searchField.addActionListener(e -> onSearchButtonClick());
        toolbar.add(searchField);

        return toolbar;
    }

    /**
     * 构建底部状态栏：左侧是增/改/删/刷新的按钮；右侧显示提示信息。
     * 触发：{@link #showAddContactDialog()}、{@link #showEditContactDialog()}、{@link #onDeleteButtonClick()}、{@link #onRefreshButtonClick()}。
     */
    private JPanel buildStatusBar() {
        JPanel status = new JPanel(new BorderLayout());
        status.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)));

        // 下端放置增/改/删/刷新按钮
        JPanel ops = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        Dimension btnSize = new Dimension(92, 32);
        Font btnFont = new Font("微软雅黑", Font.PLAIN, 12);
        ops.add(createToolButton("添加", btnSize, btnFont, e -> showAddContactDialog()));
        ops.add(createToolButton("编辑", btnSize, btnFont, e -> showEditContactDialog()));
        ops.add(createToolButton("删除", btnSize, btnFont, e -> onDeleteButtonClick()));
        ops.add(createToolButton("刷新", btnSize, btnFont, e -> onRefreshButtonClick()));

        status.add(ops, BorderLayout.WEST);
        return status;
    }

    /**
     * 构建联系人表格区域：创建 JTable、右键菜单，并注册右键选中/弹出菜单逻辑。
     * 表格数据刷新入口：{@link #refreshContactTable()} / {@link #displayContactList(List)}。
     */
    private JScrollPane buildContactTablePanel() {
        String[] columnNames = {"ID", "姓名", "电话", "备用电话", "分组", "标签", "备注"};
        contactTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        contactTable = new JTable(contactTableModel);
        contactTable.setRowHeight(30);
        contactTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contactTable.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        contactTable.setGridColor(new Color(235, 235, 235));
        contactTable.setSelectionBackground(new Color(198, 224, 255));
        contactTable.setSelectionForeground(Color.BLACK);
        contactTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 13));
        contactTable.getTableHeader().setBackground(new Color(243, 245, 248));
        contactTable.getTableHeader().setPreferredSize(new Dimension(0, 34));
        contactTable.setShowVerticalLines(false);

        rightClickMenu = new JPopupMenu();
        JMenuItem viewItem = new JMenuItem("查看详情");
        viewItem.addActionListener(e -> onViewDetailButtonClick());
        JMenuItem editItem = new JMenuItem("编辑");
        editItem.addActionListener(e -> showEditContactDialog());
        JMenuItem delItem = new JMenuItem("删除");
        delItem.addActionListener(e -> onDeleteButtonClick());
        rightClickMenu.add(viewItem);
        rightClickMenu.add(editItem);
        rightClickMenu.add(delItem);

        contactTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = contactTable.rowAtPoint(e.getPoint());
                    if (row >= 0 && row < contactTable.getRowCount()) {
                        contactTable.setRowSelectionInterval(row, row);
                        rightClickMenu.show(contactTable, e.getX(), e.getY());
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(contactTable);
        scrollPane.setBorder(new EmptyBorder(8, 8, 8, 8));
        scrollPane.getViewport().setBackground(Color.WHITE);
        return scrollPane;
    }

    /**
     * 创建统一样式的工具按钮（减少重复代码）。
     *
     * @param listener 点击时触发的回调（可为 null）
     */
    private JButton createToolButton(String text, Dimension size, Font font, java.awt.event.ActionListener listener) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(size);
        btn.setFont(font);
        btn.setFocusPainted(false);
        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(205, 210, 220), 1, true),
                new EmptyBorder(6, 16, 6, 16)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(listener);
        return btn;
    }

    // 顶部“管理”菜单（分组/标签）

    /**
     * 显示“管理”弹出菜单：分组管理/标签管理。
     * 触发：{@link #showGroupManageDialog()}、{@link #showTagManageDialog()}。
     */
    private void showManageMenu(Component anchor) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem groupItem = new JMenuItem("分组管理");
        groupItem.addActionListener(e -> showGroupManageDialog());
        JMenuItem tagItem = new JMenuItem("标签管理");
        tagItem.addActionListener(e -> showTagManageDialog());
        menu.add(groupItem);
        menu.add(tagItem);
        menu.show(anchor, 0, anchor.getHeight());
    }

    // 顶部“文件”菜单（导入/导出）

    /**
     * 显示“文件”弹出菜单：导入 vcf / 导出 vcf。
     * 触发：{@link #onImportButtonClick()}、{@link #onExportButtonClick()}。
     */
    private void showFileMenu(Component anchor) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem importItem = new JMenuItem("导入");
        importItem.addActionListener(e -> onImportButtonClick());
        JMenuItem exportItem = new JMenuItem("导出");
        exportItem.addActionListener(e -> onExportButtonClick());
        menu.add(importItem);
        menu.add(exportItem);
        menu.show(anchor, 0, anchor.getHeight());
    }

    // ============ 数据收集 ============

    /**
     * 从“添加联系人”对话框输入框收集数据，组装为 {@link Contacts}。
     * 注意：这里只负责采集与 trim，不做业务校验；校验在 {@link #onAddButtonClick(JDialog, JTextField, JTextField, JTextField, JTextField, JTextField, JTextArea)}。
     */
    public Contacts collectAddContactInput(JTextField nameField,
                                           JTextField phoneField,
                                           JTextField backupPhoneField,
                                           JTextField addressField,
                                           JTextField emailField,
                                           JTextArea notesArea) {
        Contacts contact = new Contacts();
        contact.setName(nameField.getText().trim());
        contact.setTele1(phoneField.getText().trim());
        contact.setTele2(backupPhoneField.getText().trim());
        contact.setHome(addressField.getText().trim());
        contact.setEmail(emailField.getText().trim());
        contact.setNotes(notesArea.getText().trim());
        return contact;
    }

    /**
     * 从“编辑联系人”对话框收集数据，并补上联系人 ID。
     * 调用：{@link #collectAddContactInput(JTextField, JTextField, JTextField, JTextField, JTextField, JTextArea)}。
     */
    public Contacts collectEditContactInput(int contactId,
                                            JTextField nameField,
                                            JTextField phoneField,
                                            JTextField backupPhoneField,
                                            JTextField addressField,
                                            JTextField emailField,
                                            JTextArea notesArea) {
        Contacts contact = collectAddContactInput(nameField, phoneField, backupPhoneField, addressField, emailField, notesArea);
        contact.setId(contactId);
        return contact;
    }

    /**
     * 从表格中获取当前选中的联系人 ID（删除/查看详情等都会用到）。
     * 返回 null 表示未选中或 ID 类型异常。
     */
    public Integer collectDeleteContactInput() {
        int row = contactTable.getSelectedRow();
        if (row == -1) {
            displayMessage("请先选择一个联系人");
            return null;
        }
        Object idVal = contactTableModel.getValueAt(row, 0);
        return (idVal instanceof Integer) ? (Integer) idVal : null;
    }

    /**
     * 读取搜索框内容并 trim。
     */
    public String collectSearchInput() {
        return searchField == null ? "" : searchField.getText().trim();
    }

    /**
     * 复用删除的选中逻辑获取联系人 ID，用于“查看详情”。
     * 调用：{@link #collectDeleteContactInput()}。
     */
    public Integer collectViewDetailInput() {
        return collectDeleteContactInput();
    }

    // ============ 结果展示 ============

    /**
     * 将联系人列表渲染到 JTable。
     * 为了显示分组/标签，会对每条联系人调用 service 再补齐信息：
     * 调用：{@link ContactService#findGroupTagsById(int)}。
     */
    public void displayContactList(List<Contacts> contacts) {
        contactTableModel.setRowCount(0);
        if (contacts == null || contacts.isEmpty()) {
            return;
        }
        for (Contacts c : contacts) {
            Contacts enriched = contactService.findGroupTagsById(c.getId());
            if (enriched == null) {
                enriched = c;
            }
            String groupNames = joinGroupNames(enriched);
            String tagNames = joinTagNames(enriched);
            contactTableModel.addRow(new Object[]{
                    enriched.getId(),
                    enriched.getName(),
                    enriched.getTele1(),
                    enriched.getTele2(),
                    groupNames,
                    tagNames,
                    enriched.getNotes()
            });
        }
    }

    /**
     * 显示搜索结果（当前实现直接复用表格刷新逻辑）。
     * 调用：{@link #displayContactList(List)}。
     */
    public void displaySearchResult(List<Contacts> contacts) {
        displayContactList(contacts);
    }

    /**
     * 弹窗展示联系人详情（含分组/标签/备注等）。
     */
    public void displayContactDetail(Contacts contact) {
        if (contact == null) {
            displayMessage("未获取到联系人详情");
            return;
        }
        String detail = String.format(
                "姓名: %s%n电话: %s%n备用电话: %s%n邮箱: %s%n地址: %s%n分组: %s%n标签: %s%n备注: %s",
                Objects.toString(contact.getName(), ""),
                Objects.toString(contact.getTele1(), ""),
                Objects.toString(contact.getTele2(), ""),
                Objects.toString(contact.getEmail(), ""),
                Objects.toString(contact.getHome(), ""),
                joinGroupNames(contact),
                joinTagNames(contact),
                Objects.toString(contact.getNotes(), "")
        );
        JOptionPane.showMessageDialog(this, detail, "联系人详情", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * 统一的提示弹窗封装。
     */
    public void displayMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    // ============ 事件处理 ============

    /**
     * “添加联系人”对话框的保存按钮事件：
     * 1) 读取输入 2) 做必填校验 3) 调用 service 写入数据库 4) 刷新表格。
     * 调用：{@link ContactService#addContact(String, String, String, String, String, String)}、{@link #refreshContactTable()}。
     */
    private void onAddButtonClick(JDialog dialog,
                                  JTextField nameField,
                                  JTextField phoneField,
                                  JTextField backupPhoneField,
                                  JTextField addressField,
                                  JTextField emailField,
                                  JTextArea notesArea) {
        Contacts contact = collectAddContactInput(nameField, phoneField, backupPhoneField, addressField, emailField, notesArea);
        if (contact.getName().isEmpty() || contact.getTele1().isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "姓名和电话不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        contactService.addContact(contact.getName(), contact.getTele1(), contact.getTele2(), contact.getHome(), contact.getEmail(), contact.getNotes());
        refreshContactTable();
        displayMessage("添加成功");
        dialog.dispose();
    }

    /**
     * “编辑联系人”对话框的保存按钮事件：
     * 1) 读取输入 2) 必填校验 3) 调用 service 更新 4) 刷新表格。
     * 调用：{@link ContactService#updateContactInfo(int, String, String, String, String, String, String)}、{@link #refreshContactTable()}。
     */
    private void onEditButtonClick(JDialog dialog,
                                   int contactId,
                                   JTextField nameField,
                                   JTextField phoneField,
                                   JTextField backupPhoneField,
                                   JTextField addressField,
                                   JTextField emailField,
                                   JTextArea notesArea) {
        Contacts contact = collectEditContactInput(contactId, nameField, phoneField, backupPhoneField, addressField, emailField, notesArea);
        if (contact.getName().isEmpty() || contact.getTele1().isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "姓名和电话不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        boolean ok = contactService.updateContactInfo(contactId, contact.getName(), contact.getTele1(), contact.getTele2(), contact.getHome(), contact.getEmail(), contact.getNotes());
        if (!ok) {
            displayMessage("更新失败，未找到联系人或数据有误");
            return;
        }
        refreshContactTable();
        displayMessage("更新成功");
        dialog.dispose();
    }

    /**
     * 删除按钮事件：确认后调用 service 删除，并刷新表格。
     * 调用：{@link ContactService#deleteById(int)}、{@link #refreshContactTable()}。
     */
    private void onDeleteButtonClick() {
        Integer contactId = collectDeleteContactInput();
        if (contactId == null) return;
        int confirm = JOptionPane.showConfirmDialog(this, "确定删除选中的联系人吗？", "确认删除", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        contactService.deleteById(contactId);
        refreshContactTable();
        displayMessage("删除成功");
    }

    /**
     * 搜索事件：优先按姓名查找，若无结果再按电话查找。
     * 调用：{@link ContactService#findByName(String)}、{@link ContactService#findByTele(String)}。
     */
    private void onSearchButtonClick() {
        String keyword = collectSearchInput();
        if (keyword.isEmpty()) {
            refreshContactTable();
            return;
        }
        List<Contacts> result = contactService.findByName(keyword);
        if (result == null || result.isEmpty()) {
            result = contactService.findByTele(keyword);
        }
        displaySearchResult(result);
    }

    /**
     * 查看详情事件：优先获取带分组/标签的详情，失败再查询基础联系人信息。
     * 调用：{@link ContactService#findGroupTagsById(int)}、{@link ContactService#findById(int)}。
     */
    private void onViewDetailButtonClick() {
        Integer contactId = collectViewDetailInput();
        if (contactId == null) return;
        Contacts detail = contactService.findGroupTagsById(contactId);
        if (detail == null) {
            detail = contactService.findById(contactId);
        }
        displayContactDetail(detail);
    }

    /**
     * 刷新按钮事件：刷新分组/标签筛选下拉框，并刷新联系人表格。
     * 调用：{@link #refreshGroupFilter()}、{@link #refreshTagFilter()}、{@link #refreshContactTable()}。
     */
    private void onRefreshButtonClick() {
        refreshGroupFilter();
        refreshTagFilter();
        refreshContactTable();
    }

    /**
     * 导入事件：弹出文件选择框，选择 .vcf 文件后调用 service 导入并刷新表格。
     * 调用：{@link ContactService#importVcfFile(File)}、{@link #refreshContactTable()}。
     */
    private void onImportButtonClick() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("选择要导入的 vCard 文件");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setAcceptAllFileFilterUsed(true);
        chooser.setFileFilter(new FileNameExtensionFilter("vCard 文件 (*.vcf)", "vcf"));

        int result = chooser.showOpenDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return;
        File file = chooser.getSelectedFile();
        if (file == null || !file.getName().toLowerCase().endsWith(".vcf")) {
            JOptionPane.showMessageDialog(this, "请选择 .vcf 文件", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            int count = contactService.importVcfFile(file);
            refreshContactTable();
            displayMessage("导入完成，共导入 " + count + " 条联系人");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "导入失败：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 导出事件：弹出保存对话框，选择目标文件后调用 service 导出。
     * 调用：{@link ContactService#exportVcfFile(File)}。
     */
    private void onExportButtonClick() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("选择导出保存位置");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setAcceptAllFileFilterUsed(true);
        chooser.setFileFilter(new FileNameExtensionFilter("vCard 文件 (*.vcf)", "vcf"));
        chooser.setSelectedFile(new File("contacts.vcf"));

        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return;

        File selected = chooser.getSelectedFile();
        if (selected == null) return;
        File target = selected;
        if (!selected.getName().toLowerCase().endsWith(".vcf")) {
            target = new File(selected.getParentFile(), selected.getName() + ".vcf");
        }
        if (target.exists()) {
            int confirm = JOptionPane.showConfirmDialog(this, "文件已存在，是否覆盖？\n" + target.getAbsolutePath(), "确认覆盖", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;
        }
        try {
            contactService.exportVcfFile(target);
            displayMessage("导出完成：\n" + target.getAbsolutePath());
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "导出失败：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 分组筛选下拉框变化事件：选择“全部分组”则显示全部；否则按分组查询并刷新表格。
     * 调用：{@link ContactService#findByGroup(String)}、{@link #displayContactList(List)}、{@link #refreshContactTable()}。
     */
    private void onGroupFilterChange() {
        if (groupFilterCombo == null || groupFilterCombo.getSelectedItem() == null) return;
        String name = groupFilterCombo.getSelectedItem().toString();
        if ("全部分组".equals(name)) {
            refreshContactTable();
            return;
        }
        List<Contacts> contacts = contactService.findByGroup(name);
        displayContactList(contacts);
    }

    /**
     * 标签筛选下拉框变化事件：选择“全部标签”则显示全部；否则按标签颜色查询并刷新表格。
     * 调用：{@link ContactService#findByTag(String)}、{@link #displayContactList(List)}、{@link #refreshContactTable()}。
     */
    private void onTagFilterChange() {
        TagOption option = tagFilterCombo == null ? null : (TagOption) tagFilterCombo.getSelectedItem();
        if (option == null || option.color == null) {
            refreshContactTable();
            return;
        }
        List<Contacts> contacts = contactService.findByTag(option.color);
        displayContactList(contacts);
    }

    /**
     * 分组管理对话框：展示分组列表，并提供新增/删除/给联系人分配分组。
     * 主要调用：{@link ContactService#findAllGroup()}、{@link ContactService#addGroup(String, String)}、
     * {@link ContactService#deleteGroup(String)}、{@link ContactService#addContactInGroup(List, Groups)}。
     */
    private void showGroupManageDialog() {
        JDialog dialog = new JDialog(this, "分组管理", true);
        dialog.setSize(380, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        DefaultListModel<String> model = new DefaultListModel<>();
        List<Groups> groups = contactService.findAllGroup();
        if (groups != null) {
            for (Groups g : groups) {
                model.addElement(g.getGroup_name());
            }
        }
        JList<String> list = new JList<>(model);
        list.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        dialog.add(new JScrollPane(list), BorderLayout.CENTER);

        JPanel btnBar = new JPanel(new GridLayout(2, 2, 8, 8));

        JButton add = new JButton("新增分组");
        add.addActionListener(e -> {
            JTextField name = new JTextField();
            JTextArea notes = new JTextArea(3, 20);
            notes.setLineWrap(true);
            int res = JOptionPane.showConfirmDialog(dialog, new Object[]{"分组名称:", name, "备注:", new JScrollPane(notes)}, "新增分组", JOptionPane.OK_CANCEL_OPTION);
            if (res == JOptionPane.OK_OPTION) {
                if (name.getText().trim().isEmpty()) {
                    displayMessage("分组名称不能为空");
                    return;
                }
                contactService.addGroup(name.getText().trim(), notes.getText().trim());
                refreshGroupFilter();
                list.setListData(toNameArray(contactService.findAllGroup()));
                displayMessage("新增分组成功");
            }
        });

        JButton del = new JButton("删除分组");
        del.addActionListener(e -> {
            String selected = list.getSelectedValue();
            if (selected == null) {
                displayMessage("请选择要删除的分组");
                return;
            }
            int res = JOptionPane.showConfirmDialog(dialog, "确定删除分组：" + selected + " ?", "确认", JOptionPane.YES_NO_OPTION);
            if (res != JOptionPane.YES_OPTION) return;
            contactService.deleteGroup(selected);
            refreshGroupFilter();
            list.setListData(toNameArray(contactService.findAllGroup()));
            displayMessage("删除分组完成");
        });

        JButton assign = new JButton("分配给选中联系人");
        assign.addActionListener(e -> {
            Integer contactId = collectDeleteContactInput();
            String selectedGroup = list.getSelectedValue();
            if (contactId == null || selectedGroup == null) {
                displayMessage("请选择联系人和分组");
                return;
            }
            Contacts con = contactService.findById(contactId);
            if (con == null) {
                displayMessage("联系人不存在");
                return;
            }
            Groups group = new Groups();
            group.setGroup_name(selectedGroup);
            List<Contacts> temp = new ArrayList<>();
            temp.add(con);
            contactService.addContactInGroup(temp, group);
            refreshContactTable();
            displayMessage("已分配到分组：" + selectedGroup);
        });

        JButton close = new JButton("关闭");
        close.addActionListener(e -> dialog.dispose());

        for (JButton b : new JButton[]{add, del, assign, close}) {
            b.setFocusPainted(false);
        }
        btnBar.add(add);
        btnBar.add(del);
        btnBar.add(assign);
        btnBar.add(close);

        dialog.add(btnBar, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    /**
     * 标签管理对话框：新增/删除标签，并给选中联系人打标签。
     * 主要调用：{@link ContactService#addTag(String, String, String)}、{@link ContactService#deleteTag(String)}、
     * {@link ContactService#addContactToTag(List, Tags)}。
     */
    private void showTagManageDialog() {
        JDialog dialog = new JDialog(this, "标签管理", true);
        dialog.setSize(380, 360);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(0, 1, 8, 8));
        form.setBorder(new EmptyBorder(12, 12, 12, 12));
        JTextField nameField = new JTextField();
        JTextField colorField = new JTextField();
        JTextArea notesArea = new JTextArea(3, 20);
        notesArea.setLineWrap(true);
        form.add(new JLabel("标签名称"));
        form.add(nameField);
        form.add(new JLabel("标签颜色(作为主键，例如 red 或 #FF0000)"));
        form.add(colorField);
        form.add(new JLabel("备注"));
        form.add(new JScrollPane(notesArea));
        dialog.add(form, BorderLayout.CENTER);

        JPanel btnBar = new JPanel(new GridLayout(2, 2, 8, 8));

        JButton add = new JButton("新增标签");
        add.addActionListener(e -> {
            if (colorField.getText().trim().isEmpty()) {
                displayMessage("标签颜色不能为空");
                return;
            }
            contactService.addTag(colorField.getText().trim(), nameField.getText().trim(), notesArea.getText().trim());
            displayMessage("新增标签成功");
        });

        JButton del = new JButton("删除标签");
        del.addActionListener(e -> {
            if (colorField.getText().trim().isEmpty()) {
                displayMessage("请输入要删除的标签颜色");
                return;
            }
            contactService.deleteTag(colorField.getText().trim());
            displayMessage("删除标签完成（如存在）");
        });

        JButton assign = new JButton("给联系人打标签");
        assign.addActionListener(e -> {
            Integer contactId = collectDeleteContactInput();
            if (contactId == null) {
                displayMessage("请先选择联系人");
                return;
            }
            Contacts con = contactService.findById(contactId);
            if (con == null) {
                displayMessage("联系人不存在");
                return;
            }
            Tags tag = new Tags();
            tag.setTag_color(colorField.getText().trim());
            tag.setTag_name(nameField.getText().trim());
            tag.setTag_notes(notesArea.getText().trim());
            List<Contacts> temp = new ArrayList<>();
            temp.add(con);
            contactService.addContactToTag(temp, tag);
            refreshContactTable();
            displayMessage("已为联系人添加标签");
        });

        JButton close = new JButton("关闭");
        close.addActionListener(e -> dialog.dispose());

        for (JButton b : new JButton[]{add, del, assign, close}) {
            b.setFocusPainted(false);
        }
        btnBar.add(add);
        btnBar.add(del);
        btnBar.add(assign);
        btnBar.add(close);

        dialog.add(btnBar, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // ============ 对话框（添加/编辑联系人） ============

    /**
     * 打开“添加联系人”对话框：创建表单并绑定保存/取消按钮。
     * 保存会触发：{@link #onAddButtonClick(JDialog, JTextField, JTextField, JTextField, JTextField, JTextField, JTextArea)}。
     */
    private void showAddContactDialog() {
        JDialog dialog = new JDialog(this, "添加联系人", true);
        dialog.setSize(460, 520);
        dialog.setLocationRelativeTo(this);

        JPanel form = createContactForm(null);
        JTextField nameField = (JTextField) form.getClientProperty("nameField");
        JTextField phoneField = (JTextField) form.getClientProperty("phoneField");
        JTextField backupPhoneField = (JTextField) form.getClientProperty("backupPhoneField");
        JTextField emailField = (JTextField) form.getClientProperty("emailField");
        JTextField addressField = (JTextField) form.getClientProperty("addressField");
        JTextArea notesArea = (JTextArea) form.getClientProperty("notesArea");

        JButton saveBtn = new JButton("保存");
        saveBtn.addActionListener(e -> onAddButtonClick(dialog, nameField, phoneField, backupPhoneField, addressField, emailField, notesArea));
        JButton cancelBtn = new JButton("取消");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        btnBar.add(saveBtn);
        btnBar.add(cancelBtn);

        dialog.setLayout(new BorderLayout());
        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnBar, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    /**
     * 打开“编辑联系人”对话框：先从表格取选中 ID，再从 service 读取数据填充表单。
     * 调用：{@link ContactService#findById(int)}、{@link #createContactForm(Contacts)}。
     */
    private void showEditContactDialog() {
        int row = contactTable.getSelectedRow();
        if (row == -1) {
            displayMessage("请先选择要编辑的联系人");
            return;
        }
        Object idVal = contactTableModel.getValueAt(row, 0);
        if (!(idVal instanceof Integer)) {
            displayMessage("无法获取联系人 ID");
            return;
        }
        int contactId = (Integer) idVal;
        Contacts contact = contactService.findById(contactId);
        if (contact == null) {
            displayMessage("联系人不存在");
            return;
        }

        JDialog dialog = new JDialog(this, "编辑联系人", true);
        dialog.setSize(460, 520);
        dialog.setLocationRelativeTo(this);

        JPanel form = createContactForm(contact);
        JTextField nameField = (JTextField) form.getClientProperty("nameField");
        JTextField phoneField = (JTextField) form.getClientProperty("phoneField");
        JTextField backupPhoneField = (JTextField) form.getClientProperty("backupPhoneField");
        JTextField emailField = (JTextField) form.getClientProperty("emailField");
        JTextField addressField = (JTextField) form.getClientProperty("addressField");
        JTextArea notesArea = (JTextArea) form.getClientProperty("notesArea");

        JButton saveBtn = new JButton("保存");
        saveBtn.addActionListener(e -> onEditButtonClick(dialog, contactId, nameField, phoneField, backupPhoneField, addressField, emailField, notesArea));
        JButton cancelBtn = new JButton("取消");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        btnBar.add(saveBtn);
        btnBar.add(cancelBtn);

        dialog.setLayout(new BorderLayout());
        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnBar, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // ============ 辅助 ============

    /**
     * 创建联系人表单面板（添加/编辑共用）。
     * 通过 putClientProperty 暴露表单控件，方便对话框读取输入值。
     */
    private JPanel createContactForm(Contacts contact) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(new EmptyBorder(16, 20, 10, 20));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;

        JTextField nameField = new JTextField(contact == null ? "" : contact.getName());
        JTextField phoneField = new JTextField(contact == null ? "" : contact.getTele1());
        JTextField backupPhoneField = new JTextField(contact == null ? "" : contact.getTele2());
        JTextField emailField = new JTextField(contact == null ? "" : contact.getEmail());
        JTextField addressField = new JTextField(contact == null ? "" : contact.getHome());
        JTextArea notesArea = new JTextArea(contact == null ? "" : contact.getNotes(), 4, 20);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane notesPane = new JScrollPane(notesArea);
        Dimension fieldSize = new Dimension(260, 32);
        nameField.setPreferredSize(fieldSize);
        phoneField.setPreferredSize(fieldSize);
        backupPhoneField.setPreferredSize(fieldSize);
        emailField.setPreferredSize(fieldSize);
        addressField.setPreferredSize(fieldSize);
        notesPane.setPreferredSize(new Dimension(260, 90));

        addLabeledField(panel, c, "姓名(必填)", nameField);
        addLabeledField(panel, c, "电话(必填)", phoneField);
        addLabeledField(panel, c, "备用电话", backupPhoneField);
        addLabeledField(panel, c, "邮箱", emailField);
        addLabeledField(panel, c, "地址", addressField);
        addLabeledArea(panel, c, "备注", notesPane);

        panel.putClientProperty("nameField", nameField);
        panel.putClientProperty("phoneField", phoneField);
        panel.putClientProperty("backupPhoneField", backupPhoneField);
        panel.putClientProperty("emailField", emailField);
        panel.putClientProperty("addressField", addressField);
        panel.putClientProperty("notesArea", notesArea);
        return panel;
    }

    /**
     * 在 GridBagLayout 表单中添加一行“标签 + 输入框”。
     */
    private void addLabeledField(JPanel panel, GridBagConstraints c, String label, JComponent field) {
        c.gridx = 0;
        panel.add(new JLabel(label + ":"), c);
        c.gridx = 1;
        panel.add(field, c);
        c.gridy++;
    }

    /**
     * 在 GridBagLayout 表单中添加一行“标签 + 多行区域(如备注)”。
     */
    private void addLabeledArea(JPanel panel, GridBagConstraints c, String label, JComponent area) {
        c.gridx = 0;
        panel.add(new JLabel(label + ":"), c);
        c.gridx = 1;
        c.fill = GridBagConstraints.BOTH;
        panel.add(area, c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy++;
    }

    /**
     * 刷新“分组筛选”下拉框数据。
     * 调用：{@link ContactService#findAllGroup()}。
     */
    private void refreshGroupFilter() {
        if (groupFilterCombo == null) {
            return;
        }
        groupFilterCombo.removeAllItems();
        groupFilterCombo.addItem("全部分组");
        List<Groups> groups = contactService.findAllGroup();
        if (groups != null) {
            for (Groups g : groups) {
                groupFilterCombo.addItem(g.getGroup_name());
            }
        }
        groupFilterCombo.setSelectedIndex(0);
    }

    /**
     * 刷新“标签筛选”下拉框数据。
     * 调用：{@link #loadTagOptions()}。
     */
    private void refreshTagFilter() {
        if (tagFilterCombo == null) {
            return;
        }
        tagFilterCombo.removeAllItems();
        tagFilterCombo.addItem(new TagOption(null, "全部标签"));
        for (TagOption opt : loadTagOptions()) {
            tagFilterCombo.addItem(opt);
        }
        tagFilterCombo.setSelectedIndex(0);
    }

    /**
     * 从数据库读取标签列表，组装成下拉框选项。
     * 调用：{@link DBUtil#getConnection()}。
     */
    private List<TagOption> loadTagOptions() {
        List<TagOption> list = new ArrayList<>();
        String sql = "select tag_color, tag_name from tags";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String color = rs.getString("tag_color");
                String name = rs.getString("tag_name");
                list.add(new TagOption(color, name));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 刷新联系人表格（显示全部联系人）。
     * 调用：{@link ContactService#findAll()}、{@link #displayContactList(List)}。
     */
    private void refreshContactTable() {
        List<Contacts> list = contactService.findAll();
        displayContactList(list);
    }

    /**
     * 将联系人对象中的分组列表拼接为一段可显示的字符串。
     */
    private String joinGroupNames(Contacts contact) {
        StringJoiner joiner = new StringJoiner(", ");
        if (contact.getGroups() != null) {
            for (Groups g : contact.getGroups()) {
                joiner.add(Objects.toString(g.getGroup_name(), ""));
            }
        }
        return joiner.length() == 0 ? "" : joiner.toString();
    }

    /**
     * 将联系人对象中的标签列表拼接为一段可显示的字符串。
     */
    private String joinTagNames(Contacts contact) {
        StringJoiner joiner = new StringJoiner(", ");
        if (contact.getTags() != null) {
            for (Tags t : contact.getTags()) {
                joiner.add(Objects.toString(t.getTag_name(), ""));
            }
        }
        return joiner.length() == 0 ? "" : joiner.toString();
    }

    /**
     * 将分组列表转换成分组名数组（用于刷新 JList 之类的组件）。
     */
    private String[] toNameArray(List<Groups> groups) {
        if (groups == null || groups.isEmpty()) return new String[0];
        return groups.stream().map(Groups::getGroup_name).toArray(String[]::new);
    }

    // 标签筛选项
    private static class TagOption {
        String color;
        String name;

        TagOption(String color, String name) {
            this.color = color;
            this.name = name;
        }

        String displayText() {
            if (color == null) {
                return "全部标签";
            }
            String n = name == null ? "" : name;
            return n.isEmpty() ? color : n + " (" + color + ")";
        }

        @Override
        public String toString() {
            return displayText();
        }
    }
}
