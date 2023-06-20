create database tfip_project;

use tfip_project;

create table logincreds (
	id varchar(8) not null,
	username varchar(15) not null,
    password varchar(15) not null,
    constraint logincreds_pk primary key(id)
); 

insert into logincreds values ('abcd1234', 'shiyi', 'kryptonite');
insert into logincreds values ('efgh5678', 'sokyee', 'test1234');
insert into logincreds values ('8asf2221', 'elaine', 'krypt123');

-- get login creds
select * from logincreds; 
select id from logincreds where username = 'shiyi';

create table playerInfo (
	id varchar(8) not null,
    name varchar(15) not null, 
    weight int,
    height int, 
    playerPhoto longblob,
    email varchar(30),
    phoneNumber int,
    dob date, 
    emergencyContact int, 
    emergencyName varchar(15),
    address varchar(30), 
    pastInjuries varchar(30),
    role enum('Player', 'Coach', 'Co-Coach', 'Captain', 'Vice-Captain', 'Treasurer'),
    yearJoined int, 
    constraint playerInfo_pk primary key (id),
    constraint playerInfo_fk foreign key (id) references logincreds(id)
);

insert into playerInfo values ('abcd1234', 'shiyi', 49, 164, null, 'yeoshiyi22@gmail.com', 87846863, '1997-09-02', 91234567, 'Mom, Goh', '22 Greenwich Lane', 'nil', 'Player', 2015); 
insert into playerInfo values ('efgh5678', 'sokyee', 49, 164, null, 'sokyee@gmail.com', 81234567, '1995-10-01', 91234567, 'Husband, Shawn', 'Sembawang Drive xxx', 'nil', 'Treasurer', 2017); 
insert into playerInfo values ('efgh5678', 'sokyee', 49, 164, null, 'sokyee@gmail.com', 81234567, '1995-10-01', 91234567, 'Husband, Shawn', 'Sembawang Drive xxx', 'nil', 'Treasurer', 2017); 
insert into playerInfo values ('8asf2221', 'elaine', 55, 169, null, 'ele@gmail.com', 81234567, '1995-10-01', 91234567, 'Mom, Janet', 'Bukit Panjang Road #12-45', 'nil', 'Captain', 2010); 
select * from logincreds inner join playerInfo on logincreds.id = playerInfo.id;
select * from playerInfo;
select id, name, playerPhoto from playerInfo; 

create table playerPosition(
	id varchar(8) not null,
    pos_id int not null auto_increment, 
    position enum('GS', 'GA', 'WA', 'C', 'WD', 'GD', 'GK') not null,
    constraint playerPosition_pk primary key (pos_id),
    constraint playerPosition_fk foreign key (id) references playerInfo(id)
);

insert into playerPosition (id, position) values ('abcd1234', 'GA');
insert into playerPosition (id, position) values ('abcd1234', 'GS');
insert into playerPosition (id, position) values ('abcd1234', 'WA');
insert into playerPosition (id, position) values ('efgh5678', 'C');
insert into playerPosition (id, position) values ('efgh5678', 'WA');
insert into playerPosition (id, position) values ('8asf2221', 'GD');
select * from playerPosition;
select * from playerInfo inner join playerPosition on playerInfo.id = playerPosition.id;
update playerInfo set name = ?, weight = ?, height = ?, playerPhoto = ?, email = ?, phoneNumber = ?, DOB = ?, emergencyContact = ?, emergencyName = ?, 
address = ?, pastInjuries = ?, role = ?, yearJoined = ? where id = ?; 
update playerPosition set position = ? where id = ? ; 
delete from playerPosition where id = '';

-- statistics table 
create table GameData (
	game_id int not null auto_increment, 
    label varchar(10) not null, 
    against varchar(20) not null,
    date date not null,
    photoUrl text, 
    videoUrl text,
    constraint gameData_pk primary key (game_id)
); 
insert into GameData (label, against, date) values (?, ?, ?); 
select * from GameData; 
select * from FullGameData;
select * from FullGameData where game_id = 50;
select interceptions, quarterSequence from FullGameData where game_id = 9;

create table FullGameData(
	id int not null auto_increment, 
    game_id int not null,
    gs varchar(15) not null, 
    ga varchar(15) not null, 
    wa varchar(15) not null, 
    c varchar(15) not null, 
    wd varchar(15) not null, 
    gd varchar(15) not null,
    gk varchar(15) not null, 
    ownScore int not null, 
    oppScore int not null, 
    gaShotIn int,
    gsShotIn int, 
    gaTotalShots int, 
    gsTotalShots int, 
    ownCpCount int,
    oppCpCount int, 
    oppSelfError int, 
    goodTeamD int, 
    oppMissShot int, 
    interceptions text, 
    lostSelfError int, 
    lostByIntercept int,
    quarterSequence text,
    constraint fullGameData_pk primary key (id),
    constraint fullGameData_fk foreign key (game_id) references GameData(game_id)
);

create table playerStats (
	stats_id int not null auto_increment,
    id varchar(8) not null, 
    cap int, 
    avgShootingPercent decimal,
    avgInterceptionPerGame decimal,
    lastUpdated date, 
    constraint playerStats_pk primary key (stats_id),
    constraint playerStats_fk foreign key (id) references playerInfo(id)
);
insert into playerStats (id) values ('abcd1234');
insert into playerStats (id) values ('efgh5678');
insert into playerStats (id) values ('8asf2221');
insert into playerStats (id) values ('8a62014f');
select * from playerStats;
select avgInterceptionPerGame from playerStats where id = 'abcd1234';
select cap from playerStats where id = 'abcd1234';
update playerStats set avgInterceptionPerGame = 2.0, cap = 3 where id = 'abcd1234';
update playerStats set avgShootingPercent = 83.3 where id = 'abcd1234';

-- SQL Table for Calendar Event 
create table CalendarEvent (
	eventId varchar(10) not null,
    selectedDate date,
    title varchar(20),
	startTime varchar(20),
    endTime varchar(20),
    constraint calendarEvent_pk primary key (eventId)
); 
select * from CalendarEvent;

create table Notifications (
	notifId int not null auto_increment,
	imageUrl text,
    name varchar(15) not null,
	action text not null,
	date date not null,
    constraint notification_pk primary key (notifId)
);	
select * from Notifications;

-- appproved emails for sign up

create table emailTable (
	id int not null auto_increment,
    email varchar(30),
    constraint email_pk primary key (id)
);
select * from emailTable;

-- team funds table
create table teamFunds(
	id varchar(8) not null, 
    name varchar(15) not null,
	paid boolean not null,
    constraint teamfunds_pk primary key (id)
); 
select * from teamFunds;
delete from teamFunds;
update teamFunds set paid = true where id = ?