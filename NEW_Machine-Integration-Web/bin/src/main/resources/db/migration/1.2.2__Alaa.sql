update mw_machine_tests set result_code = '' where host_code = 'WBC';
alter table mw_machine add COLUMN is_panel_order int2;
ALTER TABLE "public"."mw_machine_aud"
ADD COLUMN "is_panel_order" int2;
update mw_machine set is_panel_order = 0;
ALTER TABLE mw_machine ALTER COLUMN "is_panel_order" SET DEFAULT 0; 
--------------------------------------------------------------------------------------------------------------------------------------------
INSERT INTO "public"."mw_driver" ( "version", "created_by", "creation_date", "update_date", "updated_by", "config_txt", "observer_config", "name") VALUES ('0', '1', '2020-02-03 12:07:45', '2018-01-29 12:07:48', '1', '{
  "ASTM94": {
    "LabToLIS2A2Converter": {
      "recipient": "LIS2A2ToStringConverter"
    },
    "SocketServer": {
       "recipientActor": "AstmE138194Controller"
    },
    "StringToLIS2A2Converter": {
      "recipient": "LIS2A2ToLabConverter"
    },
    "LIS2A2ToStringConverter": {
      "recipient": "AstmE138194Controller"
    },
    "AstmE138194Controller": {
      "lowLevelRecipient": "SocketServer",
      "maxFrameSize": 240,
      "highLevelRecipient": "StringToLIS2A2Converter"
    },
    "LabHttpClient": {
      "recipient": "LabToLIS2A2Converter"
    },
    "LIS2A2ToLabConverter": {
      "recipient": "LabHttpClient"
    }
  }
  }', '{
  "Observer": {
    "endpoints": {
      "WebSocketServer": {
        "address": "127.0.0.1",
        "port": 8900
      }
    }
  }
}', 'ASTM94');
INSERT INTO "public"."mw_driver" ("version", "created_by", "creation_date", "update_date", "updated_by", "config_txt", "observer_config", "name") VALUES ( '0', '1', '2020-02-03 12:07:45', '2018-01-29 12:07:48', '1', '{
  "ASTM97": {
    "LabToLIS2A2Converter": {
      "recipient": "LIS2A2ToStringConverter"
    },
    "SocketServer": {
       "recipientActor": "AstmE138197Controller"
    },
    "StringToLIS2A2Converter": {
      "recipient": "LIS2A2ToLabConverter"
    },
    "LIS2A2ToStringConverter": {
      "recipient": "AstmE138197Controller"
    },
    "AstmE138197Controller": {
      "lowLevelRecipient": "SocketServer",
      "maxFrameSize": 240,
      "highLevelRecipient": "StringToLIS2A2Converter"
    },
    "LabHttpClient": {
      "recipient": "LabToLIS2A2Converter"
    },
    "LIS2A2ToLabConverter": {
      "recipient": "LabHttpClient"
    }
  }
  }', '{
  "Observer": {
    "endpoints": {
      "WebSocketServer": {
        "address": "127.0.0.1",
        "port": 8900
      }
    }
  }
}', 'ASTM97');
INSERT INTO "public"."mw_driver" ("version", "created_by", "creation_date", "update_date", "updated_by", "config_txt", "observer_config", "name") VALUES ('0', '1', '2020-02-03 12:07:45', '2018-01-29 12:07:48', '1', '{
  "ASTM02": {
    "LabToLIS2A2Converter": {
      "recipient": "LIS2A2ToStringConverter"
    },
    "SocketServer": {
       "recipientActor": "AstmE138102Controller"
    },
    "StringToLIS2A2Converter": {
      "recipient": "LIS2A2ToLabConverter"
    },
    "LIS2A2ToStringConverter": {
      "recipient": "AstmE138102Controller"
    },
    "AstmE138102Controller": {
      "lowLevelRecipient": "SocketServer",
      "maxFrameSize": 240,
      "highLevelRecipient": "StringToLIS2A2Converter"
    },
    "LabHttpClient": {
      "recipient": "LabToLIS2A2Converter"
    },
    "LIS2A2ToLabConverter": {
      "recipient": "LabHttpClient"
    }
  }
  }', '{
  "Observer": {
    "endpoints": {
      "WebSocketServer": {
        "address": "127.0.0.1",
        "port": 8900
      }
    }
  }
}', 'ASTM02');

alter table lkp_protocol_aud add column code VARCHAR(250);
INSERT INTO "public"."lkp_protocol" ("rid", "version", "created_by", "creation_date", "update_date", "updated_by", "name", "code", "description") VALUES ('6', '0', '1', '2020-02-03 12:57:59', null, '1', '{"en_us":"ASTM E 1381-94","ar_jo":"ASTM E 1381-94"}', 'ASTM E 1381-94', '{"en_us":"ASTM E 1381-94","ar_jo":"ASTM E 1381-94"}');

---------------------------------------------------------------------------------------------------------------------------------------------------

CREATE SEQUENCE "public"."mw_machine_type_panel_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 1
 CACHE 1;

ALTER TABLE "public"."mw_machine_type_panel_seq" OWNER TO "postgres";




CREATE TABLE "public"."mw_machine_type_panel" (
"rid" int8 DEFAULT nextval('mw_machine_type_panel_seq'::regclass) NOT NULL,
"machine_type_id" int8,
"panel_name" varchar(255) COLLATE "default",
"panel_host_code" varchar(255) COLLATE "default",
"version" int8 NOT NULL,
"created_by" int8 NOT NULL,
"creation_date" timestamp(6) NOT NULL,
"update_date" timestamp(6),
"updated_by" int8,
"is_active" int2,
PRIMARY KEY ("rid"),
FOREIGN KEY ("machine_type_id") REFERENCES "public"."mw_machine_type" ("rid") ON DELETE NO ACTION ON UPDATE NO ACTION
)
WITH (OIDS=FALSE)
;

ALTER TABLE "public"."mw_machine_type_panel" OWNER TO "postgres";
----------------------------------------------------------
CREATE TABLE "public"."mw_machine_type_panel_aud" (
"rid " int8 NOT NULL,
"machine_type_id" int8,
"panel_name" varchar(255) COLLATE "default",
"panel_host_code" varchar(255) COLLATE "default",
"created_by" int8 NOT NULL,
"creation_date" timestamp(6) NOT NULL,
"update_date" timestamp(6),
"updated_by" int8,
"rev" int4 NOT NULL,
"revtype" int2,
"is_active" int2,
PRIMARY KEY ("rid ", "rev")
)
WITH (OIDS=FALSE)
;

ALTER TABLE "public"."mw_machine_type_panel_aud" OWNER TO "postgres";