update public.lkp_message_transaction_direction set name='{"en_us":"Inbound","ar_jo":"Inbound"}', description = '{"en_us":"Inbound","ar_jo":"Inbound"}' where code='IN';
update public.lkp_message_transaction_direction set name='{"en_us":"Outbound","ar_jo":"Outbound"}', description = '{"en_us":"Outbound","ar_jo":"Outbound"}' where code='OUT';

update lkp_specimen_type set name='{"en_us":"Serum","ar_jo":"Serum"}',description ='{"en_us":"Serum","ar_jo":"Serum"}' where code='SERUM';
update lkp_specimen_type set name='{"en_us":"Whole blood","ar_jo":"Whole blood"}',description ='{"en_us":"Whole blood","ar_jo":"Whole blood"}' where code='Whole blood' or code='BLOOD';
update lkp_specimen_type set name='{"en_us":"Urine","ar_jo":"Urine"}',description ='{"en_us":"Urine","ar_jo":"Urine"}' where code='URINE';

-- drop non tenanted lkps' audits
DROP TABLE IF EXISTS "lkp_tenant_lic_type";
DROP TABLE IF EXISTS "lkp_tenant_lic_type_aud";
DROP TABLE IF EXISTS "lkp_tenant_status";
DROP TABLE IF EXISTS "lkp_tenant_status_aud";
DROP TABLE IF EXISTS "mw_machine_order_query_response_aud";
DROP TABLE IF EXISTS "lkp_city_aud";
DROP TABLE IF EXISTS "lkp_country_aud";
DROP TABLE IF EXISTS "lkp_currency_aud";
DROP TABLE IF EXISTS "lkp_master_aud";
DROP TABLE IF EXISTS "lkp_message_Transaction_type_aud";
DROP TABLE IF EXISTS "lkp_message_source_type_aud";
DROP TABLE IF EXISTS "lkp_message_transaction_direction_aud";
DROP TABLE IF EXISTS "lkp_messages_type_aud";
DROP TABLE IF EXISTS "lkp_protocol_aud";
DROP TABLE IF EXISTS "lkp_print_format";
DROP TABLE IF EXISTS "lkp_print_format_aud";
ALTER TABLE public.mw_machine_tests DROP column IF EXISTS test_caption_id;
DROP TABLE IF EXISTS "lkp_test_captions";
DROP TABLE IF EXISTS "mw_communication_setup";
DROP TABLE IF EXISTS "lkp_communication_type";
ALTER TABLE public.mw_machine_type DROP column IF EXISTS panel_order_type_id;
DROP TABLE IF EXISTS "lkp_panel_order_types";

--mw_machine_type_tests
ALTER TABLE public.mw_machine_type_tests RENAME COLUMN "defult_host_code" TO "default_host_code";
ALTER TABLE public.mw_machine_type_tests_aud RENAME COLUMN "defult_host_code" TO "default_host_code";

--mw_message_transaction
update mw_message_transaction set is_processed = 0 where  is_processed is null;
alter table mw_message_transaction alter column is_processed set not null;

alter table mw_message_transaction rename column is_succuss to is_success;
alter table mw_message_transaction_aud rename column is_succuss to is_success;
update mw_message_transaction set is_success = 0 where  is_success is null;
alter table mw_message_transaction alter column is_success set not null;

alter table mw_message_transaction_aud alter column created_by drop not null;
alter table mw_message_transaction_aud alter column creation_date drop not null;

--machine
ALTER TABLE public.mw_machine RENAME COLUMN connected to is_connected;
ALTER TABLE public.mw_machine_aud RENAME COLUMN connected to is_connected;

ALTER TABLE public.mw_machine RENAME COLUMN "is_send_state_active" TO "is_send_order";
ALTER TABLE public.mw_machine_aud RENAME COLUMN "is_send_state_active" TO "is_send_order";

ALTER TABLE public.mw_machine RENAME COLUMN "is_receive_state_active" TO "is_receive_result";
ALTER TABLE public.mw_machine_aud RENAME COLUMN "is_receive_state_active" TO "is_receive_result";

