define("commonData", [], function () {
    'use strict';
    //this data is temporary, IT MUST BE CONVERTED TO SET UP MAPPING LATER
    var varLineList = ["Brazil/Japan (P.1)", "India (B.1.617.1/3)", "India (Delta - B.1.617.2)", "India (Kappa - B.1.617.1)",
        "Omicron", "SARS-CoV-2", "South Africa (B.1.351)", "UK (B 1.1.7)", "UK (B.1.525) / Brazil (P.2)", "US California (Epsilon - B.1427/429)"];

    var resultLineList = ["COVID-19 Detected (POSITIVE - مصاب)", "Not Detected (Negative - غير مصاب)", "Undetermined"];
    var commonData = {
        tenantMessages: [],//unmodified tenant messages
        na: "N/A",
        arrow: "\u2192",
        defaultTenantRid: 0,
        appAdminRid: 0,
        defaultLocale: "en_us",
        selectedTokenBranch: null,
        workListStatus: {
            OPEN: "OPEN",
            IN_PROGRESS: "IN_PROGRESS",
            RESULTS_ENTRY: "RESULTS_ENTRY",
            FINALIZED: "FINALIZED"
            // CLOSED: "CLOSED"
        },
        pcrDialogResultsEntry: {
            resultLine: resultLineList,
            varLine: varLineList
        },
        pcrResults: {
            positive: "COVID-19 Detected (POSITIVE - مصاب)",
            negative: "Not Detected (Negative - غير مصاب)",
            undetermined: "Undetermined"
        },
        veiwPcrPageType: "ENABLE_REALTIME_PCR_VIEW"
    }
    return commonData;
});
