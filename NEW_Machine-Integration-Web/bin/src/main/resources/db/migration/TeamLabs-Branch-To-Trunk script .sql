


alter table mw_machine add COLUMN is_panel_order int2;


CREATE SEQUENCE "public"."lkp_message_transaction_direction_seq"
INCREMENT 1
MINVALUE 1
MAXVALUE 9223372036854775807
START 3
CACHE 1;
ALTER TABLE "public"."lkp_message_transaction_direction_seq" OWNER TO "postgres";
SELECT setval('"public"."lkp_message_transaction_direction_seq"', 3, true);

-- ----------------------------
-- Table structure for lkp_message_transaction_direction
-- ----------------------------
DROP TABLE IF EXISTS "public"."lkp_message_transaction_direction";
CREATE TABLE "public"."lkp_message_transaction_direction" (
"rid" int8 DEFAULT nextval('lkp_message_transaction_direction_seq'::regclass) NOT NULL,
"version" int8 NOT NULL,
"created_by" int8 NOT NULL,
"creation_date" timestamp(6) NOT NULL,
"update_date" timestamp(6),
"updated_by" int8,
"name" varchar(255) COLLATE "default",
"code" varchar(255) COLLATE "default" DEFAULT 0 NOT NULL,
"description" varchar(255) COLLATE "default"
)
WITH (OIDS=FALSE)

