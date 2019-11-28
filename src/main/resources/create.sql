create table if not exists user
(
    user_id int auto_increment not null,
    name    text,
    primary key (user_id)
);

create table if not exists account
(
    account_id int auto_increment not null,
    user_id    int,
    currency   text,
    balance    decimal(15, 2),
    primary key (account_id),
    foreign key (user_id) references user (user_id)
);