package doornot.parser;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.fortuna.ical4j.util.Dates;

import com.joestelmach.natty.*;

/**
 * For playing with codes. 
 * 
 */

public class Playground {

	public Playground() {
	
	}
		public static void main(String[] args){
//			String mes = "add \"blah\" at 12 oct";
//			
//			String str = mes.replaceFirst("add", "").trim();
//					
//			System.out.println(str);		
//			Pattern pattern = Pattern.compile( "^\".+\"\\sat\\b|^\".+\"\\s@\\b");
//			Matcher matcher = pattern.matcher(str);
//			// ensures semi colon not in name
//			
//			System.out.println(matcher.find());
//			System.out.println(matcher.group());
//			if( matcher.find()){
//				if(!extractName(param).contains(";")){
//					return true;
//				}else{
//					return false;
//				}
//			}else{
//				return false;
//			}
			
			
//			Calendar cal = Calendar.getInstance();
////			System.out.println(cal.getTime().toString());
//			
			String command = "";
//
			Parser parser = new Parser();
			List<DateGroup> groups = parser.parse(command);
			List<Date> dates = null;
			
			for(DateGroup group:groups) {
			  dates = group.getDates();
			  System.out.println(dates.get(0).toString());
			  System.out.println(dates.get(1).toString());
			  boolean isTimeInferred = group.isTimeInferred();
//			  boolean isRecurring = group.isRecurring();
			  System.out.println(isTimeInferred);
//			  System.out.println(isRecurring);
//			  Date recursUntil = group.getRecursUntil();
			  
//			  System.out.println(recursUntil.toString());
			}

//			System.out.println(dates.get(0).toString());
//			System.out.println(dates.get(1).toString());
			
			
//			Parser nattyParser = new Parser();
//			List<DateGroup> groups = nattyParser.parse(command);
//			List<Date> dates = null;
//
//			
//			
//			
//			
//			
//			for(DateGroup group:groups) {
//				dates = group.getDates();
//				System.out.println(dates);
//			}
//			Calendar nowcal = Calendar.getInstance();
//			 cal.setTime(dates.get(0));
//			String strDate = dates.get(0).toString();
//			Date now = dates.get(0);
//			
//			cal.add(Calendar.MINUTE,-2);
//			nowcal.setTime(now);
//					
//			if(cal.after(now)){
//				System.out.println("too much!");
//			}else{
//				System.out.println("okay!");
//			}
				
//			System.out.println(strDate);
			
			
			//List of all the allowed types 
			
			//date in DDMMYYYY_hhmm format
//			private String dateTimeReg = "[0-9]{8}_[0-9]{4}";
			
//			//date in DD/MM/YYYY format
//			private String dateReg = "\\b[0-9]{2}/[0-9]{2}/[0-9]{4}\\b";
//			private String dateNoYearReg = "\\b[0-9]{2}/[0-9]{2}\\b";
//			
//			//name must be between " "
//			private String taskNameReg = "\".+\"";
//			
//			//allow 'at DDMMYYYY' and '@ DDMMYYYY' and 'at DDMMYYYY_hhmm' and '@ DDMMYYYY_hhmm'
////			private String addTaskReg = "^at\\s[0-9]{8}$|^@\\s[0-9]{8}$|\\bat\\s[0-9]{8}_[0-9]{4}$|@\\s[0-9]{8}_[0-9]{4}$";
//			
//			// allow "blah" at or "blah" @
//			private String addTaskReg = "^\".+\"\\sat\\b|^\".+\"\\s@";
//			
//			// check for DD/MM/YYYY or DD/MM
////			private String addTaskDateReg = "^at\\s[0-9]{2}/[0-9]{2}/[0-9]{4}|^@\\s[0-9]{2}/[0-9]{2}/[0-9]{4}|^at\\s[0-9]{2}/[0-9]{2}|^@\\s[0-9]{2}/[0-9]{2}";
//			
//			// allow 'from DDMMYYYY to DDMMYYYY' and 'from DDMMYYYY_hhmm to DDMMYYYY_hhmm'
////			private String addEventReg = "\\bfrom\\s[0-9]{8}\\sto\\s[0-9]{8}$|\\bfrom\\s[0-9]{8}_[0-9]{4}\\sto\\s[0-9]{8}_[0-9]{4}$";
//			
//			// allow from
//			private String addEventReg = "^\".+\"\\sfrom\\s";
//			
//			// allow only name
//			private String addFloatReg = "^\".+\"$";
//			
//			// allow 'to DDMMYYYY' and 'to DDMMYYYY_hhmm'
////			private String editDateReg = "\\bto\\s[0-9]{8}$|\\bto\\s[0-9]{8}_[0-9]{4}$";
//			
//			// allow to
//			private String editDateOrEventReg = "^to\\b";
//			
//			// allow 'to " "'
//			private String editNameReg = "\\bto\\s\".+\"$";
//			
//			// allow 'to from DDMMYYYY to DDMMYYYY' and 'to from DDMMYYYY_hhmm to DDMMYYYY_hhmm'
//			private String editEventReg = "\\bto\\sfrom\\s[0-9]{8}\\sto\\s[0-9]{8}$|\\bto\\sfrom\\s[0-9]{8}_[0-9]{4}\\sto\\s[0-9]{8}_[0-9]{4}$";


		}

}
