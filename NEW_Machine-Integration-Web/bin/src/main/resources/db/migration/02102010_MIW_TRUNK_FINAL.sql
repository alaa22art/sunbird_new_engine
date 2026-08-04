------------------------------------------------------------------------------------------
ALTER TABLE mw_machine_order add COLUMN action_code VARCHAR(2);
--ALTER TABLE mw_machine_order_aud add COLUMN action_code VARCHAR(2);
ALTER TABLE mw_machine_order add COLUMN order_id int8;
ALTER TABLE mw_machine_order_aud add COLUMN order_id int8;
ALTER TABLE mw_machine_order add COLUMN source_type_id int8;
ALTER TABLE mw_machine_order_aud add COLUMN source_type_id int8;
------------------------------------------------------------------------------------------
-------------------------------------------------------------------------
create sequence lkp_message_source_type_seq
 increment 1
 minvalue 1
 maxvalue 9223372036854775807
 start 1
 cache 1;
------------------------------------------------------------------------
 ALTER SEQUENCE lkp_message_source_type_seq OWNER TO "postgres";
 
--------------------------------------------------------------------------
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

-----------------------------------------------------------------------------------------
-----------------------------------------------------------------------------------------------

INSERT INTO "lkp_message_source_type" ("rid", "version", "created_by", "creation_date", "update_date", "updated_by", "name", "code", "description") VALUES (1, 1, 1, '2019-8-5 17:25:46', '2019-8-5 17:25:51', 1, 'unknown', 'unknown', 'unknown');
INSERT INTO "lkp_message_source_type" ("rid", "version", "created_by", "creation_date", "update_date", "updated_by", "name", "code", "description") VALUES (2, 1, 1, '2019-8-6 10:29:55', '2019-8-6 10:29:57', 1, 'HL7', 'Hl7', 'Hl7');




ALTER TABLE "public"."mw_machine_order"
ADD FOREIGN KEY ("source_type_id") REFERENCES "public"."lkp_message_source_type" ("rid") ON DELETE NO ACTION ON UPDATE NO ACTION;
----------------------------------------------------------------------------------------------

------------------------------------------------------------------------------------------------
ALTER TABLE mw_machine_order ALTER COLUMN source_type_id SET DEFAULT 1;
-------------------------------------------------------------------------------------------------
update mw_machine_order set source_type_id =1;
-------------------------------------------------------------------------------------------------------
ALTER TABLE mw_machine_result add COLUMN abnormal_flag VARCHAR(10);
ALTER TABLE mw_machine_result_aud add COLUMN abnormal_flag VARCHAR(10);
ALTER TABLE mw_machine_result add COLUMN result_status VARCHAR(10);
ALTER TABLE mw_machine_result_aud add COLUMN result_status VARCHAR(10);
----------------------------------------------------------------------------------------------------
create sequence outbound_control_id_sequance
 increment 1
 minvalue 1
 maxvalue 9223372036854775807
 start 1
 cache 1;
-------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------------------------
 CREATE SEQUENCE "public"."data_outbound_hl7_message_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 16
 CACHE 1;
------------------------------------------------------------------------------------------------------------------------------------------
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

