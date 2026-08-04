
--///////////////////////////////
--16/04/2019
--///////////////////////////////
--drop sequence if exists lkp_machine_method_seq
create sequence lkp_message_source_type_seq
 increment 1
 minvalue 1
 maxvalue 9223372036854775807
 start 1
 cache 1;
 ------------------------------------------------------------------------
 ALTER SEQUENCE lkp_message_source_type_seq OWNER TO "postgres";
 
 --------------------------------------------------------------------------
 
  -------------------------------------------------------------------------

CREATE TABLE "public"."lkp_message_source_type" (
"rid" int8 NOT NULL,
"version" int8 NOT NULL,
"created_by" int8 NOT NULL,
"creation_date" timestamp(6) NOT NULL,
"update_date" timestamp(6),
"updated_by" int8,
"name" varchar(255) COLLATE "default",
"code" varchar(255) COLLATE "default" DEFAULT 0 NOT NULL,
"description" varchar(255) COLLATE "default",
PRIMARY KEY ("rid")
)
WITH (OIDS=FALSE)
;

ALTER TABLE "public"."lkp_message_source_type" OWNER TO "postgres";

ALTER TABLE lkp_message_source_type ALTER COLUMN rid SET DEFAULT nextval('lkp_message_source_type_seq');


--------------------------------------------------------------------------------------------

ALTER TABLE "public"."mw_machine_order" ADD COLUMN source_type_id INTEGER;
ALTER TABLE "public"."mw_machine_order_aud" ADD COLUMN source_type_id INTEGER;
ALTER TABLE "public"."mw_machine_order"
ADD FOREIGN KEY ("source_type_id") REFERENCES "public"."lkp_message_source_type" ("rid") ON DELETE NO ACTION ON UPDATE NO ACTION
----------------------------------------------------------------------------------------------





--///////////////////////////////
--17/04/2019
--///////////////////////////////
ALTER TABLE "public"."sec_tenant"
ADD COLUMN "is_custom_header" int2,
ADD COLUMN "is_custom_footer" int2,
ADD COLUMN "is_print_header" int2,
ADD COLUMN "is_print_footer" int2,
ADD COLUMN "is_document_auto_download" int2,
ADD COLUMN "header_image" bytea,
ADD COLUMN "footer_image" bytea;
ALTER TABLE "public"."sec_tenant_aud"
ADD COLUMN "is_custom_header" int2,
ADD COLUMN "is_custom_footer" int2,
ADD COLUMN "is_print_header" int2,
ADD COLUMN "is_print_footer" int2,
ADD COLUMN "is_document_auto_download" int2,
ADD COLUMN "header_image" bytea,
ADD COLUMN "footer_image" bytea;


CREATE TABLE "public"."lkp_print_format" (
"rid" int8 NOT NULL,
"code" varchar(50) NOT NULL,
"name" varchar(4000) NOT NULL,
"description" varchar(4000),
"version" int8 NOT NULL,
"creation_date" timestamp(6) NOT NULL,
"update_date" timestamp(6),
"created_by" int8 NOT NULL,
"updated_by" int8,
PRIMARY KEY ("rid")
);

CREATE TABLE "public"."lkp_print_format_aud" (
"rid" int8 NOT NULL,
"rev" int4 NOT NULL,
"revtype" int2,
"code" varchar(50),
"name" varchar(4000),
"description" varchar(4000),
"version" int8,
"creation_date" timestamp(6),
"update_date" timestamp(6),
"created_by" int8,
"updated_by" int8,
PRIMARY KEY ("rid", "rev")
);

CREATE SEQUENCE "public"."lkp_print_format_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 1
 CACHE 1;

ALTER TABLE "public"."lkp_print_format" OWNER TO "postgres";

 ALTER SEQUENCE lkp_print_format_seq OWNER TO "postgres";

alter table lkp_print_format alter COLUMN rid set DEFAULT nextval('lkp_print_format_seq');



ALTER TABLE "public"."sec_tenant"
ADD COLUMN "print_format_id" int8,
ADD CONSTRAINT "sec_tenant_print_format_fk" FOREIGN KEY ("print_format_id") REFERENCES "public"."lkp_print_format" ("rid") ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE "public"."sec_tenant_aud"
ADD COLUMN "print_format_id" int8;


