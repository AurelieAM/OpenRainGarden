# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table comment (
  id                        bigint auto_increment not null,
  comment                   varchar(255),
  date_posted               datetime,
  type                      varchar(255),
  is_active                 tinyint(1) default 0,
  poster_id                 bigint,
  garden_id                 bigint,
  barrel_id                 bigint,
  paver_id                  bigint,
  constraint pk_comment primary key (id))
;

create table header_footer (
  id                        bigint auto_increment not null,
  header                    longtext,
  sub_header                varchar(255),
  footer                    varchar(255),
  sub_footer                longtext,
  header_image              longblob,
  constraint pk_header_footer primary key (id))
;

create table index_content (
  id                        bigint auto_increment not null,
  title                     varchar(255),
  content                   longtext,
  first_button_text         varchar(255),
  first_button_url          varchar(255),
  second_button_text        varchar(255),
  second_button_url         varchar(255),
  image                     longblob,
  external_image_url        varchar(255),
  constraint pk_index_content primary key (id))
;

create table permeable_pavers (
  id                        bigint auto_increment not null,
  title                     varchar(255),
  property_type             varchar(255),
  address                   varchar(255),
  hide_address              varchar(255),
  description               longtext,
  date_installed            varchar(255),
  material                  varchar(255),
  previous_material         varchar(255),
  size                      varchar(255),
  installer                 varchar(255),
  comment_key               varchar(255),
  image                     longblob,
  external_image_url        varchar(255),
  is_approved               tinyint(1) default 0,
  owner_id                  bigint,
  constraint pk_permeable_pavers primary key (id))
;

create table plant (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  scientific_name           varchar(255),
  placement                 varchar(255),
  growth                    varchar(255),
  climate_type              varchar(255),
  is_initial_plant          tinyint(1) default 0,
  image                     longblob,
  external_image_url        varchar(255),
  constraint pk_plant primary key (id))
;

create table rain_barrel (
  id                        bigint auto_increment not null,
  title                     varchar(255),
  property_type             varchar(255),
  address                   varchar(255),
  hide_address              varchar(255),
  description               longtext,
  date_installed            varchar(255),
  rain_barrel_type          varchar(255),
  capacity                  varchar(255),
  color                     varchar(255),
  material                  varchar(255),
  estimated_cost            varchar(255),
  water_use                 varchar(255),
  overflow_frequency        varchar(255),
  cover                     varchar(255),
  obtained_from             varchar(255),
  installation_type         varchar(255),
  image                     longblob,
  external_image_url        varchar(255),
  is_approved               tinyint(1) default 0,
  owner_id                  bigint,
  constraint pk_rain_barrel primary key (id))
;

create table rain_garden (
  id                        bigint auto_increment not null,
  title                     varchar(255),
  property_type             varchar(255),
  address                   varchar(255),
  hide_address              varchar(255),
  description               longtext,
  date_installed            varchar(255),
  rain_garden_size          varchar(255),
  water_flow_source_size    varchar(255),
  water_flow_description    longtext,
  infiltration_rate         varchar(255),
  image                     longblob,
  is_approved               tinyint(1) default 0,
  external_image_url        varchar(255),
  owner_id                  bigint,
  constraint pk_rain_garden primary key (id))
;

create table resource (
  id                        bigint auto_increment not null,
  header                    varchar(255),
  url                       varchar(255),
  type                      varchar(255),
  image                     longblob,
  external_img              varchar(255),
  constraint pk_resource primary key (id))
;

create table user_info (
  id                        bigint auto_increment not null,
  first_name                varchar(255),
  last_name                 varchar(255),
  email                     varchar(255),
  telephone                 varchar(255),
  password                  varchar(255),
  admin                     tinyint(1) default 0,
  confirm                   tinyint(1) default 0,
  confirm_hash              varchar(255),
  constraint pk_user_info primary key (id))
;


create table rain_garden_plant (
  rain_garden_id                 bigint not null,
  plant_id                       bigint not null,
  constraint pk_rain_garden_plant primary key (rain_garden_id, plant_id))
;
alter table comment add constraint fk_comment_poster_1 foreign key (poster_id) references user_info (id) on delete restrict on update restrict;
create index ix_comment_poster_1 on comment (poster_id);
alter table comment add constraint fk_comment_garden_2 foreign key (garden_id) references rain_garden (id) on delete restrict on update restrict;
create index ix_comment_garden_2 on comment (garden_id);
alter table comment add constraint fk_comment_barrel_3 foreign key (barrel_id) references rain_barrel (id) on delete restrict on update restrict;
create index ix_comment_barrel_3 on comment (barrel_id);
alter table comment add constraint fk_comment_paver_4 foreign key (paver_id) references permeable_pavers (id) on delete restrict on update restrict;
create index ix_comment_paver_4 on comment (paver_id);
alter table permeable_pavers add constraint fk_permeable_pavers_owner_5 foreign key (owner_id) references user_info (id) on delete restrict on update restrict;
create index ix_permeable_pavers_owner_5 on permeable_pavers (owner_id);
alter table rain_barrel add constraint fk_rain_barrel_owner_6 foreign key (owner_id) references user_info (id) on delete restrict on update restrict;
create index ix_rain_barrel_owner_6 on rain_barrel (owner_id);
alter table rain_garden add constraint fk_rain_garden_owner_7 foreign key (owner_id) references user_info (id) on delete restrict on update restrict;
create index ix_rain_garden_owner_7 on rain_garden (owner_id);



alter table rain_garden_plant add constraint fk_rain_garden_plant_rain_garden_01 foreign key (rain_garden_id) references rain_garden (id) on delete restrict on update restrict;

alter table rain_garden_plant add constraint fk_rain_garden_plant_plant_02 foreign key (plant_id) references plant (id) on delete restrict on update restrict;

# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table comment;

drop table header_footer;

drop table index_content;

drop table permeable_pavers;

drop table plant;

drop table rain_garden_plant;

drop table rain_barrel;

drop table rain_garden;

drop table resource;

drop table user_info;

SET FOREIGN_KEY_CHECKS=1;