;
--------------------------------------------------------------------------------------------------------------------------------------------
CREATE TABLE "public"."lkp_message_transaction_direction_aud" (
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

ALTER TABLE "public"."lkp_message_transaction_direction_aud" OWNER TO "postgres";

------------------------------------------------------------------------------------------------------------------------------------------------------
CREATE SEQUENCE "public"."lkp_message_transaction_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 3
 CACHE 1;

ALTER TABLE "public"."lkp_message_transaction_seq" OWNER TO "postgres";
SELECT setval('"public"."lkp_message_transaction_seq"', 3, true);
------------------------------------------------------------------------------------------------------------------------------------------------------
CREATE TABLE "public"."lkp_message_transaction_type" (
"rid" int8 DEFAULT nextval('lkp_message_transaction_seq'::regclass) NOT NULL,
"version" int8 NOT NULL,
"created_by" int8 NOT NULL,
"creation_date" timestamp(6) NOT NULL,
"update_date" timestamp(6),
"updated_by" int8,
"code" varchar(50) COLLATE "default" NOT NULL,
"description" varchar(255) COLLATE "default",
"name" varchar(255) COLLATE "default" NOT NULL
)
WITH (OIDS=FALSE)

;
CREATE TABLE "public"."lkp_message_transaction_type_aud" (
"rid" int8 NOT NULL,
"created_by" int8 NOT NULL,
"creation_date" timestamp(6) NOT NULL,
"update_date" timestamp(6),
"updated_by" int8,
"code" varchar(50) COLLATE "default" NOT NULL,
"description" varchar(255) COLLATE "default",
"name" varchar(255) COLLATE "default" NOT NULL,
"rev" int4 NOT NULL,
"revtype" int2,
PRIMARY KEY ("rid", "rev")
)
WITH (OIDS=FALSE)
;

ALTER TABLE "public"."lkp_message_transaction_type_aud" OWNER TO "postgres";
------------------------------------------------------------------------------------------------------------------------------------------------------
CREATE SEQUENCE "public"."mw_message_transaction_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 3
 CACHE 1;

ALTER TABLE "public"."mw_message_transaction_seq" OWNER TO "postgres";
SELECT setval('"public"."mw_message_transaction_seq"', 3, true);
------------------------------------------------------------------------------------------------------------------------------------------------------


CREATE TABLE "public"."mw_message_transaction" (
"rid" int8 DEFAULT nextval('mw_message_transaction_seq'::regclass) NOT NULL,
"message_body" varchar(4000) COLLATE "default",
"version" int8 NOT NULL,
"created_by" int8 NOT NULL,
"creation_date" timestamp(6) NOT NULL,
"update_date" timestamp(6),
"updated_by" int8,
"barcode" varchar(255) COLLATE "default",
"branch_id" int8,
"tenant_id" int8,
"message_type_id" int8,
"machine_id" int8,
"message_direction_id" int8,
"is_processed" int2,
"is_succuss" int2,
"notes" varchar(2000) COLLATE "default",
PRIMARY KEY ("rid")
)
WITH (OIDS=FALSE)

;


CREATE TABLE "public"."mw_message_transaction_aud" (
"rid" int8 NOT NULL,
"message_body" varchar(4000) COLLATE "default",
"message_type" varchar(255) COLLATE "default",
"machine_id" int8,
"created_by" int8 NOT NULL,
"creation_date" timestamp(6) NOT NULL,
"update_date" timestamp(6),
"updated_by" int8,
"barcode" varchar(255) COLLATE "default",
"branch_id" int8,
"tenant_id" int8,
"rev" int4 NOT NULL,
"revtype" int2,
"is_processed" int2,
"is_succuss" int2,
"notes" varchar(2000) COLLATE "default",
"message_type_id" int8,
"message_direction_id" int8,
PRIMARY KEY ("rev", "rid")
)
WITH (OIDS=FALSE)
;

ALTER TABLE "public"."mw_message_transaction_aud" OWNER TO "postgres";





;

ALTER TABLE "public"."mw_machine_query_aud"
ADD COLUMN "message_transaction_id" int8;

ALTER TABLE "public"."mw_machine_query"
ADD COLUMN "message_transaction_id" int8;

ALTER TABLE "public"."mw_machine_query"
ADD FOREIGN KEY ("message_transaction_id") REFERENCES "public"."mw_message_transaction" ("rid");


ALTER TABLE "public"."mw_machine_result"
ADD COLUMN "dilution" varchar(255)  ,
ADD COLUMN "pre_delution" varchar(255)  ,
ADD COLUMN "type_of_range_id" int8,
ADD COLUMN "instrument_operator" varchar(255)  ,
ADD COLUMN "system_operator" varchar(255)  ,
ADD COLUMN "test_pipetted_date_time" timestamp(6),
ADD COLUMN "test_completed_date_time" timestamp(6),
ADD COLUMN "instrument_identification" varchar(255)  ,
ADD COLUMN "sub_module" varchar(255)  ,
ADD COLUMN "analytical_unit" varchar(255)  ,
ADD COLUMN "instrument_no" varchar(255),
ADD COLUMN "abnormal_flag_id" int8,
ADD COLUMN "machine_abnormal_flag_id" int8,
ADD COLUMN "calibration_no" varchar(255),
ADD COLUMN "bottle_qc" varchar(255)  ,
ADD COLUMN "standby_bottle_no_qc" varchar(255)  ,
ADD COLUMN "machine_test_id" int8,
ADD COLUMN "action_code_id" int8,
ADD COLUMN "result_status_id" int8,
ADD COLUMN "machine_result_status_id" int8,
ADD COLUMN "qc_name" varchar(255) ,
ADD COLUMN "qc_lot_number_ref" varchar(255),
ADD COLUMN "qc_control_item_number_ref" varchar(255),
ADD COLUMN "last_order_id" int8 NULL,
ADD COLUMN "ii_lipemia" numeric(15,10),
ADD COLUMN "ii_lcterus" numeric(15,10),
ADD COLUMN "i_hemolysis" numeric(15,10);
--------------------------------------------------------------------------------------------------------------------------------------
ALTER TABLE "public"."mw_machine_result_aud"
ADD COLUMN "dilution" varchar(255)  ,
ADD COLUMN "pre_delution" varchar(255)  ,
ADD COLUMN "type_of_range_id" int8,
ADD COLUMN "instrument_operator" varchar(255)  ,
ADD COLUMN "system_operator" varchar(255)  ,
ADD COLUMN "test_pipetted_date_time" timestamp(6),
ADD COLUMN "test_completed_date_time" timestamp(6),
ADD COLUMN "instrument_identification" varchar(255)  ,
ADD COLUMN "sub_module" varchar(255)  ,
ADD COLUMN "analytical_unit" varchar(255)  ,
ADD COLUMN "instrument_no" varchar(255),
ADD COLUMN "abnormal_flag_id" int8,
ADD COLUMN "machine_abnormal_flag_id" int8,
ADD COLUMN "calibration_no" varchar(255),
ADD COLUMN "bottle_qc" varchar(255)  ,
ADD COLUMN "standby_bottle_no_qc" varchar(255)  ,
ADD COLUMN "machine_test_id" int8,
ADD COLUMN "action_code_id" int8,
ADD COLUMN "result_status_id" int8,
ADD COLUMN "machine_result_status_id" int8,
ADD COLUMN "qc_name" varchar(255) ,
ADD COLUMN "qc_lot_number_ref" varchar(255),
ADD COLUMN "qc_control_item_number_ref" varchar(255),
ADD COLUMN "last_order_id" int8 NULL,
ADD COLUMN "ii_lipemia" numeric(15,10),
ADD COLUMN "ii_lcterus" numeric(15,10),
ADD COLUMN "i_hemolysis" numeric(15,10);


ALTER TABLE "public"."mw_machine_result_aud"
ADD COLUMN "message_transaction_id" int8;

ALTER TABLE "public"."mw_machine_result"
ADD COLUMN "message_transaction_id" int8;


---------------------------------------------------------------------------------------------------------------------------------------------

ALTER TABLE "public"."mw_machine_result"
ADD FOREIGN KEY ("message_transaction_id") REFERENCES "public"."mw_message_transaction" ("rid");

ALTER TABLE "public"."mw_machine_query"
ADD FOREIGN KEY ("message_transaction_id") REFERENCES "public"."mw_message_transaction" ("rid");

ALTER TABLE "public"."mw_machine_result"
ADD COLUMN "note" VARCHAR(1000);


ALTER TABLE "public"."mw_machine_result_aud"
ADD COLUMN "note" VARCHAR(1000);

ALTER TABLE "public"."mw_machine_result"
ADD FOREIGN KEY ("last_order_id") REFERENCES "public"."mw_machine_order" ("rid") ON DELETE NO ACTION ON UPDATE NO ACTION;




ALTER TABLE "public"."mw_machine_result"
--ADD FOREIGN KEY ("abnormal_flag_id") REFERENCES "public"."lkp_result_abnormal_flag" ("rid") ON DELETE NO ACTION ON UPDATE NO ACTION,
--ADD FOREIGN KEY ("machine_abnormal_flag_id") REFERENCES "public"."lkp_result_abnormal_flag" ("rid") ON DELETE NO ACTION ON UPDATE NO ACTION,
ADD FOREIGN KEY ("machine_test_id") REFERENCES "public"."mw_machine_tests" ("rid") ON DELETE NO ACTION ON UPDATE NO ACTION;
--ADD FOREIGN KEY ("action_code_id") REFERENCES "public"."lkp_order_action_code" ("rid") ON DELETE NO ACTION ON UPDATE NO ACTION,
--ADD FOREIGN KEY ("result_status_id") REFERENCES "public"."lkp_result_status" ("rid") ON DELETE NO ACTION ON UPDATE NO ACTION,
--ADD FOREIGN KEY ("type_of_range_id") REFERENCES "public"."lkp_type_of_range" ("rid") ON DELETE NO ACTION ON UPDATE NO ACTION;


ALTER TABLE "public"."lkp_message_transaction_type"
ADD PRIMARY KEY ("rid");

ALTER TABLE "public"."lkp_message_transaction_direction"
ADD PRIMARY KEY ("rid");

INSERT INTO "public"."lkp_message_transaction_type" VALUES ('1', '1', '1', '2020-02-11 12:32:12', '2020-02-11 12:32:15', null, 'Machine', 'Machine', 'Machine');
INSERT INTO "public"."lkp_message_transaction_type" VALUES ('2', '1', '1', '2020-02-11 12:32:12', '2020-02-11 12:32:15', null, 'LIS', 'LIS', 'LIS');


INSERT INTO "public"."lkp_message_transaction_direction" VALUES ('3', '1', '1', '2020-02-11 12:29:24', '2020-02-11 12:29:27', null, 'Inbound', 'IN', 'Inbound');
INSERT INTO "public"."lkp_message_transaction_direction" VALUES ('4', '1', '1', '2020-02-11 12:29:24', '2020-02-11 12:29:27', null, 'Outbound', 'OUT', 'Outbound');


ALTER TABLE "public"."mw_message_transaction" ADD FOREIGN KEY ("message_type_id") REFERENCES "public"."lkp_message_transaction_type" ("rid") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."mw_message_transaction" ADD FOREIGN KEY ("message_direction_id") REFERENCES "public"."lkp_message_transaction_direction" ("rid") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."mw_message_transaction" ADD FOREIGN KEY ("machine_id") REFERENCES "public"."mw_machine" ("rid") ON DELETE NO ACTION ON UPDATE NO ACTION;



INSERT INTO "public"."lkp_message_transaction_type" ( "version", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "name") VALUES ( '1', '1', '2020-02-11 12:32:12', '2020-02-11 12:32:15', NULL, 'Machine', 'Machine', 'Machine');
INSERT INTO "public"."lkp_message_transaction_type" ( "version", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "name") VALUES ( '1', '1', '2020-02-11 12:32:12', '2020-02-11 12:32:15', NULL, 'LIS', 'LIS', 'LIS');
INSERT INTO "public"."lkp_message_transaction_type" ( "version", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "name") VALUES ( '1', '1', '2020-02-11 12:29:24', '2020-02-11 12:29:27', NULL, 'QUERY_RECEIVED', '{"en_us":"Query Received","ar_jo":"Query Received"}', '{"en_us":"Query Received","ar_jo":"Query Received"}');
INSERT INTO "public"."lkp_message_transaction_type" ( "version", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "name") VALUES ( '1', '1', '2020-02-11 12:29:24', '2020-02-11 12:29:27', NULL, 'RESULT_RECEIVED', '{"en_us":"Result Received","ar_jo":"Result Received"}', '{"en_us":"Result Received","ar_jo":"Result Received"}');
INSERT INTO "public"."lkp_message_transaction_type" ( "version", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "name") VALUES ( '1', '1', '2020-02-11 12:29:24', '2020-02-11 12:29:27', NULL, 'TEST_SELECTION', '{"en_us":"Test Selection","ar_jo":"Test Selection"}', '{"en_us":"Test Selection","ar_jo":"Test Selection"}');
INSERT INTO "public"."lkp_message_transaction_type" ( "version", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "name") VALUES ( '1', '1', '2020-02-11 12:29:24', '2020-02-11 12:29:27', NULL, 'CONNECTION_OPEN', '{"en_us":"Connection Open","ar_jo":"Connection Open"}', '{"en_us":"Connection Open","ar_jo":"Connection Open"}');
INSERT INTO "public"."lkp_message_transaction_type" ( "version", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "name") VALUES ( '1', '1', '2020-02-11 12:29:24', '2020-02-11 12:29:27', NULL, 'CONNECTION_CLOSE', '{"en_us":"Connection Close","ar_jo":"Connection Close"}', '{"en_us":"Connection Close","ar_jo":"Connection Close"}');
---------------------------------------------------------------------------------------------------------------------------------------------------------------------
--ALTER TABLE mw_machine_result
--DROP CONSTRAINT machine_abnormal_flag_id;

--[2020-11-02 13:44:55.027] [020456] [new_base] [PGSQL]
ALTER TABLE "public"."mw_machine_result"
DROP COLUMN "abnormal_flag_id",
DROP COLUMN "machine_abnormal_flag_id";
----------------------------------------------------------------------------------------------------------------------------------------
/*
Navicat PGSQL Data Transfer

Source Server         : megalabs_test [40.113.104.250]
Source Server Version : 110600
Source Host           : 40.113.104.250:5432
Source Database       : acculink_prod
Source Schema         : public

Target Server Type    : PGSQL
Target Server Version : 110600
File Encoding         : 65001

Date: 2020-11-02 11:50:25
*/


-- ----------------------------
-- Table structure for lkp_order_action_code
-- ----------------------------

CREATE SEQUENCE "public"."lkp_order_action_code_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 3
 CACHE 1;



DROP TABLE IF EXISTS "public"."lkp_order_action_code";
CREATE TABLE "public"."lkp_order_action_code" (
"rid" int8 DEFAULT nextval('lkp_order_action_code_seq'::regclass) NOT NULL,
"code" varchar(50) COLLATE "default" NOT NULL,
"name" varchar(4000) COLLATE "default" NOT NULL,
"description" varchar(4000) COLLATE "default",
"version" int8 NOT NULL,
"creation_date" timestamp(6) NOT NULL,
"update_date" timestamp(6) DEFAULT now(),
"created_by" int8 NOT NULL,
"updated_by" int8
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of lkp_order_action_code
-- ----------------------------
INSERT INTO "public"."lkp_order_action_code" VALUES ('1', 'C', '{"en_us":"cancel test host","ar_jo":"cancel test host"}', '{"en_us":"cancel test host","ar_jo":"cancel test host"}', '1', '2020-04-06 03:30:39', '2020-04-06 03:30:42', '1', '1');
INSERT INTO "public"."lkp_order_action_code" VALUES ('2', 'N', '{"en_us":"patient result","ar_jo":"patient result"}', '{"en_us":"patient result","ar_jo":"patient result"}', '1', '2020-04-06 03:30:39', '2020-04-06 03:30:42', '1', '1');
INSERT INTO "public"."lkp_order_action_code" VALUES ('3', 'O', '{"en_us":"order query responce","ar_jo":"order query responce"}', '{"en_us":"order query responce","ar_jo":"order query responce"}', '1', '2020-04-06 03:32:08', '2020-04-06 03:32:13', '1', '1');
INSERT INTO "public"."lkp_order_action_code" VALUES ('4', 'A', '{"en_us":"add test","ar_jo":"add test"}', '{"en_us":"add test","ar_jo":"add test"}', '1', '2020-04-06 03:30:39', '2020-04-06 03:30:42', '1', '1');
INSERT INTO "public"."lkp_order_action_code" VALUES ('5', 'Q', '{"en_us":"quality result","ar_jo":"quality result"}', '{"en_us":"quality result","ar_jo":"quality result"}', '1', '2020-04-06 03:30:39', '2020-04-06 03:30:42', '1', '1');
INSERT INTO "public"."lkp_order_action_code" VALUES ('6', 'R', '{"en_us":"rerun test","ar_jo":"rerun test"}', '{"en_us":"rerun test","ar_jo":"rerun test"}', '1', '2020-04-06 03:35:04', '2020-04-06 03:35:08', '1', '1');

-- ----------------------------
-- Alter Sequences Owned By 
-- ----------------------------

-- ----------------------------
-- Primary Key structure for table lkp_order_action_code
-- ----------------------------
ALTER TABLE "public"."lkp_order_action_code" ADD PRIMARY KEY ("rid");

--[2020-11-02 13:56:33.275] [020456] [new_base] [PGSQL]
ALTER TABLE "public"."mw_machine_result"
ADD FOREIGN KEY ("action_code_id") REFERENCES "public"."lkp_order_action_code" ("rid");

--------------------------------------------------------------------------------------------------------
alter table mw_machine_aud add COLUMN is_panel_order int2;

