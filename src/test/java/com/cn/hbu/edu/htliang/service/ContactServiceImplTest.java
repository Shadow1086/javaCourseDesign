package com.cn.hbu.edu.htliang.service;

import com.cn.hbu.edu.htliang.dao.ContactsDao;
import com.cn.hbu.edu.htliang.entityPojo.Contacts;
import com.cn.hbu.edu.htliang.entityPojo.Groups;
import com.cn.hbu.edu.htliang.entityPojo.Tags;
import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ContactServiceImplTest {

    @Test
    public void addContact_validatesBeforeInsert() {
        FakeContactsDao dao = new FakeContactsDao();
        ContactServiceImpl service = new ContactServiceImpl(dao);

        assertFalse(service.addContact("A", "13800138000", null, null, null, null));
        assertEquals(0, dao.inserted.size());

        assertTrue(service.addContact("张三", "13800138000", null, "Beijing", "user@example.com", "notes"));
        assertEquals(1, dao.inserted.size());
    }

    @Test
    public void findById_checksExistsFirst() {
        FakeContactsDao dao = new FakeContactsDao();
        ContactServiceImpl service = new ContactServiceImpl(dao);

        assertNull(service.findById(99));

        Contacts c = new Contacts("张三", "13800138000", null, null, null, null);
        dao.insert(c);
        assertNotNull(service.findById(c.getId()));
    }

    @Test
    public void findByName_returnsNullWhenEmpty() {
        FakeContactsDao dao = new FakeContactsDao();
        ContactServiceImpl service = new ContactServiceImpl(dao);

        assertNull(service.findByName("nobody"));
        dao.insert(new Contacts("Alice", "13800138000", null, null, null, null));
        assertNotNull(service.findByName("Ali"));
    }

    @Test
    public void findByTele_rejectsInvalid() {
        FakeContactsDao dao = new FakeContactsDao();
        ContactServiceImpl service = new ContactServiceImpl(dao);

        assertNull(service.findByTele("123"));
        dao.insert(new Contacts("Alice", "13800138000", null, null, null, null));
        assertEquals(1, service.findByTele("13800138000").size());
    }

    @Test
    public void updateContactInfo_validatesAndUpdates() {
        FakeContactsDao dao = new FakeContactsDao();
        ContactServiceImpl service = new ContactServiceImpl(dao);

        assertFalse(service.updateContactInfo(1, "张三", "13800138000", null, null, null, null));

        Contacts c = new Contacts("张三", "13800138000", null, null, null, null);
        dao.insert(c);

        assertFalse(service.updateContactInfo(c.getId(), "A", "13800138000", null, null, null, null));
        assertFalse(service.updateContactInfo(c.getId(), "张三", "123", null, null, null, null));
        assertFalse(service.updateContactInfo(c.getId(), "张三", "13800138000", null, null, "bad@", null));

        assertTrue(service.updateContactInfo(c.getId(), "李四", "13900139000", "13800138000", "Shanghai", "lisi@example.com", "memo"));
        Contacts updated = dao.findById(c.getId());
        assertEquals("李四", updated.getName());
        assertEquals("13900139000", updated.getTele1());
        assertEquals("13800138000", updated.getTele2());
        assertEquals("Shanghai", updated.getHome());
        assertEquals("lisi@example.com", updated.getEmail());
        assertEquals("memo", updated.getNotes());
    }

    @Test
    public void groupAndTagOperations_handleEmptyInputs() {
        FakeContactsDao dao = new FakeContactsDao();
        ContactServiceImpl service = new ContactServiceImpl(dao);

        assertFalse(service.addGroup("", "notes"));
        assertFalse(service.deleteGroup(""));
        assertNull(service.findByGroup(""));

        assertFalse(service.addTag("", "VIP", "notes"));
        assertFalse(service.deleteTag(""));
        assertNull(service.findByTag(""));

        assertFalse(service.addContactInGroup(new ArrayList<>(), new Groups(1, "Friends", null)));
        assertFalse(service.addContactToTag(new ArrayList<>(), new Tags(1, "VIP", "red", null)));
    }

    @Test
    public void findGroupTagsById_populatesContact() {
        FakeContactsDao dao = new FakeContactsDao();
        ContactServiceImpl service = new ContactServiceImpl(dao);

        Contacts c = new Contacts("Alice", "13800138000", null, null, null, null);
        dao.insert(c);
        Groups g = new Groups(1, "Friends", "memo");
        Tags t = new Tags(1, "VIP", "red", "memo");
        dao.groupsByContact.put(c.getId(), List.of(g));
        dao.tagsByContact.put(c.getId(), List.of(t));

        Contacts result = service.findGroupTagsById(c.getId());
        assertNotNull(result);
        assertEquals(1, result.getGroups().size());
        assertEquals(1, result.getTags().size());
    }

    @Test
    public void importVcfFile_filtersInvalidContacts() throws Exception {
        FakeContactsDao dao = new FakeContactsDao();
        ContactServiceImpl service = new ContactServiceImpl(dao);

        Path vcf = Files.createTempFile("contacts", ".vcf");
        String content = "BEGIN:VCARD\n" +
                "VERSION:3.0\n" +
                "N:Zhang;San;;;\n" +
                "TEL;CELL:13800138000\n" +
                "TEL;CELL:13900139000\n" +
                "EMAIL:user@example.com\n" +
                "ADR:;;;Beijing;;;\n" +
                "NOTE:备注\n" +
                "END:VCARD\n" +
                "BEGIN:VCARD\n" +
                "VERSION:3.0\n" +
                "TEL;CELL:13800138001\n" +
                "END:VCARD\n" +
                "BEGIN:VCARD\n" +
                "VERSION:3.0\n" +
                "N:Bad;User;;;\n" +
                "TEL;CELL:\n" +
                "END:VCARD\n";
        Files.writeString(vcf, content, StandardCharsets.UTF_8);

        int inserted = service.importVcfFile(vcf.toFile());
        assertEquals(1, inserted);
        assertEquals(1, dao.batchInserted.size());
        Contacts imported = dao.batchInserted.get(0);
        assertEquals("13800138000", imported.getTele1());
        assertEquals("13900139000", imported.getTele2());
    }

    @Test
    public void exportVcfFile_writesExpectedFormat() throws Exception {
        FakeContactsDao dao = new FakeContactsDao();
        ContactServiceImpl service = new ContactServiceImpl(dao);

        dao.vcfExportList.add("Alice,13800138000,13900139000,Beijing,alice@example.com,notes,Friends,VIP");

        Path out = Files.createTempFile("contacts-export", ".vcf");
        service.exportVcfFile(out.toFile());

        String fileContent = Files.readString(out, StandardCharsets.UTF_8);
        assertTrue(fileContent.contains("BEGIN:VCARD"));
        assertTrue(fileContent.contains("FN:Alice"));
        assertTrue(fileContent.contains("TEL:13800138000"));
        assertTrue(fileContent.contains("TEL:13900139000"));
        assertTrue(fileContent.contains("ADR:Beijing"));
        assertTrue(fileContent.contains("EMAIL:alice@example.com"));
        assertTrue(fileContent.contains("X-GROUP : Friends"));
        assertTrue(fileContent.contains("X-TAG : VIP"));
    }

    private static class FakeContactsDao implements ContactsDao {
        private int nextId = 1;
        private final Map<Integer, Contacts> contacts = new HashMap<>();
        private final Map<String, List<Contacts>> contactsByGroup = new HashMap<>();
        private final Map<String, List<Contacts>> contactsByTag = new HashMap<>();
        private final List<Contacts> inserted = new ArrayList<>();
        private final List<Contacts> batchInserted = new ArrayList<>();
        private final List<String> vcfExportList = new ArrayList<>();
        private final Map<Integer, List<Groups>> groupsByContact = new HashMap<>();
        private final Map<Integer, List<Tags>> tagsByContact = new HashMap<>();

        @Override
        public void insert(Contacts con) {
            if (con.getId() == null) {
                con.setId(nextId++);
            }
            inserted.add(con);
            contacts.put(con.getId(), con);
        }

        @Override
        public void batchInsert(List<Contacts> contactsList) {
            for (Contacts con : contactsList) {
                insert(con);
                batchInserted.add(con);
            }
        }

        @Override
        public Contacts findById(int id) {
            return contacts.get(id);
        }

        @Override
        public List<Contacts> findAll() {
            return new ArrayList<>(contacts.values());
        }

        @Override
        public List<Contacts> findByName(String name) {
            List<Contacts> result = new ArrayList<>();
            for (Contacts con : contacts.values()) {
                if (con.getName() != null && con.getName().contains(name)) {
                    result.add(con);
                }
            }
            return result;
        }

        @Override
        public List<Contacts> findByTele(String tele) {
            List<Contacts> result = new ArrayList<>();
            for (Contacts con : contacts.values()) {
                if (tele.equals(con.getTele1()) || tele.equals(con.getTele2())) {
                    result.add(con);
                }
            }
            return result;
        }

        @Override
        public void update(Contacts contact) {
            contacts.put(contact.getId(), contact);
        }

        @Override
        public void updateInfo(int id, String newName, String newTele1, String newTele2, String newHome, String newEmail, String newNotes) {
            Contacts con = contacts.get(id);
            if (con == null) {
                return;
            }
            con.setName(newName);
            con.setTele1(newTele1);
            con.setTele2(newTele2);
            con.setHome(newHome);
            con.setEmail(newEmail);
            con.setNotes(newNotes);
        }

        @Override
        public void deleteById(int id) {
            contacts.remove(id);
        }

        @Override
        public boolean exists(int id) {
            return contacts.containsKey(id);
        }

        @Override
        public boolean addGroup(String name, String notes) {
            return true;
        }

        @Override
        public boolean addContactInGroup(Contacts con, Groups group) {
            contactsByGroup.computeIfAbsent(group.getGroup_name(), key -> new ArrayList<>()).add(con);
            return true;
        }

        @Override
        public boolean deleteGroup(String name) {
            return contactsByGroup.remove(name) != null;
        }

        @Override
        public List<Contacts> findByGroup(String name) {
            return contactsByGroup.getOrDefault(name, new ArrayList<>());
        }

        @Override
        public List<Groups> findAllGroup() {
            return new ArrayList<>();
        }

        @Override
        public boolean addTag(String color, String name, String notes) {
            return true;
        }

        @Override
        public boolean deleteTag(String color) {
            return contactsByTag.remove(color) != null;
        }

        @Override
        public boolean addContactToTag(Contacts con, Tags tag) {
            contactsByTag.computeIfAbsent(tag.getTag_color(), key -> new ArrayList<>()).add(con);
            return true;
        }

        @Override
        public List<Contacts> findByTag(String color) {
            return contactsByTag.getOrDefault(color, new ArrayList<>());
        }

        @Override
        public List<Tags> findAllTags() {
            return new ArrayList<>();
        }

        @Override
        public List<Groups> findGroupsByContact(int id) {
            return groupsByContact.getOrDefault(id, new ArrayList<>());
        }

        @Override
        public List<Tags> findTagsByContact(int id) {
            return tagsByContact.getOrDefault(id, new ArrayList<>());
        }

        @Override
        public List<String> writeVcfFileInService(java.io.BufferedWriter bw) {
            return new ArrayList<>(vcfExportList);
        }
    }
}
