--회원관리
create table spmember(
mem_num number not null,
id varchar2(16) unique not null,
nick_name varchar2(30) unique,
authority varchar2(30) default 'ROLE_USER' not null,
constraint spmember_pk primary key (mem_num)
);

create table spmember_detail(
mem_num number not null,
name varchar2(30) not null,
passwd varchar2(60) not null,
phone varchar2(15) not null,
email varchar2(50) not null,
zipcode varchar2(5) not null,
address1 varchar2(120) not null,
address2 varchar2(90) not null,
photo blob,
photo_name varchar2(100),
reg_date date default sysdate not null,
modify_date date,
constraint spmember_detail_pk primary key (mem_num),
constraint spmember_detail_fk foreign key (mem_num) references spmember (mem_num)

);

create sequence spmember_seq;

--자동 로그인 -- 테이블 이름은 이걸로 고정임(Spring에서 이테이블을 reference하기 때문에)
create table persistent_logins(
series varchar2(64) primary key,
username varchar2(64) not null,
token varchar2(64) not null,
last_used timestamp not null,

);


--게시판 
create table spboard(
	board_num number not null,
	title varchar2(90) not null,
	content clob not null,
	hit number(8) default 0 not null,
	reg_date date default sysdate not null,
	modify_date date,
	filename varchar2(400),
	ip varchar2(40) not null,
	mem_num number not null,
	constraint spboard_pk primary key (board_num),
	constraint spboard_fk foreign key (mem_num) references spmember (mem_num)
);
create sequence spboard_seq;


--게시판 좋아요
create table spboard_fav(
board_num number not null,
mem_num number not null,
constraint fav_fk1 foreign key (board_num) references spboard (board_num),
constraint fav_fk2 foreign key (mem_num) references spmember (mem_num)
);

--댓글
create table spboard_reply(
	re_num number not null,
	re_content varchar2(900) not null,
	re_date date default sysdate not null,
	re_mdate date,
	re_ip varchar2(40) not null,
	board_num number not null,
	mem_num number not null,
	constraint spreply_pk primary key (re_num),
	constraint spreply_fk1 foreign key (board_num) references spboard (board_num),
	constraint spreply_fk2 foreign key (mem_num) references spmember (mem_num)
);


create sequence spreply_seq;

--대댓글
create table spboard_response
(
	te_num number not null,
	te_content varchar2(900) not null,
	te_date date default sysdate not null,
	te_mdate date,
	te_parent_num number not null, --부모글의 번호가 들어감, 자식글이 아니라 부모글일 경우는 0
	te_depth number not null, --자식글의 깊이. 부모글의 자식글A 1,자식글 B 2, 부모글일 경우 0
	te_ip varchar2(40) not null,
	re_num number not null,
	mem_num number not null,
	constraint spres_pk primary key (te_num),
	constraint spres_fk foreign key (re_num) references spboard_reply(re_num),
	constraint spres_fk1 foreign key (mem_num) references spmember (mem_num)
);

create sequence response_seq;

--그룹 채팅
create table sptalkroom(
talkroom_num number not null,
basic_name varchar2(900) not null, --채팅 멤버를 추가할 때 채팅방 이름을 basic_name에서 가져다 쓰게 만들기
talkroom_date date default sysdate not null,
constraint sptalkroom_pk primary key (talkroom_num)
);

create sequence sptalkroom_seq;

create table sptalk(
talk_num number not null,
talkroom_num number not null,	--수신 그룹
mem_num number not null -- 발신자
message varchar2(4000) not null,
chat_date date default sysdate not null,
constraint sptalk_pk primary key (talk_num),
constraint sptalk_fk1 foreign key (talkroom_num) references sptalkroom (talkroom_num),
constraint sptalk_fk2 foreign key (mem_num) references spmember (mem_num)
);

create sequence sptalk_seq;

create table sptalk_read(
talkroom_num number not null,
talk_num number not null,
mem_num number not null,
constraint read_fk foreign key (talkroom_num) references sptalkroom (talkroom_num),
constraint read_fk2 foreign key (talk_num) references sptalk (talk_num),
constraint read_fk3 foreign key (mem_num) references spmember (mem_num)
);

create table sptalk_member(
talkroom_num number not null,
mem_num number not null,
member_date date default sysdate not null,
constraint sptalkmember_fk1 foreign key (talkroom_num) references sptalkroom (talkroom_num),
constraint sptalkmember_fk2 foreign key (mem_num) references spmember(mem_num)

);
