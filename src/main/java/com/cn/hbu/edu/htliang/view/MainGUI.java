package com.cn.hbu.edu.htliang.view;

import com.cn.hbu.edu.htliang.entityPojo.Contacts;
import com.cn.hbu.edu.htliang.service.ContactService;
import com.cn.hbu.edu.htliang.service.ContactServiceImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * 类名: MainGUI
 * 创建时间: 2025/12/12 20:34
 * 项目描述:
 *
 * @author htLiang
 */
public class MainGUI extends JFrame {
    // 主方法
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() -> {
            MainGUI app = new MainGUI();
            app.setVisible(true);
        });
    }

    // Service 层
    private final ContactService contactService = new ContactServiceImpl();

    // 界面组件
    private JTable contactTable;
    private DefaultTableModel tableModel;
    private JPopupMenu rightClickMenu;

    public MainGUI() {

        // 设置窗口基本属性
        setTitle("手机通讯录");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 居中显示

        // 创建主面板
        JPanel mainPanel = new JPanel(new BorderLayout());

        // ========== 顶部工具栏 ==========

        // 创建工具栏面板，使用FlowLayout，增加水平间距
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        toolbarPanel.setBackground(new Color(245, 247, 250));
        toolbarPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));

        // 定义按钮尺寸
        Dimension btnSize = new Dimension(90, 32);
        Font btnFont = new Font("微软雅黑", Font.PLAIN, 13);

        // 添加按钮
        JButton addBtn = createToolButton("添加", btnSize, btnFont);
        addBtn.addActionListener(e -> showAddContactDialog());

        JButton searchBtn = createToolButton("搜索", btnSize, btnFont);
        searchBtn.addActionListener(e -> showSearchDialog());

        JButton refreshBtn = createToolButton("刷新", btnSize, btnFont);
        refreshBtn.addActionListener(e -> refreshTable());

        JButton groupBtn = createToolButton("分组", btnSize, btnFont);
        groupBtn.addActionListener(e -> showGroupDialog());

        JButton importBtn = createToolButton("导入", btnSize, btnFont);
        importBtn.addActionListener(e -> importContacts());

        JButton exportBtn = createToolButton("导出", btnSize, btnFont);
        exportBtn.addActionListener(e -> exportContacts());

        // 添加到工具栏
        toolbarPanel.add(addBtn);
        toolbarPanel.add(searchBtn);
        toolbarPanel.add(refreshBtn);
        toolbarPanel.add(groupBtn);
        toolbarPanel.add(Box.createHorizontalStrut(10)); // 分隔
        toolbarPanel.add(importBtn);
        toolbarPanel.add(exportBtn);

        // 添加搜索框
        JTextField searchField = new JTextField(15);
        searchField.setPreferredSize(new Dimension(150, 30));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(2, 8, 2, 8)
        ));
        searchField.setToolTipText("输入姓名或电话搜索，按回车确认");
        searchField.addActionListener(e -> searchContacts(searchField.getText()));
        toolbarPanel.add(Box.createHorizontalStrut(10));
        toolbarPanel.add(searchField);

        mainPanel.add(toolbarPanel, BorderLayout.NORTH);

        // ========== 主体表格 ==========
        String[] columnNames = {"ID", "姓名", "电话", "备用电话", "分组", "颜色标签", "备注"};
        tableModel = new DefaultTableModel(columnNames, 0);
        contactTable = new JTable(tableModel);

        // 设置表格样式
        contactTable.setRowHeight(32);
        contactTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contactTable.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        contactTable.setGridColor(new Color(230, 230, 230));
        contactTable.setSelectionBackground(new Color(184, 207, 229));
        contactTable.setSelectionForeground(Color.BLACK);
        contactTable.setIntercellSpacing(new Dimension(0, 0));
        contactTable.setShowGrid(true);
        contactTable.setShowHorizontalLines(true);
        contactTable.setShowVerticalLines(false);

        // 设置表头样式
        contactTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 13));
        contactTable.getTableHeader().setBackground(new Color(240, 242, 245));
        contactTable.getTableHeader().setForeground(new Color(60, 60, 60));
        contactTable.getTableHeader().setPreferredSize(new Dimension(0, 36));

        // 设置列宽
        contactTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        contactTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        contactTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        contactTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        contactTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        contactTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        contactTable.getColumnModel().getColumn(6).setPreferredWidth(150);

        // 添加滚动面板
        JScrollPane scrollPane = new JScrollPane(contactTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // ========== 右键菜单 ==========
        rightClickMenu = new JPopupMenu();

        JMenuItem viewItem = new JMenuItem("查看详情");
        viewItem.addActionListener(e -> showContactDetail());

        JMenuItem editItem = new JMenuItem("修改信息");
        editItem.addActionListener(e -> showEditContactDialog());

        JMenuItem deleteItem = new JMenuItem("删除");
        deleteItem.addActionListener(e -> deleteContact());

        rightClickMenu.add(viewItem);
        rightClickMenu.add(editItem);
        rightClickMenu.add(deleteItem);

        // 为表格添加右键菜单事件
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

        // 填充表格数据
        refreshTable();

        // 添加主面板到窗口
        add(mainPanel);
    }

    // 刷新表格数据
    private void refreshTable() {
        tableModel.setRowCount(0); // 清空表格
        List<Contacts> contactList = contactService.findAll();
        for (Contacts contact : contactList) {
            Object[] rowData = {
                    contact.getId(),
                    contact.getName(),
                    contact.getTele1(),
                    contact.getTele2(),
                    "", // 分组（暂无）
                    "", // 颜色标签（暂无）
                    contact.getNotes()
            };
            tableModel.addRow(rowData);
        }
    }

    // 显示添加联系人对话框
    private void showAddContactDialog() {
        JDialog dialog = new JDialog(this, "添加联系人", true);
        dialog.setSize(400, 600);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(9, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField nameField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField backupPhoneField = new JTextField(); // 备用电话
        JTextField groupField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField colorTagField = new JTextField(); // 颜色标签
        JTextArea notesArea = new JTextArea(5, 20); // 备注，多行输入
        notesArea.setLineWrap(true); // 自动换行
        notesArea.setWrapStyleWord(true); // 按单词换行
        JScrollPane notesScrollPane = new JScrollPane(notesArea);
        notesScrollPane.setPreferredSize(new Dimension(200, 80));

        panel.add(new JLabel("姓名:"));
        panel.add(nameField);
        panel.add(new JLabel("电话:"));
        panel.add(phoneField);
        panel.add(new JLabel("备用电话:"));
        panel.add(backupPhoneField);
        panel.add(new JLabel("分组:"));
        panel.add(groupField);
        panel.add(new JLabel("邮箱:"));
        panel.add(emailField);
        panel.add(new JLabel("地址:"));
        panel.add(addressField);
        panel.add(new JLabel("颜色标签:"));
        panel.add(colorTagField);
        panel.add(new JLabel("备注:"));
        panel.add(notesScrollPane);

        JButton saveBtn = new JButton("保存");
        saveBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            if (name.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "姓名和电话不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 调用 Service 添加联系人
            contactService.addContact(
                    name,
                    phone,
                    backupPhoneField.getText().trim(),
                    addressField.getText().trim(),
                    emailField.getText().trim(),
                    notesArea.getText().trim()
            );
            refreshTable();
            dialog.dispose();
        });

        JButton cancelBtn = new JButton("取消");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    // 显示修改联系人对话框
    private void showEditContactDialog() {
        int selectedRow = contactTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择一个联系人！", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int contactId = (int) tableModel.getValueAt(selectedRow, 0);
        Contacts contact = contactService.findId(contactId);
        if (contact == null) return;

        JDialog dialog = new JDialog(this, "修改联系人", true);
        dialog.setSize(450, 450);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(9, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField nameField = new JTextField(contact.getName());
        JTextField phoneField = new JTextField(contact.getTele1());
        JTextField backupPhoneField = new JTextField(contact.getTele2()); // 备用电话
        JTextField groupField = new JTextField(); // 分组暂无
        JTextField emailField = new JTextField(contact.getEmail());
        JTextField addressField = new JTextField(contact.getHome());
        JTextField colorTagField = new JTextField(); // 颜色标签暂无
        JTextArea notesArea = new JTextArea(contact.getNotes(), 5, 20); // 备注，多行输入
        notesArea.setLineWrap(true); // 自动换行
        notesArea.setWrapStyleWord(true); // 按单词换行
        JScrollPane notesScrollPane = new JScrollPane(notesArea);
        notesScrollPane.setPreferredSize(new Dimension(200, 80));

        panel.add(new JLabel("姓名:"));
        panel.add(nameField);
        panel.add(new JLabel("电话:"));
        panel.add(phoneField);
        panel.add(new JLabel("备用电话:"));
        panel.add(backupPhoneField);
        panel.add(new JLabel("分组:"));
        panel.add(groupField);
        panel.add(new JLabel("邮箱:"));
        panel.add(emailField);
        panel.add(new JLabel("地址:"));
        panel.add(addressField);
        panel.add(new JLabel("颜色标签:"));
        panel.add(colorTagField);
        panel.add(new JLabel("备注:"));
        panel.add(notesScrollPane);

        JButton saveBtn = new JButton("保存");
        saveBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            if (name.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "姓名和电话不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 调用 Service 更新联系人
            contactService.updateContactInfo(
                    contactId,
                    name,
                    phone,
                    backupPhoneField.getText().trim(),
                    addressField.getText().trim(),
                    emailField.getText().trim(),
                    notesArea.getText().trim()
            );

            refreshTable();
            dialog.dispose();
        });

        JButton cancelBtn = new JButton("取消");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    // 显示联系人详情
    private void showContactDetail() {
        int selectedRow = contactTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择一个联系人！", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int contactId = (int) tableModel.getValueAt(selectedRow, 0);
        Contacts contact = contactService.findId(contactId);
        if (contact == null) return;

        String detail = String.format(
                "姓名: %s\n电话: %s\n备用电话: %s\n邮箱: %s\n地址: %s\n备注: %s",
                contact.getName(),
                contact.getTele1(),
                contact.getTele2(),
                contact.getEmail(),
                contact.getHome(),
                contact.getNotes()
        );

        JOptionPane.showMessageDialog(this, detail, "联系人详情", JOptionPane.INFORMATION_MESSAGE);
    }

    // 删除联系人
    private void deleteContact() {
        int selectedRow = contactTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择一个联系人！", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int contactId = (int) tableModel.getValueAt(selectedRow, 0);
        String contactName = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "确定要删除联系人 " + contactName + " 吗？",
                "确认删除",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            contactService.deleteId(contactId);
            refreshTable();
        }
    }

    // 显示搜索对话框
    private void showSearchDialog() {
        String keyword = JOptionPane.showInputDialog(this, "请输入搜索关键词:", "搜索联系人", JOptionPane.QUESTION_MESSAGE);
        if (keyword != null && !keyword.trim().isEmpty()) {
            searchContacts(keyword.trim());
        }
    }

    // 搜索联系人
    private void searchContacts(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            refreshTable();
            return;
        }

        // 先按姓名搜索
        List<Contacts> searchResults = contactService.findName(keyword);

        // 如果姓名没搜到，尝试按电话搜索
        if (searchResults == null || searchResults.isEmpty()) {
            searchResults = contactService.findTele(keyword);
        }

        // 显示搜索结果
        tableModel.setRowCount(0);
        if (searchResults != null) {
            for (Contacts contact : searchResults) {
                Object[] rowData = {
                        contact.getId(),
                        contact.getName(),
                        contact.getTele1(),
                        contact.getTele2(),
                        "", // 分组（暂无）
                        "", // 颜色标签（暂无）
                        contact.getNotes()
                };
                tableModel.addRow(rowData);
            }
        }
    }

    // 显示分组管理对话框
    private void showGroupDialog() {
        JOptionPane.showMessageDialog(this, "分组管理功能开发中...", "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    // 创建工具栏按钮
    private JButton createToolButton(String text, Dimension size, Font font) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(size);
        btn.setFont(font);
        btn.setFocusPainted(false);
        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // 导入联系人
    private void importContacts() {
        JOptionPane.showMessageDialog(this, "导入功能开发中...", "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    // 导出联系人
    private void exportContacts() {
        JOptionPane.showMessageDialog(this, "导出功能开发中...", "提示", JOptionPane.INFORMATION_MESSAGE);
    }

}