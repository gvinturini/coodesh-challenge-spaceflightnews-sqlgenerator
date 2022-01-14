use db_spacenews;

create table tb_news (
	id integer not null primary key,
    title varchar(300) not null,
    url varchar(185) not null,
    image_url varchar(250) not null,
    news_site varchar(25) not null,
    summary varchar(1220) not null,
    published_at VARCHAR(25) not null,
    updated_at VARCHAR(25) not null,
    featured boolean not null
);

create table tb_launches (
	id_seq integer auto_increment not null primary key,
    id varchar(36),
    provider varchar(16),
    id_news integer,
    foreign key (id_news) references tb_news(id)
);

create table tb_events (
	id_seq integer auto_increment not null primary key,
    id integer,
    provider varchar(16),
    id_news integer,
    foreign key (id_news) references tb_news(id)
);
