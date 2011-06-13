create table pdb_admin.cross_section (id numeric(20, 0) not null unique, creation_date timestamp not null, description varchar(255), editing_date timestamp not null, editing_user varchar(50) not null, line Geometry, measurement_date timestamp, name varchar(50) not null unique, station numeric(8, 1) not null, state numeric(20, 0) not null, water_body numeric(20, 0) not null, primary key (id), unique (name))
create table pdb_admin.cross_section_part (id numeric(20, 0) not null unique, category varchar(50) not null, description varchar(255), line Geometry, name varchar(50) not null unique, cross_section numeric(20, 0) not null, primary key (id), unique (name))
create table pdb_admin.event (id numeric(20, 0) not null unique, creation_date timestamp not null, description varchar(255), editing_date timestamp not null, editing_user varchar(50) not null, measurement_date timestamp, name varchar(100) not null unique, source varchar(255), type varchar(50), water_body numeric(20, 0) not null, primary key (id), unique (name))
create table pdb_admin.info (key varchar(50) not null unique, value varchar(255), primary key (key))
create table pdb_admin.point (id numeric(20, 0) not null unique, consecutive_num int8 not null, description varchar(255), hight numeric(8, 4), hyk varchar(50), kz varchar(50), location Geometry, name varchar(50) not null, roughness_k_value numeric(8, 1), roughness_kst_value numeric(8, 1), vegetation_ax numeric(8, 3), vegetation_ay numeric(8, 3), vegetation_dp numeric(8, 3), width numeric(8, 4), cross_section_part numeric(20, 0) not null, roughness varchar(50), roughness_point_kind varchar(50), vegetation varchar(50), vegetation_point_kind varchar(50), primary key (id))
create table pdb_admin.point_kind (name varchar(50) not null unique, description varchar(255), label varchar(100) not null, primary key (name))
create table pdb_admin.roughness (name varchar(50) not null, point_kind varchar(50) not null, k_value numeric(8, 1), description varchar(255), kst_value numeric(8, 1), label varchar(100), primary key (name, point_kind))
create table pdb_admin.state (id numeric(20, 0) not null unique, creation_date timestamp not null, description varchar(255), editing_date timestamp not null, editing_user varchar(50) not null, isstatezero char(1) not null, measurement_date timestamp, name varchar(100) not null unique, source varchar(255), primary key (id), unique (name))
create table pdb_admin.vegetation (name varchar(50) not null, point_kind varchar(50) not null, ax numeric(8, 3) not null, ay numeric(8, 3) not null, description varchar(255), dp numeric(8, 3) not null, label varchar(100), primary key (name, point_kind))
create table pdb_admin.water_body (id numeric(20, 0) not null unique, description varchar(255), label varchar(100) not null, name varchar(100) not null unique, riverline Geometry, primary key (id), unique (name))
create table pdb_admin.waterlevel_fixation (id numeric(20, 0) not null unique, creation_date timestamp not null, description varchar(255), discharge numeric(8, 3), editing_date timestamp not null, editing_user varchar(50) not null, location Geometry, measurement_date timestamp, station numeric(8, 1) not null, waterlevel numeric(8, 3), event numeric(20, 0) not null, primary key (id))
alter table pdb_admin.cross_section add constraint FK92AB7DE65D9546F4 foreign key (water_body) references pdb_admin.water_body
alter table pdb_admin.cross_section add constraint FK92AB7DE624428D73 foreign key (state) references pdb_admin.state
alter table pdb_admin.cross_section_part add constraint FK3BE45EAC32C0E2FA foreign key (cross_section) references pdb_admin.cross_section
alter table pdb_admin.event add constraint FK5C6729A5D9546F4 foreign key (water_body) references pdb_admin.water_body
alter table pdb_admin.point add constraint FK65E559059FEF1D3 foreign key (cross_section_part) references pdb_admin.cross_section_part
alter table pdb_admin.point add constraint FK65E5590FCAE211F foreign key (vegetation, vegetation_point_kind) references pdb_admin.vegetation
alter table pdb_admin.point add constraint FK65E5590FD88DEE3 foreign key (roughness, roughness_point_kind) references pdb_admin.roughness
alter table pdb_admin.roughness add constraint FKF40536909E32C9F8 foreign key (point_kind) references pdb_admin.point_kind
alter table pdb_admin.vegetation add constraint FK8EC7C10E9E32C9F8 foreign key (point_kind) references pdb_admin.point_kind
alter table pdb_admin.waterlevel_fixation add constraint FK4EEDCF3222B9E985 foreign key (event) references pdb_admin.event
create sequence seq_pdb