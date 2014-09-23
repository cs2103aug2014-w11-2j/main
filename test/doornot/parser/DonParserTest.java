package doornot.parser;
import static org.junit.Assert.*;

import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.BeforeClass;
import org.junit.Test;

import doornot.parser.IDonCommand.CommandType;

public class DonParserTest {

	private static DonParser parser;
	private DonCommand addCommandTest = new DonCommand();
	@BeforeClass
	public static void init(){
		parser = new DonParser();
	}
	
	@Test
	public void testAdd(){
		
		addCommandTest.setType(CommandType.ADD_TASK);
		addCommandTest.setName("hihihi");
		addCommandTest.setNewDeadline(new GregorianCalendar(2014,8,9));
		
		assertEquals(addCommandTest.getType(), parser.parseCommand("add hihihi at 09082014").getType());
		assertEquals(addCommandTest.getName(), parser.parseCommand("add hihihi at 09082014").getName());
		assertEquals(addCommandTest.getNewDeadline(), 
				parser.parseCommand("add hihihi at 09082014").getNewDeadline());
		
		String test = "hihihi fdfdfd     at 09082014";
		Pattern pattern = Pattern.compile("\\bat\\s[0-9]{8}$|@\\s[0-9]{8}$");
		Matcher m = pattern.matcher(test);
		System.out.println(m.find());
		System.out.println(m.group(0));
		String[] splits = m.group(0).split("\\D");
		System.out.println(splits[splits.length-1]);

		System.out.println(Integer.parseInt(splits[splits.length-1].substring(4,8)));
//		assertEquals(true,matcher.find());/
	}
	
	

}
