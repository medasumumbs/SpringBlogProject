-- Главная таблица проекта - хранит в себе все посты в блоге
create table if not exists post (
    post_id bigint auto_increment PRIMARY KEY,
    title VARCHAR(512) NOT NULL,
    picture_base_64 clob,
    content clob
);
create table if not exists comment (
    comment_id bigint auto_increment PRIMARY KEY,
    content varchar(1024),
    post_id bigint,
    foreign key (post_id) references post(post_id) on delete cascade
);
create table if not exists likes (
    like_id bigint auto_increment PRIMARY KEY,
    post_id bigint,
    foreign key (post_id) references post(post_id) on delete cascade
);
create table if not exists tag (
     tag_id bigint auto_increment PRIMARY KEY,
     tag_value varchar(128),
     post_id bigint,
     foreign key (post_id) references post(post_id) on delete cascade
);
CREATE INDEX tag_value_index ON tag(tag_value);