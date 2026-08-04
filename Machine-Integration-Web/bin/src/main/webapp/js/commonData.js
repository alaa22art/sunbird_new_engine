define("commonData", [], function () {
    'use strict';
    var commonData = {
        tenantMessages: [],//unmodified tenant messages
        na: "N/A",
        arrow: "\u2192",
        defaultTenantRid: 0,
        appAdminRid: 0,
        defaultLocale: "en_us",
        selectedTokenBranch: null,
        internalHomepage: "home",
        fileTypes: {
            pdf: "application/pdf",
            excel: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        },
        generatedTypes: {
            BY_USER: "BY_USER",
            AUTOMATED: "AUTOMATED"
        },
        sampleStatuses: {
            OPEN: "OPEN",
            CLOSE: "CLOSE",
            DISABLE: "DISABLE"
        },
        inventoryItemStatuses: {
            OPEN: "OPEN",
            CLOSED: "CLOSED",
            NEW: "NEW"
        },
        eventTypes: {
            OUT_OF_CONTROL: "OUT_OF_CONTROL",
            CONTROL_REQUIRED: "CONTROL_REQUIRED",
            CONTROL_EXPIRED: "CONTROL_EXPIRED",
            REAGENT_EXPIRED: "REAGENT_EXPIRED",
            DELTA_CHECK_FAIL: "DELTA_CHECK_FAIL",
            INTERFERENCE_INDICES: "INTERFERENCE_INDICES",
            CRITICAL_RESULT: "CRITICAL_RESULT",
            MACHINE_ALARM: "MACHINE_ALARM",
            FIRST_DILUTION: "FIRST_DILUTION",
            SECOND_DILUTION: "SECOND_DILUTION",
            THIRD_DILUTION: "THIRD_DILUTION",
            DILUTION_REQUIRED: "DILUTION_REQUIRED"
        },
        eventStatuses: {
            NEW: "NEW",
            ACKNOWLEDGED: "ACKNOWLEDGED",
            FIXED: "FIXED",
            ABORTED: "ABORTED",
            BROKEN: "BROKEN"
        },
        eventCategories: {
            QC: "QC",
            RV: "RV",
            PM: "PM",
            CM: "CM",
            CL: "CL",
            SC: "SC"
        },
        eventSeverities: {
            ERROR: "ERROR",
            WARNING: "WARNING",
            REQUIRED_ACTION: "REQUIRED_ACTION",
            NOTIFICATION: "NOTIFICATION"
        },
        apexModules: {
            logs: {
                id: 1,
                label: "logs",
                icon: "fas fa-book"
            },
            machines: {
                id: 2,
                label: "machines",
                icon: "fas fa-microscope"
            },
            testMapping: {
                id: 3,
                label: "testMapping",
                icon: "fas fa-vial"
            },
            tests: {
                id: 5,
                label: "tests",
                icon: "fas fa-vials"
            },
            securityControl: {
                id: 6,
                label: "securityControl",
                icon: "fas fa-shield-alt"
            },
            systemSettings: {
                id: 7,
                label: "systemSettings",
                icon: "fas fa-cog"
            }
        }
    }
    return commonData;
});