CREATE SEQUENCE "public"."data_inbound_hl7_message_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 1
 CACHE 1;
 
 
--Navicat PGSQL Data Transfer
--Source Server         : New Server VM1
--Source Server Version : 90602
--Source Host           : 172.16.30.1:5432
--Source Database       : MIW_22_04_2019
--Source Schema         : public
--Target Server Type    : PGSQL
--Target Server Version : 90602
--File Encoding         : 65001
--Date: 2019-05-22 11:44:52

-------------------------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------- Sequence --------------------------------------------------------------
-------------------------------------------------------------------------------------------------------------------------------------------
 
 CREATE SEQUENCE "public"."outbound_Control_ID_Sequance"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 16
 CACHE 1;
 
 ------------------------------------------------------------------------------------------------------------------------------------------
 CREATE SEQUENCE "public"."data_order_vs_inbound_hl7_messages_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 16
 CACHE 1;
 ------------------------------------------------------------------------------------------------------------------------------------------
 CREATE SEQUENCE "public"."data_outbound_hl7_message_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 16
 CACHE 1;
 ------------------------------------------------------------------------------------------------------------------------------------------
 CREATE SEQUENCE "public"."data_inbound_hl7_message_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 16
 CACHE 1;
 ------------------------------------------------------------------------------------------------------------------------------------------
 CREATE SEQUENCE "public"."data_result_vs_outbound_hl7_message_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 16
 CACHE 1;
 ------------------------------------------------------------------------------------------------------------------------------------------
 CREATE SEQUENCE "public"."lkp_message_source_type_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 16
 CACHE 1;
-------------------------------------------------------------------------------------------------------------------------------------------
-------------------------------------------------------------------   Table  --------------------------------------------------------------
-------------------------------------------------------------------------------------------------------------------------------------------


