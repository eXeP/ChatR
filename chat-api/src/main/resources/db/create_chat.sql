create table if not exists users (
	id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    added TIMESTAMP,
    username varchar(30) NOT NULL,
    displayname varchar(30) NOT NULL,
    pw_hash varchar(100)
)COLLATE='utf8_unicode_ci';


create table if not exists access_tokens (
	id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    added TIMESTAMP,
    expires TIMESTAMP,
    user_id BIGINT,
    token varchar(20)
)COLLATE='utf8_unicode_ci';

create table if not exists chats (
	id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    added TIMESTAMP,
    name varchar(100),
    starter_user_id varchar(30) NOT NULL,
    private BOOLEAN
)COLLATE='utf8_unicode_ci';

create table if not exists participation (
	id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    added TIMESTAMP,
    user_id BIGINT,
    chat_id BIGINT
)COLLATE='utf8_unicode_ci';

create table if not exists messages (
	id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    sent TIMESTAMP,
    user_id BIGINT,
    chat_id BIGINT,
    message TEXT
)COLLATE='utf8_unicode_ci';