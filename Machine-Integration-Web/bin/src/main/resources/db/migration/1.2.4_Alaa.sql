--[2020-08-31 14:03:06.591] [011444] [new_base] [PGSQL]
ALTER TABLE "public"."mw_machine_result"
ADD COLUMN "action_code_id" int8;
--------------------------------------------------------------------------------------------------------
CREATE SEQUENCE "public"."lkp_order_action_code_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 6
 CACHE 1;

ALTER TABLE "public"."lkp_order_action_code_seq" OWNER TO "postgres";

SELECT setval('"public"."lkp_order_action_code_seq"', 6, true);


-------------------------------------------------------------------------------------

/*
Navicat PGSQL Data Transfer

Source Server         : New Server VM1
Source Server Version : 90602
Source Host           : 172.16.30.1:5432
Source Database       : miw_2_1_5_test
Source Schema         : public

Target Server Type    : PGSQL
Target Server Version : 90602
File Encoding         : 65001

Date: 2020-08-31 14:06:20
*/


-- ----------------------------
-- Table structure for lkp_order_action_code
-- ----------------------------
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


---[2020-08-31 14:12:38.318] [011444] [new_base] [PGSQL]
ALTER TABLE "public"."mw_machine_result"
ADD FOREIGN KEY ("action_code_id") REFERENCES "public"."lkp_order_action_code" ("rid");



ALTER TABLE "public"."mw_machine_result"
ADD COLUMN "last_order_id" int8;

ALTER TABLE "public"."mw_machine_result_aud"
ADD COLUMN "last_order_id" int8;

ALTER TABLE "public"."mw_machine_result"
ADD FOREIGN KEY ("last_order_id") REFERENCES "public"."mw_machine_order" ("rid");

ALTER TABLE "public"."mw_machine_result"
ADD COLUMN "machine_test_id" int8;

ALTER TABLE "public"."mw_machine_result"
ADD COLUMN "note" varchar(200);

ALTER TABLE "public"."mw_machine_result_aud"
ADD COLUMN "note" varchar(200);

ALTER TABLE "public"."mw_machine_result_aud"
ADD COLUMN "test_completed_date_time" Date;

ALTER TABLE "public"."mw_machine_result"
ADD COLUMN "note" varchar(200);

ALTER TABLE "public"."mw_machine_result"
ADD COLUMN "last_order_id" int8;

 

ALTER TABLE "public"."mw_machine_result_aud"
ADD COLUMN "last_order_id" int8;

 

ALTER TABLE "public"."mw_machine_result"
ADD FOREIGN KEY ("last_order_id") REFERENCES "public"."mw_machine_order" ("rid");

ALTER TABLE "public"."mw_machine_result_aud"
ADD COLUMN action_code_id int;