-- ----------------------------
-- Table structure for data_outbound_hl7_message
-- ----------------------------
CREATE TABLE "public"."data_outbound_hl7_message" (
"rid" int8 DEFAULT nextval('data_outbound_hl7_message_seq'::regclass) NOT NULL,
"message_controller_id" int8,
"message_body" varchar(255) COLLATE "default",
"priority" varchar(255) COLLATE "default",
"source" varchar(255) COLLATE "default",
"version" int8 NOT NULL,
"creation_date" timestamp(6) NOT NULL,
"update_date" timestamp(6),
"updated_by" int8,
"tenant_id" int8,
"created_by" int8 NOT NULL,
"branch_id" int8
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Table structure for data_result_vs_outbound_hl7_message
-- ----------------------------
CREATE TABLE "public"."data_result_vs_outbound_hl7_message" (
"rid" int8 DEFAULT nextval('data_result_vs_outbound_hl7_message_seq'::regclass) NOT NULL,
"result_id" int8,
"message_id" int8,
"version" int8,
"creation_date" timestamp(6) NOT NULL,
"update_date" timestamp(6),
"updated_by" int8,
"tenant_id" int8 NOT NULL,
"created_by" int8 NOT NULL,
"branch_id" varchar(255) COLLATE "default"
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Table structure for data_inbound_hl7_message
-- ----------------------------
CREATE TABLE "public"."data_inbound_hl7_message" (
"rid" int8 DEFAULT nextval('data_inbound_hl7_message_seq'::regclass) NOT NULL,
"message_controller_id" int8,
"message_body" varchar(4000) COLLATE "default",
"priority" varchar(255) COLLATE "default",
"source" varchar(255) COLLATE "default",
"version" int8 NOT NULL,
"creation_date" timestamp(6) NOT NULL,
"update_date" timestamp(6),
"updated_by" int8,
"tenant_id" int8,
"created_by" int8 NOT NULL,
"branch_id" int8
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Table structure for data_order_vs_inbound_hl7_messages
-- ----------------------------
CREATE TABLE "public"."data_order_vs_inbound_hl7_messages" (
"rid" int8 DEFAULT nextval('data_order_vs_inbound_hl7_messages_seq'::regclass) NOT NULL,
"order_id" int8,
"inbound_message_id" int8,
"version" int8 NOT NULL,
"creation_date" timestamp(6) NOT NULL,
"update_date" timestamp(6),
"updated_by" int8,
"tenant_id" int8,
"created_by" int8 NOT NULL,
"branch_id" int8
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Table structure for lkp_message_source_type
-- ----------------------------
CREATE TABLE "public"."lkp_message_source_type" (
"rid" int8 DEFAULT nextval('lkp_message_source_type_seq'::regclass) NOT NULL,
"version" int8 NOT NULL,
"created_by" int8 NOT NULL,
"creation_date" timestamp(6) NOT NULL,
"update_date" timestamp(6),
"updated_by" int8,
"name" varchar(255) COLLATE "default",
"code" varchar(255) COLLATE "default" DEFAULT 0 NOT NULL,
"description" varchar(255) COLLATE "default",
PRIMARY KEY ("rid")
)
WITH (OIDS=FALSE)
;

ALTER TABLE "public"."lkp_message_source_type" OWNER TO "postgres";
-- ----------------------------
-- Primary Key structure for table data_inbound_hl7_message
-- ----------------------------
ALTER TABLE "public"."data_inbound_hl7_message" ADD PRIMARY KEY ("rid");

-- ----------------------------
-- Primary Key structure for table data_order_vs_inbound_hl7_messages
-- ----------------------------
ALTER TABLE "public"."data_order_vs_inbound_hl7_messages" ADD PRIMARY KEY ("rid");

-- ----------------------------
-- Primary Key structure for table data_outbound_hl7_message
-- ----------------------------
ALTER TABLE "public"."data_outbound_hl7_message" ADD PRIMARY KEY ("rid");

-- ----------------------------
-- Primary Key structure for table data_result_vs_outbound_hl7_message
-- ----------------------------
ALTER TABLE "public"."data_result_vs_outbound_hl7_message" ADD PRIMARY KEY ("rid");

-- ----------------------------
-- Foreign Key structure for table "public"."data_order_vs_inbound_hl7_messages"
-- ----------------------------
ALTER TABLE "public"."data_order_vs_inbound_hl7_messages" ADD FOREIGN KEY ("inbound_message_id") REFERENCES "public"."data_inbound_hl7_message" ("rid") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."data_order_vs_inbound_hl7_messages" ADD FOREIGN KEY ("order_id") REFERENCES "public"."mw_machine_order" ("rid") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Key structure for table "public"."data_result_vs_outbound_hl7_message"
-- ----------------------------
ALTER TABLE "public"."data_result_vs_outbound_hl7_message" ADD FOREIGN KEY ("result_id") REFERENCES "public"."mw_machine_result" ("rid") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."data_result_vs_outbound_hl7_message" ADD FOREIGN KEY ("message_id") REFERENCES "public"."data_outbound_hl7_message" ("rid") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Table structure for data_inbound_hl7_message_aud
-- ----------------------------
CREATE TABLE "public"."data_inbound_hl7_message_aud" (
"rid" int8 NOT NULL,
"message_controller_id" int8,
"message_body" varchar(4000) COLLATE "default",
"priority" varchar(255) COLLATE "default",
"source" varchar(255) COLLATE "default",
"version" int8,
"creation_date" timestamp(6),
"update_date" timestamp(6),
"updated_by" int8,
"tenant_id" int8,
"created_by" int8,
"rev" int4 NOT NULL,
"revtype" int2
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Table structure for data_order_vs_inbound_hl7_messages_aud
-- ----------------------------
CREATE TABLE "public"."data_order_vs_inbound_hl7_messages_aud" (
"rid" int8 DEFAULT nextval('data_order_vs_inbound_hl7_messages_seq'::regclass) NOT NULL,
"order_id" int8,
"inbound_message_id" int8,
"version" int8,
"creation_date" timestamp(6),
"update_date" timestamp(6),
"updated_by" int8,
"tenant_id" int8,
"created_by" int8,
"rev" int4 NOT NULL,
"revtype" int2,
"branch_id" int8
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Table structure for data_outbound_hl7_message_aud
-- ----------------------------
CREATE TABLE "public"."data_outbound_hl7_message_aud" (
"rid" int8 DEFAULT nextval('data_outbound_hl7_message_seq'::regclass) NOT NULL,
"message_controller_id" int8,
"message_body" varchar(255) COLLATE "default",
"priority" varchar(255) COLLATE "default",
"source" varchar(255) COLLATE "default",
"version" int8,
"creation_date" timestamp(6),
"update_date" timestamp(6),
"updated_by" int8,
"tenant_id" int8,
"created_by" int8,
"rev" int4 NOT NULL,
"revtype" int2,
"branch_id" int8
)
WITH (OIDS=FALSE)

;
-- ----------------------------
-- Table structure for data_result_vs_outbound_hl7_message_aud
-- ----------------------------
CREATE TABLE "public"."data_result_vs_outbound_hl7_message_aud" (
"rid" int8 DEFAULT nextval('data_result_vs_outbound_hl7_message_seq'::regclass) NOT NULL,
"result_id" int8,
"message_id" int8,
"version" int8,
"creation_date" timestamp(6),
"update_date" timestamp(6),
"updated_by" int8,
"tenant_id" int8,
"created_by" int8,
"rev" int4 NOT NULL,
"revtype" int2,
"branch_id" int8
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Table structure for lkp_message_source_type_aud
-- ----------------------------
CREATE TABLE "public"."lkp_message_source_type_aud" (
"rid" int8 DEFAULT nextval('lkp_message_source_type_seq'::regclass) NOT NULL,
"version" int8,
"created_by" int8,
"creation_date" timestamp(6),
"update_date" timestamp(6),
"updated_by" int8,
"name" varchar(255) COLLATE "default",
"code" varchar(255) COLLATE "default" DEFAULT 0,
"description" varchar(255) COLLATE "default",
"rev" int4 NOT NULL,
"revtype" int2
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Alter Sequences Owned By 
-- ----------------------------

-- ----------------------------
-- Primary Key structure for table data_inbound_hl7_message_aud
-- ----------------------------
ALTER TABLE "public"."data_inbound_hl7_message_aud" ADD PRIMARY KEY ("rid", "rev");

-- ----------------------------
-- Primary Key structure for table data_order_vs_inbound_hl7_messages_aud
-- ----------------------------
ALTER TABLE "public"."data_order_vs_inbound_hl7_messages_aud" ADD PRIMARY KEY ("rid", "rev");

-- ----------------------------
-- Primary Key structure for table data_outbound_hl7_message_aud
-- ----------------------------
ALTER TABLE "public"."data_outbound_hl7_message_aud" ADD PRIMARY KEY ("rid", "rev");
-- ----------------------------
-- Primary Key structure for table data_result_vs_outbound_hl7_message_aud
-- ----------------------------
ALTER TABLE "public"."data_result_vs_outbound_hl7_message_aud" ADD PRIMARY KEY ("rid", "rev");

-- ----------------------------
-- Primary Key structure for table lkp_message_source_type_aud
-- ----------------------------
ALTER TABLE "public"."lkp_message_source_type_aud" ADD PRIMARY KEY ("rid", "rev");

-- ----------------------------
-- Foreign Key structure for table "public"."data_inbound_hl7_message_aud"
-- ----------------------------
ALTER TABLE "public"."data_inbound_hl7_message_aud" ADD FOREIGN KEY ("rev") REFERENCES "public"."revinfo" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Key structure for table "public"."data_order_vs_inbound_hl7_messages_aud"
-- ----------------------------
ALTER TABLE "public"."data_order_vs_inbound_hl7_messages_aud" ADD FOREIGN KEY ("rev") REFERENCES "public"."revinfo" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Key structure for table "public"."data_outbound_hl7_message_aud"
-- ----------------------------
ALTER TABLE "public"."data_outbound_hl7_message_aud" ADD FOREIGN KEY ("rev") REFERENCES "public"."revinfo" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Key structure for table "public"."data_outbound_information_aud"
-- ----------------------------
ALTER TABLE "public"."data_outbound_information_aud" ADD FOREIGN KEY ("rev") REFERENCES "public"."revinfo" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Key structure for table "public"."data_result_vs_outbound_hl7_message_aud"
-- ----------------------------
ALTER TABLE "public"."data_result_vs_outbound_hl7_message_aud" ADD FOREIGN KEY ("rev") REFERENCES "public"."revinfo" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Key structure for table "public"."lkp_message_source_type_aud"
-- ----------------------------
ALTER TABLE "public"."lkp_message_source_type_aud" ADD FOREIGN KEY ("rev") REFERENCES "public"."revinfo" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;


