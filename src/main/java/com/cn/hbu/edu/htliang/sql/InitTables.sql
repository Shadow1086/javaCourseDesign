-- 删除所有表结构，慎重！！！！！！
DROP TABLE IF EXISTS contacts_group;

DROP TABLE IF EXISTS tag_contacts;

DROP TABLE IF EXISTS tags;

DROP TABLE IF EXISTS GROUPS;

DROP TABLE IF EXISTS contacts;

-- SQLite 默认关闭外键约束，需手动开启
pragma foreign_keys = ON;

pragma foreign_keys;
-- 返回 1 表示已启用

-- 初始化多对多表之间的关联关系
CREATE TABLE contacts (
    id integer PRIMARY KEY autoincrement,
    name varchar(50) NOT NULL,
    tele1 varchar(20) NOT NULL UNIQUE,
    tele2 varchar(20),
    home varchar(100),
    email varchar(50),
    notes varchar(200),
    created_at datetime DEFAULT CURRENT_TIMESTAMP,
    updated_at datetime
);
-- 添加触发器
create trigger update_contacts_timestamp
    after update on contacts
begin
    update contacts set updated_at = current_timestamp
        where id = new.id;
end;

CREATE INDEX idx_contacts_name ON contacts (name);

CREATE INDEX idx_contacts_tele2 ON contacts (tele2);

CREATE TABLE GROUPS (
    id integer PRIMARY KEY autoincrement,
    group_name varchar(40) NOT NULL UNIQUE,
    group_notes varchar(50)
);

CREATE TABLE contacts_group (
    id integer PRIMARY KEY autoincrement,
    group_id integer NOT NULL REFERENCES GROUPS (id) ON UPDATE CASCADE ON DELETE CASCADE,
    contacts_id integer NOT NULL REFERENCES contacts (id) ON UPDATE CASCADE ON DELETE CASCADE,
    UNIQUE (group_id, contacts_id)
);

-- 创建索引为了加快按照组名查询联系人
CREATE INDEX idx_groups_group_name ON GROUPS (group_name);

CREATE TABLE tags (
    id integer PRIMARY KEY autoincrement,
    tag_color varchar(10) NOT NULL UNIQUE,
    tag_name varchar(40) UNIQUE,
    tag_notes varchar(50)
);

CREATE TABLE tag_contacts (
    id integer PRIMARY KEY autoincrement,
    contacts_id integer NOT NULL REFERENCES contacts (id) ON UPDATE CASCADE ON DELETE CASCADE,
    tag_id integer NOT NULL REFERENCES tags (id) ON UPDATE CASCADE ON DELETE CASCADE,
    UNIQUE (contacts_id, tag_id)
);

-- 创建索引，加快按照标签查询联系人
CREATE INDEX idx_tags_tag_name ON tags (tag_name);

CREATE INDEX idx_tags_contacts_tag_id ON tag_contacts (tag_id);