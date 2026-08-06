package com.sunbird.machine.web.machine.order.controller;
/*
 * 
 * Appointment JSON will receive from Certacure Engine Should Re send this JSON to CL   : 
{

"AppointmentId": "21212121", 

"MRN": "1212121" ,

"PatientCode":  "21212",

"ResourceId":  "22",

"AppointmentDate":  "2023-12-31 16:20:00",

"StartTime":  "03:00",

"EndTime": "04:00",

"SpecialityId":  "21451", 

"AppointmnetStatus": "1", //OPEN = 1, OutPatient = CLOSE

"HasCoverage": "1",  // AA =1 , AR =2

"PatientType":"1"  , //InPatient = 1, OutPatient = 2
}
 */
public class Appointment 
{        
    
    public String MRN;
        
    public String AppointmentId;
    
    public String NationalId;
    
    public String PatientCode;
    
    public String ResourceId;
    
    public String AppointmentDate;
    
    public String StartTime;
    
    public String EndTime;
    
    public String SpecialityId;
    
    public String AppointmnetStatus; //=> Open = 1, Close = 2
    
    public String HasCoverage; //=> AA = 1, AR = 2
    
    public String PatientType; //InPatient = 1, OutPatient = 2
    

}
