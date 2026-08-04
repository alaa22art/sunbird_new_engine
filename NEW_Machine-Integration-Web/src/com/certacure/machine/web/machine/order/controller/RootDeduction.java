package com.certacure.machine.web.machine.order.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.asn1.eac.PublicKeyDataObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class RootDeduction{
	
public List<Deduction> deductionList;
public Type listDeductionType = new TypeToken<List<String>>() {}.getType();
private static  Gson gson;

String strDeducation;




 public RootDeduction(String strDeducation) 
 {
	// gson = new Gson();
	 //deductionList =  gson.fromJson(strDeducation, listDeductionType);
	 
	 if(strDeducation.startsWith("[") && strDeducation.endsWith("]"))
	 {
		 
		 this.strDeducation = strDeducation.substring(1,strDeducation.length()-2);
	 }
 }
 
 @Override
 public String toString()
 {
	 return strDeducation;
	 //return deductionList.get(0).toString();
 }


}