/*
Navicat PGSQL Data Transfer

Source Server         : new_base
Source Server Version : 90610
Source Host           : localhost:5432
Source Database       : miw_2_1_3
Source Schema         : public

Target Server Type    : PGSQL
Target Server Version : 90610
File Encoding         : 65001

Date: 2020-02-20 10:48:43
*/




CREATE SEQUENCE "public"."lkp_message_transaction_direction_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 3
 CACHE 1;

ALTER TABLE "public"."lkp_message_transaction_direction_seq" OWNER TO "postgres";
SELECT setval('"public"."lkp_message_transaction_direction_seq"', 3, true);
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
CREATE SEQUENCE "public"."mw_message_transaction_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 3
 CACHE 1;

ALTER TABLE "public"."mw_message_transaction_seq" OWNER TO "postgres";
SELECT setval('"public"."mw_message_transaction_seq"', 3, true);
------------------------------------------------------------------------------------------------------------------------------------------------------


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
--------------------------------------------------------------------------------------------------------------------------------------------
-- ----------------------------
-- Records of lkp_message_transaction_direction
-- ----------------------------
INSERT INTO "public"."lkp_message_transaction_direction" VALUES ('3', '1', '1', '2020-02-11 12:29:24', '2020-02-11 12:29:27', null, 'Inbound', 'IN', 'Inbound');
INSERT INTO "public"."lkp_message_transaction_direction" VALUES ('4', '1', '1', '2020-02-11 12:29:24', '2020-02-11 12:29:27', null, 'Outbound', 'OUT', 'Outbound');

-- ----------------------------
-- Table structure for lkp_message_Transaction_type
-- ----------------------------
DROP TABLE IF EXISTS "public"."lkp_message_Transaction_type";
CREATE TABLE "public"."lkp_message_Transaction_type" (
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
--------------------------------------------------------------------------------------------------------------------------------------------
CREATE TABLE "public"."lkp_message_Transaction_type_aud" (
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

ALTER TABLE "public"."lkp_message_Transaction_type_aud" OWNER TO "postgres";
-- ----------------------------
-- Records of lkp_message_Transaction_type
-- ----------------------------
INSERT INTO "public"."lkp_message_Transaction_type" VALUES ('1', '1', '1', '2020-02-11 12:32:12', '2020-02-11 12:32:15', null, 'Machine', 'Machine', 'Machine');
INSERT INTO "public"."lkp_message_Transaction_type" VALUES ('2', '1', '1', '2020-02-11 12:32:12', '2020-02-11 12:32:15', null, 'LIS', 'LIS', 'LIS');

-- ----------------------------
-- Table structure for mw_message_transaction
-- ----------------------------
DROP TABLE IF EXISTS "public"."mw_message_transaction";
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
"notes" varchar(2000) COLLATE "default"
)
WITH (OIDS=FALSE)

;
-------------------------------------------------------------------------------------------------------------------------------------------
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

-- ----------------------------
-- Alter Sequences Owned By 
-- ----------------------------

-- ----------------------------
-- Primary Key structure for table lkp_message_transaction_direction
-- ----------------------------
ALTER TABLE "public"."lkp_message_transaction_direction" ADD PRIMARY KEY ("rid");

-- ----------------------------
-- Primary Key structure for table lkp_message_Transaction_type
-- ----------------------------
ALTER TABLE "public"."lkp_message_Transaction_type" ADD PRIMARY KEY ("rid");

-- ----------------------------
-- Primary Key structure for table mw_message_transaction
-- ----------------------------
ALTER TABLE "public"."mw_message_transaction" ADD PRIMARY KEY ("rid");

-- ----------------------------
-- Foreign Key structure for table "public"."mw_message_transaction"
-- -----------------------------------------------------------------------------------------------------------------------------------------
ALTER TABLE "public"."mw_message_transaction" ADD FOREIGN KEY ("message_type_id") REFERENCES "public"."lkp_message_Transaction_type" ("rid") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."mw_message_transaction" ADD FOREIGN KEY ("message_direction_id") REFERENCES "public"."lkp_message_transaction_direction" ("rid") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."mw_message_transaction" ADD FOREIGN KEY ("machine_id") REFERENCES "public"."mw_machine" ("rid") ON DELETE NO ACTION ON UPDATE NO ACTION;
--------------------------------------------------------------------------------------------------------------------------------------------
ALTER TABLE "public"."mw_message_transaction" ADD FOREIGN KEY ("machine_id") REFERENCES "public"."mw_machine" ("rid") ON DELETE NO ACTION ON UPDATE NO ACTION;
--------------------------------------------------------------------------------------------------------------------------------------------
ALTER TABLE "public"."mw_machine_result_aud"
ADD COLUMN "message_transaction_id" int8;
ALTER TABLE "public"."mw_machine_result"
ADD COLUMN "message_transaction_id" int8;
--------------------------------------------------------------------------------------------------------------------------------------------
ALTER TABLE "public"."mw_machine_query_aud"
ADD COLUMN "message_transaction_id" int8;
ALTER TABLE "public"."mw_machine_query"
ADD COLUMN "message_transaction_id" int8;
---------------------------------------------------------------------------------------------------------------------------------------------
ALTER TABLE "public"."mw_machine_query"
ADD FOREIGN KEY ("message_transaction_id") REFERENCES "public"."mw_message_transaction" ("rid");
---------------------------------------------------------------------------------------------------------------------------------------------
ALTER TABLE "public"."mw_machine_result"
ADD FOREIGN KEY ("message_transaction_id") REFERENCES "public"."mw_message_transaction" ("rid");
---------------------------------------------------------------------------------------------------------------------------------------------
