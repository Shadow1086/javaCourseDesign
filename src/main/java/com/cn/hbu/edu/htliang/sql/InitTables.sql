-- 删除所有表结构，慎重！！！！！！
drop table if exists  contacts_group;
drop table if exists  tag_contacts;
drop table if exists  tags;
drop table if exists  groups;
drop table if exists contacts;


-- SQLite 默认关闭外键约束，需手动开启
pragma foreign_keys = on;
pragma foreign_keys; -- 返回 1 表示已启用

-- 初始化多对多表之间的关联关系
create table contacts
(
    id    integer primary key autoincrement,
    name  varchar(50) not null,
    tele1 varchar(20) not null unique,
    tele2 varchar(20),
    home  varchar(100),
    email varchar(50),
    notes varchar(200),
    created_at datetime default current_timestamp,
    updated_at datetime default current_timestamp
);
-- 添加触发器
create trigger update_contacts_timestamp
    after update on contacts
begin
    update contacts set updated_at = current_timestamp
        where id = new.id;
end;

create index idx_contacts_name on contacts(name);
create index idx_contacts_tele2 on contacts(tele2);

create table groups
(
    id          integer primary key autoincrement,
    group_name  varchar(40) not null,
    group_notes varchar(50)
);
create table contacts_group
(
    id          integer primary key autoincrement,
    group_id    integer not null references groups (id) on update cascade on delete cascade,
    contacts_id integer not null references contacts (id) on update cascade on delete cascade,
    unique (group_id, contacts_id)
);

-- 创建索引为了加快按照组名查询联系人
create index idx_groups_group_name on groups(group_name);

create table tags
(
    id        integer primary key autoincrement,
    tag_color varchar(10) not null,
    tag_name  varchar(40),
    tag_notes varchar(50)
);
create table tag_contacts
(
    id          integer primary key autoincrement,
    contacts_id integer not null references contacts (id) on update cascade on delete cascade,
    tag_id      integer not null references tags (id) on update cascade on delete cascade,
    unique (contacts_id, tag_id)
);

-- 创建索引，加快按照标签查询联系人
create index idx_tags_tag_name on tags(tag_name);
create index idx_tags_contacts_tag_id on tag_contacts(tag_id);