----------------------------------------------------------------------------------------
alter table data_outbound_hl7_message ALTER COLUMN message_body TYPE VARCHAR(3000);
-------------------------------------------------------------------------------------------
-- --------------------------------------------------------------------------------------------
-- Table structure for data_outbound_hl7_message_aud
-- ----------------------------------------------------------------------------------------------
DROP TABLE IF EXISTS "public"."data_outbound_hl7_message_aud";
CREATE TABLE "public"."data_outbound_hl7_message_aud" (
"rid" int8 NOT NULL,
"message_controller_id" int8,
"message_body" varchar(3000) COLLATE "default",
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
 ------------------------------------------------------------------------------------------------------------------------------------------
 CREATE SEQUENCE "public"."data_result_vs_outbound_hl7_message_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 16
 CACHE 1;
 ------------------------------------------------------------------------------------------------------------------------------------------
-- ----------------------------
-- Table structure for data_result_vs_outbound_hl7_message
-- ----------------------------
CREATE TABLE "public"."data_result_vs_outbound_hl7_message" (
"rid" int8 DEFAULT nextval('data_result_vs_outbound_hl7_message_seq'::regclass) NOT NULL,
"result_id" int8,
"message_id" int8,
"order_id" int8,
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
-- ----------------------------*/
-- ----------------------------
-- Table structure for data_result_vs_outbound_hl7_message
-- ----------------------------
CREATE TABLE "public"."data_result_vs_outbound_hl7_message_aud" (
"rid" int8 NOT NULL,
"result_id" int8,
"message_id" int8,
"order_id" int8,
"version" int8,
"creation_date" timestamp(6) NOT NULL,
"update_date" timestamp(6),
"updated_by" int8,
"tenant_id" int8 NOT NULL,
"created_by" int8 NOT NULL,
"branch_id" varchar(255) COLLATE "default",
"rev" int4 NOT NULL,
"revtype" int2

)
WITH (OIDS=FALSE)

;
 -- ----------------------------
-- Table structure for data_result_vs_outbound_hl7_message_aud
-- ----------------------------
DROP TABLE IF EXISTS "public"."data_result_vs_outbound_hl7_message_aud";

CREATE TABLE "public"."data_result_vs_outbound_hl7_message_aud" (
"rid" int8 DEFAULT nextval('data_result_vs_outbound_hl7_message_seq'::regclass) NOT NULL,
"result_id" int8,
"message_id" int8,
"order_id" int8,
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
-- ----------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------------------------
 CREATE SEQUENCE "public"."data_inbound_hl7_message_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 16
 CACHE 1;
 ------------------------------------------------------------------------------------------------------------------------------------------
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

--ALTER TABLE "public"."data_inbound_hl7_message" OWNER TO "postgres";
-- ----------------------------

CREATE TABLE "public"."data_inbound_hl7_message_aud" (
"rid" int8  NOT NULL,
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
"branch_id" int8,
PRIMARY KEY ("rid", "rev")
)
WITH (OIDS=FALSE)
;

ALTER TABLE "public"."data_inbound_hl7_message_aud" OWNER TO "postgres";


------------------------------------------------------------------------------------------------------------------------------------------
 CREATE SEQUENCE "public"."data_order_vs_inbound_hl7_messages_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 16
 CACHE 1;
 ------------------------------------------------------------------------------------------------------------------------------------------

 
 
 
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
"branch_id" int8,
PRIMARY KEY ("rid")
--FOREIGN KEY ("inbound_message_id") REFERENCES "public"."data_inbound_hl7_message" ("rid") ON DELETE NO ACTION ON UPDATE NO ACTION,
--FOREIGN KEY ("order_id") REFERENCES "public"."mw_machine_order" ("rid") ON DELETE NO ACTION ON UPDATE NO ACTION
)
WITH (OIDS=FALSE)
;

--ALTER TABLE "public"." data_order_vs_inbound_hl7_messages" OWNER TO "postgres"; 

CREATE TABLE "public"."data_order_vs_inbound_hl7_messages_aud" (
"rid" int8 NOT NULL,
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

---------------------------------------------------------------------------------------------------------
CREATE TABLE "public"."lkp_message_source_type_aud" (
"rid" int8 NOT NULL,
"version" int8,
"created_by" int8,
"creation_date" timestamp(6),
"update_date" timestamp(6),
"updated_by" int8,
"name" varchar(255) COLLATE "default",
"code" varchar(255) COLLATE "default" DEFAULT 0,
"description" varchar(255) COLLATE "default",
"rev" int4 NOT NULL,
"revtype" int2,
PRIMARY KEY ("rid", "rev")
)
WITH (OIDS=FALSE)
;

ALTER TABLE "public"."lkp_message_source_type_aud" OWNER TO "postgres";



---------------------------------------------------------------------------------------------------------------------------------------------
ALTER TABLE "public"."data_inbound_hl7_message" ADD PRIMARY KEY ("rid");
--ALTER TABLE "public"."data_order_vs_inbound_hl7_messages" ADD PRIMARY KEY ("rid");
ALTER TABLE "public"."data_outbound_hl7_message" ADD PRIMARY KEY ("rid");
ALTER TABLE "public"."data_result_vs_outbound_hl7_message" ADD PRIMARY KEY ("rid");
ALTER TABLE "public"."data_order_vs_inbound_hl7_messages" ADD FOREIGN KEY ("inbound_message_id") REFERENCES "public"."data_inbound_hl7_message" ("rid") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."data_order_vs_inbound_hl7_messages" ADD FOREIGN KEY ("order_id") REFERENCES "public"."mw_machine_order" ("rid") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."data_result_vs_outbound_hl7_message" ADD FOREIGN KEY ("result_id") REFERENCES "public"."mw_machine_result" ("rid") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."data_result_vs_outbound_hl7_message" ADD FOREIGN KEY ("message_id") REFERENCES "public"."data_outbound_hl7_message" ("rid") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."data_order_vs_inbound_hl7_messages_aud" ADD PRIMARY KEY ("rid", "rev");
ALTER TABLE "public"."data_outbound_hl7_message_aud" ADD PRIMARY KEY ("rid", "rev");
ALTER TABLE "public"."data_result_vs_outbound_hl7_message_aud" ADD PRIMARY KEY ("rid", "rev");
--ALTER TABLE "public"."lkp_message_source_type_aud" ADD PRIMARY KEY ("rid", "rev");
-----------------------------------------------------------------------------------------------------
alter table revinfo RENAME "id" to "rid";
alter table revinfo RENAME "date_oper" to "creation_date";
alter table revinfo RENAME "revtstmp" to "timestamp";
alter table revinfo drop COLUMN "username";
alter table revinfo add COLUMN "created_by" int8;
----------------------------------------------------------------------------------------------------------------
ALTER TABLE "public"."data_inbound_hl7_message_aud" ADD FOREIGN KEY ("rev") REFERENCES "public"."revinfo" ("rid") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."data_order_vs_inbound_hl7_messages_aud" ADD FOREIGN KEY ("rev") REFERENCES "public"."revinfo" ("rid") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."data_outbound_hl7_message_aud" ADD FOREIGN KEY ("rev") REFERENCES "public"."revinfo" ("rid") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."data_result_vs_outbound_hl7_message_aud" ADD FOREIGN KEY ("rev") REFERENCES "public"."revinfo" ("rid") ON DELETE NO ACTION ON UPDATE NO ACTION;
----------------------------------------------------------------------------------------------------------------------------------------------
update lkp_message_source_type set code = 'ASTM' , name = 'ASTM' , description = 'ASTM' where code = 'unknown'
--------------------------------------------------------------------------------------------------------
--update mw_machine_tests set result_code = 'F' where machine_id = 242;
----------------------------------------------------------------------------------------------------------------------
--LKP MASTER
delete from lkp_master;

INSERT INTO "public"."lkp_master" ("version", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "name", "entity") VALUES 
('0', '1', '2019-01-03 10:52:29', '2019-01-10 10:52:36', '1', 'CITY', '{"en_us":"City","ar_jo":"مدينة"}', '{"en_us":"City","ar_jo":"مدينة"}', 'LkpCity');

INSERT INTO "public"."lkp_master" ("version", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "name", "entity") VALUES 
('0', '1', '2019-01-03 10:52:29', '2019-01-10 10:52:36', '1', 'COUNTRY', '{"en_us":"Country","ar_jo":"دولة"}', '{"en_us":"Country","ar_jo":"دولة"}', 'LkpCountry');

INSERT INTO "public"."lkp_master" ("version", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "name", "entity") VALUES 
('0', '1', '2019-01-03 10:52:29', '2019-01-10 10:52:36', '1', 'CURRENCY', '{"en_us":"Currency","ar_jo":"عملة"}', '{"en_us":"Currency","ar_jo":"عملة"}', 'LkpCurrency');

INSERT INTO "public"."lkp_master" ("version", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "name", "entity") VALUES 
('0', '1', '2019-01-03 10:52:29', '2019-01-10 10:52:36', '1', 'GENDER', '{"en_us":"Gender","ar_jo":"جنس"}', '{"en_us":"Gender","ar_jo":"جنس"}', 'LkpGender');

INSERT INTO "public"."lkp_master" ("version", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "name", "entity") VALUES 
('0', '1', '2019-01-03 10:52:29', '2019-01-10 10:52:36', '1', 'MESSAGE_SOURCE_TYPE', '{"en_us":"Message source type","ar_jo":"Message source type"}', '{"en_us":"Message source type","ar_jo":"Message source type"}', 'LkpMessageSourceType');

INSERT INTO "public"."lkp_master" ("version", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "name", "entity") VALUES 
('0', '1', '2019-01-03 10:52:29', '2019-01-10 10:52:36', '1', 'MESSAGE_TYPE', '{"en_us":"Message type","ar_jo":"نوع الرسالة"}', '{"en_us":"Message type","ar_jo":"نوع الرسالة"}', 'LkpMessagesType');

INSERT INTO "public"."lkp_master" ("version", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "name", "entity") VALUES 
('0', '1', '2019-01-03 10:52:29', '2019-01-10 10:52:36', '1', 'PRINT_FORMAT', '{"en_us":"Print format","ar_jo":"شكل الطباعة"}', '{"en_us":"Print format","ar_jo":"شكل الطباعة"}', 'LkpPrintFormat');

INSERT INTO "public"."lkp_master" ("version", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "name", "entity") VALUES 
('0', '1', '2019-01-03 10:52:29', '2019-01-10 10:52:36', '1', 'PROTOCOL', '{"en_us":"Protocol","ar_jo":"بروتوكول"}', '{"en_us":"Protocol","ar_jo":"بروتوكول"}', 'LkpProtocol');

INSERT INTO "public"."lkp_master" ("version", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "name", "entity") VALUES 
('0', '1', '2019-01-03 10:52:29', '2019-01-10 10:52:36', '1', 'SPECIMEN_TYPE', '{"en_us":"Specimen type","ar_jo":"Specimen type"}', '{"en_us":"Specimen type","ar_jo":"Specimen type"}', 'LkpSpecimenType');

INSERT INTO "public"."lkp_master" ("version", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "name", "entity") VALUES 
('0', '1', '2019-01-03 10:52:29', '2019-01-10 10:52:36', '1', 'USER_STATUS', '{"en_us":"User status","ar_jo":"User status"}', '{"en_us":"User status","ar_jo":"User status"}', 'LkpUserStatus');
-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- rights
INSERT INTO "public"."sys_page" ("version", "created_by", "creation_date", "update_date", "updated_by", "description", "name", "module_id") VALUES ('0', '1', '2019-12-01 15:23:10', NULL, NULL, '{"en_us":"Tenant Management","ar_jo":"Tenant Management"}', '{"en_us":"Tenant Management","ar_jo":"Tenant Management"}', '1');
INSERT INTO "public"."sec_rights" ("name", "creation_date", "update_date", "created_by", "updated_by", "version", "code", "description", "page_id") VALUES ('{"en_us":"Update Tenant","ar_jo":"Update Tenant"}', '2018-11-12 13:56:59.47', NULL, '0', NULL, '0', 'UPD_TENANT', '{"en_us":"Update Tenant","ar_jo":"Update Tenant"}', (select max(rid) from sys_page));
INSERT INTO "public"."sec_rights" ("name", "creation_date", "update_date", "created_by", "updated_by", "version", "code", "description", "page_id") VALUES ('{"en_us":"View Tenant Management","ar_jo":"إدارة المستأجر"}', '2017-11-28 13:02:39.844', NULL, '2', NULL, '0', 'VIEW_TENANT_MANAGEMENT', '{"en_us":"View Tenant Management","ar_jo":"View Tenant Management"}',(select max(rid) from sys_page));

INSERT INTO "public"."sys_page" ("version", "created_by", "creation_date", "update_date", "updated_by", "description", "name", "module_id") VALUES ('0', '1', '2019-12-01 15:24:39', NULL, NULL, '{"en_us":"Branch Management","ar_jo":"Branch Management"}', '{"en_us":"Branch Management","ar_jo":"Branch Management"}', '1');
INSERT INTO "public"."sec_rights" ("name", "creation_date", "update_date", "created_by", "updated_by", "version", "code", "description", "page_id") VALUES ('{"en_us":"View Branch","ar_jo":"View Branch"}', '2018-08-16 05:47:32.544', NULL, '0', NULL, '0', 'VIEW_BRANCH', '{"en_us":"View Branch","ar_jo":"View Branch"}', (select max(rid) from sys_page));
INSERT INTO "public"."sec_rights" ("name", "creation_date", "update_date", "created_by", "updated_by", "version", "code", "description", "page_id") VALUES ('{"en_us":"Update Branch","ar_jo":"Update Branch"}', '2018-08-16 05:47:56.647', NULL, '0', NULL, '0', 'UPD_BRANCH', '{"en_us":"Update Branch","ar_jo":"Update Branch"}', (select max(rid) from sys_page));
INSERT INTO "public"."sec_rights" ("name", "creation_date", "update_date", "created_by", "updated_by", "version", "code", "description", "page_id") VALUES ('{"en_us":"Activate Branch","ar_jo":"Activate Branch"}', '2018-08-16 05:48:34.834', NULL, '0', NULL, '0', 'ACTIVATE_BRANCH', '{"en_us":"Activate Branch","ar_jo":"Activate Branch"}', (select max(rid) from sys_page));
INSERT INTO "public"."sec_rights" ("name", "creation_date", "update_date", "created_by", "updated_by", "version", "code", "description", "page_id") VALUES ('{"en_us":"Delete Branch","ar_jo":"Delete Branch"}', '2018-08-16 05:48:10.916', NULL, '0', NULL, '0', 'DEL_BRANCH', '{"en_us":"Delete Branch","ar_jo":"Delete Branch"}', (select max(rid) from sys_page));
INSERT INTO "public"."sec_rights" ("name", "creation_date", "update_date", "created_by", "updated_by", "version", "code", "description", "page_id") VALUES ('{"en_us":"Deactivate Branch","ar_jo":"Deactivate Branch"}', '2018-08-16 05:48:45.691', NULL, '0', NULL, '0', 'DEACTIVATE_BRANCH', '{"en_us":"Deactivate Branch","ar_jo":"Deactivate Branch"}', (select max(rid) from sys_page));
INSERT INTO "public"."sec_rights" ("name", "creation_date", "update_date", "created_by", "updated_by", "version", "code", "description", "page_id") VALUES ('{"en_us":"Add Branch","ar_jo":"Add Branch"}', '2018-08-16 05:47:46.188', NULL, '0', NULL, '0', 'ADD_BRANCH', '{"en_us":"Add Branch","ar_jo":"Add Branch"}', (select max(rid) from sys_page));



-- fix lkp protocl names
update lkp_protocol set name='{"en_us":"ASTM E 1381-97","ar_jo":"ASTM E 1381-97"}',description = '{"en_us":"ASTM E 1381-97","ar_jo":"ASTM E 1381-97"}' where name = 'ASTM E 1381-97';
update lkp_protocol set name='{"en_us":"ASTM E 1381-91","ar_jo":"ASTM E 1381-91"}',description = '{"en_us":"ASTM E 1381-91","ar_jo":"ASTM E 1381-91"}' where name = 'ASTM E 1381-91';
update lkp_protocol set name='{"en_us":"ASTM E 1381-95","ar_jo":"ASTM E 1381-95"}',description = '{"en_us":"ASTM E 1381-95","ar_jo":"ASTM E 1381-95"}' where name = 'ASTM E 1381-95';

--tenant
alter table "public"."sec_tenant" DROP COLUMN "payer_id";
alter table "public"."sec_tenant_aud" DROP COLUMN "payer_id";

alter table "public"."sec_tenant" DROP COLUMN "is_document_auto_download";
alter table "public"."sec_tenant_aud" DROP COLUMN "is_document_auto_download";

alter table "public"."sec_tenant" DROP COLUMN "is_custom_header";
alter table "public"."sec_tenant_aud" DROP COLUMN "is_custom_header";

alter table "public"."sec_tenant" DROP COLUMN "is_custom_footer";
alter table "public"."sec_tenant_aud" DROP COLUMN "is_custom_footer";

alter table "public"."sec_tenant" DROP COLUMN "is_print_header";
alter table "public"."sec_tenant_aud" DROP COLUMN "is_print_header";

alter table "public"."sec_tenant" DROP COLUMN "is_print_footer";
alter table "public"."sec_tenant_aud" DROP COLUMN "is_print_footer";

alter table "public"."sec_tenant" DROP COLUMN "header_image";
alter table "public"."sec_tenant_aud" DROP COLUMN "header_image";

alter table "public"."sec_tenant" DROP COLUMN "footer_image";
alter table "public"."sec_tenant_aud" DROP COLUMN "footer_image";

alter table "public"."sec_tenant" DROP COLUMN "print_format_id";
alter table "public"."sec_tenant_aud" DROP COLUMN "print_format_id";

alter table "public"."sec_tenant" DROP COLUMN "no_of_branches";
alter table "public"."sec_tenant_aud" DROP COLUMN "no_of_branches";

alter table "public"."sec_tenant" DROP COLUMN "no_of_users";
alter table "public"."sec_tenant_aud" DROP COLUMN "no_of_users";

alter table "public"."sec_tenant" DROP COLUMN "status_id";
alter table "public"."sec_tenant_aud" DROP COLUMN "status_id";

alter table "public"."sec_tenant" DROP COLUMN "license_type_id";
alter table "public"."sec_tenant_aud" DROP COLUMN "license_type_id";

--alter table "public"."sec_tenant" DROP COLUMN "start_license_date";
--alter table "public"."sec_tenant_aud" DROP COLUMN "start_license_date";

ALTER TABLE "public"."sec_tenant" DROP COLUMN "ending_license_date";
ALTER TABLE "public"."sec_tenant_aud" DROP COLUMN "ending_license_date";

ALTER TABLE "public"."sec_tenant" ALTER COLUMN "phone_no" TYPE varchar(255) COLLATE "default";
ALTER TABLE "public"."sec_tenant_aud" ALTER COLUMN "phone_no" TYPE varchar(255) COLLATE "default";

ALTER TABLE "public"."sec_tenant" ADD CONSTRAINT "sec_tenant_email_uk" UNIQUE ("email");
ALTER TABLE "public"."sec_tenant" ADD CONSTRAINT "sec_tenant_code_uk" UNIQUE ("code");


--branch
INSERT INTO "public"."lab_branch" ("rid", "name", "phone_no", "address", "version", "creation_date", "update_date", "is_active", "city_id", "created_by", "updated_by", "tenant_id", "code", "integration_url", "integration_token", "mobile_pattern") 
VALUES ('0', '{"en_us":"Admin branch","ar_jo":"Admin branch"}', '4564564', '{"en_us":"amman","ar_jo":"amman"}', '0', '2019-11-28 10:55:31', '2019-11-28 10:55:41.358575', '1', '2', '1', NULL, '0', 'AB', NULL, NULL, '+962###');

ALTER TABLE "public"."lab_branch" ALTER COLUMN "phone_no" TYPE varchar(255);
ALTER TABLE "public"."lab_branch_aud" ALTER COLUMN "phone_no" TYPE varchar(255);
ALTER TABLE "public"."lab_branch" ALTER COLUMN "name" TYPE varchar(4000);
ALTER TABLE "public"."lab_branch_aud" ALTER COLUMN "name" TYPE varchar(4000);
ALTER TABLE "public"."lab_branch" ALTER COLUMN "address" TYPE varchar(4000);
ALTER TABLE "public"."lab_branch_aud" ALTER COLUMN "address" TYPE varchar(4000);


UPDATE "public"."lab_branch" SET "name"='{"en_us":"TeamLab 7th Circle Branch","ar_jo":"TeamLab 7th Circle Branch"}',"address"='{"en_us":"Amman 7th Circle","ar_jo":"Amman 7th Circle"}' WHERE ("code"='SCL');
UPDATE "public"."lab_branch" SET "name"='{"en_us":"Teamlab Sweleh Branch","ar_jo":"Teamlab Sweleh Branch"}', "address"='{"en_us":"Amman -Sweleh","ar_jo":"Amman -Sweleh"}' WHERE ("code"='ASL');
UPDATE "public"."lab_branch" SET "name"='{"en_us":"TeamLab Tla Al-Ali Branch","ar_jo":"TeamLab Tla Al-Ali Branch"}', "address"='{"en_us":"TeamLab Tla Al-Ali Branch","ar_jo":"TeamLab Tla Al-Ali Branch"}' WHERE("code"='PML'); 
UPDATE "public"."lab_branch" SET "name"='{"en_us":"TeamLab Tabarbour Branch","ar_jo":"TeamLab Tabarbour Branch"}', "address"='{"en_us":"amman - Tabarbour","ar_jo":"amman - Tabarbour"}' WHERE("code"='CML');
UPDATE "public"."lab_branch" SET "name"='{"en_us":"Admin branch","ar_jo":"Admin branch"}',"address"='{"en_us":"amman","ar_jo":"amman"}' WHERE ("code"='AB');


alter table lab_branch add column country_id int8;
alter table lab_branch_aud add column country_id int8;
update lab_branch set country_id = 30;
alter table lab_branch alter column country_id set not null;
alter table lab_branch add CONSTRAINT lab_branch_country_id_fkey foreign key(country_id) REFERENCES lkp_country(rid);


