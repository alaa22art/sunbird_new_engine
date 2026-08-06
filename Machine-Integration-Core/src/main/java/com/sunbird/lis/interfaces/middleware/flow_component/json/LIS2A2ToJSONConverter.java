package com.sunbird.lis.interfaces.middleware.flow_component.json;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.sunbird.core.base.helper.SearchCriterion;
import com.sunbird.core.base.helper.SearchCriterion.FilterOperator;
import com.sunbird.core.common.util.SpringUtil;
import com.sunbird.lis.interfaces.entities.CertacureAdmissionClass;
import com.sunbird.lis.interfaces.entities.LkpAdmissionClass;
import com.sunbird.lis.interfaces.entities.LkpMessageSourceType;
import com.sunbird.lis.interfaces.entities.LkpMessageTransactionDirection;
import com.sunbird.lis.interfaces.entities.LkpMessageTransactionType;
import com.sunbird.lis.interfaces.entities.Machine;
import com.sunbird.lis.interfaces.entities.MessageTransaction;
import com.sunbird.lis.interfaces.entities.OutboundErrorEmailEntity;
import com.sunbird.lis.interfaces.entities.PostDetailFinancialTransaction;
import com.sunbird.lis.interfaces.entities.ScheduleAppointment;
import com.sunbird.lis.interfaces.middleware.core.FlowComponent;
import com.sunbird.lis.interfaces.middleware.core.RecipientConf;
import com.sunbird.lis.interfaces.middleware.enums.Enums.API_URL_PREFEX;
import com.sunbird.lis.interfaces.middleware.enums.Enums.DISCHARGE_REASON;
import com.sunbird.lis.interfaces.middleware.enums.Enums.ERROR_TYPE;
import com.sunbird.lis.interfaces.middleware.enums.Enums.HL7_v24_DFT_P03_TYPE;
import com.sunbird.lis.interfaces.middleware.enums.Enums.HTTP_REQUESTED_METHOD_TYPE;
import com.sunbird.lis.interfaces.middleware.enums.Enums.PATIENT_CLASS;
import com.sunbird.lis.interfaces.middleware.enums.Enums.VALUDATION_RESULT_TYPE;
import com.sunbird.lis.interfaces.middleware.flow_component.lab_http.httpRequstTransaction;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2Msg;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2_ADT_Msg;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2_DFT_Msg;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2_SIU_Msg;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.OBRRecord;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_Appointment;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_CommonOrder;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_DiagnosisRecord;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_EventTypeRecord;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_HeaderRecord;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_PatientRecord;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_Visit1Record;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_Visit2Record;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_v24_FinancialTransaction;
import com.sunbird.lis.interfaces.middleware.parser.hl7.HL7Parser.MessageDirection;
import com.sunbird.lis.interfaces.middleware.parser.hl7.HL7Parser.MessageSourceType;
import com.sunbird.lis.interfaces.service.CertacureAdmissionClassService;
import com.sunbird.lis.interfaces.service.LkpService;
import com.sunbird.lis.interfaces.service.MachineService;
import com.sunbird.lis.interfaces.service.MappingCodesService;
import com.sunbird.lis.interfaces.service.MessageTransactionService;
import com.sunbird.lis.interfaces.service.OutboundErrorEmailService;
import com.sunbird.lis.interfaces.service.PostDetailFinancialTransactionService;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class LIS2A2ToJSONConverter extends FlowComponent<RecipientConf> {

    private LIS2A2_SIU_Msg lis2a2SIU;
    // private String SUFFIX_API_URL_PATH = "http://192.168.21.114:6607/";
    // private String SUFFIX_API_URL_PATH = "http://192.168.21.90:6607/";
    // private String SUFFIX_API_URL_PATH = "http://his.certacure.com:6607/";

    Config config= ConfigFactory.load();
    private String SUFFIX_API_URL_PATH = config.getString("system.certacure.api.suffix.url.path");

    private String API_AUTH_KEY = config.getString("system.certacure.api.authintication.key.value");
    // "793f8156-dfca-4693-940a-ac87feca305e";

    private MessageTransactionService messageTransactionService;
    private MachineService machineService;
    private Machine machine;
    private LkpService lkpService;
    private MessageTransaction messageTransaction;
    private LkpMessageTransactionDirection lkpMessageTransactionDirection;
    private CertacureAdmissionClass kpCertacureAdmissionClass;
    private LkpMessageTransactionType lkpMessageTransactionType;
    private LkpAdmissionClass lkpAdmissionClass;
    private String sampleBarcode= "No Sample Number";
    private PostDetailFinancialTransaction postDetailFinancialTransaction;
    private PostDetailFinancialTransactionService postDetailFinancialTransactionService;
    private LkpMessageTransactionType lkpDirectionType;
    private MappingCodesService mappingCodesService;
    private OutboundErrorEmailService outboundErrorEmailService;
    private CertacureAdmissionClassService certacureAdmissionClassService;
    private ScheduleAppointment scheduleAppointment;
    private static Gson gson;

    private String strRequest;
    /////////////////////////////
    private HL7_V24_HeaderRecord headerRecord;
    private HL7_V24_DiagnosisRecord diagnosisRecord;
    private HL7_V24_PatientRecord patientRecord;
    private HL7_V24_EventTypeRecord eventTypeRecord;
    private HL7_V24_Visit1Record visit1Record;
    private HL7_V24_Visit2Record visit2Record;
    private HL7_V24_HeaderRecord globalHeaderRecord;
    private HL7_V24_Appointment appointment;
    private HL7_V24_CommonOrder commonOrder;
    private HL7_v24_FinancialTransaction financialTransaction;
    private OBRRecord OBRRecord;
    private LIS2A2Msg lis2aMessage;
    private Class<? extends LIS2A2Msg> msgType;
    private String MSH1_FieldSeparator;
    private String MSH2_EncodingCharacters;
    private String MSH3_SendingApplication;
    private String MSH4_SendingFacility;
    private String MSH5_ReceivingApplication;
    private String MSH6_ReceivingFacility;
    private String MSH7_DateTimeOfMessage;
    private String MSH8_Security;
    private String MSH9_MessageType;
    private String MSH10_MessageControl;
    private String MSH11_ProcessingID;
    private String MSH12_VersionID;
    private String MSH13_SequenceNumber;
    private String MSH14_ContinuationPointer;
    private String MSH15_AcceptAcknowledgmentType;
    private String MSH16_ApplicationAcknowledgmentType;
    private String MSH17_CountryCode;
    private String MSH18_CharacterSet;
    private String MSH19_PrincipalLanguageOfMessage;
    private String MSH20_AlternateCharacterSetHandlingScheme;
    private String MSH21_ConformanceStatementID;
    //////////////////////////////////////////////////////////////////////////////////////
    private String EVN1_EventTypeCode;
    private String EVN2_RecordedDateTime;
    private String EVN3_DateTimePlannedEvent;
    private String EVN4_EventReasonCode;
    private String EVN5_OperatorID;
    private String EVN6_EventOccurred;
    private String EVN7_EventFacility;
    //////////////////////////////////////////////////////////////////////////////////////
    private String PID1_SetID_PID;
    private String PID2_PatientID;
    private String PID3_PatientIdentifierList;
    private String PID4_AlternatePatientID_PID;
    private String PID5_1_PatientEnFirstName;
    private String PID5_2_PatientEnSecondName;
    private String PID5_3_PatientEnThirdName;
    private String PID5_4_PatientEnFamilyName;
    private String PID5_5_PatientArFirstName;
    private String PID5_6_PatientArSecondName;
    private String PID5_7_PatientArThirdName;
    private String PID5_8_PatientArFamilyName;
    private String PID6_MotherMaidenName;
    private String PID7_DateTimeOfBirth;
    private String PID8_AdministrativeSex;
    private String PID9_PatientAlias;
    private String PID10_Race;
    private String PID11_PatientAddress;
    private String PID12_CountyCode;
    private String PID13_PhoneNumber_Home;
    private String PID14_PhoneNumber_Business;
    private String PID15_PrimaryLanguage;
    private String PID16_MaritalStatus;
    private String PID17_Religion;
    private String PID18_PatientAccountNumber;
    private String PID19_SSN_Number_Patient;
    private String PID20_DriverLicenseNumberPatient;
    private String PID21_MotherIdentifier;
    private String PID22_EthnicGroup;
    private String PID23_BirthPlace;
    private String PID24_MultipleBirth;
    private String PID25_BirthOrder;
    private String PID26_Citizenship;
    private String PID27_VeteransMilitaryStatus;
    private String PID28_Nationality;
    private String PID29_PatientDeathDateAndTime;
    private String PID30_PatientDeathIndicator;
    private String PID31_IdentityUnknownIndicator;
    private String PID32_IdentityReliabilityCode;
    private String PID33_LastUpdateDateTime;
    private String PID34_LastUpdateFacility;
    private String PID35_SpeciesCode;
    private String PID36_BreedCode;
    private String PID37_Strain;
    private String PID38_ProductionClassCode;
    private String PID50_Alternative_AppointmentID;
///////////////////////////////////////////////////////////////////////////////////////
    private String PV1_1_SetID_PV1;
    private String PV1_2_PatientClass;
    private String PV1_3_AssignedPatient;
    private String PV1_3_1_AssignedPatient_PointOfCare;
    private String PV1_3_2_AssignedPatient_Room;
    private String PV1_3_3_AssignedPatient_Bed;
    private String PV1_4_AdmissionType;
    private String PV1_5_PreadmitNumber;
    private String PV1_6_PriorPatientLocation;
    private String PV1_7_AttendingDoctor;
    private String PV1_7_1_AttendingDoctorCode;
    private String PV1_7_2_AttendingDoctorFamilyName;
    private String PV1_7_3_AttendingDoctorSecondAndFatherName;
    private String PV1_7_4_AttendingDoctorSuffix;
    private String PV1_8_ReferringDoctor;
    private String PV1_8_1_ReferringDoctorCode;
    private String PV1_9_ConsultingDoctor;
    private String PV1_9_1_ConsultingDoctorCode;
    private String PV1_10_HospitalService;
    private String PV1_11_TemporaryLocation;
    private String PV1_12_PreadmitTestIndicator;
    private String PV1_13_Re_admissionIndicator;
    private String PV1_14_AdmitSource;
    private String PV1_15_AmbulatoryStatus;
    private String PV1_16_VIPIndicator;
    private String PV1_17_AdmittingDoctor;
    private String PV1_17_1_AdmittingDoctorCode;
    private String PV1_17_AdmittingDoctor_2;
    private String PV1_17_AdmittingDoctor_3;
    private String PV1_18_PatientType;
    private String PV1_19_VisitNumber;
    private String PV1_20_FinancialClass;
    private String PV1_21_ChargePriceIndicator;
    private String PV1_22_CourtesyCode;
    private String PV1_23_CreditRating;
    private String PV1_24_ContractCode;
    private String PV1_25_ContractEffectiveDate;
    private String PV1_26_ContractAmount;
    private String PV1_27_ContractPeriod;
    private String PV1_28_InterestCode;
    private String PV1_29_TransferToBadDebtCode;
    private String PV1_30_TransferToBadDebtDate;
    private String PV1_31_BadDebtAgencyCode;
    private String PV1_32_BadDebtTransferAmount;
    private String PV1_33_BadDebtRecoveryAmount;
    private String PV1_34_DeleteAccountIndicator;
    private String PV1_35_DeleteAccountDate;
    private String PV1_36_DischargeDisposition;
    private String PV1_37_DischargedToLocation;
    private String PV1_38_DietType;
    private String PV1_39_ServicingFacility;
    private String PV1_40_BedStatus;
    private String PV1_41_AccountStatus;
    private String PV1_42_PendingLocation;
    private String PV1_43_PriorTemporaryLocation;
    private String PV1_44_AdmitDateTime;
    private String PV1_45_DischargeDateTime;
    private String PV1_46_CurrentPatientBalance;
    private String PV1_47_TotalCharges;
    private String PV1_48_TotalAdjustments;
    private String PV1_49_TotalPayments;
    private String PV1_50_AlternateVisit;
    private String PV1_51_VisitIndicator;
    private String PV1_52_OtherHealthcareProvider;
    ///////////////////////////////////////////////////////
    private String PV2_1_PriorPendingLocation;
    private String PV2_2_AccommodationCode;
    private String PV2_3_AdmitReason;
    private String PV2_4_TransferReason;
    private String PV2_5_PatientValuables;
    private String PV2_6_PatientValuablesLocation;
    private String PV2_7_VisitUserCode;
    private String PV2_8_ExpectedAdmitDateTime;
    private String PV2_9_ExpectedDischargeDateTime;
    private String PV2_10_EstimatedLengthofInpatientStay;
    private String PV2_11_ActualLengthofInpatientStay;
    private String PV2_12_VisitDescription;
    private String PV2_13_ReferralSourceCode;
    private String PV2_14_PreviousServiceDate;
    private String PV2_15_EmploymentIllnessRelatedIndicator;
    private String PV2_16_PurgeStatusCode;
    private String PV2_17_PurgeStatusDate;
    private String PV2_18_SpecialProgramCode;
    private String PV2_19_RetentionIndicator;
    private String PV2_20_ExpectedNumberofInsurancePlans;
    private String PV2_21_VisitPublicityCode;
    private String PV2_22_VisitProtectionIndicator;
    private String PV2_23_ClinicOrganizationName;
    private String PV2_24_PatientStatusCode;
    private String PV2_25_VisitPriorityCode;
    private String PV2_26_PreviousTreatmentDate;
    private String PV2_27_ExpectedDischargeDisposition;
    private String PV2_28_SignatureonFileDate;
    private String PV2_29_FirstSimilarIllnessDate;
    private String PV2_30_PatientChargeAdjustmentCode;
    private String PV2_31_RecurringServiceCode;
    private String PV2_32_BillingMediaCode;
    private String PV2_33_ExpectedSurgeryDateandTime;
    private String PV2_34_MilitaryPartnershipCode;
    private String PV2_35_MilitaryNon_AvailabilityCode;
    private String PV2_36_NewbornBabyIndicator;
    private String PV2_37_BabyDetainedIndicator;
    private String PV2_38_ModeofArrivalCode;
    private String PV2_39_RecreationalDrugUseCode;
    private String PV2_40_AdmissionLevelofCareCode;
    private String PV2_41_PrecautionCode;
    private String PV2_42_PatientConditionCode;
    private String PV2_43_LivingWillCode;
    private String PV2_44_OrganDonorCode;
    private String PV2_45_AdvanceDirectiveCode;
    private String PV2_46_PatientStatusEffectiveDate;
    private String PV2_47_ExpectedLOAReturnDateTime;
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private String DG1_1_SetID_DG1;
    private String DG1_2_DiagnosisCodingMethod;
    private String DG1_3_DiagnosisCode_DG1;
    private String DG1_4_DiagnosisDescription;
    private String DG1_5_DiagnosisDateTime;
    private String DG1_6_DiagnosisType;
    private String DG1_7_MajorDiagnosticCategory;
    private String DG1_8_DiagnosticRelatedGroup;
    private String DG1_9_DRGApprovalIndicator;
    private String DG1_10_DRGGrouperRZviewCode;
    private String DG1_11_OutlierType;
    private String DG1_12_OutlierDays;
    private String DG1_13_OutlierCost;
    private String DG1_14_GrouperVersionAndType;
    private String DG1_15_DiagnosisPriority;
    private String DG1_16_DiagnosingClinician;
    private String DG1_17_DiagnosisClassification;
    private String DG1_18_ConfidentialIndicator;
    private String DG1_19_AttestationDateTime;
///////////////////////////////////////////////////////////////////////////////////////////////////////
    private String SCH_1_PlacerAppointment;
    private String SCH_2_FillerAppointmentID;
    private String SCH_3_OccurrenceNumber;
    private String SCH_4_PlacerGroupNumber;
    private String SCH_5_ScheduleID;
    private String SCH_6_EventReason;
    private String SCH_7_AppointmentReason;
    private String SCH_8_AppointmentType;
    private String SCH_9_AppointmentDuration;
    private String SCH_10_AppointmentDurationUnits;
    private String SCH_11_AppointmentTimingQuantity;
    private String SCH_12_PlacerContactPerson;
    private String SCH_13_PlacerContactPhoneNumber;
    private String SCH_14_PlacerContactAddress;
    private String SCH_15_PlacerContactLocation;
    private String SCH_11_4_AppointmentTimingQuantityStartDate;
    private String SCH_11_5_AppointmentTimingQuantityEndDate;
    private String SCH_16_FillerContactPerson;
    private String SCH_17_FillerContactPhoneNumber;
    private String SCH_18_FillerContactAddress;
    private String SCH_19_FillerContactLocation;
    private String SCH_20_EnteredByPerson;
    private String SCH_21_EnteredByPhoneNumber;
    private String SCH_22_EnteredbyLocation;
    private String SCH_23_ParentPlacerAppointmentID;
    private String SCH_24_ParentFillerAppointmentID;
    private String SCH_25_FillerStatusCode;
    private String SCH_26_PlacerOrderNumber;
    private String SCH_27_FillerOrderNumber;
    /////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////
    private String SFT_1_SoftwareVendorOrganization;
    private String SFT_2_SoftwareCertifiedVersionorReleaseNumber;
    private String SFT_3_SoftwareProductName;
    private String SFT_4_SoftwareBinaryID;
    private String SFT_5_SoftwareProductInformation;
    ////////////////////////////////////////////////////////////////////////////////////////
    private String NTE_1_SetID_NTE;
    private String NTE_2_SourceOfComment;
    private String NTE_3_Comment;
    private String NTE_4_CommentType;
    ///////////////////////////////////////////////////////////////////////////////////////
    private String RGS_1_Set_ID;
    private String RGS_2_Segment_Action_Code;
    private String RGS_3_Resource_Group_ID;
    //////////////////////////////////////////////////////////////////////////////
    private String FT1_1_SetID;
    private String FT1_2_TransactionID;
    private String FT1_3_TransactionBatchID;
    private String FT1_4_TransactionDate;
    private String FT1_5_TransactionPostingDate;
    private String FT1_6_TransactionType;
    private String FT1_7_TransactionCode;
    private String FT1_8_TransactionDescription;
    private String FT1_9_TransactionDescriptionAlt;
    private String FT1_10_TransactionQuantity;
    private String FT1_11_TransactionAmountExtended;
    private String FT1_12_TransactionAmountUnit;
    private String FT1_13_DepartmentCode;
    private String FT1_14_InsurancePlanID;
    private String FT1_15_InsuranceAmount;
    private String FT1_16_AssignedPatientLocation;
    private String FT1_17_FeeSchedule;
    private String FT1_18_PatientType;
    private String FT1_19_DiagnosisCode;
    private String FT1_20_PerformedByCode;
    private String FT1_21_OrderedByCode;
    private String FT1_21_1_OrderedByCode;
    private String FT1_22_UnitCost;
    private String FT1_23_FillerOrderNumber;
    private String FT1_24_EnteredByCode;
    private String FT1_26_ProcedureCodeModifier;
    ////////////////////////////////////
    private String ORC1_OrderControl;
    private String ORC2_PlacerOrderNumber;
    private String ORC3_FillerOrderNumber;
    private String ORC4_PlacerGroupNumber;
    private String ORC5_OrderStatus;
    private String ORC6_ResponseFlag;
    private String ORC7_QuantityTiming;
    private String ORC8_ParentOrder;
    private String ORC9_DateTimeofTransaction;
    private String ORC10_EnteredBy;
    private String ORC11_VerifiedBy;
    private String ORC12_OrderingProvider;
    private String ORC13_EnterersLocation;
    private String ORC14_CallBackPhoneNumber;
    private String ORC15_OrderEffectiveDateTime;
    private String ORC16_OrderControlCodeReason;
    private String ORC17_EnteringOrganization;
    private String ORC18_EnteringDevice;
    private String ORC19_ActionBy;
    private String ORC20_AdvancedBeneficiaryNoticeCode;
    private String ORC21_OrderingFacilityName;
    private String ORC22_OrderingFacilityAddress;
    private String ORC23_OrderingFacilityPhoneNumber;
    private String ORC24_OrderingProviderAddress;
    private String ORC25_OrderStatusModifier;
    private String ORC27_4_ExpectedStartDate;
    private String ORC27_5_ExpectedEndDate;

    ///////////////////
    private String OBR24_DiagnosticServSectID;

    @Override

    protected PartialFunction<Object, BoxedUnit> getBehaviour() {

        return ReceiveBuilder

// .match(LIS2A2Msg.class, this::convertAndForward)
            .match(httpRequstTransaction.class, this::convertAndForward).build();
    }

    private void convertAndForward(httpRequstTransaction httpRequestObj) {

        gson= new Gson();
        strRequest= "";
        headerRecord= null;
        diagnosisRecord= null;
        patientRecord= null;
        eventTypeRecord= null;
        visit1Record= null;
        visit2Record= null;
        appointment= null;
        commonOrder= null;
        financialTransaction = null;
        OBRRecord= null;
        httpRequestObj.setAPI_AUTH_KEY(API_AUTH_KEY);
        lis2aMessage= httpRequestObj.getLis2aHL7Msg();
        machineService= (MachineService) SpringUtil.getBean("MachineService");
        messageTransactionService= (MessageTransactionService) SpringUtil
            .getBean("MessageTransactionService");
        machine= machineService.getMachineByActorPath(getContext().parent().toString());

        postDetailFinancialTransactionService= (PostDetailFinancialTransactionService) SpringUtil
            .getBean("PostDetailFinancialTransactionService");

        mappingCodesService= (MappingCodesService) SpringUtil.getBean("MappingCodesService");

        postDetailFinancialTransaction = new PostDetailFinancialTransaction();
        lkpService = (LkpService) SpringUtil.getBean("LkpService");

        try {
            msgType= getMsgType(lis2aMessage);
            if (msgType == null) {

                /*
                httpRequestObj.setSendingFacility("UNKNOWN");
                httpRequestObj.setReceivingFacility("UNKNOWN");
                httpRequestObj.setMessageID("undefined");
                httpRequestObj.setErrorDesc("Message Type not defined ,Uknown Message Type or Segment(s)");
                setHTTPRequestParam(httpRequestObj, strRequest, VALUDATION_RESULT_TYPE.UNKNOWNTYPE,
                		"Message Type not defined", "Uknown Message Type or Segment(s)");
                httpRequestObj.getMessageTransaction()
                		.setMessageType(getMessageTypeObj(MessageSourceType.HL7.toString()));
                httpRequestObj.getMessageTransaction().setIsSent(false);
                httpRequestObj.getMessageTransaction().setIsSuccuss(false);
                httpRequestObj.getMessageTransaction().setIsValidated(false);
                */

                httpRequestObj.setSendingFacility("UNKNOWN");
                httpRequestObj.setReceivingFacility("UNKNOWN");
                httpRequestObj
                    .setMessageID(httpRequestObj.getMessageTransaction().getMessageControlID());
                httpRequestObj
                    .setErrorDesc("Message Type not defined ,Uknown Message Type or Segment(s)");
                setHTTPRequestParam(httpRequestObj, strRequest, VALUDATION_RESULT_TYPE.UNKNOWNTYPE,
                    "Message Type not defined", "Uknown Message Type or Segment(s)");
                httpRequestObj.getMessageTransaction()
                    .setMessageType(getMessageTypeObj(MessageSourceType.HL7.toString()));
                httpRequestObj.getMessageTransaction().setIsSent(false);
                httpRequestObj.getMessageTransaction().setIsSuccuss(false);
                httpRequestObj.getMessageTransaction().setIsValidated(false);
                httpRequestObj.getMessageTransaction()
                    .setNotes("the MSG type not defined, there is unknown segment");

            }
            if (msgType == LIS2A2_ADT_Msg.class) {
                initializeADTSegments();
                setMSH(headerRecord);
                setPID(patientRecord);
                setEVN(eventTypeRecord);
                setVisit(visit1Record);
                setDG1(diagnosisRecord);

                httpRequestObj.setMSG_Type("ADT");

                httpRequestObj.getMessageTransaction().setMessageControlID(MSH10_MessageControl);
                httpRequestObj.setMessageID(headerRecord.getMessageControlId());
                httpRequestObj.setAPI_URL_SUFFIX(SUFFIX_API_URL_PATH);
                
                httpRequestObj.getMessageTransaction().setPatientID(PID3_PatientIdentifierList);
                httpRequestObj.getMessageTransaction().setNationalID(PID2_PatientID);

                switch (headerRecord.getEventTrigger()) {

                case A01: /// validation
                    adt_a01_valudateAdmissionFields(httpRequestObj);

                    strRequest = adt_a01_CreateAdmissionRequest();
                    setHttpRequstPostParam(httpRequestObj, API_URL_PREFEX.PATIENT_ADDMISIONS);

                    httpRequestObj.getMessageTransaction()
                        .setMessageType(getMessageTypeObj(MessageSourceType.ADT_A01.toString()));
                    httpRequestObj.setHttpMethodType(HTTP_REQUESTED_METHOD_TYPE.POST);
                    
                    httpRequestObj.getMessageTransaction()
                    .setAdtOperationType(MessageSourceType.ADT_A01.getValue());
                    
                    httpRequestObj.setAdmissionNumber(PV1_19_VisitNumber);

                    break;
                case A02: /// Transfer
                    adt_a02_ValidateAdmissionTransferFields(httpRequestObj);

                    strRequest= adt_a02_CreateAdmissionTransferRequst();

                    httpRequestObj.setAPI_URL_PREFIX(
                        String.format(API_URL_PREFEX.PATIENT_TRANSFER_ADDMISIONS.getValue(),
                            PV1_19_VisitNumber));
                    
                    httpRequestObj.setAdmissionNumber(PV1_19_VisitNumber);
                    httpRequestObj.setHttpMethodType(HTTP_REQUESTED_METHOD_TYPE.POST);
                    httpRequestObj.setSendingFacility(MSH4_SendingFacility);
                    httpRequestObj.setReceivingFacility(MSH6_ReceivingFacility);

                    httpRequestObj.getMessageTransaction()
                        .setMessageType(getMessageTypeObj(MessageSourceType.ADT_A02.toString()));
                    httpRequestObj.setHttpMethodType(HTTP_REQUESTED_METHOD_TYPE.POST);
                    
                    httpRequestObj.getMessageTransaction()
                    .setAdtOperationType(MessageSourceType.ADT_A02.getValue());
                    
                    break;

                case A03: // Discharge

                    String strPatientClass;
                    strPatientClass= PV1_2_PatientClass;

                    // Check Patient Class
                    // Call Discharge in case I
                    // Call End Visit(OPD) in Case O

                    if (PATIENT_CLASS.IN_PATIENT.getValue().equals(strPatientClass)) {

                        adt_a03_valudateDischargeAdmissionFields(httpRequestObj);
                        strRequest= adt_a03CreateDischargeAdmissionRequst();
                        httpRequestObj.setAPI_URL_PREFIX(
                            String.format(API_URL_PREFEX.DISCHARGE_ADDMISIONS.getValue(),
                                PV1_19_VisitNumber));
                        
                        httpRequestObj.setAdmissionNumber(PV1_19_VisitNumber);
                        
                        httpRequestObj.setHttpMethodType(HTTP_REQUESTED_METHOD_TYPE.POST);
                        httpRequestObj.setSendingFacility(MSH4_SendingFacility);
                        httpRequestObj.setReceivingFacility(MSH6_ReceivingFacility);

                        httpRequestObj.getMessageTransaction()
                            .setMessageType(
                                getMessageTypeObj(MessageSourceType.ADT_A03.toString()));
                        
                        httpRequestObj.getMessageTransaction()
                        .setAdtOperationType(MessageSourceType.ADT_A03.getValue());


                    } else if (PATIENT_CLASS.OUT_PATIENT.getValue().equals(strPatientClass)) {
                        adt_a03_CloseVisitValudate(httpRequestObj);
                        strRequest= adt_a03_CloseVisitRequst();
                        httpRequestObj.setAPI_URL_PREFIX(
                            String.format(API_URL_PREFEX.PATIENT_VISIT_CLOSE.getValue(),
                                PV1_19_VisitNumber));
                        httpRequestObj.setHttpMethodType(HTTP_REQUESTED_METHOD_TYPE.POST);
                        httpRequestObj.setSendingFacility(MSH4_SendingFacility);
                        httpRequestObj.setReceivingFacility(MSH6_ReceivingFacility);

                        httpRequestObj.getMessageTransaction()
                            .setMessageType(
                                getMessageTypeObj(MessageSourceType.ADT_A03.toString()));
                        httpRequestObj.setHttpMethodType(HTTP_REQUESTED_METHOD_TYPE.POST);
                        
                        httpRequestObj.getMessageTransaction()
                        .setAdtOperationType(MessageSourceType.ADT_A03.getValue());


                    } else {
                        throw new Exception(
                            "ERROR_:_UNDEFINED_PATIENT_CLASS_[PV1_2_PatientClass]:" +
                                strPatientClass);
                    }

                    break;

                case A04:

                    if (PV1_50_AlternateVisit.equals("")) {
                        strRequest= adt_a04CreateVisitRequst();
                        setHttpRequstPostParam(httpRequestObj, API_URL_PREFEX.PATIENT_VISIT_CREATE);
                        httpRequestObj.getMessageTransaction()
                            .setMessageType(
                                getMessageTypeObj(MessageSourceType.ADT_A04.toString()));
                        
                        httpRequestObj.getMessageTransaction()
                        .setAdtOperationType(MessageSourceType.ADT_A04.getValue());

                        
                        httpRequestObj.setHttpMethodType(HTTP_REQUESTED_METHOD_TYPE.POST);

                    } else {
                        strRequest= adt_a04CreateVirtualVisitRequst();

                        setHttpRequstPostParam(httpRequestObj,
                            API_URL_PREFEX.PATIENT_VISIT_CREATE_BY_APPOINTMENT,
                            strRequest);

                        httpRequestObj.setAPI_URL_PREFIX(String.format(
                            API_URL_PREFEX.PATIENT_VISIT_CREATE_BY_APPOINTMENT.getValue(),
                            PV1_50_AlternateVisit));
                        
                        httpRequestObj.getMessageTransaction()
                        .setAdtOperationType(MessageSourceType.ADT_A04.getValue());
                        
                        httpRequestObj.getMessageTransaction()
                            .setMessageType(
                                getMessageTypeObj(MessageSourceType.ADT_A04.toString()));
                        httpRequestObj.setHttpMethodType(HTTP_REQUESTED_METHOD_TYPE.PATCH);

                    }

                    break;

                case A11: /// Cancel Admission
                    adt_a_11_CancelAdmissionFieldsValudate(httpRequestObj);
                    strRequest= adt_a_11_CancelAdmissionRequest();
                    /*
                     * setHttpRequstPatchParam(httpRequestObj,
                     * API_URL_PREFEX.PATIENT_CANCEL_ADDMISIONS, "ACK^ADT^A04",
                     * PID3_PatientIdentifierList);
                     */
                    httpRequestObj.setAPI_URL_PREFIX(
                        String.format(API_URL_PREFEX.PATIENT_CANCEL_ADDMISIONS.getValue(),
                            PV1_19_VisitNumber));
                    httpRequestObj.setAdmissionNumber(PV1_19_VisitNumber);
                    httpRequestObj.setHttpMethodType(HTTP_REQUESTED_METHOD_TYPE.POST);
                    httpRequestObj.setSendingFacility(MSH4_SendingFacility);
                    httpRequestObj.setReceivingFacility(MSH6_ReceivingFacility);

                    httpRequestObj.getMessageTransaction()
                        .setMessageType(getMessageTypeObj(MessageSourceType.ADT_A11.toString()));
                    
                    httpRequestObj.getMessageTransaction()
                    .setAdtOperationType(MessageSourceType.ADT_A11.getValue());


                    break;

                case A08: /// validation
                    adt_a08_UpdateVisitFieldsValidate(httpRequestObj);
                    strRequest= adt_a08_UpdateVisitRequest();
                    /*
                     * setHttpRequstPatchParam(httpRequestObj, API_URL_PREFEX.UPDATE_ADDMISIONS,
                     * "ACK^A08^ACK", PID3_PatientIdentifierList);
                     */
                    httpRequestObj.setAPI_URL_PREFIX(
                        API_URL_PREFEX.UPDATE_ADDMISIONS.getValue() + PV1_19_VisitNumber);
                    httpRequestObj.setHttpMethodType(HTTP_REQUESTED_METHOD_TYPE.PATCH);
                    httpRequestObj.setSendingFacility(MSH4_SendingFacility);
                    httpRequestObj.setReceivingFacility(MSH6_ReceivingFacility);

                    httpRequestObj.getMessageTransaction()
                        .setMessageType(getMessageTypeObj(MessageSourceType.ADT_A08.toString()));

                    break;

                case A28: // Patient Create
                    adt_a28_CreatePatientFieldsValidate(httpRequestObj);
                    strRequest= adt_a28_CreatePatientRequest();
                    setHttpRequstPostParam(httpRequestObj, API_URL_PREFEX.PATIENT_CREATE);
                    httpRequestObj.getMessageTransaction()
                        .setMessageType(getMessageTypeObj(MessageSourceType.ADT_A28.toString()));
                    httpRequestObj.setHttpMethodType(HTTP_REQUESTED_METHOD_TYPE.POST);
                    
                    httpRequestObj.getMessageTransaction()
                    .setAdtOperationType(MessageSourceType.ADT_A28.getValue());


                    
                    
                    break;

                case A05: // Patient Preadmission

                    adt_a05_PatientPreAdmissionFieldsValidate(httpRequestObj);
                    strRequest= adt_a05_PreAdmissionRequest();
                    setHttpRequstPostParam(httpRequestObj, API_URL_PREFEX.PATIENT_PREADMISSION);
                    httpRequestObj.getMessageTransaction()
                        .setMessageType(getMessageTypeObj(MessageSourceType.ADT_A05.toString()));
                    
                    httpRequestObj.getMessageTransaction()
                    .setAdtOperationType(MessageSourceType.ADT_A05.getValue());


                    break;

                case A13:

                    httpRequestObj.setSendingFacility("UNKNOWN");
                    httpRequestObj.setReceivingFacility("UNKNOWN");
                    httpRequestObj.setMessageID("undefined");
                    httpRequestObj.setErrorDesc(
                        "Message Type not defined ,Uknown Message Type or Segment(s)");
                    setHTTPRequestParam(httpRequestObj, strRequest, VALUDATION_RESULT_TYPE.FAILED,
                        "Message Type not defined", "Uknown Message Type or Segment(s)");
                    httpRequestObj.getMessageTransaction()
                        .setMessageType(getMessageTypeObj(MessageSourceType.HL7.toString()));
                    httpRequestObj.getMessageTransaction().setIsSent(false);
                    httpRequestObj.getMessageTransaction().setIsSuccuss(false);
                    httpRequestObj.getMessageTransaction().setIsValidated(false);

                    strRequest= "";
                    
                    httpRequestObj.getMessageTransaction()
                    .setMessageType(getMessageTypeObj(MessageSourceType.ADT_A13.toString()));

                    
                    break;

                case A31:

                    adt_a31_UpdatePatientFieldsValidate(httpRequestObj);
                    strRequest= adt_a31_UpdatePatientRequest();
                    /*
                     * setHttpRequstPatchParam(httpRequestObj, API_URL_PREFEX.PATIENT_UPDATE,
                     * "ACK^A31^ACK", PID3_PatientIdentifierList);
                     */
                    
                    httpRequestObj.setAPI_URL_PREFIX(
                            String.format(
                                API_URL_PREFEX.PATIENT_UPDATE
                                    .getValue(),
                                    PID3_PatientIdentifierList));
                    httpRequestObj.setHttpMethodType(HTTP_REQUESTED_METHOD_TYPE.PATCH);
                    httpRequestObj.setSendingFacility(MSH4_SendingFacility);
                    httpRequestObj.setReceivingFacility(MSH6_ReceivingFacility);

                    httpRequestObj.getMessageTransaction()
                        .setMessageType(getMessageTypeObj(MessageSourceType.ADT_A31.toString()));
                    
                    httpRequestObj.getMessageTransaction()
                    .setAdtOperationType(MessageSourceType.ADT_A31.getValue());
                    
                    httpRequestObj.getMessageTransaction().setMessageBody(strRequest);


                    break;

                default:

                    System.out.println("undefined message ADT Trigger type");
                    throw new Exception("ERROR:_ADT MESSAGE TYPE NOT DEFINED");
                }

                setHTTPRequestParam(httpRequestObj, strRequest, VALUDATION_RESULT_TYPE.SUCCUSS,
                    "New Request Created New JSON",
                    "New Arrive");
                
                

            } else if (msgType == LIS2A2_SIU_Msg.class) {
                initialzedHL7Segmants();
                setMSH(headerRecord);
                setPID(patientRecord);
                setVisit(visit1Record);
                setEVN(eventTypeRecord);
                setAppointment(appointment);
                httpRequestObj.setMessageID(headerRecord.getMessageControlId());
                httpRequestObj.getMessageTransaction().setMessageControlID(MSH10_MessageControl);
                httpRequestObj.setAPI_URL_SUFFIX(SUFFIX_API_URL_PATH);

                httpRequestObj.setMSG_Type("SIU");
                scheduleAppointment= new ScheduleAppointment();
                switch (headerRecord.getEventTrigger()) {
                case S12:
                	
                	//Check if appointment in post order table 
                	 //postDetailFinancialTransactionService = (PostDetailFinancialTransactionService) SpringUtil
                     //.getBean("PostDetailFinancialTransactionService");
                	 
                	 //List<PostDetailFinancialTransaction> lstPostOrder = postDetailFinancialTransactionService.
                			// getOrdersByAppointmentId(SCH_2_FillerAppointmentID);
                	 
                	//if(lstPostOrder.size() == 0)
                   // {
                		strRequest = siu_s12_CreateAppointmentRequest();
                    
                    
						httpRequestObj.getMessageTransaction().setPatientID(PID3_PatientIdentifierList);
						httpRequestObj.getMessageTransaction().setNationalID(PID2_PatientID);

						httpRequestObj.setMSG_Type("SIU_S12");
						httpRequestObj.getMessageTransaction()
								.setAdtOperationType(MessageSourceType.SIU_S12.getValue());

						setHttpRequstPostParam(httpRequestObj, API_URL_PREFEX.APPOINTMENTS_CREATE);
						httpRequestObj.getMessageTransaction()
								.setMessageType(getMessageTypeObj(MessageSourceType.SIU_S12.toString()));
						httpRequestObj.setHttpMethodType(HTTP_REQUESTED_METHOD_TYPE.POST);

						httpRequestObj.getMessageTransaction()
								.setAdtOperationType(MessageSourceType.SIU_S12.getValue());

						scheduleAppointment.setAppointmentId(SCH_2_FillerAppointmentID);
						scheduleAppointment.setDoctor(PV1_7_1_AttendingDoctorCode);
						scheduleAppointment.setFacility(MSH4_SendingFacility);
						scheduleAppointment.setRecordedDate(MSH7_DateTimeOfMessage);
						scheduleAppointment.setAppointmentDate(PV1_44_AdmitDateTime);
						scheduleAppointment.setRecordedBy(EVN5_OperatorID);
						scheduleAppointment.setPatientClass(PV1_2_PatientClass);
						scheduleAppointment.setAppointmentType(SCH_11_AppointmentTimingQuantity);
						
						
						httpRequestObj.setScheduleAppointment(scheduleAppointment);
					
                   // }
              /*  else if(lstPostOrder.size() > 0)
                    {
                    	
                    	
                    	strRequest = modifyPostedAppointment();
                        
                        
						httpRequestObj.getMessageTransaction().setPatientID(PID3_PatientIdentifierList);
						httpRequestObj.getMessageTransaction().setNationalID(PID2_PatientID);

						httpRequestObj.setMSG_Type("SIU_INTERNAL_UPDATE");
						httpRequestObj.getMessageTransaction()
								.setAdtOperationType(MessageSourceType.SIU_INTERNAL_UPDATE.getValue());

						
						
						setHttpRequstPostParam(httpRequestObj, API_URL_PREFEX.APPOINTMENTS_INTERNAL_UPDATE);
						
						  
						httpRequestObj.setAPI_URL_PREFIX(
		                            String.format(
		                                API_URL_PREFEX.APPOINTMENTS_INTERNAL_UPDATE
		                                    .getValue(),
		                                    SCH_2_FillerAppointmentID));
		                httpRequestObj.setHttpMethodType(HTTP_REQUESTED_METHOD_TYPE.PATCH);
						
						httpRequestObj.getMessageTransaction()
								.setMessageType(getMessageTypeObj(MessageSourceType.SIU_INTERNAL_UPDATE.toString()));
				

						httpRequestObj.getMessageTransaction()
								.setAdtOperationType(MessageSourceType.SIU_INTERNAL_UPDATE.getValue());

						scheduleAppointment.setAppointmentId(SCH_2_FillerAppointmentID);
						scheduleAppointment.setDoctor(PV1_7_1_AttendingDoctorCode);
						scheduleAppointment.setFacility(MSH4_SendingFacility);
						scheduleAppointment.setRecordedDate(MSH7_DateTimeOfMessage);
						scheduleAppointment.setAppointmentDate(PV1_44_AdmitDateTime);
						scheduleAppointment.setRecordedBy(EVN5_OperatorID);
						scheduleAppointment.setPatientClass(PV1_2_PatientClass);
						scheduleAppointment.setAppointmentType(SCH_11_AppointmentTimingQuantity);
						httpRequestObj.setPostDetailFinancialTransaction(lstPostOrder.get(0));
						httpRequestObj.setScheduleAppointment(scheduleAppointment);
                    	
                    }*/
                    break;
                case S13:
                    strRequest= siu_s13_UpdateAppointmentRequest();
                    setHttpRequstPatchParam(httpRequestObj, API_URL_PREFEX.APPOINTMENTS_UPDATE,
                        PID3_PatientIdentifierList);
                    httpRequestObj.setHttpMethodType(HTTP_REQUESTED_METHOD_TYPE.PATCH);
                    
                    httpRequestObj.getMessageTransaction()
                    .setMessageType(getMessageTypeObj(MessageSourceType.SIU_S13.toString()));
                    httpRequestObj.getMessageTransaction()
                    .setAdtOperationType(MessageSourceType.SIU_S13.getValue());


                    break;
                case S15:
                    strRequest= siu_s15_CancelAppointmentRequest();

                    httpRequestObj.setAPI_URL_PREFIX(
                        String.format(API_URL_PREFEX.APPOINTMENTS_CANCEL.getValue(),
                            SCH_2_FillerAppointmentID));
                    httpRequestObj.setHttpMethodType(HTTP_REQUESTED_METHOD_TYPE.POST);
                    httpRequestObj.setSendingFacility(MSH4_SendingFacility);
                    httpRequestObj.setReceivingFacility(MSH6_ReceivingFacility);
                    httpRequestObj.getMessageTransaction()
                        .setMessageType(getMessageTypeObj(MessageSourceType.SIU_S15.toString()));
                    httpRequestObj.getMessageTransaction()
                    .setAdtOperationType(MessageSourceType.SIU_S15.getValue());


                    break;
                default:
                    System.out.println("undefined message type");
                    throw new Exception("ERROR:_MESSAGE TYPE NOT DEFINED");

                }
                setHTTPRequestParam(httpRequestObj, strRequest, VALUDATION_RESULT_TYPE.SUCCUSS,
                    "Request Created new JSON body", "");

                try {
                    isJSONValid(strRequest);

                    httpRequestObj.getMessageTransaction().setIsValidated(true);
                } catch (Exception e) {
                    httpRequestObj.getMessageTransaction().setIsValidated(false);
                    throw new Exception(e.getMessage());
                }

            } else if (msgType == LIS2A2_DFT_Msg.class) {
                initializeDFTSegments();
                setMSH(headerRecord);
                setPID(patientRecord);
                setEVN(eventTypeRecord);
                setVisit(visit1Record);
                setFinancialTransaction(financialTransaction);
                setOBRRecord(OBRRecord);
                setCommonOrder(commonOrder);
                setDG1(diagnosisRecord);

                httpRequestObj.setMSG_Type("DFT_P03");
                
                httpRequestObj.getMessageTransaction().setAdtOperationType("DFT");


                httpRequestObj.setMessageID(headerRecord.getMessageControlId());
                httpRequestObj.getMessageTransaction().setMessageControlID(MSH10_MessageControl);
                httpRequestObj.setAPI_URL_SUFFIX(SUFFIX_API_URL_PATH);
                
                httpRequestObj.getMessageTransaction().setPatientID(PID3_PatientIdentifierList);
                httpRequestObj.getMessageTransaction().setNationalID(PID2_PatientID);

                // Date expectedStartDate= new SimpleDateFormat("yyyyMMddHHmmss")
                // .parse(ORC27_4_ExpectedStartDate);
                // Date expectedEndtDate= new SimpleDateFormat("yyyyMMddHHmmss")
                // .parse(ORC27_5_ExpectedEndDate);
                switch (headerRecord.getEventTrigger()) {

				case P03:

					postDetailFinancialTransaction.setVistaOrderID(FT1_2_TransactionID);

					postDetailFinancialTransaction.setType(ORC1_OrderControl);

					postDetailFinancialTransaction.setGroupReference(ORC2_PlacerOrderNumber);

					postDetailFinancialTransaction.setAppointmentID(PV1_5_PreadmitNumber);

					postDetailFinancialTransaction.setAttendingDoctorID(PV1_7_1_AttendingDoctorCode);
					postDetailFinancialTransaction.setAssignedPatientLocation(PV1_3_AssignedPatient);

					postDetailFinancialTransaction.setTransactionDate(FT1_4_TransactionDate);
					postDetailFinancialTransaction.setTransactionPostingDate(FT1_5_TransactionPostingDate);
					postDetailFinancialTransaction.setTransactionType(FT1_6_TransactionType);

					postDetailFinancialTransaction.setCode(ORC16_OrderControlCodeReason);

					postDetailFinancialTransaction.setOrderByDoctorId(FT1_21_1_OrderedByCode);

					httpRequestObj.setHttpMethodType(HTTP_REQUESTED_METHOD_TYPE.POST);

					if (!FT1_2_TransactionID.isEmpty()) {

						postDetailFinancialTransaction.setOrder_by_id(FT1_2_TransactionID);
					}
					postDetailFinancialTransaction.setServSection(OBR24_DiagnosticServSectID);
					/// postDetailFinancialTransaction
					// .setExpectedStartDate(expectedStartDate);
					// postDetailFinancialTransaction
					// .setExpectedEndDate(expectedEndtDate);
					postDetailFinancialTransaction.setTenantId(machine.getTenantId());
					postDetailFinancialTransaction.setBranchId(machine.getBranchId());
					postDetailFinancialTransaction.setRCMItemCode(FT1_7_TransactionCode.replaceAll("[^\\d.]", ""));
					// postDetailFinancialTransactionService.add(postDetailFinancialTransaction);

					List<PostDetailFinancialTransaction> lisDFTOrders = postDetailFinancialTransactionService
							.getOrdersByVistaOrderId(FT1_2_TransactionID);

					switch (OBR24_DiagnosticServSectID) {
					case "PSO":
					case "PSJ":

						if (FT1_6_TransactionType.equals("PF")) {

							httpRequestObj.getMessageTransaction().setAdtOperationType("DFT_PF");

							throw new Exception("THE ORDER COMPLET MSG (PF) IS Not IMPLEMENTED IN RCM (IGNOR)" + ","
									+ " VISTA ORDER ID: [" + FT1_2_TransactionID + "]" + "," + "VISTA ITEM COE: ["
									+ FT1_7_TransactionCode + "]");
						}

						else if (FT1_6_TransactionType.equals("CO")) {

							if (lisDFTOrders.size() > 0) {
								strRequest = getOrderCompleteJSON();
								// completeOrder(httpRequestObj, lisDFTOrders);

								httpRequestObj.setJSON(strRequest);

								httpRequestObj.setAPI_URL_PREFIX(
										String.format(API_URL_PREFEX.DETAIL_FINANCIAL_TRANSACTION_COMPLETE.getValue(),
												lisDFTOrders.get(0).getRCMOrderActionID()));

								httpRequestObj.setHttpMethodType(HTTP_REQUESTED_METHOD_TYPE.POST);
								httpRequestObj.setSendingFacility(MSH4_SendingFacility);
								httpRequestObj.setReceivingFacility(MSH6_ReceivingFacility);

							} else {
								throw new Exception("OPERATION FAILED , CAN NOT COMPLETE ORDER!!,"
										+ "ORDER MEDICATION IS NOT EXISTED [ VISTA ORDER ID:" + FT1_7_TransactionCode
										+ "], VISTA ORDER ITEM [" + FT1_2_TransactionID + "]");
							}

						} else if (FT1_6_TransactionType.equals("DC") || FT1_6_TransactionType.equals("CR")) {
							if (lisDFTOrders.size() > 0) {
								/// api/PharmacyOrders/{identifier}/returnDispensed
								strRequest = returnDispensedItem();

								httpRequestObj.setJSON(strRequest);

								httpRequestObj.setAPI_URL_PREFIX(String.format(
										API_URL_PREFEX.DETAIL_FINANCIAL_TRANSACTION_PHARMACY_RETURN_DISPENCE.getValue(),
										lisDFTOrders.get(0).getRCMOrderActionID()));
								httpRequestObj.setSendingFacility(MSH4_SendingFacility);
								httpRequestObj.setReceivingFacility(MSH6_ReceivingFacility);
							} else {
								throw new Exception("ERROR:ITEM MEDICATION NOT DISPENCE FOR THIS ORDER ["
										+ FT1_2_TransactionID + "] , OPERATION FAILED!!");
							}

						} else // if (FT1_6_TransactionType.equals("CH"))
						{
							strRequest = createPharmacyOrder();

							httpRequestObj.setJSON(strRequest);

							httpRequestObj.setAPI_URL_PREFIX(
									API_URL_PREFEX.DETAIL_FINANCIAL_TRANSACTION_PHARMACY_CREATE.getValue());
							httpRequestObj.setHttpMethodType(HTTP_REQUESTED_METHOD_TYPE.POST);
							httpRequestObj.setSendingFacility(MSH4_SendingFacility);
							httpRequestObj.setReceivingFacility(MSH6_ReceivingFacility);

							// setHttpRequstPostParam(httpRequestObj,
							// API_URL_PREFEX.PHARMACY_ORDER_TO_PENDING_AREA , strRequest);
						}
						break;

					case "LA":
					case "RA":

						if (FT1_6_TransactionType.equals("PF")) {

							httpRequestObj.getMessageTransaction().setAdtOperationType("DFT_PF");

							throw new Exception("THE ORDER COMPLET MSG (PF) IS Not IMPLEMENTED IN RCM (IGNOR)" + ","
									+ " VISTA ORDER ID: [" + FT1_2_TransactionID + "]" + "," + "VISTA ITEM COE: ["
									+ FT1_7_TransactionCode + "]");

						}

						else if (FT1_6_TransactionType.equals("CO")) {
							if (lisDFTOrders.size() > 0) {

								strRequest = getOrderCompleteJSON();
								// completeOrder(httpRequestObj, lisDFTOrders);

								httpRequestObj.setJSON(strRequest);

								httpRequestObj.setAPI_URL_PREFIX(
										String.format(API_URL_PREFEX.DETAIL_FINANCIAL_TRANSACTION_COMPLETE.getValue(),
												lisDFTOrders.get(0).getRCMOrderActionID()));

								httpRequestObj.setHttpMethodType(HTTP_REQUESTED_METHOD_TYPE.POST);
								httpRequestObj.setSendingFacility(MSH4_SendingFacility);
								httpRequestObj.setReceivingFacility(MSH6_ReceivingFacility);

								httpRequestObj.getMessageTransaction().setMessageType(
										getMessageTypeObj(MessageSourceType.DFT_P03_COMPLETE.toString()));
								httpRequestObj.getMessageTransaction()
										.setAdtOperationType(MessageSourceType.DFT_P03_COMPLETE.getValue());

							} else {
								throw new Exception("OPERATION FAILED!!, ORDER NOT FOUND  [" + FT1_2_TransactionID
										+ "] , ORDER CAN NOT BE COMPLETED");

							}

						} else if (FT1_6_TransactionType.equals("CR") || FT1_6_TransactionType.equals("DC")) {

							if (lisDFTOrders.size() != 0) {
								strRequest = getOrderCancelJSON();

								httpRequestObj.setJSON(strRequest);

								httpRequestObj.setAPI_URL_PREFIX(
										String.format(API_URL_PREFEX.DETAIL_FINANCIAL_TRANSACTION_CANCEL.getValue(),
												lisDFTOrders.get(0).getRCMOrderActionID()));
								httpRequestObj.setHttpMethodType(HTTP_REQUESTED_METHOD_TYPE.POST);
								httpRequestObj.setSendingFacility(MSH4_SendingFacility);
								httpRequestObj.setReceivingFacility(MSH6_ReceivingFacility);
								
								//httpRequestObj.getPostDetailFinancialTransaction().setTransactionDate(FT1_4_TransactionDate)
								

								httpRequestObj.getMessageTransaction()
										.setMessageType(getMessageTypeObj(MessageSourceType.DFT_P03_CANCEL.toString()));
								httpRequestObj.getMessageTransaction()
										.setAdtOperationType(MessageSourceType.DFT_P03_CANCEL.getValue());

							} else {
								throw new Exception("OPERATION FAILED!!, ORDER NOT FOUND  [" + FT1_2_TransactionID
										+ "] , ORDER CAN NOT BE CANCEL");
							}

						} else 
							// if (FT1_6_TransactionType.equals("CH"))
						{
							// check if Vista ORder Item Exist and relace with Certa Item code , if
							// not keep
							// it as is
							String strTransactionCodeString = mappingCodesService
									.getCertaItemCodeByVistaItemCodeAndServSection(
											FT1_7_TransactionCode.replaceAll("[^\\d.]", ""),
											OBR24_DiagnosticServSectID);

							if (strTransactionCodeString == null) {
								throw new Exception("MAPPING ERROR!!! ," + "ORDER [" + FT1_2_TransactionID + "]"
										+ " FAILED TO ADD!!!" + "VISTA ORDER ITEM [" + FT1_7_TransactionCode + "]"
										+ "IS NOT DEFINED");

							} else {

								FT1_7_TransactionCode = strTransactionCodeString;

							}
							
							
							if(OBR24_DiagnosticServSectID.equals("RA"))
							{
								//Duplicated Case Order
								
								if((lisDFTOrders.size() !=0) && (lisDFTOrders.get(0).getAppointmentID() != postDetailFinancialTransaction.getAppointmentID()))
								{
									LocalDateTime now = LocalDateTime.now();
									// Define the custom formatter
							        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
							        String formattedDateTime = now.format(formatter);
									
									httpRequestObj.setMSG_Type("DFT_P03_DUPICATED_RAD_ORDER_CORRECT");
									httpRequestObj.setPostDetailFinancialTransaction(postDetailFinancialTransaction);
									strRequest = createNewOrder();
									httpRequestObj.setJSON(strRequest);

									httpRequestObj
											.setAPI_URL_PREFIX(API_URL_PREFEX.DETAIL_FINANCIAL_TRANSACTION_CREATE.getValue());
									httpRequestObj.setHttpMethodType(HTTP_REQUESTED_METHOD_TYPE.PATCH);
									httpRequestObj.setSendingFacility(MSH4_SendingFacility);
									httpRequestObj.setReceivingFacility(MSH6_ReceivingFacility);
									httpRequestObj.setAppointmentID(PV1_5_PreadmitNumber);
									httpRequestObj.setOldAppointmentID(lisDFTOrders.get(0).getAppointmentID());
									httpRequestObj.setRecivedDateTime(getDateTimeISO8601(formattedDateTime));
									httpRequestObj.getMessageTransaction()
											.setMessageType(getMessageTypeObj(MessageSourceType.DFT_P03_DUPICATED_RAD_ORDER_CORRECT.toString()));
									httpRequestObj.getMessageTransaction()
											.setAdtOperationType(MessageSourceType.DFT_P03_DUPICATED_RAD_ORDER_CORRECT.getValue());
									
									httpRequestObj.getMessageTransaction().setIsSent(false);
									httpRequestObj.getMessageTransaction().setIsSuccuss(false);
									httpRequestObj.getMessageTransaction().setIsValidated(false);
									
									
								}else
								{
									strRequest = createNewOrder();

									httpRequestObj.setJSON(strRequest);

									httpRequestObj
											.setAPI_URL_PREFIX(API_URL_PREFEX.DETAIL_FINANCIAL_TRANSACTION_CREATE.getValue());
									httpRequestObj.setHttpMethodType(HTTP_REQUESTED_METHOD_TYPE.PATCH);
									httpRequestObj.setSendingFacility(MSH4_SendingFacility);
									httpRequestObj.setReceivingFacility(MSH6_ReceivingFacility);
									httpRequestObj.setAppointmentID(PV1_5_PreadmitNumber);

									httpRequestObj.getMessageTransaction()
											.setMessageType(getMessageTypeObj(MessageSourceType.DFT_P03_CREATE.toString()));
									httpRequestObj.getMessageTransaction()
											.setAdtOperationType(MessageSourceType.DFT_P03_CREATE.getValue());
								}
								 
							}else
							{

								strRequest = createNewOrder();

								httpRequestObj.setJSON(strRequest);

								httpRequestObj.setAPI_URL_PREFIX(
										API_URL_PREFEX.DETAIL_FINANCIAL_TRANSACTION_CREATE.getValue());
								httpRequestObj.setHttpMethodType(HTTP_REQUESTED_METHOD_TYPE.PATCH);
								httpRequestObj.setSendingFacility(MSH4_SendingFacility);
								httpRequestObj.setReceivingFacility(MSH6_ReceivingFacility);
								httpRequestObj.setAppointmentID(PV1_5_PreadmitNumber);

								httpRequestObj.getMessageTransaction()
										.setMessageType(getMessageTypeObj(MessageSourceType.DFT_P03_CREATE.toString()));
								httpRequestObj.getMessageTransaction()
										.setAdtOperationType(MessageSourceType.DFT_P03_CREATE.getValue());
							}

						}

					}

					break;

                default:
                    break;
                }

                httpRequestObj.setPostDetailFinancialTransaction(postDetailFinancialTransaction);
                httpRequestObj.setHttpMethodType(HTTP_REQUESTED_METHOD_TYPE.POST);

                httpRequestObj.getMessageTransaction().setIsValidated(true);
                setHTTPRequestParam(httpRequestObj, strRequest, VALUDATION_RESULT_TYPE.SUCCUSS,
                    "Request Created", "");
				/*httpRequestObj.getMessageTransaction()
						.setMessageType(getMessageTypeObj(MessageSourceType.DFT_P03.toString()));
				httpRequestObj.getMessageTransaction().setAdtOperationType(MessageSourceType.DFT_P03.getValue());*/

                httpRequestObj.setPostDetailFinancialTransaction(postDetailFinancialTransaction);

            }

            httpRequestObj.setJSON(strRequest);
            httpRequestObj.getMessageTransaction().setMessageBody(strRequest);
            

            try {
                isJSONValid(strRequest);

                httpRequestObj.getMessageTransaction().setIsValidated(true);
            } catch (Exception e) {
                httpRequestObj.getMessageTransaction().setIsValidated(false);
                throw new Exception(e.getMessage());
            }

        } catch (Exception ex) {

            String fileName= "";
            String methodName= "";
            int lineNumber= 0;
            String strAllErrors= "";

            StackTraceElement[] stackTraceElement= ex.getStackTrace();

            fileName= stackTraceElement[stackTraceElement.length - 1].getFileName();
            methodName= stackTraceElement[stackTraceElement.length - 1].getMethodName();
            lineNumber= stackTraceElement[stackTraceElement.length - 1].getLineNumber();

            strAllErrors+= ex.getMessage() + ":" + fileName + ":" + methodName + ":" + lineNumber +
                "\r\n";

            System.out.println(strAllErrors);

            System.out.println(ex.getMessage());
            httpRequestObj.getMessageTransaction().setIsValidated(false);
            httpRequestObj.getMessageTransaction().setIsSuccuss(false);
            httpRequestObj.getMessageTransaction().setIsSent(false);

           // httpRequestObj.getMessageTransaction().setNotes(strAllErrors);
            
            httpRequestObj.getMessageTransaction().setResponse(strAllErrors);
            httpRequestObj.getMessageTransaction().setNotes("Error in converting from LIS TO JSON");


            setHTTPRequestParam(httpRequestObj, strRequest, VALUDATION_RESULT_TYPE.FAILED,
                httpRequestObj.getErrorDesc(), strAllErrors);
            

        } finally {

            messageTransactionService.addMessageTransaction(
                httpRequestObj.getMessageTransaction().getMachine(),
                strRequest, httpRequestObj.getMessageTransaction().getMessageDirection(),
                httpRequestObj.getMessageTransaction().getMessageType(),
                httpRequestObj.getMessageTransaction().getIsSuccuss(),
                httpRequestObj.getMessageTransaction().getIsValidated(),
                httpRequestObj.getMessageTransaction().getIsSent(),
                httpRequestObj.getMessageTransaction().getNotes(), "",
                httpRequestObj.getMessageTransaction().getMessageControlID(),  
                httpRequestObj.getMessageTransaction().getPatientID(),
                httpRequestObj.getMessageTransaction().getNationalID(),
                httpRequestObj.getMessageTransaction().getAdtOperationType());

            conf.recipient.tell(httpRequestObj, self());
        }

    }

    private String adt_a04CreateVirtualVisitRequst() throws Exception {

        return "{ "

        ////////////////////////////////////////////////////////
            + "\"" + "doctor" + "\"" + " : " + "{" + "\"" + "code" + "\"" + " : " + "{" + "\"" +
            "value" + "\"" + " : " + "\"" + PV1_7_1_AttendingDoctorCode + "\"" + " }},"
            ////////////////////////////////////////////////////////
            + "\"" + "facility" + "\"" + " : " + "{" + "" + "\"" + "code" + "\"" + " : " + "{" +
            "" + "\"" + "value" + "\"" + " : " + "\"" + MSH4_SendingFacility + "\"" + "}}" + ","
            ////////////////////////////////////////////////////////
            + "\"" + "identifier" + "\"" + " : " + "{" + "\"" + "value" + "\"" + " : " + "\"" +
            PV1_19_VisitNumber + "\"" + "},"
            ///////////////////////////////////////////////////////
            + "\"" + "recordedBy" + "\"" + " : " + "{" + "" + "\"" + "code" + "\"" + ":" + "{" +
            "" + "\"" + "value" + "\"" + ":" + "\"" + EVN5_OperatorID + "\"" + "}}" + ","
            ////////////////////////////////////////////////////////
            + "\"" + "recordedDate" + "\"" + " : " + "\"" +
            getDateTimeISO8601(EVN2_RecordedDateTime) + "\"" + ","
            ////////////////////////////////////////////////////////
            + "\"" + "source" + "\"" + " : " + "\"" +
            getAssignedPatientLocation(PV1_2_PatientClass) + "\"" + ","
            ////////////////////////////////////////////////////////
            + "\"" + "type" + "\"" + " : " + "\"" + getAdmissionTypeID(PV1_4_AdmissionType) + "\"" +
            ","
            ////////////////////////////////////////////////////////
            + "\"" + "visitDate" + "\"" + " : " + "\"" + getDateTimeISO8601(EVN2_RecordedDateTime) +
            "\"" +
            ////////////////////////////////////////////////////////

            "}";
    }

    private String returnDispensedItem() throws Exception {
        /*
         * 
         * facility* Facility{...} item* DispensedItemToReturn{...} recordedBy*
         * RecordedBy{...} recordedDate* [...]
         * 
         */

        return "{" + "\r\n" + "\"" + "facility" + "\"" + ":" + "{" + "\r\n" + "\"" + "code" + "\"" +
            ":" + "{" + "\r\n" + "\"" + "value" + "\"" + ":" + "\"" + MSH4_SendingFacility + "\"" +
            "\r\n" + "}" + "\r\n" + "}," + "\r\n" + "\"" + "DispensedItemToReturn" + "\"" + ":" +
            "\"" + "--" + "\"" + "," + "\r\n" + "\"" + "recordedBy" + "\"" + ":" + "{" + "\r\n" +
            "\"" + "code" + "\"" + ":" + "{" + "\r\n" + "\"" + "value" + "\"" + ":" + "\"" +
            EVN5_OperatorID + "\"" + "\r\n" + "}" + "\r\n" + "}," + "\r\n" + "\"" + "recordedDate" +
            "\"" + ":" + "\"" + getDateTimeISO8601(MSH7_DateTimeOfMessage) + "\"" + "\r\n" + "}";
    }

    private void completeOrder(httpRequstTransaction httpRequestObj,
        List<PostDetailFinancialTransaction> lisDFTOrders)
        throws Exception {
        // if(lisDFTOrders.size() !=0)
        // {
        httpRequestObj.setAPI_URL_PREFIX(
            String.format(API_URL_PREFEX.DETAIL_FINANCIAL_TRANSACTION_COMPLETE.getValue(),
                lisDFTOrders.get(0).getRCMOrderID()));
        // }else
        // {
        // throw new Exception("ERROR:_ORDER NOT EXIST TO COMPLETE");
        // }
    }

    private String createNewOrder() throws Exception {
       
        strRequest= getOrderCreateJSON();
        
        return strRequest;
    }
    

    

	private String createPharmacyOrder() throws Exception {

        /*
         * 
         * authoredOn* [...] doctor* Doctor{...} encounter* Encounter{...} facility*
         * Facility{...} identifier* Code{...} isDischargePrescription* [...]
         * orderItems* [...] patient* Patient{...} recordedBy* RecordedBy{...}
         * recordedDate* [...] section* Section{...}
         * 
         */

        if (FT1_7_TransactionCode.isEmpty())
            throw new Exception("Error_:_FT1_7_TransactionCode.Not.Send");
        if (FT1_10_TransactionQuantity.isEmpty())
            throw new Exception("Error_:FT1_10_TransactionQuantity.Not.Send");
        if (FT1_20_PerformedByCode.isEmpty())
            throw new Exception("Error_:FT1_10_TransactionQuantity.Not.Send");
        return "{" + "\r\n" + "\"" + "authoredOn" + "\"" + ":" + "\"" +
            getDateTimeISO8601(MSH7_DateTimeOfMessage) + "\"" + "," + "\r\n" + "\"" + "doctor" +
            "\"" + ":" + "{" + "\r\n" + "\"" + "code" + "\"" + ":" + "{" + "\r\n" + "\"" + "value" +
            "\"" + ":" + "\"" + FT1_21_1_OrderedByCode + "\"" + "\r\n" + "}" + "\r\n" + "}," +
            "\r\n" + getMaindatoryEncounter(PV1_19_VisitNumber, PV1_2_PatientClass) + "\"" +
            "facility" + "\"" + ":" + "{" + "\r\n" + "\"" + "code" + "\"" + ":" + "{" + "\r\n" +
            "\"" + "value" + "\"" + ":" + "\"" + MSH4_SendingFacility + "\"" + "\r\n" + "}" +
            "\r\n" + "}," + "\r\n" + "\"" + "identifier" + "\"" + ":" + "{" + "\r\n" + "\"" +
            "value" + "\"" + ":" + "\"" + ORC3_FillerOrderNumber + "\"" + "\r\n" + "}," + "\r\n" +
            "\"" + "isDischargePrescription" + "\"" + ":" +
            (FT1_23_FillerOrderNumber.equals("O") ? true : false) + "," + "\r\n" + "\"" +
            "orderItems" + "\"" + ":" + "[" + "\r\n" + "{" + "\r\n" + "\"" + "code" + "\"" + ":" +
            "{" + "\r\n" + "\"" + "value" + "\"" + ":" + "\"" + FT1_7_TransactionCode + "\"" +
            "\r\n" + "}," + "\r\n" + "\"" + "quantity" + "\"" + ":" + "{" + "\r\n" + "\"" +
            "value" + "\"" + ":" + "\"" + FT1_10_TransactionQuantity + "\"" + "\r\n" + "}," +
            "\r\n" + "\"" + "unit" + "\"" + ":" + "{" + "\r\n" + "\"" + "value" + "\"" + ":" +
            "\"" + FT1_20_PerformedByCode + "\"" + "\r\n" + "}," + "\r\n" + "\"" +
            "isStableMedication" + "\"" + " : " +
            (FT1_26_ProcedureCodeModifier.equals("STA") ? true : false) + "}" + "\r\n" + "]," +
            "\r\n" + "\"" + "patient" + "\"" + ":" + "{" + "\r\n" + "\"" + "code" + "\"" + ":" +
            "{" + "\r\n" + "\"" + "value" + "\"" + ":" + "\"" + PID3_PatientIdentifierList + "\"" +
            "\r\n" + "}," + "\r\n" + "\"" + "name" + "\"" + ":" + "{" + "\r\n" + "\"" +
            "firstName" + "\"" + ":" + "\"" + PID5_1_PatientEnFirstName + "\"" + "," + "\r\n" +
            "\"" + "fatherName" + "\"" + ":" + "\"" + PID5_2_PatientEnSecondName + "\"" + "," +
            "\r\n" + "\"" + "midName" + "\"" + ":" + "\"" + PID5_3_PatientEnThirdName + "\"" + "," +
            "\r\n" + "\"" + "lastName" + "\"" + ":" + "\"" + PID5_4_PatientEnFamilyName + "\"" +
            "," + "\r\n" + "\"" + "otherFirstName" + "\"" + ":" + "\"" +
            decodeToArabicName(PID5_5_PatientArFirstName) + "\"" + "," + "\r\n" + "\"" +
            "otherFatherName" + "\"" + ":" + "\"" + decodeToArabicName(PID5_6_PatientArSecondName) +
            "\"" + "," + "\r\n" + "\"" + "otherMidName" + "\"" + ":" + "\"" +
            decodeToArabicName(PID5_7_PatientArThirdName) + "\"" + "," + "\r\n" + "\"" +
            "otherLastName" + "\"" + ":" + "\"" + decodeToArabicName(PID5_8_PatientArFamilyName) +
            "\"" + "\r\n" + "}" + "\r\n" + "}," + "\r\n" + "\"" + "recordedBy" + "\"" + ":" + "{" +
            "\r\n" + "\"" + "code" + "\"" + ":" + "{" + "\r\n" + "\"" + "value" + "\"" + ":" +
            "\"" + EVN5_OperatorID + "\"" + "\r\n" + "}" + "\r\n" + "}," + "\r\n" + "\"" +
            "recordedDate" + "\"" + ":" + "\"" + getDateTimeISO8601(MSH7_DateTimeOfMessage) + "\"" +
            "," + "\r\n" + "\"" + "orderSection" + "\"" + ":" + "{" + "\r\n" + "\"" + "code" + "\"" +
            ":" + "{" + "\r\n" + "\"" + "value" + "\"" + ":" + "\"" + PV1_3_1_AssignedPatient_PointOfCare + "\"" +
            "\r\n" + "}}"+","+"\"" + "pharmacySection" + "\"" + ":" + "{" + "\r\n" + "\"" + "code" + "\"" +
            ":" + "{" + "\r\n" + "\"" + "value" + "\"" + ":" + "\"" + FT1_13_DepartmentCode + "\"" +
            "\r\n" + "}}"+ "}";
    }

    public void isJSONValid(String jsonInString) throws Exception {
        try {
            gson.fromJson(jsonInString, Object.class);

        } catch (com.google.gson.JsonSyntaxException ex) {
            throw new Exception("ERROR:BAD JSON REQUEST:" + "JSON MESSAGE : " + jsonInString + ":" +
                ex.getMessage());
        }
    }

    private String getOrderCompleteJSON() {

        try {
            strRequest= "{" + "\r\n" + "\"" + "facility" + "\"" + ":" + "{" + "\"" + "code" + "\"" +
                ":" + "{" + "\"" + "value" + "\"" + ":" + "\"" + MSH4_SendingFacility + "\"" + "}" +
                "}," + "\"" + "recordedBy" + "\"" + ":" + "{" + "\"" + "code" + "\"" + ":" + "{" +
                "\"" + "value" + "\"" + ":" + "\"" + EVN5_OperatorID + "\"" + "}" + "}," + "\"" +
                "recordedDate" + "\"" + ":" + "\"" + getDateTimeISO8601(MSH7_DateTimeOfMessage) +
                "\"" + "}";
            ;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return strRequest;

    }

    private String getOrderCancelJSON() {
        String strData= "";
        try {
            strData= "{" + "\r\n" + "\"" + "facility" + "\"" + ":" + "{" + "\"" + "code" + "\"" +
                ":" + "{" + "\"" + "value" + "\"" + ":" + "\"" + MSH4_SendingFacility + "\"" + "}" +
                "}," + "\"" + "recordedBy" + "\"" + ":" + "{" + "\"" + "code" + "\"" + ":" + "{" +
                "\"" + "value" + "\"" + ":" + "\"" + EVN5_OperatorID + "\"" + "}" + "}," + "\"" +
                "recordedDate" + "\"" + ":" + "\"" + getDateTimeISO8601(MSH7_DateTimeOfMessage) +
                "\"" + "}";
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return strData;

    }

    private String getEncounter(String pV1_19_VisitNumber2, String pV1_2) {
       
    	// return "";
    	 
    	if (pV1_19_VisitNumber2.isEmpty() || pV1_2.isEmpty()) 
    	{ return ""; }
        if (pV1_19_VisitNumber2 == null || pV1_2 == null) {
            return "";
        } else {
            return "\"" + "encounter" + "\"" + ":" + "{" + "\r\n" + "\"" + "identifier" + "\"" +
                ":" + "{" + "\r\n" + "\"" + "value" + "\"" + ":" + "\"" + PV1_19_VisitNumber +
                "\"" + "\r\n" + "}," + "\r\n" + "\"" + "type" + "\"" + ":" + "\"" + pV1_2 + "\"" +
                "\r\n" + "}," + "\r\n";
        }
    }

    private String getMaindatoryEncounter(String pV1_19_VisitNumber2, String pV1_2) {

       /* if (pV1_19_VisitNumber2.equals("")) {
            return "";

        } else {

            return "\"" + "encounter" + "\"" + ":" + "{" + "\r\n" + "\"" + "identifier" + "\"" +
                ":" + "{" + "\r\n" + "\"" + "value" + "\"" + ":" + "\"" + PV1_19_VisitNumber +
                "\"" + "\r\n" + "}," + "\r\n" + "\"" + "type" + "\"" + ":" + "\"" + pV1_2 + "\"" +
                "\r\n" + "}," + "\r\n";
        }*/
    	
    	 return "";
    }

    private String getOrderCreateJSON() throws Exception {

        strRequest= // getOrderListWithJsonFormat(lisDFTOrders);

            "{" + "\r\n" + "\"" + "authoredOn" + "\"" + ":" + "\"" +
                getDateTimeISO8601(FT1_4_TransactionDate) + "\"" + "," + "\r\n" + "\"" + "doctor" +
                "\"" + ":" + "{" + "\r\n" + "\"" + "code" + "\"" + ":" + "{" + "\r\n" + "\"" +
                "value" + "\"" + ":" + "\"" + FT1_21_1_OrderedByCode + "\"" + "\r\n" + "}" +
                "\r\n" + "}," + "\r\n" + getEncounter(PV1_19_VisitNumber, PV1_2_PatientClass) +
                "\"" + "facility" + "\"" + ":" + "{" + "\r\n" + "\"" + "code" + "\"" + ":" + "{" +
                "\r\n" + "\"" + "value" + "\"" + ":" + "\"" + MSH4_SendingFacility + "\"" + "\r\n" +
                "}" + "\r\n" + "}," + "\r\n" + "\"" + "identifier" + "\"" + ":" + "{" + "\r\n" +
                "\"" + "value" + "\"" + ":" + "\"" + FT1_2_TransactionID + "\"" + "\r\n" + "}," +
                getItemOrders(FT1_7_TransactionCode, ORC2_PlacerOrderNumber,
                    FT1_10_TransactionQuantity, OBR24_DiagnosticServSectID)

                // FT1_7_TransactionCode.replaceAll("[^\\d.]", "")
                // //getItemOrders(FT1_7_TransactionCode)
                + "\"" + "patient" + "\"" + ":" + "{" + "\r\n" + "\"" + "code" + "\"" + ":" + "{" +
                "\r\n" + "\"" + "value" + "\"" + ":" + "\"" + PID3_PatientIdentifierList + "\"" +
                "\r\n" + "}," + "\r\n" + "\"" + "name" + "\"" + ":" + "{" + "\r\n" + "\"" +
                "firstName" + "\"" + ":" + "\"" + PID5_1_PatientEnFirstName + "\"" + "," + "\r\n" +
                "\"" + "fatherName" + "\"" + ":" + "\"" + PID5_2_PatientEnSecondName + "\"" + "," +
                "\r\n" + "\"" + "midName" + "\"" + ":" + "\"" + PID5_3_PatientEnThirdName + "\"" +
                "," + "\r\n" + "\"" + "lastName" + "\"" + ":" + "\"" + PID5_4_PatientEnFamilyName +
                "\"" + "," + "\r\n" + "\"" + "otherFirstName" + "\"" + ":" + "\"" +
                decodeToArabicName(PID5_5_PatientArFirstName) + "\"" + "," + "\r\n" + "\"" +
                "otherFatherName" + "\"" + ":" + "\"" +
                decodeToArabicName(PID5_6_PatientArSecondName) + "\"" + "," + "\r\n" + "\"" +
                "otherMidName" + "\"" + ":" + "\"" + decodeToArabicName(PID5_7_PatientArThirdName) +
                "\"" + "," + "\r\n" + "\"" + "otherLastName" + "\"" + ":" + "\"" +
                decodeToArabicName(PID5_8_PatientArFamilyName) + "\"" + "\r\n" + "}" + "\r\n" +
                "}," + "\r\n" + "\"" + "recordedBy" + "\"" + ":" + "{" + "\"" + "code" + "\"" +
                ":" + "{" + "\r\n" + "\"" + "value" + "\"" + ":" + "\"" + EVN5_OperatorID + "\"" +
                "\r\n" + "}" + "\r\n" + "}," + "\r\n" + "\"" + "recordedDate" + "\"" + ":" + "\"" +
                getDateTimeISO8601(MSH7_DateTimeOfMessage) + "\"" + "," + "\r\n" + "\"" +
                "section" + "\"" + ":" + "{" + "\r\n" + "\"" + "code" + "\"" + ":" + "{" + "\r\n" +
                "\"" + "value" + "\"" + ":" + "\"" + PV1_3_AssignedPatient + "\"" + "\r\n" + "}" +
                "}" + "}";
        return strRequest;
    }

    private String getItemOrders(String fT1_7_TransactionCode, String orc2_PlacerOrderNumber,
        String fT1_10_TransactionQuantity, String obr24_DiagnosticServSectID) {

        String strOrderData= "\"" + "orderItems" + "\"" + ":" + "[";
        int indexOfChar= orc2_PlacerOrderNumber.indexOf(';');
        fT1_7_TransactionCode= fT1_7_TransactionCode.replaceAll("[^\\d.]", "");

        if (indexOfChar != -1) {

            String[] arrOrderCode= orc2_PlacerOrderNumber.split(";");

            String strORC_2PlacerOrderNumber= arrOrderCode[0] +
                arrOrderCode[arrOrderCode.length - 1];

            strOrderData= "\"" + "orderItems" + "\"" + ": [{";

            // for(int i = 0 ; i < arrOrderCode.length; i++)
            // {
            // if(i > 0 )
            // {
            // strOrderData +=",";
            // }

            strOrderData+= "\"" + "code" + "\"" + ":" + "{" + "\r\n" + "\"" + "value" + "\"" + ":" +
                "\"" + fT1_7_TransactionCode + "\"" + "\r\n" + "}," + "\r\n" + "\"" + "quantity" +
                "\"" + ":" + "{" + "\r\n" + "\"" + "value" + "\"" + ":" + "\"" +
                fT1_10_TransactionQuantity + "\"" + "\r\n" + "}" + "\r\n" +
                getGroupReferance(strORC_2PlacerOrderNumber, obr24_DiagnosticServSectID);
            // getDiagnosisDetails(DG1_1_SetID_DG1, DG1_2_DiagnosisCodingMethod) +

            // }

            strOrderData+= "}";

        } else {

            strOrderData= "\"" + "orderItems" + "\"" + ": [{";
            strOrderData+= "\"" + "code" + "\"" + ":" + "{" + "\r\n" + "\"" + "value" + "\"" + ":" +
                "\"" + fT1_7_TransactionCode + "\"" + "\r\n" + "}," + "\r\n" + "\"" + "quantity" +
                "\"" + ":" + "{" + "\r\n" + "\"" + "value" + "\"" + ":" + "\"" +
                fT1_10_TransactionQuantity + "\"" + "\r\n" + "}" + "\r\n" +
                getGroupReferance(orc2_PlacerOrderNumber, obr24_DiagnosticServSectID) +
                // getDiagnosisDetails(DG1_1_SetID_DG1, DG1_2_DiagnosisCodingMethod) +
                "}";
        }

        strOrderData+= "],";

        return strOrderData;

    }

    private String decodeToArabicName(String input) {
    	
    	return input;
       
    	/*String strOutput= "";
        String retValue= "";
        String convertValue2= "";
        ByteBuffer convertedBytes= null;

        try {

            CharsetEncoder encoder2= Charset.forName("ISO-8859-1").newEncoder();
            // CharsetEncoder encoder3 = Charset.forName("UTF-8").newEncoder();
            System.out.println("value = " + input);

            assert encoder2.canEncode(input);
            // assert encoder3.canEncode(input);

            ByteBuffer conv1Bytes2= encoder2.encode(CharBuffer.wrap(input.toCharArray()));
            // ByteBuffer conv1Bytes3 = 
            // encoder2.encode(CharBuffer.wrap(input.toCharArray()));

            retValue= new String(conv1Bytes2.array(), Charset.forName("Windows-1256"));

            System.out.println("retValue = " + retValue);

            // convertedBytes = encoder3.encode(CharBuffer.wrap(retValue.toCharArray()));
            // convertValue2 = new String(convertedBytes.array(), Charset.forName("UTF-8"));

            // convertValue2 = convertValue2.substring(0,convertValue2.length()-1);
            // System.out.println("convertedValue =" + convertValue2);

            // convertValue2 = convertValue2.replaceAll("\u0000", "");

        } catch (Exception ex) {

        }

        return retValue;
        // strOutput.substring(0,strOutput.length()-1 );*/

    }

    private void setCommonOrder(HL7_V24_CommonOrder commonOrder2) {
        // TODO Auto-generated method stub
        ORC1_OrderControl= commonOrder2.getOrderControl();
        ORC2_PlacerOrderNumber= commonOrder2.getPlacerOrderNumber();
        ORC3_FillerOrderNumber= commonOrder2.getFillerOrderNumber();
        ORC6_ResponseFlag= commonOrder2.getResponseFlag();
        ORC27_4_ExpectedStartDate= commonOrder2.getExpectedStartDate();
        ORC27_5_ExpectedEndDate= commonOrder2.getExpectedEndDate();
        ORC16_OrderControlCodeReason= "";

    }

    private void setOBRRecord(OBRRecord oBRRecord2) {
        // TODO Auto-generated method stub
        OBR24_DiagnosticServSectID= OBRRecord.getDiagnosticServSectID();
    }

    private void setFinancialTransaction(HL7_v24_FinancialTransaction financialTransaction2) {
        // TODO Auto-generated method stub
        FT1_2_TransactionID= financialTransaction2.getTransactionID();
        FT1_3_TransactionBatchID= financialTransaction2.getTransactionBatchID();
        FT1_4_TransactionDate= financialTransaction2.getTransactionDate();
        FT1_5_TransactionPostingDate= financialTransaction.getTransactionPostingDate();
        FT1_6_TransactionType= financialTransaction2.getTransactionType();
        FT1_7_TransactionCode= financialTransaction2.getTransactionCode();
        FT1_8_TransactionDescription= financialTransaction2.getTransactionDescription();
        FT1_10_TransactionQuantity= financialTransaction2.getTransactionQuantity();
        FT1_13_DepartmentCode= financialTransaction2.getDepartmentCode();
        FT1_23_FillerOrderNumber= financialTransaction2.getFillerOrderNumber();
        FT1_20_PerformedByCode= financialTransaction2.getPerformedByCode();
        FT1_21_1_OrderedByCode= financialTransaction2.getOrderedByCode();
        FT1_26_ProcedureCodeModifier= financialTransaction2.getProcedureCodeModifier();

    }

    private String getOrderListWithJsonFormat(List<PostDetailFinancialTransaction> lisDFTOrders) {
        String strJSONOrderObject= "\"" + "orderItems" + "\"" + ":" + "[";

        for (int index= 0; index < lisDFTOrders.size(); index++ ) {

            /*
             * "code": { "value": "string" }, "quantity": { "value": 0 }, "groupReference":
             * { "value": "string" }, "diagnosis": { "condition": { "code": { "value":
             * "string" } }, "type": "unspecified" }
             */

            strJSONOrderObject+=
                // getOrderCode(lisDFTOrders.get(index).getRcmOrderID())
                getOrderQuantity(1) + getGroupReferance("", "") + getDiagnosisDetails("", "");
            // + getOrderType("")
            // + "}"

        }

        return strJSONOrderObject;

    }

    private String getGroupReferance(String strGroupReferance, String obr24_DiagnosticServSectID2) {
       
    	if(strGroupReferance.equals(""))
    	{
    		return "";
    	}

        if (obr24_DiagnosticServSectID2.equals("LA")) {
            return ", " + "\"" + "groupReference" + "\"" + ":" + "{" + "\"" + "value" + "\"" + ":" +
                "\"" + strGroupReferance

                + "\"" + "}";
        } else {

            return "";
        }
    }

    private String getOrderQuantity(int i) {

        /*
         * "quantity": { "value": 0 },
         */
        return "\"" + "quantity" + "\"" + ":" + "{" + "\"" + "value" + "\"" + "\"" + i + "\"" +
            "},";
    }

    private String getOrderCode(int strCertaOrder) {
        // TODO Auto-generated method stub
        return "\"" + "code" + "\"" + ":" + "{" + "\"" + "value" + "\"" + "\"" + "xxxxx" + "\"" +
            "},";
    }

    private List<PostDetailFinancialTransaction> getPreviousMappedOrder(int Ref_No) {
        return null;
    }

    private String adt_a03_CloseVisitRequst() throws Exception {

        strRequest= "";
        strRequest= "{" + "\r\n" + "\"" + "closeDate" + "\"" + ":" + "\"" +
            getDateTimeISO8601(PV1_45_DischargeDateTime) + "\"" + "," + "\r\n" + "\"" + "closedBy" +
            "\"" + ":" + "{" + "\r\n" + "\"" + "code" + "\"" + ":" + "{" + "\r\n" + "\"" + "value" +
            "\"" + ":" + "\"" + PV1_7_1_AttendingDoctorCode + "\"" + "\r\n" + "}}," + "\r\n" +
            "\"" + "facility" + "\"" + ":" + "{" + "\r\n" + "\"" + "code" + "\"" + ":" + "{" +
            "\r\n" + "\"" + "value" + "\"" + ":" + "\"" + MSH4_SendingFacility + "\"" + "\r\n" +
            "}}," + "\r\n" + getDischargeReason(PV1_36_DischargeDisposition) + "\"" + "recordedBy" +
            "\"" + ":" + "{" + "\r\n" + "\"" + "code" + "\"" + ":" + "{" + "\r\n" + "\"" + "value" +
            "\"" + ":" + "\"" + EVN5_OperatorID + "\"" + "\r\n" + "}}," 
            + "\"" + "recordedDate" +
            "\"" + ":" + "\"" + getDateTimeISO8601(PV1_45_DischargeDateTime) 
            + "\"" 
            + "}";

        return strRequest;
    }

    private void adt_a03_CloseVisitValudate(httpRequstTransaction httpRequestObj) throws Exception {
       
        if (PV1_7_1_AttendingDoctorCode.isEmpty()) {
            System.out.println("PV17_AttendingDoctor is null");

            throw new Exception("ERROR:_PV1_7_1_AttendingDoctorCode.field.is.null");
        }
        if (MSH4_SendingFacility.isEmpty()) {
            System.out.println("MSH4_SendingFacility is null");

            throw new Exception("ERROR:_MSH4_SendingFacility.field.is.null");
        }
        if (EVN5_OperatorID.isEmpty()) {
            System.out.println("EVN5_OperatorID is null");

            throw new Exception("ERROR:_EVN5_OperatorID.field.is.null");
        }
        
      /*  if (PV1_36_DischargeDisposition.isEmpty()) {
            System.out.println("PV1_36_DischargeDisposition is null");

            throw new Exception("ERROR:PV1_36_DischargeDisposition.field.is.empty");
        }*/
       /* if (PV1_21_ChargePriceIndicator.isEmpty()) {
            System.out.println("PV1_21_ChargePriceIndicator is empty");

            throw new Exception("ERROR:PV1_21_ChargePriceIndicator.field.is.empty");
        }*/
        if (PV1_45_DischargeDateTime.isEmpty()) {
            System.out.println("PV1_45_DischargeDateTime is null");

            throw new Exception("ERROR:_PV1_45_DischargeDateTime.field.is.null");
        }

    }

    private String adt_a03CreateDischargeAdmissionRequst() throws Exception {

        strRequest= "{" + "\r\n" + "\"" + "dischargeDate" + "\"" + ":" + "\"" +
            getDateTimeISO8601(PV1_45_DischargeDateTime) + "\"" + "," + "\r\n" + "\"" +
            "dischargedBy" + "\"" + ":" + "{" + "\r\n" + "\"" + "code" + "\"" + ":" + "{" + "\r\n" +
            "\"" + "value" + "\"" + ":" + "\"" + PV1_7_1_AttendingDoctorCode + "\"" + "\r\n" +
            "}}," + "\r\n" + "\"" + "facility" + "\"" + ":" + "{" + "\r\n" + "\"" + "code" + "\"" +
            ":" + "{" + "\r\n" + "\"" + "value" + "\"" + ":" + "\"" + MSH4_SendingFacility + "\"" +
            "\r\n" + "}}," + "\r\n" + getDischargeReason(PV1_36_DischargeDisposition) + "\"" +
            "recordedBy" + "\"" + ":" + "{" + "\r\n" + "\"" + "code" + "\"" + ":" + "{" + "\r\n" +
            "\"" + "value" + "\"" + ":" + "\"" + EVN5_OperatorID + "\"" + "\r\n" + "}}," + "\"" +
            "recordedDate" + "\"" + ":" + "\"" + getDateTimeISO8601(PV1_45_DischargeDateTime) +
            "\"" + "}"

        ;

        return strRequest;
    }

   
    
    
    private String getDischargeReason(String str) {
    	 if (!str.isEmpty()) {
             int reasonCode= Integer.parseInt(str);
             switch (reasonCode) {
             case 29:
                 str= DISCHARGE_REASON.IRREGULAR.getValue();
                 return "\"" + "reason" + "\"" + ":" + "\"" + str + "\"" + "," + "\r\n";
             case 40:
                 str= DISCHARGE_REASON.CANCELADMISSION.getValue();
                 return "\"" + "reason" + "\"" + ":" + "\"" + str + "\"" + "," + "\r\n";
             case 27:
                 str= DISCHARGE_REASON.DEATH.getValue();
                 return "\"" + "reason" + "\"" + ":" + "\"" + str + "\"" + "," + "\r\n";
             case 50:
                 str= DISCHARGE_REASON.DOCTORPERMISSION.getValue();
                 return "\"" + "reason" + "\"" + ":" + "\"" + str + "\"" + "," + "\r\n";
             case 51:
                 str= DISCHARGE_REASON.TRANSFEROUT.getValue();
                 return "\"" + "reason" + "\"" + ":" + "\"" + str + "\"" + "," + "\r\n";
             case 49:
                 str= DISCHARGE_REASON.CANCELADMISSION.getValue();
                 return "\"" + "reason" + "\"" + ":" + "\"" + str + "\"" + "," + "\r\n";
             default:
                 str= DISCHARGE_REASON.UNSPECIFIED.getValue();
                 return "\"" + "reason" + "\"" + ":" + "\"" + str + "\"" + "," + "\r\n";
             }
																	 
        } else
        {
            return  "\"" + "reason" + "\"" + ":" + "\"" + DISCHARGE_REASON.UNSPECIFIED.getValue() + "\"" + "," + "\r\n";
        }	
    }
    
    private String getRecordedDate(String pV1_45_DischargeDateTime2) {
        // TODO Auto-generated method stub
        return null;
    }

    private String getRecordedBy(String eVN5_OperatorID2) {
        // TODO Auto-generated method stub
        return null;
    }

    private String getFacility(String strMSH4_SendingFacility) {

        String strFacilityData= "\"" + "facility" + "\"" + ":" + "{" + "\r\n" + "\"" + "code" +
            "\"" + ":" + "{" + "\r\n" + "\"" + "value" + "\"" + ":" + "\"" +
            strMSH4_SendingFacility + "\"" + "\r\n" + "}},";

        return strFacilityData;
    }

    private String getDischargeClosedBy(String PV1_7_1_AttendingDoctorCode) {
        String strDischargeDoctor= "";

        if (PV1_7_1_AttendingDoctorCode != "") {
            strDischargeDoctor= "\"" + "closedBy" + "\"" + ":" + "{" + "\"" + "code" + "\"" + ":" +
                "{" + "\"" + "value" + "\"" + ":" + "\"" + PV1_7_1_AttendingDoctorCode + "\"" +
                "}" + "},";
        }

        return strDischargeDoctor;
    }

    private void adt_a03_valudateDischargeAdmissionFields(httpRequstTransaction httpRequestObj)
        throws Exception {
        if (PV1_45_DischargeDateTime.isEmpty()) {

            throw new Exception("Error_:_PV1_45_DischargeDateTime.Not.Send");
        }
        if (PV1_7_1_AttendingDoctorCode.isEmpty()) {

            throw new Exception("Error_:_PV1_7_1_AttendingDoctorCode.Not.Send");
        }
        if (MSH4_SendingFacility.isEmpty()) {

            throw new Exception("Error_:_MSH4_SendingFacility.Not.Send");
        }
        if (EVN5_OperatorID.isEmpty()) {

            throw new Exception("Error_:_EVN5_OperatorID.Not.Send");
        }
        if (PV1_19_VisitNumber.isEmpty()) {

            throw new Exception("Error_:_PV1_19_VisitNumber.Not.Send");
        }
    }

    private String siu_s15_CancelAppointmentRequest() throws Exception {
        String strRequest;
        if (EVN5_OperatorID.isEmpty()) {

            throw new Exception("Error_:_EVN5_OperatorID.Not.Send");
        }
        if (MSH4_SendingFacility.isEmpty()) {

            throw new Exception("Error_:_MSH4_SendingFacility.Not.Send");
        }
        strRequest= "{\r\n" + "\"" + "facility" + "\"" + ":" + "{" + "\r\n" + "\"" + "code" + "\"" +
            ":" + "{" + "\r\n" + "\"" + "value" + "\"" + ":" + "\"" + MSH6_ReceivingFacility +
            "\"" + "\r\n" + "}}," + "\r\n" + "\"" + "recordedBy" + "\"" + ":" + "{" + "\r\n" +
            "\"" + "code" + "\"" + ":" + "{" + "\r\n" + "\"" + "value" + "\"" + ":" + "\"" +
            PV1_7_1_AttendingDoctorCode + "\"" + "\r\n" + "}}," + "\r\n" + "\"" + "recordedDate" +
            "\"" + ":" + "\"" + getDateTimeISO8601(MSH7_DateTimeOfMessage) + "\"" + "\r\n" + "}";
        return strRequest;
    }

    private String siu_s13_UpdateAppointmentRequest() throws Exception {
        String strRequest;
        
		strRequest = "{\r\n" + "\"" + "appointmentType" + "\"" + ":" + "\"" 
		+ getAppointmentType(SCH_11_AppointmentTimingQuantity) 
		+ "\"" + "," + "\r\n"
		+ "\"" + "doctor" + "\"" + ":" + "{" + "\r\n" + "\"" + "code" + "\"" + ":" + "{" + "\r\n" + "\""
		+ "value" + "\"" + ":" + "\"" + PV1_7_1_AttendingDoctorCode + "\"" + "\r\n" + "}}," + "\r\n" + "\""
		+ "end" + "\"" + ":" + "\"" + getEndTime(PV1_44_AdmitDateTime) + "\"" + "," 
		+ "\r\n" + "\"" + "facility"
		+ "\"" + ":" + "{" + "\r\n" + "\"" + "code" + "\"" + ":" + "{" + "\r\n" + "\"" + "value" + "\"" + ":"
		+ "\"" + MSH6_ReceivingFacility + "\"" + "\r\n" + "}}," 
		+ "\r\n" + "\"" + "patient" + "\"" + ":" + "{"
		+ "\r\n" + "\"" + "code" + "\"" + ":" + "{" + "\r\n" + "\"" + "value" + "\"" + ":" + "\""
		+ PID3_PatientIdentifierList + "\"" + "\r\n" + "}," 
		+ "\r\n" + "\"" + "name" + "\"" + ":" + "{" + "\r\n"
		+ "\"" + "firstName" + "\"" + ":" + "\"" + PID5_1_PatientEnFirstName + "\"" + "," + "\r\n" + "\""
		+ "fatherName" + "\"" + " : " + "\"" + PID5_2_PatientEnSecondName + "\"" + "," + "\r\n" + "\""
		+ "midName" + "\"" + " : " + "\"" + PID5_2_PatientEnSecondName + "\"" + "," + "\r\n" + "\"" + "lastName"
		+ "\"" + " : " + "\"" + PID5_4_PatientEnFamilyName + "\"" + "," + "\r\n" + "\"" + "otherFirstName"
		+ "\"" + " : " + "\"" + PID5_5_PatientArFirstName + "\"" + "," + "\r\n" + "\"" + "otherFatherName"
		+ "\"" + " : " + "\"" + PID5_6_PatientArSecondName + "\"" + "," + "\r\n" + "\"" + "otherMidName" + "\""
		+ " : " + "\"" + PID5_7_PatientArThirdName + "\"" + "," + "\r\n" + "\"" + "otherLastName" + "\"" + " : "
		+ "\"" + PID5_8_PatientArFamilyName + "\"" + "\r\n" + "}}}";
		
        return strRequest;
    }
    
    
    private String modifyPostedAppointment() throws Exception {
        String strRequest;
        
        
		strRequest = "{"+ "\"" + "newIdentifier" +  "\"" + ":" +
				"{" + "\""
				+ "value" + "\"" + ":" + "\"" + SCH_2_FillerAppointmentID + "\"" + "}," 
				+  "\"" +"start" + "\"" + ":" + "\"" + getDateTimeISO8601(PV1_44_AdmitDateTime) + "\"" + "," 
				+  "\"" +"end" + "\"" + ":" + "\"" + getEndTime(PV1_44_AdmitDateTime) + "\"" + "," 
			    + "\"" + "facility"
				+ "\"" + ":" + "{" + "\"" + "code" + "\"" + ":" + "{" + "\"" + "value" + "\"" + ":"
				+ "\"" + MSH6_ReceivingFacility + "\"" + "}}," 
				+ "\"" + "recordedBy" + "\"" + 
		            " : " + "{" + "\"" + "code" + "\"" + " : " + "{" + "\"" + "value" + "\"" +
		            " : " + "\"" + "2116" + "\"" + "}}" + "," + "\"" + "recordedDate" + "\"" + " : " +
		            "\"" + getDateTimeISO8601(MSH7_DateTimeOfMessage) + "\"" + "}";
				
				
        return strRequest;
    }

    private void initializeDFTSegments() {
        LIS2A2_DFT_Msg LIS2A2DFTMsg= (LIS2A2_DFT_Msg) lis2aMessage;
        headerRecord= LIS2A2DFTMsg.getHL7v24HeaderRecords().size() != 0 ?
            LIS2A2DFTMsg.getHL7v24HeaderRecords().get(0) :
            null;
        /// headerRecord.getEventTrigger()
        patientRecord= LIS2A2DFTMsg.getHL7v24PatientRecords().size() != 0 ?
            LIS2A2DFTMsg.getHL7v24PatientRecords().get(0) :
            null;
        eventTypeRecord= LIS2A2DFTMsg.getHL7v24EventTypeRecords().size() != 0 ?
            LIS2A2DFTMsg.getHL7v24EventTypeRecords().get(0) :
            null;
        visit1Record= LIS2A2DFTMsg.getHL7v24Visit1Records().size() != 0 ?
            LIS2A2DFTMsg.getHL7v24Visit1Records().get(0) :
            null;
        commonOrder= LIS2A2DFTMsg.getHL7_V24_CommonOrderRecords().size() != 0 ?
            LIS2A2DFTMsg.getHL7_V24_CommonOrderRecords().get(0) :
            null;
        financialTransaction= LIS2A2DFTMsg.getHL7v24FinancialTransactionRecords().size() != 0 ?
            LIS2A2DFTMsg.getHL7v24FinancialTransactionRecords().get(0) :
            null;
        OBRRecord= LIS2A2DFTMsg.getObservationRequestRecords().size() != 0 ?
            LIS2A2DFTMsg.getObservationRequestRecords().get(0) :
            null;
    }

    private void setHTTPRequestParam(httpRequstTransaction httpRequestObj, String strRequest,
        VALUDATION_RESULT_TYPE validation_result_type, String note, String msg) {

        System.out.println(strRequest);

        httpRequestObj.setMessageID(headerRecord.getMessageControlId());
        httpRequestObj.setEnviroment("D");

        if (strRequest == "") {
            httpRequestObj.setJSON(strRequest == "" ? "NO JSON CREATED" : strRequest);
            httpRequestObj.getMessageTransaction().setIsValidated(false);
        } else {
            httpRequestObj.getMessageTransaction().setIsValidated(true);
        }

        httpRequestObj.getMessageTransaction().setIsSuccuss(false);
        switch (validation_result_type) {
        case SUCCUSS:
            httpRequestObj.setValudation_result_type(VALUDATION_RESULT_TYPE.SUCCUSS);
            httpRequestObj.getMessageTransaction().setBranchId(machine.getBranchId());
            httpRequestObj.getMessageTransaction().setTenantId(machine.getTenantId());
            // httpRequestObj.getMessageTransaction()
            // .setMessageType(new LkpMessageTransactionType("HL7"));
            httpRequestObj.getMessageTransaction().setNotes(note);
            break;
        case FAILED:
            ERROR_TYPE error= ERROR_TYPE.PARSING;
            httpRequestObj.setValudation_result_type(VALUDATION_RESULT_TYPE.FAILED);
            httpRequestObj.setErrorType(error);
            httpRequestObj.setErrorDesc(msg);
            httpRequestObj.getMessageTransaction().setNotes(httpRequestObj.getErrorDesc());
            // httpRequestObj.getMessageTransaction()
            // .setMessageType(new LkpMessageTransactionType("HL7"));
            httpRequestObj.setAPI_URL_SUFFIX(SUFFIX_API_URL_PATH);
            httpRequestObj.getMessageTransaction().setNotes(httpRequestObj.getErrorDesc());
            break;

        case UNKNOWNTYPE:
            httpRequestObj
                .setMessageID(httpRequestObj.getMessageTransaction().getMessageControlID());
            ERROR_TYPE error2= ERROR_TYPE.UNKNOW_MESSAGE_TYPE;
            httpRequestObj.setValudation_result_type(VALUDATION_RESULT_TYPE.UNKNOWNTYPE);
            httpRequestObj.setErrorType(error2);
            httpRequestObj.setErrorDesc(msg);
            httpRequestObj.getMessageTransaction().setNotes(httpRequestObj.getErrorDesc());
            // httpRequestObj.getMessageTransaction()
            // .setMessageType(new LkpMessageTransactionType("HL7"));
            httpRequestObj.setAPI_URL_SUFFIX(SUFFIX_API_URL_PATH);
            httpRequestObj.getMessageTransaction().setNotes(httpRequestObj.getErrorDesc());
            break;

        }

    }

    private void setHttpRequstPatchParam(httpRequstTransaction httpRequestObj,
        API_URL_PREFEX api_url_prefex,
        String mrn) {
        httpRequestObj.setAPI_URL_PREFIX(api_url_prefex.getValue().format(mrn));

        httpRequestObj.setHttpMethodType(HTTP_REQUESTED_METHOD_TYPE.PATCH);
        httpRequestObj.setSendingFacility(MSH4_SendingFacility);
        httpRequestObj.setReceivingFacility(MSH6_ReceivingFacility);

    }

    private void setHttpRequstPostParam(httpRequstTransaction httpRequestObj,
        API_URL_PREFEX api_url_prefex) {
        httpRequestObj.setAPI_URL_PREFIX(api_url_prefex.getValue());
        httpRequestObj.setHttpMethodType(HTTP_REQUESTED_METHOD_TYPE.POST);
        httpRequestObj.setSendingFacility(MSH4_SendingFacility);
        httpRequestObj.setReceivingFacility(MSH6_ReceivingFacility);

    }

    private void setHttpRequstPostParam(httpRequstTransaction httpRequestObj,
        API_URL_PREFEX api_url_prefex,
        String strRequest) {
        httpRequestObj.setJSON(strRequest);

        httpRequestObj.setAPI_URL_PREFIX(api_url_prefex.getValue());
        httpRequestObj.setHttpMethodType(HTTP_REQUESTED_METHOD_TYPE.POST);
        httpRequestObj.setSendingFacility(MSH4_SendingFacility);
        httpRequestObj.setReceivingFacility(MSH6_ReceivingFacility);

    }

    private String adt_a31_UpdatePatientRequest() throws Exception {
        return "{\r\n"

            + "\"" + "address" + "\"" + " : " + "\"" + PID11_PatientAddress + "\"" + " ," + "\r\n"
            // --------------------//
            + "\"" + "birthDate" + "\"" + " : " + "\"" + getDateTimeISO8601(PID7_DateTimeOfBirth) +
            "\"" + "," + "\r\n"
            // ------------------//
            + "\"" + "countryCode" + "\"" + " : " + "\"" + MSH17_CountryCode + "\"" + "," + "\r\n"
            // ---------------------//
            + "\"" + "fatherName" + "\"" + " : " + "\"" + PID5_2_PatientEnSecondName + "\"" + "," +
            "\r\n"
            // -------------------//
            + "\"" + "firstName" + "\"" + " : " + "\"" + PID5_1_PatientEnFirstName + "\"" + "," +
            "\r\n"
            // ---------------------//
            + "\"" + "gender" + "\"" + " : " + "\"" + PID8_AdministrativeSex + "\"" + "," + "\r\n"
            // ---------------------//
            + "\"" + "isActive" + "\"" + " : " + "true" + " ," + "\r\n"
            // ---------------------//
            + "\"" + "isDonor" + "\"" + " : " +
            (PID35_SpeciesCode.equals("Yes") ? "true" : "false") + " ," + "\r\n"
            // ---------------------//
            + "\"" + "isResearch" + "\"" + " : " + getIsResearch(PID36_BreedCode) + " ," + "\r\n"
            // ---------------------//
            + "\"" + "lastName" + "\"" + " : " + "\"" + PID5_4_PatientEnFamilyName + "\"" + "," +
            "\r\n"
            // ---------------------//
            + getMappedMaritalStatus(PID16_MaritalStatus)
            // ---------------------//
            + "\"" + "middleName" + "\"" + " : " + "\"" + PID5_3_PatientEnThirdName + "\"" + "," +
            "\r\n"
            // ---------------------//
            + "\"" + "modifiedDate" + "\"" + " : " + "\"" +
            getDateTimeISO8601(MSH7_DateTimeOfMessage) + "\"" + "," + "\r\n"
            // ---------------------//
            + "\"" + "nationalIdentityNumber" + "\"" + " : " + "\"" + PID2_PatientID + "\"" + "," +
            "\r\n"
            // ---------------------//
            + "\"" + "nationalityCode" + "\"" + " : " + "\"" + PID28_Nationality + "\"" + "," +
            "\r\n"
            // ---------------------//
            + "\"" + "otherFatherName" + "\"" + " : " + "\"" +
            decodeToArabicName(PID5_6_PatientArSecondName) + "\"" + "," + "\r\n"
            // ---------------------//
            + "\"" + "otherFirstName" + "\"" + " : " + "\"" +
            decodeToArabicName(PID5_5_PatientArFirstName) + "\"" + "," + "\r\n"
            // ---------------------//
            + "\"" + "otherLastName" + "\"" + " : " + "\"" +
            decodeToArabicName(PID5_8_PatientArFamilyName) + "\"" + "," + "\r\n"
            // ---------------------//
            + "\"" + "otherMiddleName" + "\"" + " : " + "\"" +
            decodeToArabicName(PID5_7_PatientArThirdName) + "\"" + "," + "\r\n"
            // ---------------------//
            + "\"" + "phoneNumber" + "\"" + " : " + "\"" + PID13_PhoneNumber_Home + "\"" + "," +
            "\r\n"
            // ---------------------//
            + "\"" + "staffCode" + "\"" + " : " + "\"" + EVN5_OperatorID + "\"" +
            // ---------------------//
            getDonorMedicalRecordNumber(PID21_MotherIdentifier) + getReligion(PID17_Religion) +
            getDoc(PID32_IdentityReliabilityCode, PID19_SSN_Number_Patient) + "\r\n" +

            "}";
    }

    private void adt_a31_UpdatePatientFieldsValidate(httpRequstTransaction httpRequestObj)
        throws Exception {
        if (PID5_1_PatientEnFirstName.isEmpty()) {

            throw new Exception("Error_:_PID_5_1_PatientEnFirstName.Not.Send");
        }

        if (PID5_4_PatientEnFamilyName.isEmpty()) {

            throw new Exception("Error_:_PID5_4_PatientEnFamilyName.Not.Send");
        }

        if (PID5_5_PatientArFirstName.isEmpty()) {

            throw new Exception("Error_:_PID5_5_PatientArFirstName.Not.Send");
        }

        if (PID5_8_PatientArFamilyName.isEmpty()) {

            throw new Exception("Error_:_PID_5_6_PatientArFamilyName.Not.Send");
        }

        if (PID2_PatientID.isEmpty()) {

            throw new Exception("Error_:_PID_2_PatientID.Not.Send");
        }

        if (PID7_DateTimeOfBirth.isEmpty()) {

            throw new Exception("Error_:_PID_7_DateTimeOfBirth.Not.Send");
        }

        if (PID8_AdministrativeSex.isEmpty()) {

            throw new Exception("Error_:_PID_8_AdministrativeSex.Not.Send");
        }

        if (EVN5_OperatorID.isEmpty()) {

            throw new Exception("Error_:_EVN_5_OperatorID.Not.Send");
        }

        if (MSH7_DateTimeOfMessage.isEmpty()) {

            throw new Exception("Error_:_MSH_7_DateTimeOfMessage.Not.Send");
        }
    }

    private String adt_a05_PreAdmissionRequest() throws Exception {
        return "{"

            + "\"" + "admissionDoctor" + "\"" + " : " + "{" + "\"" + "code" + "\"" + ":" + "{" +
            "\"" + "value" + "\"" + ":" + "\"" + PV1_7_AttendingDoctor + "\"" + "}}" + "," + "\"" +
            "expectedAdmitDate" + "\"" + " : " + "\"" +
            getDateTimeISO8601(PV2_8_ExpectedAdmitDateTime) + "\"" + "," + "\"" + "patient" + "\"" +
            " : " + "{" + "\"" + "code" + "\"" + " : " + "{" + "\"" + "value" + "\"" + " :" + "\"" +
            PID3_PatientIdentifierList + "\"" + "}" + "," + "\"" + "name" + "\"" + " : " + "{" +
            "\"" + "firstName" + "\"" + " : " + "\"" + PID5_1_PatientEnFirstName + "\"" + "," +
            "\"" + "fatherName" + "\"" + " : " + "\"" + PID5_2_PatientEnSecondName + "\"" + "," +
            "\"" + "midName" + "\"" + " : " + "\"" + PID5_3_PatientEnThirdName + "\"" + "," + "\"" +
            "lastName" + "\"" + " : " + "\"" + PID5_4_PatientEnFamilyName + "\"" + "," + "\"" +
            "otherFirstName" + "\"" + " : " + "\"" + PID5_5_PatientArFirstName + "\"" + "," + "\"" +
            "otherSecondName" + "\"" + " : " + "\"" + PID5_6_PatientArSecondName + "\"" + "," +
            "\"" + "otherMidName" + "\"" + " : " + "\"" + PID5_7_PatientArThirdName + "\"" + "," +
            "\"" + "otherLastName" + "\"" + " : " + "\"" + PID5_8_PatientArFamilyName + "\"" +
            "}}" + "," + "\"" + "identifier" + "\"" + " : " + "{" + "\"" + "value" + "\"" + " : " +
            "\"" + PV1_50_AlternateVisit + "\"" + "}" + "," + "\"" + "diagnosis" + "\"" + " : " +
            "{" + "\"" + "condition" + "\"" + ":" + "{" + "\"" + "code" + "\"" + " : " + "{" +
            "\"" + "value" + "\"" + " : " + "\"" + DG1_1_SetID_DG1 + "\"" + "}}" + "," + "\"" +
            "type" + "\"" + ":" + "\"" + DG1_2_DiagnosisCodingMethod + "\"" + "}," + "\"" +
            "facility" + "\"" + " : " + "{" + "\"" + "code" + "\"" + " : " + "{" + "\"" + "value" +
            "\"" + " : " + "\"" + MSH6_ReceivingFacility + "\"" + "}}" + "," + "\"" + "recordedBy" +
            "\"" + " : " + "{" + "\"" + "code" + "\"" + " : " + "{" + "\"" + "value" + "\"" +
            " : " + "\"" + "2116" + "\"" + "}}" + "," + "\"" + "recordedDate" + "\"" + " : " +
            "\"" + EVN2_RecordedDateTime + "\"" + "}";
    }

    private String getPatientNamesObj(String strMsgType, String patientEnFirstName,
        String patientEnSecondName,
        String patientEnFamilyName, String patientArFirstName, String patientArSecondName,
        String patientArFamilyName) {

        String strNameObj= "";

        if (strMsgType == "A01") {
            // firstName
            // lastName
            // otherFirstName
            // otherLastName
            try {

                if (patientEnFirstName == "") {
                    throw new Exception("Error_:_PID5_1_PatientEnFirstName_is_Empty");

                } else if (patientEnFirstName != "") {
                    strNameObj+= "\"" + "name" + "\"" + " : " + "{" + "\"" + "firstName" + "\"" +
                        " : " + "\"" + patientEnFirstName + "\"";
                }

                if (patientEnSecondName != "") {
                    strNameObj+= "\"" + "," + "\"" + "fatherName" + "\"" + " : " + "\"" +
                        patientEnSecondName + "\"";
                }

                if (patientEnFamilyName == "") {
                    throw new Exception("Error_:_PID5_4_PatientEnFamilyName_is_Empty");

                } else if (patientEnFamilyName != "") {
                    strNameObj+= "," + "\"" + "lastName" + "\"" + " : " + "\"" +
                        patientEnFamilyName + "\"" + ",";
                }
                if (patientArFirstName == "") {
                    throw new Exception("Error_:_PID5_5_PatientArFirstName_is_Empty");
                } else if (patientArFirstName != "") {
                    strNameObj+= "\"" + "otherFirstName" + "\"" + " : " + "\"" +
                        patientArFirstName + "\"" + ",";
                }

                if (patientArSecondName != "") {
                    strNameObj+= "\"" + "otherSecondName" + "\"" + " : " + "\"" +
                        patientArSecondName + "\"";
                }

                if (patientArFamilyName == "") {
                    throw new Exception("Error_:_PID5_8_PatientArFamilyName_is_Empty");

                } else if (patientArFamilyName != "") {
                    strNameObj+= "\"" + patientArFamilyName + "\"" + "," + "\"" + "otherLastName" +
                        "\"" + " : " + "\"" + PID5_8_PatientArFamilyName + "\"" + "}";
                }

                /*
                 * return "\"" + "name" + "\"" + " : " + "{" + "\"" + "firstName" + "\"" + " : "
                 * + "\"" + PID5_1_PatientEnFirstName + "\"" + "," + "\"" + "fatherName" + "\""
                 * + " : " + "\"" + PID5_2_PatientEnSecondName + "\"" + "," + "\"" + "midName" +
                 * "\"" + " : " + "\"" + "midName" + "\"" + "," + "\"" + "lastName" + "\"" +
                 * " : " + "\"" + PID5_3_PatientEnFamilyName + "\"" + "," + "\"" +
                 * "otherFirstName" + "\"" + " : " + "\"" + PID5_4_PatientArFirstName + "\"" +
                 * "," + "\"" + "otherSecondName" + "\"" + " : " + "\"" +
                 * PID5_5_PatientArSecondName + "\"" + "," + "\"" + "otherMidName" + "\"" +
                 * " : " + "\"" + PID5_5_PatientArSecondName + "\"" + "," + "\"" +
                 * "otherLastName" + "\"" + " : " + "\"" + PID5_6_PatientArFamilyName + "\"" +
                 * "}";
                 */

            } catch (Exception ex) {

            }

        }

        return strNameObj;
    }

    private void adt_a05_PatientPreAdmissionFieldsValidate(httpRequstTransaction httpRequestObj)
        throws Exception {
        if (diagnosisRecord == null) {

            throw new Exception("Error_:_ADT A05 Message Type Without DG1 segment");
        }

        if (eventTypeRecord == null) {

            throw new Exception("Error_:_ADT A05 Message Type Without EVN segment");
        }

        if (visit1Record == null) {

            throw new Exception("Error_:_ADT A05 Message Type Without PV1 segment");
        }
    }

    private String adt_a28_CreatePatientRequest() throws Exception {
        return "{\r\n"

            + "\"" + "mrn" + "\"" + " : " + "\"" + PID3_PatientIdentifierList + "\"" + "," + "\r\n"
            // ---------------------//
            + "\"" + "firstName" + "\"" + " : " + "\"" + PID5_1_PatientEnFirstName + "\"" + "," +
            "\r\n"
            // ---------------------//
            + "\"" + "fatherName" + "\"" + " : " + "\"" + PID5_2_PatientEnSecondName + "\"" + "," +
            "\r\n"
            // ---------------------//
            + "\"" + "middleName" + "\"" + " : " + "\"" + PID5_3_PatientEnThirdName + "\"" + "," +
            "\r\n"
            // ---------------------//
            + "\"" + "lastName" + "\"" + " : " + "\"" + PID5_4_PatientEnFamilyName + "\"" + "," +
            "\r\n"
            // ---------------------//
            + "\"" + "otherFirstName" + "\"" + " : " + "\"" +
            decodeToArabicName(PID5_5_PatientArFirstName) + "\"" + "," + "\r\n"
            // ---------------------//
            + "\"" + "otherFatherName" + "\"" + " : " + "\"" +
            decodeToArabicName(PID5_6_PatientArSecondName) + "\"" + "," + "\r\n"
            // ---------------------//
            + "\"" + "otherMiddleName" + "\"" + " : " + "\"" +
            decodeToArabicName(PID5_7_PatientArThirdName) + "\"" + "," + "\r\n"
            // ---------------------//
            + "\"" + "otherLastName" + "\"" + " : " + "\"" +
            decodeToArabicName(PID5_8_PatientArFamilyName) + "\"" + "," + "\r\n"
            // ---------------------//
            + "\"" + "nationalIdentityNumber" + "\"" + " : " + "\"" + PID2_PatientID + "\"" + "," +
            "\r\n"
            // ---------------------//
            + "\"" + "nationalityCode" + "\"" + " : " + "\"" + PID28_Nationality + "\"" + "," +
            "\r\n"
            // ---------------------//
            + "\"" + "birthDate" + "\"" + " : " + "\"" + getDateTimeISO8601(PID7_DateTimeOfBirth) +
            "\"" + "," + "\r\n"
            // ---------------------//
            + "\"" + "gender" + "\"" + " : " + "\"" + PID8_AdministrativeSex + "\"" + "," + "\r\n"
            // ---------------------//
            + "\"" + "countryCode" + "\"" + " : " + "\"" + MSH17_CountryCode + "\"" + "," + "\r\n"
            // ---------------------//
            + "\"" + "phoneNumber" + "\"" + " : " + "\"" + PID13_PhoneNumber_Home + "\"" + "," +
            "\r\n"
            // ---------------------//
            + getMappedMaritalStatus(PID16_MaritalStatus)
            // ---------------------//
            + "\"" + "recordedDate" + "\"" + " : " + "\"" +
            getDateTimeISO8601(MSH7_DateTimeOfMessage) + "\"" + "," + "\r\n"
            // ---------------------//
            + "\"" + "staffCode" + "\"" + " : " + "\"" + EVN5_OperatorID + "\"" + "," + "\n"
            // ---------------------//
            + "\"" + "isActive" + "\"" + " : " + "true" + " ," + "\r\n"
            // ---------------------//
            + "\"" + "isDonor" + "\"" + " : " +
            (PID21_MotherIdentifier.equals("") ? "false" : "true") + " ," + "\r\n"
            // ---------------------//
            + "\"" + "isResearch" + "\"" + " : " + getIsResearch(PID36_BreedCode) + " ," + "\r\n"
            // ---------------------//
            + "\"" + "address" + "\"" + " : " + "\"" + PID11_PatientAddress + "\"" + " ," + "\r\n"
            // ---------------------//
            + "\"" + "facilityCode" + "\"" + " : " + "\"" + MSH4_SendingFacility + "\"" +
            getDonorMedicalRecordNumber(PID21_MotherIdentifier) + getReligion(PID17_Religion) +
            getDoc(PID32_IdentityReliabilityCode, PID19_SSN_Number_Patient) + "\r\n" +

            "}";
    }

    private String getDoc(String pID32_IdentityReliabilityCode2, String pID19_SSN_Number_Patient2) {
        // TODO Auto-generated method stub
        if (pID19_SSN_Number_Patient2.isEmpty() || pID32_IdentityReliabilityCode2.isEmpty())
            return "";
        else
            return "\r\n" + "," + "\"" + "documentReference" + "\"" + ":" + "{" + "\r\n" + "\"" +
                "type" + "\"" + ":" + "{" + "\r\n" + "\"" + "code" + "\"" + ":" + "{" + "\r\n" +
                "\"" + "value" + "\"" + ":" + "\"" + PID32_IdentityReliabilityCode + "\"" + "\r\n" +
                "}" + "\r\n" + "}," + "\r\n" + "\"" + "identifier" + "\"" + ":" + "{" + "\r\n" +
                "\"" + "value" + "\"" + ":" + "\"" + PID19_SSN_Number_Patient + "\"" + "\r\n" +

                "}" + "\r\n" + "}";

    }

    private String getReligion(String pID17_Religion2) {

        if (pID17_Religion2.isEmpty() || pID17_Religion2.equals(""))
            return "";
        else
            return "," + "\r\n" + "\"" + "religion" + "\"" + ": {" + "\r\n" + "\"" + "code" + "\"" +
                ": {" + "\r\n" + "\"" + "value" + "\"" + ":" + "\"" + pID17_Religion2 + "\"" +
                "\r\n" + "}" + "\r\n";

    }

    private String getDonorMedicalRecordNumber(String pID21_MotherIdentifier) {
        if (!pID21_MotherIdentifier.equals("")) {

            return "," + "\"" + "donorMrn" + "\"" + ":" + "\"" + pID21_MotherIdentifier + "\"";
        }

        return "";

    }

    private String getIsResearch(String pID35) {
        if (pID35.equals("Yes")) {

            return "true";
        }

        return "false";

    }

    private void adt_a28_CreatePatientFieldsValidate(httpRequstTransaction httpRequestObj)
        throws Exception {
        // birthDate
        // facilityCode
        // firstName
        // lastName
        // otherFirstName
        // otherLastName
        // gender
        // isActive //??
        // isDonor //PID 21
        // isResearch // PID 36
        // mrn
        // nationalIdentityNumber
        // recordedDate
        // staffCode

        if (PID7_DateTimeOfBirth.isEmpty()) {

            throw new Exception("Error_:_PID7_DateTimeOfBirth_is_empty");
        }

        if (MSH4_SendingFacility.isEmpty()) {

            throw new Exception("Error_:_MSH_4_SendingFacility.is.null");
        }

        // Patient File number
        if (PID3_PatientIdentifierList.isEmpty()) {

            throw new Exception("Error_:_PID_3_PatientIdentifier_is_empty");
        }
        if (PID5_1_PatientEnFirstName.isEmpty()) {

            throw new Exception("Error_:_PID_5_1_PatientEnFirstName_is_empty");
        }
        if (PID5_4_PatientEnFamilyName.isEmpty()) {

            throw new Exception("Error_:_PID_5_4_PatientEnFamilyName_is_empty");
        }
        if (PID5_5_PatientArFirstName.isEmpty()) {

            throw new Exception("Error_:_PID_5_5_PatientArFirstName_is_empty");
        }
        if (PID5_8_PatientArFamilyName.isEmpty()) {

            throw new Exception("Error_:_PID5_8_PatientArFamilyName_is_empty");
        }
        if (PID2_PatientID.isEmpty()) {

            throw new Exception("Error_:_PID_2_PatientID_is_empty");
        }

        if (PID8_AdministrativeSex.isEmpty()) {

            throw new Exception("Error_:_PID_8_AdministrativeSex_is_empty");
        }

        if (EVN5_OperatorID.isEmpty()) {

            throw new Exception("Error_:_EVN_5_OperatorID_is_empty");
        }

        if (MSH7_DateTimeOfMessage.isEmpty()) {

            throw new Exception("Error_:_MSH7_DateTimeOfMessage_is_empty");
        }

    }

    private String adt_a08_UpdateVisitRequest() throws Exception {
        return "{" + "\r\n" + "\"" + "admitDate" + "\"" + ":" + "\"" +
            getDateTimeISO8601(PV1_44_AdmitDateTime) + "\"" + "," 
        		
            
            + "\"" + "AdmissionDoctor" + "\"" + ":" +
            "{" + "\r\n" + "\"" + "code" + "\"" + ":" + "{" + "\r\n" + "\"" + "value" + "\"" + ":" +
            "\"" + PV1_17_1_AdmittingDoctorCode + "\"" + "\r\n" + "}" + "\r\n" + "}," + 
            
  			getAttendingDoctor(PV1_7_1_AttendingDoctorCode) +
  			getConsultingDoctor(PV1_9_1_ConsultingDoctorCode) +
  			getReferringDoctor(PV1_8_1_ReferringDoctorCode) +
            
            "\r\n" +
            "\"" + "facility" + "\"" + ":" + "{" + "\r\n" + "\"" + "code" + "\"" + ":" + "{" +
            "\r\n" + "\"" + "value" + "\"" + ":" + "\"" + MSH4_SendingFacility + "\"" + "\r\n" +
            "}" + "\r\n" + "}," + "\r\n" + "\"" + "patient" + "\"" + ":" + "{" + "\r\n" + "\"" +
            "code" + "\"" + ":" + "{" + "\r\n" + "\"" + "value" + "\"" + ":" + "\"" +
            PID3_PatientIdentifierList + "\"" + "\r\n" + "}," + "\r\n" + "\"" + "name" + "\"" +
            ":" + "{" + "\r\n" + "\"" + "firstName" + "\"" + ":" + "\"" +
            PID5_1_PatientEnFirstName + "\"" + "," + "\r\n" + "\"" + "fatherName" + "\"" + " : " +
            "\"" + PID5_2_PatientEnSecondName + "\"" + "," + "\r\n" + "\"" + "midName" + "\"" +
            " : " + "\"" + PID5_2_PatientEnSecondName + "\"" + "," + "\r\n" + "\"" + "lastName" +
            "\"" + " : " + "\"" + PID5_4_PatientEnFamilyName + "\"" + "," + "\r\n" + "\"" +
            "otherFirstName" + "\"" + " : " + "\"" + PID5_5_PatientArFirstName + "\"" + "," +
            "\r\n" + "\"" + "otherFatherName" + "\"" + " : " + "\"" + PID5_6_PatientArSecondName +
            "\"" + "," + "\r\n" + "\"" + "otherMidName" + "\"" + " : " + "\"" +
            PID5_7_PatientArThirdName + "\"" + "," + "\r\n" + "\"" + "otherLastName" + "\"" +
            " : " + "\"" + PID5_8_PatientArFamilyName + "\"" + "\r\n" + "}}" + "," + "\r\n" + "\"" +
            "recordedBy" + "\"" + ":" + "{" + "\r\n" + "\"" + "code" + "\"" + ":" + "{" + "\r\n" +
            "\"" + "value" + "\"" + ":" + "\"" + EVN5_OperatorID + "\"" + "\r\n" + "}}," + "\r\n" +
            "\"" + "recordedDate" + "\"" + ":" + "\"" + getDateTimeISO8601(EVN2_RecordedDateTime) +
            "\"" + "\r\n" + getDiagnosisDetails(DG1_1_SetID_DG1, DG1_2_DiagnosisCodingMethod) +
            getDischargeDateTime(PV1_45_DischargeDateTime) + "}";
    }

    private void adt_a08_UpdateVisitFieldsValidate(httpRequstTransaction httpRequestObj)
        throws Exception {
        if (PV1_44_AdmitDateTime.isEmpty()) {
            System.out.println("PV144_AdmitDateTime is null");

            throw new Exception("ERROR:_PV1_44_AdmitDateTime.field.is.null");
        }
        if (MSH4_SendingFacility.isEmpty()) {
            System.out.println("MSH6_ReceivingFacility.is.null");

            throw new Exception("ERROR:_MSH6_ReceivingFacilit.field.is.null");
        }
        if (PID3_PatientIdentifierList.isEmpty()) {
            System.out.println("PID3_PatientIdentifierList.is.null");

            throw new Exception("ERROR:_PID3_PatientIdentifierList.field.is.null");
        }
        if (EVN5_OperatorID.isEmpty()) {
            System.out.println("EVN5_OperatorID.is.null");

            throw new Exception("ERROR:_EVN5_OperatorID.field.is.null");
        }
        if (EVN2_RecordedDateTime.isEmpty()) {
            System.out.println("EVN2_RecordedDateTime is null");

            throw new Exception("ERROR:_EVN_2_RecordedDateTime field is null");
        }
        if (PV1_17_1_AdmittingDoctorCode.isEmpty()) {
            System.out.println("PV1_17_1_AdmittingDoctorCode is null");

            throw new Exception("ERROR:PV1_17_1_AdmittingDoctorCode.field.is.null");
        }
        if (PID5_1_PatientEnFirstName.isEmpty()) {
            System.out.println("PID5_1_PatientEnFirstName is null");

            throw new Exception("ERROR:_PID5_1_PatientEnFirstName.field.is.null");

        }
        if (PID5_4_PatientEnFamilyName.isEmpty()) {
            System.out.println("PID5_4_PatientEnFamilyName is null");

            throw new Exception("ERROR:_PID5_3_PatientEnFamilyName.field.is.null");
        }
        if (PV1_19_VisitNumber.isEmpty()) {
            System.out.println("PV1_19_VisitNumber is null");

            throw new Exception("ERROR:_PV1_19_VisitNumber.field.is.null");
        }
    }

    private String adt_a_11_CancelAdmissionRequest() throws Exception {
        return "{\r\n" + "\"" + "facility" + "\"" + " : " + "{" + "\r\n" + "\"" + "code" + "\"" +
            " : " + "{" + "\r\n" + "\"" + "value" + "\"" + " : " + "\"" + MSH4_SendingFacility +
            "\"" + "}}" + "," + "\r\n" + "\"" + "recordedBy" + "\"" + " : " + "{" + "\r\n" + "\"" +
            "code" + "\"" + ":" + "{" + "\r\n" + "\"" + "value" + "\"" + ":" + "\"" +
            EVN5_OperatorID + "\"" + "}}" + "," + "\r\n" + "\"" + "recordedDate" + "\"" + " : " +
            "\"" + getDateTimeISO8601(EVN2_RecordedDateTime) + "\"" + "\r\n" + "}";
    }

    private void adt_a_11_CancelAdmissionFieldsValudate(httpRequstTransaction httpRequestObj)
        throws Exception {
        if (EVN5_OperatorID.isEmpty()) {
            System.out.println("EVN_5_OperatorID_is_null");

            throw new Exception("ERROR:_EVN_5_OperatorID_is_null.field.is.null");
        }
        if (EVN2_RecordedDateTime.isEmpty()) {
            System.out.println("EVN2_RecordedDateTime_is_null");

            throw new Exception("ERROR:_EVN2_RecordedDateTime_is_null.field.is.null");
        }
        if (PV1_44_AdmitDateTime.isEmpty()) {
            System.out.println("PV1_44_AdmitDateTime_is_null is null");

            throw new Exception("ERROR:_PV1_44_AdmitDateTime.field.is.null");
        }
        if (MSH6_ReceivingFacility.isEmpty()) {
            System.out.println("MSH6_ReceivingFacility.is.null");

            throw new Exception("ERROR:_MSH6_ReceivingFacilit.field.is.null");
        }
        if (PID3_PatientIdentifierList.isEmpty()) {
            System.out.println("PID3_PatientIdentifierList.is.null");

            throw new Exception("ERROR:_PID3_PatientIdentifierList.field.is.null");
        }
        if (EVN5_OperatorID.isEmpty()) {
            System.out.println("EVN5_OperatorID.is.null");

            throw new Exception("ERROR:_EVN5_OperatorID.field.is.null");
        }
        if (EVN2_RecordedDateTime.isEmpty()) {

            throw new Exception("ERROR:_MSH7_DateTimeOfMessage.field.is.null");
        }
        if (PV1_19_VisitNumber.isEmpty()) {
            System.out.println("EVN2_RecordedDateTime is null");

            throw new Exception("ERROR:_PV1_19_VisitNumber.field.is.null");
        }
    }

    private String adt_a04CreateVisitRequst() throws Exception {
        return "{ " + "\"" + "type" + "\"" + " : " + "\"" +
            getAdmissionTypeID(PV1_4_AdmissionType) + "\"" + "," + "\"" + "doctor" + "\"" + " : " +
            "{" + "\"" + "code" + "\"" + " : " + "{" + "\"" + "value" + "\"" + " : " + "\"" +
            PV1_7_1_AttendingDoctorCode + "\"" + " }}," + ""
            ////////////////////////////////////////////////////////
            + "\"" + "facility" + "\"" + " : " + "{" + "" + "\"" + "code" + "\"" + " : " + "{" +
            "" + "\"" + "value" + "\"" + " : " + "\"" + MSH4_SendingFacility + "\"" + "}}" + ","
            ////////////////////////////////////////////////////////
            + "\"" + "identifier" + "\"" + " : " + "{" + "\"" + "value" + "\"" + " : " + "\"" +
            PV1_19_VisitNumber + "\"" + "}," + ""
            ///////////////////////////////////////////////////////
            + "\"" + "patient" + "\"" + " : " + "{" + "" + "\"" + "code" + "\"" + " : " + "{" + "" +
            "\"" + "value" + "\"" + " : " + "\"" + PID3_PatientIdentifierList + "\"" + "}" + "," +
            "" + "\"" + "name" + "\"" + " : " + "{" + "" + "\"" + "firstName" + "\"" + " : " +
            "\"" + "A" + "\"" + "," + "" + "\"" + "fatherName" + "\"" + " : " + "\"" +
            PID5_2_PatientEnSecondName + "\"" + "," + "" + "\"" + "" + "midName" + "\"" + " : " +
            "\"" + PID5_2_PatientEnSecondName + "\"" + "," + "" + "\"" + "lastName" + "\"" + " : " +
            "\"" + "A" + "\"" + "," + "" + "\"" + "otherFirstName" + "\"" + " : " + "\"" +
            decodeToArabicName(PID5_5_PatientArFirstName) + "\"" + "," + "" + "\"" +
            "otherFatherName" + "\"" + " : " + "\"" +
            decodeToArabicName(PID5_6_PatientArSecondName) + "\"" + "," + "" + "\"" +
            "otherMidName" + "\"" + " : " + "\"" + decodeToArabicName(PID5_7_PatientArThirdName) +
            "\"" + "," + "" + "\"" + "otherLastName" + "\"" + " : " + "\"" +
            decodeToArabicName(PID5_8_PatientArFamilyName) + "\"" + "}}" + "," + "" + "" + "\"" +
            "recordedBy" + "\"" + " : " + "{" + "" + "\"" + "code" + "\"" + ":" + "{" + "" + "\"" +
            "value" + "\"" + ":" + "\"" + EVN5_OperatorID + "\"" + "}}" + "," + "" + "\"" +
            "recordedDate" + "\"" + " : " + "\"" + getDateTimeISO8601(EVN2_RecordedDateTime) +
            "\"" + "," + "" + "\"" + "section" + "\"" + ":" + "{" + "" + "\"" + "code" + "\"" +
            ":" + "{" + "" + "\"" + "value" + "\"" + ":" + "\"" +
            PV1_3_1_AssignedPatient_PointOfCare + "\"" + "}}," + "" + "\"" + "source" + "\"" +
            " : " + "\"" + getAssignedPatientLocation(PV1_2_PatientClass) + "\"" + "," + "" + "\"" +
            "visitDate" + "\"" + " : " + "\"" + getDateTimeISO8601(EVN2_RecordedDateTime) + "\"" +
            AlternativeAppointmentID(PID50_Alternative_AppointmentID) + "}";
    }

    private String AlternativeAppointmentID(String pID50_Alternative_AppointmentID2) {

        if (!pID50_Alternative_AppointmentID2.equals("")) {
            return "," + "\"" + "appointment" + "\"" + ":" + "{" + "\"" + "identifier" + "\"" +
                ":" + "{" + "\"" + "value" + "\"" + ":" + "\"" + pID50_Alternative_AppointmentID2 +
                "\"" + "}}";
        }

        return "";

    }

    private String adt_a02_CreateAdmissionTransferRequst()throws Exception {
        return "{" + "\"" + "bed" + "\"" + " : " + "{" + "" + "\"" + "code" + "\"" + ":" + "{" +
        "\"" + "value" + "\"" + ":" + "\"" + PV1_3_2_AssignedPatient_Room + "\"" + "}}," +
        "\"" + "remark" + "\"" + " : " + "\"" + PV1_50_AlternateVisit + "\"" + "," + "\"" +
        "transferReason" + "\"" + ":" + "{" + "" + "\"" + "code" + "\"" + ":" + "{" + "" +
        "\"" + "value" + "\"" + ":" + "\"" + PV1_20_FinancialClass + "\"" + "}}" + "," + "" +
        "\"" + "facility" + "\"" + " : " + "{" + "" + "\"" + "code" + "\"" + " : " + "{" + "" +
        "\"" + "value" + "\"" + " : " + "\"" + MSH4_SendingFacility + "\"" + "}}" + "," + "" +
        "\"" + "recordedBy" + "\"" + " : " + "{" + "" + "\"" + "code" + "\"" + ":" + "{" + "" +
        "\"" + "value" + "\"" + ":" + "\"" + EVN5_OperatorID + "\"" + "}}" + "," + "" + "\"" +
        "recordedDate" + "\"" + " : " + "\"" + getDateTimeISO8601(EVN2_RecordedDateTime) +
        "\"" + getActualClass(PV1_21_ChargePriceIndicator.replace(' ', '_')) +
       // getSection(PV1_3_1_AssignedPatient_PointOfCare) +
        //getRoom(PV1_3_2_AssignedPatient_Room) +
        "}";
    }

    private String getRequestedClass(String pV1_21_ChargePriceIndicator) {
        // TODO Auto-generated method stub
    	
    	certacureAdmissionClassService = (CertacureAdmissionClassService) SpringUtil.getBean("CertacureAdmissionClassService");
    	List<CertacureAdmissionClass> AdmissionClassList ;
    	
    	AdmissionClassList = certacureAdmissionClassService.getCertacureAdmissionCode(pV1_21_ChargePriceIndicator);
    	
    	
        if (AdmissionClassList != null)
            return "," + "\"" + "requestedClass" + "\"" + ":" + "{" + "\r\n" +
            "\"" + "code" + "\"" + ": {" + "\r\n" +
            "\"" + "value" + "\"" + ":" + "\"" + AdmissionClassList.get(0).getCode() + "\"" + "\r\n" +
            "}" + "\r\n" +
            "}";
        else
            return "Actual Class Not Found";

    }

    
    private String getActualClass(String pV1_21_ChargePriceIndicator) {
        // TODO Auto-generated method stub
    	
    	certacureAdmissionClassService = (CertacureAdmissionClassService) SpringUtil.getBean("CertacureAdmissionClassService");
    	List<CertacureAdmissionClass> AdmissionClassList ;
    	
    	AdmissionClassList = certacureAdmissionClassService.getCertacureAdmissionCode(pV1_21_ChargePriceIndicator);
    	
    	
        if (AdmissionClassList != null)
            return "," + "\"" + "actualClass" + "\"" + ":" + "{" + "\r\n" +
            "\"" + "code" + "\"" + ": {" + "\r\n" +
            "\"" + "value" + "\"" + ":" + "\"" + AdmissionClassList.get(0).getCode() + "\"" + "\r\n" +
            "}" + "\r\n" +
            "}";
        else
            return "Actual Class Not Found";

    }

    private String getChargePriceIndicatorCode(String strAdmissionClassTitle) {
    	
    	lkpAdmissionClass = lkpService.findOneAnyLkp(

    	            java.util.Arrays.asList(new SearchCriterion("name", strAdmissionClassTitle, FilterOperator.eq)),

    	            LkpMessageTransactionDirection.class);
    	
    	if (lkpAdmissionClass != null)
    	{
    		return lkpAdmissionClass.getCode();
    	}else
    	{
    		return null;
    	}
	}

	private String getRoom(String pV1_3_2_AssignedPatient_Room) {

        if (!pV1_3_2_AssignedPatient_Room.equals("")) {
            return "," + "\"" + "room" + "\"" + " : " + "{" + "" + "\"" + "code" + "\"" + ":" +
                "{" + "" + "\"" + "value" + "\"" + ":" + "\"" + pV1_3_2_AssignedPatient_Room +
                "\"" + "}}";
        }

        return "";

    }

    private String getSection(String pV1_3_1_AssignedPatient_PointOfCare) {
        if (!pV1_3_1_AssignedPatient_PointOfCare.equals("")) {
            return "," + "\"" + "section" + "\"" + ":" + "{" + "" + "\"" + "code" + "\"" + ":" +
            "{" +
            "" + "\"" + "value" + "\"" + ":" + "\"" + pV1_3_1_AssignedPatient_PointOfCare +
            "\"" + "}}";
        }

        return "";

    }

    private void adt_a02_ValidateAdmissionTransferFields(httpRequstTransaction httpRequestObj)
        throws Exception {

        if (PV1_3_2_AssignedPatient_Room.isEmpty()) {
            System.out.println("PV1_3_2_AssignedPatient_Room is null");
            throw new Exception("ERROR:PV1_3_2_AssignedPatient_Room.field.is.null");
        }
        if (MSH6_ReceivingFacility.isEmpty()) {
            System.out.println("MSH6_ReceivingFacility.is.null");

            throw new Exception("ERROR:_MSH6_ReceivingFacilit.field.is.null");
        }

        if (EVN5_OperatorID.isEmpty()) {
            System.out.println("EVN5_OperatorID.is.null");

            throw new Exception("ERROR:_EVN5_OperatorID.field.is.null");
        }
        if (EVN2_RecordedDateTime.isEmpty()) {
            System.out.println("EVN2_RecordedDateTime is null");

            throw new Exception("ERROR:_EVN_2_RecordedDateTime field is null");
        }

        if (EVN2_RecordedDateTime.isEmpty()) {
            System.out.println("EVN2_RecordedDateTime.is.null");

            throw new Exception("ERROR:_EVN2_RecordedDateTime.field.is.null");
        }
        if (PV1_50_AlternateVisit.isEmpty()) {
            System.out.println("PV1_50_AlternateVisit.is.null");

            throw new Exception("ERROR:_PV1_50_AlternateVisit.field.is.null");
        }
        if (PV1_19_VisitNumber.isEmpty()) {

            throw new Exception("ERROR:_PV1_19_VisitNumber.field.is.null");
        }
    }

    private String adt_a01_CreateAdmissionRequest()throws Exception {
        return "{" +
        
        getAdmissionDoctor(PV1_7_1_AttendingDoctorCode) +
        getAttendingDoctor(PV1_7_1_AttendingDoctorCode) +
        getConsultingDoctor(PV1_9_1_ConsultingDoctorCode) +
        getReferringDoctor(PV1_8_1_ReferringDoctorCode) +
        
        
        getAdmitDateTime(PV1_44_AdmitDateTime) +
        getBedDetails(PV1_3_2_AssignedPatient_Room) + "," +
        getDiagnosisDetails(DG1_1_SetID_DG1, DG1_2_DiagnosisCodingMethod) + "\"" + "facility" +
        "\"" + ":" + "{" + "\"" + "code" + "\"" + ":" + "{" + "\"" + "value" + "\"" + ":" +
        "\"" + MSH4_SendingFacility + "\"" + "}}" + "," + "\"" + "identifier" + "\"" + " : " +
        "{" + "\"" + "value" + "\"" + " : " + "\"" + PV1_19_VisitNumber + "\"" + "}" +
        getActualLengthOfInpatientStay(PV2_11_ActualLengthofInpatientStay) + "," + "\"" +
        "patient" + "\"" + ":" + "{" + "\"" + "code" + "\"" + ":" + "{" + "\"" + "value" +
        "\"" + ":" + "\"" + PID3_PatientIdentifierList + "\"" + "}" + "," + "\"" + "name" +
        "\"" + ":" + "{" + "\"" + "firstName" + "\"" + ":" + "\"" + PID5_1_PatientEnFirstName +
        "\"" + "," + "\"" + "fatherName" + "\"" + ":" + "\"" + PID5_2_PatientEnSecondName +
        "\"" + "," + "\"" + "" + "midName" + "\"" + ":" + "\"" + PID5_3_PatientEnThirdName +
        "\"" + "," + "\"" + "lastName" + "\"" + ":" + "\"" + PID5_4_PatientEnFamilyName + "\"" +
        "," + "\"" + "otherFirstName" + "\"" + ":" + "\"" +
        "NOT_RECIVED_AR_FIRST_NAME" + "\"" + "," + "\"" + "otherFatherName" +
        "\"" + ":" + "\"" + "NOT_RECIVED_AR_SECOND_NAME" + "\"" + "," + "\"" +
        "otherMidName" + "\"" + ":" + "\"" + "NOT_RECIVED_AR_THIRD_NAME" +
        "\"" + "," + "\"" + "otherLastName" + "\"" + ":" + "\"" +
        "NOT_RECIVED_AR_FAMILY_NAME" + "\"" + "}}" + "," +
        getPreaddmissionDetails(PV1_5_PreadmitNumber) + "\"" + "recordedBy" + "\"" + ":" + "{" +
        "\"" + "code" + "\"" + ":" + "{" + "\"" + "value" + "\"" + ":" + "\"" +
        EVN5_OperatorID + "\"" + "}}" + "," + "\"" + "recordedDate" + "\"" + ":" + "\"" +
        getDateTimeISO8601(EVN2_RecordedDateTime) + "\"" + 
        //getAssignRoomDetails(PV1_3_2_AssignedPatient_Room) +
       // getAssignPointOfCare(PV1_3_1_AssignedPatient_PointOfCare) +
       getRequestedClass(PV1_21_ChargePriceIndicator)
        + "}";
    }

	private String getAdmitDateTime(String pV1_44_AdmitDateTime2) throws Exception {
		return "\"" + "admitDate" + "\"" + ":" + "\"" +  getDateTimeISO8601(PV1_44_AdmitDateTime) + "\"";
	}

	private String getReferringDoctor(String pV1_8_1_ReferringDoctorCode) {
		if (pV1_8_1_ReferringDoctorCode.equals("")) {
			return "";
		}

		return "\"" + "referringDoctor" + "\"" + ":" + "{" + "\""  +"code" +"\"" + ":" + "{" + "\""  + "value" + "\"" 
				+ pV1_8_1_ReferringDoctorCode + "\"" + "}},";
	}

	private String getConsultingDoctor(String pV1_9_1_ConsultingDoctorCode) {
		if (pV1_9_1_ConsultingDoctorCode.equals("")) {
			return "";
		}

		return "\"" + "consultingDoctor" + "\"" + ":" + "{" + "\""  +"code" +"\"" + ":" + "{" + "\""  + "value" + "\"" + ":" + "\""
				+ pV1_9_1_ConsultingDoctorCode + "\"" + "}},";
	}

	private String getAttendingDoctor(String pV1_7_1_AttendingDoctorCode) {
		if (pV1_7_1_AttendingDoctorCode.equals("")) {
			return "";
		}

		return "\"" + "attendingDoctor" + "\"" + ":" + "{" + "\""  +"code" +"\"" + ":" + "{" + "\""  + "value" + "\"" + ":" + "\""
				+ pV1_7_1_AttendingDoctorCode + "\"" + "}},";
	}

	private String getAdmissionDoctor(String pV1_doctor) 
	{
		return "\"" + "admissionDoctor" + "\"" + ":" + "{" + "\"" + "code" + "\"" + ":" + "{" + "\"" + "value" + "\""
				+ ":" + "\"" + pV1_doctor + "\"" + "}}" +
				 ",";
	}

	private String getBedDetails(String pV1_3_2_AssignedPatient_Room) {

        if (!pV1_3_2_AssignedPatient_Room.equals("")) {
            return "," + "\"" + "bed" + "\"" + ":" + "{" + "\"" + "code" + "\"" + ":" + "{" + "\"" +
                "value" + "\"" + ":" + "\"" + pV1_3_2_AssignedPatient_Room + "\"" + "}}";
        } else {
            return "";
        }
    }

    private String getAssignPointOfCare(String pV1_3_1_AssignedPatient_PointOfCare) {
        if (pV1_3_1_AssignedPatient_PointOfCare == "" ||
            pV1_3_1_AssignedPatient_PointOfCare == null) {
            return "";
        } else {
            return "," + "\"" + "section" + "\"" + ":" + "{" + "\r\n" + "\"" + "code" + "\"" + ":" +
                "{" + "\r\n" + "\"" + "value" + "\"" + ":" + "\"" +
                PV1_3_1_AssignedPatient_PointOfCare + "\"" + "}}";
        }
    }

    private String getAssignRoomDetails(String pV1_3_2_AssignedPatient_Room2) {

        if (pV1_3_2_AssignedPatient_Room2 == "" || pV1_3_2_AssignedPatient_Room2 == null) {
            return "";
        } else {
            return "," + "\"" + "room" + "\"" + " : " + "{" + "\r\n" + "\"" + "code" + "\"" + ":" +
                "{" + "\r\n" + "\"" + "value" + "\"" + ":" + "\"" + PV1_3_2_AssignedPatient_Room +
                "\"" + "}}";
        }

    }

    private void adt_a01_valudateAdmissionFields(httpRequstTransaction httpRequestObj)
        throws Exception {

        // admissionDoctor
        // admitDate
        // bed
        // Facility
        // Identifier
        // Patient
        // RecordedBy
        // RecordedDate

        if (PV1_17_1_AdmittingDoctorCode.isEmpty()) {
            System.out.println("PV1_17_1_AdmittingDoctorCode is null");

            throw new Exception("ERROR:PV1_17_1_AdmittingDoctorCode");
        }
        if (PV1_44_AdmitDateTime.isEmpty()) {
            System.out.println("PV144_AdmitDateTime_field_is_empty");

            throw new Exception("ERROR:_PV1_44_AdmitDateTime_field_is_empty");

        }
        if (PV1_3_2_AssignedPatient_Room.isEmpty()) {
            System.out.println("PV1_3_2_AssignedPatient_Room_field_is_empty");
            throw new Exception("ERROR:PV1_3_2_AssignedPatient_Room");
        }
        if (MSH6_ReceivingFacility.isEmpty()) {
            System.out.println("MSH6_ReceivingFacility_field_is_empty");

            throw new Exception("ERROR:_MSH6_ReceivingFacilit_field_is_empty");
        }
        if (PID3_PatientIdentifierList.isEmpty()) {
            System.out.println("PID3_PatientIdentifierList_field_is_empty");

            throw new Exception("ERROR:_PID3_PatientIdentifierList_field_is_empty");
        }
        if (EVN5_OperatorID.isEmpty()) {
            System.out.println("EVN5_OperatorID.is.null");

            throw new Exception("ERROR:_EVN5_OperatorID_field_is_empty");
        }
        if (EVN2_RecordedDateTime.isEmpty()) {
            System.out.println("EVN2_RecordedDateTime_field_is_empty");

            throw new Exception("ERROR:_EVN_2_RecordedDateTime_field_is_empty");
        }
        if (PID5_1_PatientEnFirstName.isEmpty()) {

            throw new Exception("Error_:_PID_5_1_PatientEnFirstName_is_empty");
        }
        if (PID5_4_PatientEnFamilyName.isEmpty()) {

            throw new Exception("Error_:_PID_5_4_PatientEnFamilyName_is_empty");
        }
        /*
         * if (PV1_3_2_AssignedPatient_Room.isEmpty()) {
         * System.out.println("PV1_3_2_AssignedPatient_Room_field_is_empty");
         * 
         * 
         * throw new Exception("ERROR:_PV1_3_2_AssignedPatient_Room_field_is_empty"); }
         * if (PV1_3_1_AssignedPatient_PointOfCare.isEmpty()) {
         * System.out.println("PV1_3_1_AssignedPatient_PointOfCare_field_is_empty");
         * 
         * 
         * throw new Exception(
         * "ERROR:_PV1_3_1_AssignedPatient_PointOfCare_field_is_empty"); }
         */
    }

    private void setEVN(HL7_V24_EventTypeRecord eventTypeRecord) {

        if (eventTypeRecord != null) {
            EVN1_EventTypeCode= eventTypeRecord.getEventTypeCode();
            EVN2_RecordedDateTime= eventTypeRecord.getRecordedDateTime();
            EVN3_DateTimePlannedEvent= eventTypeRecord.getDateTimePlannedEvent();
            EVN4_EventReasonCode= eventTypeRecord.getEventReasonCode();
            EVN5_OperatorID= eventTypeRecord.getOperatorID();
            EVN6_EventOccurred= eventTypeRecord.getEventOccurred();
            EVN7_EventFacility= eventTypeRecord.getEventFacility();
        } else {
            EVN5_OperatorID= "Vista";
        }

    }

    private void initializeADTSegments() {
        LIS2A2_ADT_Msg lis2a2adt= (LIS2A2_ADT_Msg) lis2aMessage;
        headerRecord= lis2a2adt.getHL7v24HeaderRecords().size() != 0 ?
            lis2a2adt.getHL7v24HeaderRecords().get(0) :
            null;
        /// headerRecord.getEventTrigger()
        patientRecord= lis2a2adt.getHL7v24PatientRecords().size() != 0 ?
            lis2a2adt.getHL7v24PatientRecords().get(0) :
            null;
        diagnosisRecord= lis2a2adt.getHL7v24DiagnosisRecords().size() != 0 ?
            lis2a2adt.getHL7v24DiagnosisRecords().get(0) :
            null;
        eventTypeRecord= lis2a2adt.getHL7v24EventTypeRecords().size() != 0 ?
            lis2a2adt.getHL7v24EventTypeRecords().get(0) :
            null;
        visit1Record= lis2a2adt.getHL7v24Visit1Records().size() != 0 ?
            lis2a2adt.getHL7v24Visit1Records().get(0) :
            null;
        visit2Record= lis2a2adt.getHL7v24Visit2Records().size() != 0 ?
            lis2a2adt.getHL7v24Visit2Records().get(0) :
            null;

        if (diagnosisRecord != null) {
            DG1_1_SetID_DG1= diagnosisRecord.getDG1_ID();
            DG1_2_DiagnosisCodingMethod= diagnosisRecord.getDG1_2_DiagnosisCodingMethod();
        } else {
            DG1_1_SetID_DG1= "";
            DG1_2_DiagnosisCodingMethod= "";
        }

    }

    private String siu_s12_CreateAppointmentRequest() throws Exception {
        String strRequest;
        if (SCH_2_FillerAppointmentID.isEmpty()) {
            throw new Exception("Error_:_SCH_2_FillerAppointmentID_field_is_empty");
        }
        if (PID3_PatientIdentifierList.isEmpty()) {
            throw new Exception("Error_:_PID3_PatientIdentifierList.Not.Send");

        }

        if (PV1_7_1_AttendingDoctorCode.isEmpty()) {
            throw new Exception("Error_:_PV1_7_1_AttendingDoctorCode_field_is_empty");
        }
        if (PV1_3_1_AssignedPatient_PointOfCare.isEmpty()) {
            throw new Exception("Error_:_PV1_3_1_AssignedPatient_PointOfCare_field_is_empty");
        }
        if (PV1_44_AdmitDateTime.isEmpty()) {
            throw new Exception("Error_:_PV1_44_AdmitDateTime_is_empty");
        }
        if (MSH6_ReceivingFacility.isEmpty()) {
            throw new Exception("Error_:_MSH6_ReceivingFacility_field_is_empty");
        }

        if (SCH_11_AppointmentTimingQuantity.isEmpty()) {
            throw new Exception("Error_:_SCH_11_AppointmentTimingQuantity_is_empty");
        }
        if (EVN5_OperatorID.isEmpty()) { throw new Exception("Error_:_EVN5_OperatorID_is_empty"); }
        if (MSH7_DateTimeOfMessage.isEmpty()) {
            throw new Exception("Error_:_MSH7_DateTimeOfMessage_is_empty");
        }
        strRequest= "{\r\n" + "\"" + "identifier" + "\"" + ":" + "{" + "\"" + "value" + "\"" + ":" +
            "\"" + SCH_2_FillerAppointmentID + "\"" + "\r\n" + "}," + "\r\n" + "\"" + "patient" +
            "\"" + ":" + "{" + "\r\n" + "\"" + "code" + "\"" + ":" + "{" + "\r\n" + "\"" + "value" +
            "\"" + ":" + "\"" + PID3_PatientIdentifierList + "\"" + "\r\n" + "}," + "\r\n" + "\"" +
            "name" + "\"" + ":" + "{" + "\r\n" + "\"" + "firstName" + "\"" + ":" + "\"" + "A" +
            "\"" + "," + "\r\n" + "\"" + "fatherName" + "\"" + " : " + "\"" +
            PID5_2_PatientEnSecondName + "\"" + "," + "\r\n" + "\"" + "midName" + "\"" + " : " +
            "\"" + PID5_2_PatientEnSecondName + "\"" + "," + "\r\n" + "\"" + "lastName" + "\"" +
            " : " + "\"" + "A" + "\"" + "," + "\r\n" + "\"" + "otherFirstName" + "\"" + " : " +
            "\"" + decodeToArabicName(PID5_5_PatientArFirstName) + "\"" + "," + "\r\n" + "\"" +
            "otherFatherName" + "\"" + " : " + "\"" + PID5_6_PatientArSecondName + "\"" + "," +
            "\r\n" + "\"" + "otherMidName" + "\"" + " : " + "\"" + PID5_7_PatientArThirdName +
            "\"" + "," + "\r\n" + "\"" + "otherLastName" + "\"" + " : " + "\"" +
            PID5_8_PatientArFamilyName + "\"" + "\r\n" + "}}" + "," + "\r\n" + "\"" + "doctor" +
            "\"" + ":" + "{" + "\r\n" + "\"" + "code" + "\"" + ":" + "{" + "\r\n" + "\"" + "value" +
            "\"" + ":" + "\"" + PV1_7_1_AttendingDoctorCode + "\"" + "\r\n" + "}}," + "\r\n" +
            "\"" + "section" + "\"" + ":" + "{" + "\r\n" + "\"" + "code" + "\"" + ":" + "{" +
            "\r\n" + "\"" + "value" + "\"" + ":" + "\"" + PV1_3_1_AssignedPatient_PointOfCare +
            "\"" + "\r\n" + "}}, " +

            getProcedure(PV1_10_HospitalService) +

            "\"" + "start" + "\"" + ":" + "\"" + getDateTimeISO8601(PV1_44_AdmitDateTime) + "\"" +
            "," + "\r\n" + "\"" + "end" + "\"" + ":" + "\"" + getEndTime(PV1_44_AdmitDateTime) + 
            "\"" + "," + getAppReason(PV1_28_InterestCode) + "\r\n" +
            // "\"" + "procedure" + "\"" + ":" + "\"" + PV1_10_HospitalService +
            // "\"" + "," + "\r\n" +
            "\"" + "appointmentType" + "\"" + ":" + "\"" + SCH_11_AppointmentTimingQuantity + "\"" +
            "," + "\r\n" + "\"" + "facility" + "\"" + ":" + "{" + "\r\n" + "\"" + "code" + "\"" +
            ":" + "{" + "\r\n" + "\"" + "value" + "\"" + ":" + "\"" + MSH6_ReceivingFacility +
            "\"" + "\r\n" + "}}," + "\r\n" + "\"" + "recordedBy" + "\"" + ":" + "{" + "\r\n" +
            "\"" + "code" + "\"" + ":" + "{" + "\r\n" + "\"" + "value" + "\"" + ":" + "\"" +
            EVN5_OperatorID + "\"" + "\r\n" + "}}," + "\r\n" + "\"" + "recordedDate" + "\"" + ":" +
            "\"" + getDateTimeISO8601(MSH7_DateTimeOfMessage) + "\"" + "\r\n" + "}";
        return strRequest;
    }

    private String getProcedure(String pV1_10_HospitalService) {
        if (pV1_10_HospitalService.equals("")) {
            return "";
        } else {
            return "\"" + "procedure" + "\"" + ":" + "\"" + pV1_10_HospitalService + "\"" + ",";
        }
    }

    private String getAppReason(String pV1_28_InterestCode) {

        if(pV1_28_InterestCode.equals(""))
        {
         return "";
        }
         return  "\r\n" +
         "\"" + "reason" + "\"" + ":" + "\"" + pV1_28_InterestCode + "\"" + ",";
    }

    private void setAppointment(HL7_V24_Appointment appointment2) {

        SCH_1_PlacerAppointment= appointment.getPlacerAppointment();
        SCH_2_FillerAppointmentID= appointment.getFillerAppointmentID();
        SCH_3_OccurrenceNumber= appointment.getOccurrenceNumber();
        SCH_4_PlacerGroupNumber= appointment.getPlacerGroupNumber();
        SCH_5_ScheduleID= appointment.getScheduleID();
        SCH_6_EventReason= appointment.getEventReason();
        SCH_7_AppointmentReason= appointment.getAppointmentReason();
        SCH_8_AppointmentType= appointment.getAppointmentType();
        SCH_9_AppointmentDuration= appointment.getAppointmentDuration();
        SCH_10_AppointmentDurationUnits= appointment.getAppointmentDurationUnits();
        SCH_11_AppointmentTimingQuantity= getAppointmentType(
            appointment.getAppointmentTimingQuantity()); // appointment.getAppointmentTimingQuantity();
        SCH_11_4_AppointmentTimingQuantityStartDate= appointment
            .getAppointmentTimingQuantityStartDate();
        SCH_11_5_AppointmentTimingQuantityEndDate= appointment
            .getAppointmentTimingQuantityEndDate();
        SCH_12_PlacerContactPerson= appointment.getPlacerContactPerson();
        SCH_13_PlacerContactPhoneNumber= appointment.getPlacerContactPhoneNumber();
        SCH_14_PlacerContactAddress= appointment.getPlacerContactAddress();
        SCH_15_PlacerContactLocation= appointment.getPlacerContactLocation();
        SCH_16_FillerContactPerson= appointment.getFillerContactPerson();
        SCH_17_FillerContactPhoneNumber= appointment.getFillerContactPhoneNumber();
        SCH_18_FillerContactAddress= appointment.getFillerContactAddress();
        SCH_19_FillerContactLocation= appointment.getFillerContactLocation();
        SCH_20_EnteredByPerson= appointment.getEnteredByPerson();
        SCH_21_EnteredByPhoneNumber= appointment.getEnteredByPhoneNumber();
        SCH_22_EnteredbyLocation= appointment.getEnteredbyLocation();
        SCH_23_ParentPlacerAppointmentID= appointment.getParentPlacerAppointmentID();
        SCH_24_ParentFillerAppointmentID= appointment.getParentFillerAppointmentID();
        SCH_25_FillerStatusCode= appointment.getFillerStatusCode();
        SCH_26_PlacerOrderNumber= appointment.getPlacerOrderNumber();
        SCH_27_FillerOrderNumber= appointment.getFillerOrderNumber();

    }

    private String getAppointmentType(String appointmentTimingQuantity) {
        if (appointmentTimingQuantity.equals("First Visit")) {
            return "new";
        } else {
            return "followup";
        }
    }

    private void initialzedHL7Segmants() {
        lis2a2SIU= (LIS2A2_SIU_Msg) lis2aMessage;
        headerRecord= lis2a2SIU.getHL7v24HeaderRecords().size() != 0 ?
            lis2a2SIU.getHL7v24HeaderRecords().get(0) :
            null;
        /// headerRecord.getEventTrigger()
        patientRecord= lis2a2SIU.getHL7v24PatientRecords().size() != 0 ?
            lis2a2SIU.getHL7v24PatientRecords().get(0) :
            null;
        visit1Record= lis2a2SIU.getHL7v24Visit1Records().size() != 0 ?
            lis2a2SIU.getHL7v24Visit1Records().get(0) :
            null;
        // Appointment
        appointment= lis2a2SIU.getSchedulingActivity().size() != 0 ?
            lis2a2SIU.getSchedulingActivity().get(0) :
            null;
    }

    private void setVisit(HL7_V24_Visit1Record visit1Record) {
        if (visit1Record != null) {
            PV1_2_PatientClass= visit1Record.getPatientClass();
            PV1_3_AssignedPatient= visit1Record.getAssignedPatientLocation();
            ///////////////////////////////
            PV1_3_1_AssignedPatient_PointOfCare= visit1Record.getAssignedPatient_PointOfCare();
            PV1_3_2_AssignedPatient_Room= visit1Record.getAssignedPatient_Room();
            PV1_3_3_AssignedPatient_Bed= visit1Record.getAssignedPatient_Bed();
            PV1_4_AdmissionType= visit1Record.getAdmissionType();
            PV1_5_PreadmitNumber= visit1Record.getPreadmitNumber();
            PV1_6_PriorPatientLocation= visit1Record.getPriorPatientLocation();
            PV1_7_AttendingDoctor= visit1Record.getAttendingDoctor();
            PV1_7_1_AttendingDoctorCode = visit1Record.getAttendingDoctorCode();
            PV1_7_2_AttendingDoctorFamilyName= visit1Record.getAttendingDoctorFamilyName();
            PV1_7_3_AttendingDoctorSecondAndFatherName= visit1Record
                .getAttendingDoctorSecondAndFatherName();
            PV1_7_4_AttendingDoctorSuffix = visit1Record.getAttendingDoctorSuffix();
            PV1_8_ReferringDoctor = visit1Record.getReferringDoctor();
            PV1_8_1_ReferringDoctorCode = visit1Record.getReferringDoctorCode();
            PV1_9_ConsultingDoctor = visit1Record.getConsultingDoctor();
            PV1_9_1_ConsultingDoctorCode = visit1Record.getConsultingDoctorCode();
            PV1_10_HospitalService= visit1Record.getHospitalService();
            PV1_11_TemporaryLocation= visit1Record.getTemporaryLocation();
            PV1_12_PreadmitTestIndicator= visit1Record.getPreadmitTestIndicator();
            PV1_13_Re_admissionIndicator= visit1Record.getRe_admissionIndicator();
            PV1_14_AdmitSource= visit1Record.getAdmitSource();
            PV1_15_AmbulatoryStatus= visit1Record.getAmbulatoryStatus();
            PV1_16_VIPIndicator= visit1Record.getVIPIndicator();
            PV1_17_AdmittingDoctor= visit1Record.getAttendingDoctor();
            PV1_17_1_AdmittingDoctorCode = visit1Record.getAdmittingDoctorCode();
            PV1_17_AdmittingDoctor_2= visit1Record.getAdmittingDoctor_2();
            PV1_17_AdmittingDoctor_3= visit1Record.getAdmittingDoctor_3();

            PV1_18_PatientType= visit1Record.getPatientType();
            PV1_19_VisitNumber= visit1Record.getVisitNumber();
            PV1_20_FinancialClass= visit1Record.getFinancialClass();
            PV1_21_ChargePriceIndicator= visit1Record.getChargePriceIndicator();
            PV1_22_CourtesyCode= visit1Record.getCourtesyCode();
            PV1_23_CreditRating= visit1Record.getCreditRating();
            PV1_24_ContractCode= visit1Record.getContractCode();
            PV1_25_ContractEffectiveDate= visit1Record.getContractEffectiveDate();
            PV1_26_ContractAmount= visit1Record.getContractAmount();
            PV1_27_ContractPeriod= visit1Record.getContractPeriod();
            PV1_28_InterestCode= visit1Record.getInterestCode();
            PV1_29_TransferToBadDebtCode= visit1Record.getTransfertoBadDebtCode();
            PV1_30_TransferToBadDebtDate= visit1Record.getTransfertoBadDebtDate();
            PV1_31_BadDebtAgencyCode= visit1Record.getBadDebtAgencyCode();
            PV1_32_BadDebtTransferAmount= visit1Record.getBadDebtTransferAmount();
            PV1_33_BadDebtRecoveryAmount= visit1Record.getBadDebtRecoveryAmount();
            PV1_34_DeleteAccountIndicator= visit1Record.getDeleteAccountDate();
            PV1_35_DeleteAccountDate= visit1Record.getDeleteAccountDate();
            PV1_36_DischargeDisposition= visit1Record.getDischargeDisposition();
            PV1_37_DischargedToLocation= visit1Record.getDischargedtoLocation();
            PV1_38_DietType= visit1Record.getDietType();
            PV1_39_ServicingFacility= visit1Record.getServicingFacility();
            PV1_40_BedStatus= visit1Record.getBedStatus();
            PV1_41_AccountStatus= visit1Record.getAccountStatus();
            PV1_42_PendingLocation= visit1Record.getPendingLocation();
            PV1_43_PriorTemporaryLocation= visit1Record.getPriorTemporaryLocation();
            PV1_44_AdmitDateTime= visit1Record.getAdmitDateTime();
            PV1_45_DischargeDateTime= visit1Record.getDischargeDateTime();
            PV1_46_CurrentPatientBalance= visit1Record.getCurrentPatientBalance();
            PV1_47_TotalCharges= visit1Record.getTotalCharges();
            PV1_48_TotalAdjustments= visit1Record.getTotalAdjustments();
            PV1_49_TotalPayments= visit1Record.getTotalPayments();
            PV1_50_AlternateVisit= visit1Record.getAlternateVisitID();
            PV1_51_VisitIndicator= visit1Record.getVisitIndicator();
            PV1_52_OtherHealthcareProvider= visit1Record.getOtherHealthcareProvider();
        }
        if (visit2Record != null) {
            PV2_11_ActualLengthofInpatientStay= visit2Record.getActualLengthofInpatientStay();
            PV2_8_ExpectedAdmitDateTime= visit2Record.getExpectedAdmitDateTime();

        } else if (visit2Record == null) {
            PV2_11_ActualLengthofInpatientStay= "";
        }
    }

    private void setPID(HL7_V24_PatientRecord patientRecord) {

        if (patientRecord != null) {
            PID1_SetID_PID= patientRecord.getFieldValue(1);
            PID2_PatientID= patientRecord.getPatientID();
            PID3_PatientIdentifierList= patientRecord.getFieldValue(3);
            PID4_AlternatePatientID_PID= patientRecord.getAlternatePatientID();
            PID5_1_PatientEnFirstName= patientRecord.getFirstName();
            PID5_2_PatientEnSecondName= patientRecord.getSecondName();
            PID5_3_PatientEnThirdName= patientRecord.getThiredName();
            PID5_4_PatientEnFamilyName= patientRecord.getFamilyName();
            PID5_5_PatientArFirstName= patientRecord.getARFirstName();
            PID5_6_PatientArSecondName= patientRecord.getArSecondName();
            PID5_7_PatientArThirdName= patientRecord.getArThirdName();
            PID5_8_PatientArFamilyName= patientRecord.getArFamilyName();
            PID6_MotherMaidenName= patientRecord.getMotherMaidenName();
            PID7_DateTimeOfBirth= patientRecord.getDateTimeOfBirth();
            // getDateTimeISO8601(patientRecord.getDateTimeOfBirth()) ;
            PID8_AdministrativeSex= patientRecord.getAdministrativeSex();
            PID9_PatientAlias= patientRecord.getPatientAlias();
            PID10_Race= patientRecord.getRace();
            PID11_PatientAddress= patientRecord.getPatientAddress();
            PID12_CountyCode= patientRecord.getCountyCode();
            PID13_PhoneNumber_Home= patientRecord.getPhoneNumberHome();
            PID14_PhoneNumber_Business= patientRecord.getPhoneNumberBusiness();
            PID15_PrimaryLanguage= patientRecord.getPrimaryLanguage();
            PID16_MaritalStatus= patientRecord.getMaritalStatus();// getMaritalStatus(patientRecord.getMaritalStatus());//
                                                                  // patientRecord.getMaritalStatus();
            PID17_Religion= patientRecord.getReligion();
            PID18_PatientAccountNumber= patientRecord.getPatientAccountNumber();
            PID19_SSN_Number_Patient= patientRecord.getSSNNumberPatient();
            PID20_DriverLicenseNumberPatient= patientRecord.getDriverLicenseNumberPatient();
            PID21_MotherIdentifier= patientRecord.getMotherIdentifier();
            PID22_EthnicGroup= patientRecord.getEthnicGroup();
            PID23_BirthPlace= patientRecord.getBirthPlace();
            PID24_MultipleBirth= patientRecord.getMultipleBirth();
            PID25_BirthOrder= patientRecord.getBirthOrder();
            PID26_Citizenship= patientRecord.getCitizenship();
            PID27_VeteransMilitaryStatus= patientRecord.getVeteransMilitaryStatus();
            PID28_Nationality= patientRecord.getNationality();
            PID29_PatientDeathDateAndTime= patientRecord.getPatientDeathDateAndTime();
            PID30_PatientDeathIndicator= patientRecord.getPatientDeathIndicator();
            PID31_IdentityUnknownIndicator= patientRecord.getIdentityUnknownIndicator();
            PID32_IdentityReliabilityCode= patientRecord.getIdentityReliabilityCode();
            PID33_LastUpdateDateTime= patientRecord.getLastUpdateDateTime();
            PID34_LastUpdateFacility= patientRecord.getLastUpdateFacility();
            PID35_SpeciesCode= patientRecord.getSpeciesCode();
            PID36_BreedCode= patientRecord.getBreedCode();
            PID37_Strain= patientRecord.getStrain();
            PID38_ProductionClassCode= patientRecord.getProductionClassCode();
            PID50_Alternative_AppointmentID= patientRecord.getAlternativeAppointmentID();
        }

    }

    private String getMappedMaritalStatus(String maritalStatus) {
        if (maritalStatus.toUpperCase().equals("SINGLE"))
            return "\"" + "maritalStatus" + "\"" + " : " + "\"" + "s" + "\"" + "," + "\r\n";
        else if (maritalStatus.toUpperCase().equals("MARRIED")) {
            return "\"" + "maritalStatus" + "\"" + " : " + "\"" + "m" + "\"" + "," + "\r\n";
        } else if (maritalStatus.equals("WIDOWED"))
            return "\"" + "maritalStatus" + "\"" + " : " + "\"" + "w" + "\"" + "," + "\r\n";
        else if (maritalStatus.equals("DIVORCED"))
            return "\"" + "maritalStatus" + "\"" + " : " + "\"" + "d" + "\"" + "," + "\r\n";
        else if (maritalStatus.isEmpty())
            return "";
        else if (maritalStatus.equals("OTHERS"))
            return "";
        else {
            return "";
        }
    }

    private void setMSH(HL7_V24_HeaderRecord headerRecord) {

        if (headerRecord != null) {
            MSH3_SendingApplication= headerRecord.getSendingApp();
            MSH4_SendingFacility= headerRecord.getSendingFacility();
            MSH5_ReceivingApplication= headerRecord.getReceivingApp();
            MSH6_ReceivingFacility= headerRecord.getReceivingFacility();
            MSH7_DateTimeOfMessage= headerRecord.getDateTimeOfMessage();
            MSH8_Security= headerRecord.getSecurity();
            MSH9_MessageType= headerRecord.getMessageType();
            MSH10_MessageControl= headerRecord.getMessageControlId();
            MSH11_ProcessingID= headerRecord.getProcessingID();
            MSH12_VersionID= headerRecord.getVersion();
            MSH13_SequenceNumber= headerRecord.getSequanceNo();
            MSH14_ContinuationPointer= headerRecord.getContinuationPointer();
            MSH15_AcceptAcknowledgmentType= headerRecord.getAcceptAckType();
            MSH16_ApplicationAcknowledgmentType= headerRecord.getAppAckType();
            MSH17_CountryCode= headerRecord.getCountryCode();
            MSH18_CharacterSet= headerRecord.getCharacterSet();
            MSH19_PrincipalLanguageOfMessage= headerRecord.getPrincipalLanguageOfMessage();
            MSH20_AlternateCharacterSetHandlingScheme= headerRecord
                .getAlternateCharacterSetHandlingScheme();
            MSH21_ConformanceStatementID= headerRecord.getConformanceStatementID();
        }

    }

    private String getDischargeDate(String pV1_45_DischargeDateTime2) {

        try {

            if (pV1_45_DischargeDateTime2.isEmpty()) {

                return "";
            } else {

                return "\r\n" + "\"" + "AdmitDate" + "\"" + " : " + "\"" +
                    getDateTimeISO8601(PV1_44_AdmitDateTime.toString()) + "\"" + "," + "\r\n";

            }

        } catch (Exception e) {

            return "";
        }
    }

    private String getAssignedPatientLocation(String strHL7PatientClass) {
        if (strHL7PatientClass.toUpperCase().equals("O"))
            return "outpatient";
        else
            return "emergency";

    }

    private String getAdmissionTypeID(String strAdmissionType) {
        if (strAdmissionType.equals("Follow-up visit"))
            return "followup";
        else if (strAdmissionType.equals("First Visit"))
            return "new";
        else
            return "other";
    }

    private String getPreaddmissionDetails(String pV1_5_PreadmitNumber2) {
        if (pV1_5_PreadmitNumber2.isEmpty()) {
            return "";
        } else {
            return "\"" + "preadmission" + "\"" + ":" + "{" + "\"" + "identifier" + "\"" + ":" +
                "{" + "\"" + "value" + "\"" + ":" + "\"" + PV1_5_PreadmitNumber + "\"" + "}}" + ",";
        }

    }

    public String getDateTimeISO8601(String strDateTime) throws Exception {

        int indexOfDash= strDateTime.indexOf('-');

        if (indexOfDash != -1) {

            strDateTime= strDateTime.substring(0, indexOfDash);
        }

        if (strDateTime.length() < 15) {
            String str= Strings.padEnd(strDateTime, 14, '0');
            strDateTime= str;
            System.out.println(strDateTime);
        }

        if (strDateTime.length() > 14) {

            System.out.println("data time length more than 14");
            System.out.println(strDateTime.substring(0, 14));
            strDateTime= strDateTime.substring(0, 14);

        }

        checkTimeFormat(strDateTime);
        String strDate= strDateTime;
        DateTimeFormatter formatter= DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime localDate= LocalDateTime.parse(strDate, formatter);
        DateTimeFormatter formatter2= DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
            .withZone(ZoneId.of("UTC"));
        return localDate.format(formatter2);
    }

    public Date convertToDate(String strDateTime) throws Exception {

        SimpleDateFormat formatter= new SimpleDateFormat("yyyMMddyyyyHHmmss", Locale.ENGLISH);

        String dateInString= "strDateTime";
        Date date= formatter.parse(dateInString);

        return date;
    }

    public String getEndTime(String strDateTime) throws Exception {
        if (strDateTime.length() < 15) {
            String str= Strings.padEnd(strDateTime, 14, '0');
            strDateTime= str;
            System.out.println(strDateTime);
        }
        checkTimeFormat(strDateTime);
        String strDate= strDateTime;
        DateTimeFormatter formatter= DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime localDate= LocalDateTime.parse(strDate, formatter);
        localDate= localDate.plusMinutes(5);
        DateTimeFormatter formatter2= DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
            .withZone(ZoneId.of("UTC"));
        return localDate.format(formatter2);
    }

    private void checkTimeFormat(String dateTime) {
        try {
            SimpleDateFormat format= new SimpleDateFormat("yyyyMMddHHmmss");
            format.parse(dateTime);
            System.out.println("Correct date");

        } catch (ParseException e) {
            System.out.println("Incorrect date");
        }

    }

    public Class<? extends LIS2A2Msg> getMsgType(LIS2A2Msg msg) {
        log.debug("Detecting message type");
        Class<? extends LIS2A2Msg> msgType= null;
        if (msg instanceof LIS2A2_DFT_Msg)
            msgType= LIS2A2_DFT_Msg.class;
        if (msg instanceof LIS2A2_ADT_Msg)
            msgType= LIS2A2_ADT_Msg.class;
        if (msg instanceof LIS2A2_SIU_Msg)
            msgType= LIS2A2_SIU_Msg.class;

        /*
         * for (LIS2A2Record record : msg.getAllRecords()) if (record instanceof
         * OrderRecord) msgType= LIS2A2OrderMsg.class; else if (record instanceof
         * HL7_V24_EventTypeRecord) msgType= LIS2A2_ADT_Msg.class; else if (record
         * instanceof QueryRecord) msgType= LIS2A2QueryMsg.class; else if (record
         * instanceof HL7_V24_Appointment) msgType= LIS2A2_SIU_Msg.class; else if
         * (record instanceof ResultRecord) msgType= LIS2A2ResultMsg.class; else if
         * (record instanceof OBXRecord) msgType= LIS2A2ResultMsg.class; return msgType;
         */
        return msgType;
    }

    private void setLkpMessageTransactionDirection(MessageDirection dir) {

        lkpMessageTransactionDirection= lkpService.findOneAnyLkp(

            java.util.Arrays.asList(new SearchCriterion("code", dir.getValue(), FilterOperator.eq)),

            LkpMessageTransactionDirection.class);

    }

    private MessageTransaction createMessageTransaction(String msgAsString,
        MessageTransactionService messageTransactionService, Machine machine,
        MessageTransaction messageTransaction,
        LkpMessageTransactionDirection msgDirection, LkpMessageTransactionType msgTransactionType,
        String sampleBarcode, HL7_V24_HeaderRecord globalHeaderRecord) {

        messageTransaction.setMachine(machine);
        messageTransaction.setNotes("Inbound Message Stored");
        messageTransaction.setMessageType(msgTransactionType);
        messageTransaction.setBranchId(machine.getBranchId());
        messageTransaction.setTenantId(machine.getTenantId());
        messageTransaction.setMessageDirection(msgDirection);
        messageTransaction.setMessageBody(msgAsString);
        messageTransaction.setIsValidated(false);
        messageTransaction.setIsSuccuss(false);
        messageTransaction= messageTransactionService.add(messageTransaction);
        return messageTransaction;

    }

    private String getDiagnosisDetails(String DG1_1_SetID_DG1, String DG1_2_DiagnosisCodingMethod) {
        if (DG1_1_SetID_DG1.isEmpty() || DG1_2_DiagnosisCodingMethod.isEmpty()) {
            return "";
        } else {

            return "," + "\"" + "diagnosis" + "\"" + " : " + "{" + "\r\n" + "\"" + "condition" +
                "\"" + " : " + "{" + "\r\n" + "\"" + "code" + "\"" + " : " + "{" + "\r\n" + "\"" +
                "value" + "\"" + " : " + "\"" + DG1_1_SetID_DG1 + "\"" + "}}" + "," + "\r\n" +
                "\"" + "type" + "\"" + ":" + "\"" + DG1_2_DiagnosisCodingMethod + "\"" + "}" +
                "\r\n";
        }

    }

    private String getAlternativeVisit(String PID) {
        if (PV1_50_AlternateVisit.isEmpty()) {
            // System.out.println("empty Identifier");
            return "";
        } else {
            return "," + "\"" + "identifier" + "\"" + " : " + "{" + "\"" + "value" + "\"" + " : " +
                "\"" + PV1_50_AlternateVisit + "\"" + "}";
        }

    }

    private String getActualLengthOfInpatientStay(String PV2_11_ActualLengthofInpatientStay) {
        if (PV2_11_ActualLengthofInpatientStay.isEmpty() ||
            PV2_11_ActualLengthofInpatientStay == null) {
            return "";
        } else {
            return "," + "\"" + "lengthOfStay" + "\"" + ":" + "\"" +
                PV2_11_ActualLengthofInpatientStay + "\"";
        }
    }

    private String getDischargeDateTime(String PV1_45_DischargeDateTime) throws Exception {

        if (PV1_45_DischargeDateTime.isEmpty() || PV1_45_DischargeDateTime == null) {
            return "";
        } else {
            return "," + "\"" + "dischargeDate" + "\"" + ":" + "\"" +
                getDateTimeISO8601(PV1_45_DischargeDateTime) + "\"";
        }
    }

    private String getCloseDateTime(String PV1_45_DischargeDateTime) throws Exception {

        if (PV1_45_DischargeDateTime.isEmpty() || PV1_45_DischargeDateTime == null) {
            return "";
        } else {
            return "\"" + "closeDate" + "\"" + ":" + "\"" +
                getDateTimeISO8601(PV1_45_DischargeDateTime) + "\"" + "," + "\r\n";
        }
    }

    private String getDaynamicFormatedProperityDateTime(String strProperityName,
        String PV1_45_DischargeDateTime)
        throws Exception {

        if (PV1_45_DischargeDateTime.isEmpty() || PV1_45_DischargeDateTime == null) {
            return "";
        } else {
            return "\"" + strProperityName + "\"" + ":" + "\"" +
                getDateTimeISO8601(PV1_45_DischargeDateTime) + "\"" + "," + "\r\n";
        }
    }

   /* private LkpMessageTransactionType getMessageTypeObj(String strType) {
        lkpMessageTransactionType= lkpService.findOneAnyLkp(
            java.util.Arrays.asList(new SearchCriterion("code", strType, FilterOperator.eq)),
            LkpMessageTransactionType.class);

        return lkpDirectionType;
    }*/
    
    
    private LkpMessageTransactionType getMessageTypeObj(String strType) {
        lkpMessageTransactionType= lkpService.findOneAnyLkp(
            java.util.Arrays.asList(new SearchCriterion("code", strType, FilterOperator.eq)),
            LkpMessageTransactionType.class);

        return lkpMessageTransactionType;
    }

    private void setDG1(HL7_V24_DiagnosisRecord diagnosisRecord2) {
        // TODO Auto-generated method stub

        DG1_1_SetID_DG1= "";
        DG1_2_DiagnosisCodingMethod= "";

    }

}