--machine type
ALTER TABLE public.mw_machine_type DROP COLUMN is_machine_name_required;
ALTER TABLE public.mw_machine_type RENAME COLUMN "is_machineNameRequired" TO is_machine_name_required;

--LABELS
UPDATE public.com_tenant_messages set description ='{"en_us":"Optimiza Solutions, All rights reserved.","ar_jo":"جميع الحقوق محفوظة لشركة الفارس الأهلية"}' where code='copyrights';
UPDATE public.com_tenant_messages set code ='tenantManagement' where code='tenantManegement' and not exists(select ctm from com_tenant_messages ctm where ctm.code<>'tenantManagement');
UPDATE public.com_tenant_messages set description='{"en_us":"Upload","ar_jo":"تحميل ملف"}' where code='upload';
UPDATE public.com_tenant_messages set code='externalTestsDetails' where code='External_Tests_Details';
UPDATE public.com_tenant_messages set code='externalTestsCode' where code='External_Tests_Code';
UPDATE public.com_tenant_messages set description ='{"en_us":"Success","ar_jo":"نجاح"}' where code='success';
UPDATE public.com_tenant_messages set description ='{"en_us":"Transaction Date Time","ar_jo":"تاريخ و وقت المعاملة"}' where code='transactionDatetime';
update public.com_tenant_messages set description ='{"en_us":"High Level Protocol","ar_jo":"High Level Protocol"}' where code='highLevelProtocol';
update public.com_tenant_messages set description ='{"en_us":"Low Level Protocol","ar_jo":"Low Level Protocol"}' where code='lowLevelProtocol';
update public.com_tenant_messages set description ='{"en_us":"Selected Protocol","ar_jo":"Selected Protocol"}' where code='selectedProtocol';
update public.com_tenant_messages set code = 'serverIP' where code = 'ServerIP';
update public.com_tenant_messages set description = '{"en_us":"Username","ar_jo":"اسم المستخدم"}' where code = 'username';

delete from com_tenant_messages  where code ='isActive';
delete from com_tenant_messages  where code ='tenantMessages';
delete from com_tenant_messages  where code ='successToast';
delete from com_tenant_messages  where code = 'isMachineTypeSelected';
delete from com_tenant_messages  where code='machineGridTypehighLevelProtocol';
delete from com_tenant_messages  where code='machineTypeGridlowLevelProtocol';
delete from com_tenant_messages  where code ='TestCatalageGridRequesterCode';
delete from com_tenant_messages  where code='defultHostCode';
delete from com_tenant_messages  where code='HostCode';
delete from com_tenant_messages  where code = 'descriptionLabel';
delete from com_tenant_messages  where code='machineGridserverPort';

