-- 初始化多对多表之间的关联关系
create table contacts
(
    id    integer primary key autoincrement,
    name  varchar(50) not null,
    tele1 varchar(20) not null unique,
    tele2 varchar(20),
    home  varchar(100),
    email varchar(50),
    notes varchar(200)
);

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
