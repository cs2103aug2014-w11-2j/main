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
			String mes = "add \"blah\" at 12 oct";
			
			String str = mes.replaceFirst("add", "").trim();
					
			System.out.println(str);		
			Pattern pattern = Pattern.compile( "^\".+\"\\sat\\b|^\".+\"\\s@\\b");
			Matcher matcher = pattern.matcher(str);
			// ensures semi colon not in name
			
			System.out.println(matcher.find());
			System.out.println(matcher.group());
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
//			String command = "11/11";
//
//			Parser parser = new Parser();
//			List<DateGroup> groups = parser.parse(command);
//			List<Date> dates = null;
//			
//			for(DateGroup group:groups) {
//			  dates = group.getDates();
//			  System.out.println(dates.get(0).toString());
//			 
//			  boolean isTimeInferred = group.isTimeInferred();
//			  boolean isRecurring = group.isRecurring();
//			  System.out.println(isTimeInferred);
//			  System.out.println(isRecurring);
//			  Date recursUntil = group.getRecursUntil();
//			  
//			  System.out.println(recursUntil.toString());
//			}

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
		

		}

}