INSERT INTO public.com_tenant_messages (tenant_id,created_by,creation_date,update_date,updated_by,code,description,type_id,"version") select st.rid,0,'2020-07-21 08:00:23.248',NULL,NULL,'devices','{"en_us":"Devices","ar_jo":"الأجهزة"}',59,0 from sec_tenant st where not exists(select ctm from com_tenant_messages ctm where ctm.code = 'devices');
INSERT INTO public.com_tenant_messages (tenant_id,created_by,creation_date,update_date,updated_by,code,description,type_id,"version") select st.rid,0,'2020-04-29 08:11:56.213',NULL,NULL,'singleSelection','{"en_us":"Single Selection","ar_jo":"الاختيار الفردي"}',59,0 from sec_tenant st where not exists(select ctm from com_tenant_messages ctm where ctm.code = 'singleSelection');
INSERT INTO public.com_tenant_messages (tenant_id,created_by,creation_date,update_date,updated_by,code,description,type_id,"version") select st.rid,0,'2020-07-29 10:24:57.822',NULL,NULL,'logs','{"en_us":"Logs","ar_jo":"السجلات"}',59,0 from sec_tenant st where not exists(select ctm from com_tenant_messages ctm where ctm.code = 'logs');
INSERT INTO public.com_tenant_messages (tenant_id,created_by,creation_date,update_date,updated_by,code,description,type_id,"version") select st.rid,0,'2020-05-11 11:40:36.043',NULL,NULL,'systemSettings','{"en_us":"System Settings","ar_jo":"إعدادات النظام"}',59,0 from sec_tenant st where not exists(select ctm from com_tenant_messages ctm where ctm.code = 'systemSettings');
INSERT INTO public.com_tenant_messages (tenant_id,created_by,creation_date,update_date,updated_by,code,description,type_id,"version") select st.rid,0,'2020-04-09 11:19:11.268',NULL,NULL,'machines','{"en_us":"Machines","ar_jo":"الأجهزة"}',59,0 from sec_tenant st where not exists(select ctm from com_tenant_messages ctm where ctm.code = 'machines');
INSERT INTO public.com_tenant_messages (tenant_id,created_by,creation_date,update_date,updated_by,code,description,type_id,"version") select st.rid,0,'2020-08-13 10:36:51.685',NULL,NULL,'patientSampleLookup','{"en_us":"Patient Sample Lookup","ar_jo":"Patient Sample Lookup"}',59,0 from sec_tenant st where not exists(select ctm from com_tenant_messages ctm where ctm.code = 'patientSampleLookup');
INSERT INTO public.com_tenant_messages (tenant_id,created_by,creation_date,update_date,updated_by,code,description,type_id,"version") select st.rid,0,'2020-05-31 09:07:13.410',NULL,NULL,'barcode','{"en_us":"Barcode","ar_jo":"Barcode"}',59,0 from sec_tenant st where not exists(select ctm from com_tenant_messages ctm where ctm.code = 'barcode');
INSERT INTO public.com_tenant_messages (tenant_id,created_by,creation_date,update_date,updated_by,code,description,type_id,"version") select st.rid,0,'2020-05-12 08:54:10.303',NULL,NULL,'to','{"en_us":"To","ar_jo":"إلى"}',59,0 from sec_tenant st where not exists(select ctm from com_tenant_messages ctm where ctm.code = 'to');
INSERT INTO public.com_tenant_messages (tenant_id,created_by,creation_date,update_date,updated_by,code,description,type_id,"version") select st.rid,0,'2020-05-12 08:53:48.156',NULL,NULL,'from','{"en_us":"From","ar_jo":"من"}',59,0 from sec_tenant st where not exists(select ctm from com_tenant_messages ctm where ctm.code = 'from');
INSERT INTO public.com_tenant_messages (tenant_id,created_by,creation_date,update_date,updated_by,code,description,type_id,"version") select st.rid,0,'2020-05-13 09:19:22.986',NULL,NULL,'inProgress','{"en_us":"In Progress","ar_jo":"قيد التنفيذ"}',59,0 from sec_tenant st where not exists(select ctm from com_tenant_messages ctm where ctm.code = 'inProgress');
INSERT INTO public.com_tenant_messages (tenant_id,created_by,creation_date,update_date,updated_by,code,description,type_id,"version") select st.rid,0,'2020-08-13 12:19:51.826',NULL,NULL,'ordered','{"en_us":"Ordered","ar_jo":"Ordered"}',59,0 from sec_tenant st where not exists(select ctm from com_tenant_messages ctm where ctm.code = 'ordered');
INSERT INTO public.com_tenant_messages (tenant_id,created_by,creation_date,update_date,updated_by,code,description,type_id,"version") select st.rid,0,'2020-05-13 09:59:13.373',NULL,NULL,'order','{"en_us":"Order","ar_jo":"طلب"}',59,0 from sec_tenant st where not exists(select ctm from com_tenant_messages ctm where ctm.code = 'order');
INSERT INTO public.com_tenant_messages (tenant_id,created_by,creation_date,update_date,updated_by,code,description,type_id,"version") select st.rid,0,'2020-07-21 08:00:23.248',NULL,NULL,'devices','{"en_us":"Devices","ar_jo":"الأجهزة"}',59,0 from sec_tenant st where not exists(select ctm from com_tenant_messages ctm where ctm.code = 'devices');
INSERT INTO public.com_tenant_messages (tenant_id,created_by,creation_date,update_date,updated_by,code,description,type_id,"version") select st.rid,0,'2020-07-29 10:23:53.145',NULL,NULL,'deviceDetails','{"en_us":"Device Details","ar_jo":"تفاصيل الأجهزة"}',59,0 from sec_tenant st where not exists(select ctm from com_tenant_messages ctm where ctm.code = 'deviceDetails');
INSERT INTO public.com_tenant_messages (tenant_id,created_by,creation_date,update_date,updated_by,code,description,type_id,"version") select st.rid,0,'2020-08-06 08:24:01.109',NULL,NULL,'deviceTestsMapping','{"en_us":"Device Tests Mapping","ar_jo":"Device Tests Mapping"}',59,0 from sec_tenant st where not exists(select ctm from com_tenant_messages ctm where ctm.code = 'deviceTestsMapping');
INSERT INTO public.com_tenant_messages (tenant_id,created_by,creation_date,update_date,updated_by,code,description,type_id,"version") select st.rid,0,'2020-07-19 12:25:52.678',NULL,NULL,'general','{"en_us":"General","ar_jo":"عام"}',59,0 from sec_tenant st where not exists(select ctm from com_tenant_messages ctm where ctm.code = 'general');
INSERT INTO public.com_tenant_messages (tenant_id,created_by,creation_date,update_date,updated_by,code,description,type_id,"version") select st.rid,0,'2020-07-14 09:54:39.249',NULL,NULL,'enabled','{"en_us":"Enabled","ar_jo":"ممكن"}',59,0 from sec_tenant st where not exists(select ctm from com_tenant_messages ctm where ctm.code = 'enabled');
INSERT INTO public.com_tenant_messages (tenant_id,created_by,creation_date,update_date,updated_by,code,description,type_id,"version") select st.rid,0,'2020-06-07 12:48:22.374',NULL,NULL,'details','{"en_us":"Details","ar_jo":"تفاصيل"}',59,0 from sec_tenant st where not exists(select ctm from com_tenant_messages ctm where ctm.code = 'details');
INSERT INTO public.com_tenant_messages (tenant_id,created_by,creation_date,update_date,updated_by,code,description,type_id,"version") select st.rid,0,'2020-08-05 08:50:44.143',NULL,NULL,'serverHost','{"en_us":"Server Host","ar_jo":"Server Host"}',59,0 from sec_tenant st where not exists(select ctm from com_tenant_messages ctm where ctm.code = 'serverHost');
INSERT INTO public.com_tenant_messages (tenant_id,created_by,creation_date,update_date,updated_by,code,description,type_id,"version") select st.rid,0,'2020-08-05 06:37:19.246',NULL,NULL,'cloud','{"en_us":"Cloud","ar_jo":"Cloud"}',59,0 from sec_tenant st where not exists(select ctm from com_tenant_messages ctm where ctm.code = 'cloud');
INSERT INTO public.com_tenant_messages (tenant_id,created_by,creation_date,update_date,updated_by,code,description,type_id,"version") select st.rid,0,'2020-08-05 06:36:43.550',NULL,NULL,'disconnected','{"en_us":"Disconnected","ar_jo":"غير متصل"}',59,0 from sec_tenant st where not exists(select ctm from com_tenant_messages ctm where ctm.code = 'disconnected');
INSERT INTO public.com_tenant_messages (tenant_id,created_by,creation_date,update_date,updated_by,code,description,type_id,"version") select st.rid,0,'2020-08-05 06:36:28.553',NULL,NULL,'connected','{"en_us":"Connected","ar_jo":"متصل"}',59,0 from sec_tenant st where not exists(select ctm from com_tenant_messages ctm where ctm.code = 'connected');
INSERT INTO public.com_tenant_messages (tenant_id,created_by,creation_date,update_date,updated_by,code,description,type_id,"version") select st.rid,0,'2020-07-29 05:35:30.104',NULL,NULL,'noDeviceSelected','{"en_us":"No Device Is Selected","ar_jo":"لم يتم تحديد أي جهاز"}',59,0 from sec_tenant st where not exists(select ctm from com_tenant_messages ctm where ctm.code = 'noDeviceSelected');
INSERT INTO public.com_tenant_messages (tenant_id,created_by,creation_date,update_date,updated_by,code,description,type_id,"version") select st.rid,0,'2020-07-16 07:40:14.414',NULL,NULL,'driverConfig','{"en_us":"Driver Config","ar_jo":"Driver Config"}',59,0 from sec_tenant st where not exists(select ctm from com_tenant_messages ctm where ctm.code = 'driverConfig');
INSERT INTO public.com_tenant_messages (tenant_id,created_by,creation_date,update_date,updated_by,code,description,type_id,"version") select st.rid,0,'2020-07-16 07:43:48.439',NULL,NULL,'drivers','{"en_us":"Drivers","ar_jo":"Drivers"}',59,0 from sec_tenant st where not exists(select ctm from com_tenant_messages ctm where ctm.code = 'drivers');
INSERT INTO public.com_tenant_messages (tenant_id,created_by,creation_date,update_date,updated_by,code,description,type_id,"version") select st.rid,0,'2020-08-11 07:04:42.212',NULL,NULL,'driverAssaysMapping','{"en_us":"Driver Assays Mapping","ar_jo":"Driver Assays Mapping"}',59,0 from sec_tenant st where not exists(select ctm from com_tenant_messages ctm where ctm.code = 'driverAssaysMapping');
INSERT INTO public.com_tenant_messages (tenant_id,created_by,creation_date,update_date,updated_by,code,description,type_id,"version") select st.rid,0,'2020-07-14 11:41:25.748',NULL,NULL,'noDriverSelected','{"en_us":"No Driver Is Selected","ar_jo":"No Driver Is Selected"}',59,0 from sec_tenant st where not exists(select ctm from com_tenant_messages ctm where ctm.code = 'noDriverSelected');
INSERT INTO public.com_tenant_messages (tenant_id,created_by,creation_date,update_date,updated_by,code,description,type_id,"version") select st.rid,0,'2020-08-11 05:25:39.165',NULL,NULL,'unmapped','{"en_us":"Unmapped","ar_jo":"Unmapped"}',59,0 from sec_tenant st where not exists(select ctm from com_tenant_messages ctm where ctm.code = 'unmapped');
INSERT INTO public.com_tenant_messages (tenant_id,created_by,creation_date,update_date,updated_by,code,description,type_id,"version") select st.rid,0,'2020-08-11 05:25:13.025',NULL,NULL,'mapped','{"en_us":"Mapped","ar_jo":"Mapped"}',59,0 from sec_tenant st where not exists(select ctm from com_tenant_messages ctm where ctm.code = 'mapped');
INSERT INTO public.com_tenant_messages (tenant_id,created_by,creation_date,update_date,updated_by,code,description,type_id,"version") select st.rid,0,'2020-08-05 08:55:26.975',NULL,NULL,'network','{"en_us":"Network","ar_jo":"Network"}',59,0 from sec_tenant st where not exists(select ctm from com_tenant_messages ctm where ctm.code = 'network');
INSERT INTO public.com_tenant_messages (tenant_id,created_by,creation_date,update_date,updated_by,code,description,type_id,"version") select st.rid,0,'2020-08-05 08:51:04.565',NULL,NULL,'moxaDeviceName','{"en_us":"MOXA Device Name","ar_jo":"MOXA Device Name"}',59,0 from sec_tenant st where not exists(select ctm from com_tenant_messages ctm where ctm.code = 'moxaDeviceName');
INSERT INTO public.com_tenant_messages (tenant_id,created_by,creation_date,update_date,updated_by,code,description,type_id,"version") select st.rid,0,'2020-04-29 07:12:43.172',NULL,NULL,'select','{"en_us":"Select","ar_jo":"إختار"}',59,0 from sec_tenant st where not exists(select ctm from com_tenant_messages ctm where ctm.code = 'select');
INSERT INTO public.com_tenant_messages (tenant_id,created_by,creation_date,update_date,updated_by,code,description,type_id,"version") select st.rid,0,'2020-07-19 11:35:26.611',NULL,NULL,'assayCode','{"en_us":"Assay Code","ar_jo":"رمز الفحص"}',59,0 from sec_tenant st where not exists(select ctm from com_tenant_messages ctm where ctm.code = 'assayCode');
INSERT INTO public.com_tenant_messages (tenant_id,created_by,creation_date,update_date,updated_by,code,description,type_id,"version") select st.rid,0,'2020-04-29 07:12:43.172',NULL,NULL,'driverAssays','{"en_us":"Driver Assays","ar_jo":"Driver Assays"}',59,0 from sec_tenant st where not exists(select ctm from com_tenant_messages ctm where ctm.code = 'driverAssays');
INSERT INTO public.com_tenant_messages (tenant_id,created_by,creation_date,update_date,updated_by,code,description,type_id,"version") select st.rid,0,'2020-04-29 07:12:43.172',NULL,NULL,'driverAssay','{"en_us":"Driver Assay","ar_jo":"Driver Assay"}',59,0 from sec_tenant st where not exists(select ctm from com_tenant_messages ctm where ctm.code = 'driverAssay');
INSERT INTO public.com_tenant_messages (tenant_id,created_by,creation_date,update_date,updated_by,code,description,type_id,"version") select st.rid,0,'2020-04-06 09:22:17.358',NULL,NULL,'download','{"en_us":"Download","ar_jo":"تحميل"}',59,0 from sec_tenant st where not exists(select ctm from com_tenant_messages ctm where ctm.code = 'download');
INSERT INTO public.com_tenant_messages (tenant_id,created_by,creation_date,update_date,updated_by,code,description,type_id,"version") select st.rid,0,'2020-04-06 09:21:38.060',NULL,NULL,'import','{"en_us":"Import","ar_jo":"استيراد"}',59,0 from sec_tenant st where not exists(select ctm from com_tenant_messages ctm where ctm.code = 'import');
INSERT INTO public.com_tenant_messages (tenant_id,created_by,creation_date,update_date,updated_by,code,description,type_id,"version") select st.rid,0,'2020-04-06 09:23:11.344',NULL,NULL,'or','{"en_us":"OR","ar_jo":"أو"}',59,0 from sec_tenant st where not exists(select ctm from com_tenant_messages ctm where ctm.code = 'or');
INSERT INTO public.com_tenant_messages (tenant_id,created_by,creation_date,update_date,updated_by,code,description,type_id,"version") select st.rid,0,'2020-08-13 12:22:04.810',NULL,NULL,'completed','{"en_us":"Completed","ar_jo":"Completed"}',59,0 from sec_tenant st where not exists(select ctm from com_tenant_messages ctm where ctm.code = 'completed');
INSERT INTO public.com_tenant_messages (tenant_id,created_by,creation_date,update_date,updated_by,code,description,type_id,"version") select st.rid,0,'2020-04-29 07:11:55.178',NULL,NULL,'systemLabels','{"en_us":"System Labels","ar_jo":"نصوص النظام"}',59,0 from sec_tenant st where not exists(select ctm from com_tenant_messages ctm where ctm.code = 'systemLabels');
INSERT INTO public.com_tenant_messages (tenant_id,created_by,creation_date,update_date,updated_by,code,description,type_id,"version") select st.rid,0,'2020-08-05 08:35:45.613',NULL,NULL,'receiveResult','{"en_us":"Receive Result","ar_jo":"Receive Result"}',59,0 from sec_tenant st where not exists(select ctm from com_tenant_messages ctm where ctm.code = 'receiveResult');
INSERT INTO public.com_tenant_messages (tenant_id,created_by,creation_date,update_date,updated_by,code,description,type_id,"version") select st.rid,0,'2020-08-05 08:33:47.685',NULL,NULL,'sendOrder','{"en_us":"Send Order","ar_jo":"Send Order"}',59,0 from sec_tenant st where not exists(select ctm from com_tenant_messages ctm where ctm.code = 'sendOrder');
INSERT INTO public.com_tenant_messages (tenant_id,created_by,creation_date,update_date,updated_by,code,description,type_id,"version") select st.rid,0,'2020-07-16 07:57:33.021',NULL,NULL,'machineNameRequired','{"en_us":"Machine Name Required","ar_jo":"Machine Name Required"}',59,0 from sec_tenant st where not exists(select ctm from com_tenant_messages ctm where ctm.code = 'machineNameRequired');
INSERT INTO public.com_tenant_messages (tenant_id,created_by,creation_date,update_date,updated_by,code,description,type_id,"version") select st.rid,0,'2018-08-12 13:44:24.514',NULL,NULL,'credentialsInvalid','{"en_us":"Invalid Username or Password","ar_jo":"خطأ في اسم المستخدم أو كلمة المرور"}',61,1 from sec_tenant st where not exists(select ctm from com_tenant_messages ctm where ctm.code = 'credentialsInvalid');
INSERT INTO public.com_tenant_messages (tenant_id,created_by,creation_date,update_date,updated_by,code,description,type_id,"version") select st.rid,0,'2018-08-12 13:54:48.457',NULL,NULL,'capsLock','{"en_us":"Caps lock is on","ar_jo":"الحروف الكبيرة مفعلة"}',62,0 from sec_tenant st where not exists(select ctm from com_tenant_messages ctm where ctm.code = 'capsLock');
INSERT INTO public.com_tenant_messages (tenant_id,created_by,creation_date,update_date,updated_by,code,description,type_id,"version") select st.rid,0,'2020-04-21 06:11:11.914',NULL,NULL,'requiresApplicationAdmin','{"en_us":"This Feature Requires an Application Admin","ar_jo":"تتطلب هذه الميزة مسؤول تطبيق"}',61,0 from sec_tenant st where not exists(select ctm from com_tenant_messages ctm where ctm.code = 'requiresApplicationAdmin');
INSERT INTO public.com_tenant_messages (tenant_id,created_by,creation_date,update_date,updated_by,code,description,type_id,"version") select st.rid,0,'2020-04-21 09:00:23.921',NULL,NULL,'invalidFormat','{"en_us":"Invalid Format","ar_jo":"تنسيق غير صالح"}',59,0 from sec_tenant st where not exists(select ctm from com_tenant_messages ctm where ctm.code = 'invalidFormat');
INSERT INTO public.com_tenant_messages (tenant_id,created_by,creation_date,update_date,updated_by,code,description,type_id,"version") select st.rid,0,'2020-05-13 09:28:22.039',NULL,NULL,'successTransaction','{"en_us":"Transaction done successfully","ar_jo":"تحت العملية بنجاح"}',59,0 from sec_tenant st where not exists(select ctm from com_tenant_messages ctm where ctm.code = 'successTransaction');
INSERT INTO public.com_tenant_messages (tenant_id,created_by,creation_date,update_date,updated_by,code,description,type_id,"version") select st.rid,0,'2020-05-12 11:00:07.119',NULL,NULL,'all','{"en_us":"All","ar_jo":"الكل"}',59,0 from sec_tenant st where not exists(select ctm from com_tenant_messages ctm where ctm.code = 'all');
INSERT INTO public.com_tenant_messages (tenant_id,created_by,creation_date,update_date,updated_by,code,description,type_id,"version") select st.rid,0,'2020-05-13 09:19:56.161',NULL,NULL,'disabled','{"en_us":"Disabled","ar_jo":"معطل"}',59,0 from sec_tenant st where not exists(select ctm from com_tenant_messages ctm where ctm.code = 'disabled');