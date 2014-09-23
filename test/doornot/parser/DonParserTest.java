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
	private DonCommand CommandTest = new DonCommand();
	@BeforeClass
	public static void init(){
		parser = new DonParser();
	}
	
	@Test
	public void testAddTask(){
		
		CommandTest.setType(CommandType.ADD_TASK);
		CommandTest.setName("hihihi");
		CommandTest.setNewDeadline(new GregorianCalendar(2014,8,9));
		
		assertEquals(CommandTest.getType(), parser.parseCommand("add hihihi at 09082014").getType());
		assertEquals(CommandTest.getName(), parser.parseCommand("add hihihi at 09082014").getName());
		assertEquals(CommandTest.getNewDeadline(), 
				parser.parseCommand("add hihihi at 09082014").getNewDeadline());
		
		String test = "'door to door' to 'deer to deer'";
		Pattern pattern = Pattern.compile("\'\\sto\\s");
		Matcher m = pattern.matcher(test);
		System.out.println(m.find());
		System.out.println(m.group());
		String[] splits = test.split("\'\\sto\\s");
		System.out.println(splits[splits.length-1]);

		System.out.println(Integer.parseInt("555"));
	}
	
	@Test
	public void testAddEvent(){
		CommandTest.setType(CommandType.ADD_EVENT);
		CommandTest.setName("hihihi");
		CommandTest.setNewStartDate(new GregorianCalendar(2014,8,7));
		CommandTest.setNewEndDate(new GregorianCalendar(2014,8,9));
		
		
		assertEquals(CommandTest.getType(),
				parser.parseCommand("add hihihi from 07082014 to 09082014").getType());
		assertEquals(CommandTest.getName(), 
				parser.parseCommand("add hihihi from 07082014 to 09082014").getName());
		assertEquals(CommandTest.getNewStartDate(), 
				parser.parseCommand("add hihihi from 07082014 to 09082014").getNewStartDate());
		assertEquals(CommandTest.getNewEndDate(), 
				parser.parseCommand("add hihihi from 07082014 to 09082014").getNewEndDate());
	}
	
	@Test
	public void testAddFloat(){
		CommandTest.setType(CommandType.ADD_FLOAT);
		CommandTest.setName("hihihi");
		
		
		assertEquals(CommandTest.getType(),
				parser.parseCommand("add hihihi").getType());
		assertEquals(CommandTest.getName(), 
				parser.parseCommand("add hihihi").getName());
	}
	
	@Test
	public void testMark(){
		CommandTest.setType(CommandType.MARK);
		CommandTest.setName("blah95");
		
		
		assertEquals(CommandTest.getType(),
				parser.parseCommand("mark blah95").getType());
		assertEquals(CommandTest.getName(), 
				parser.parseCommand("mark blah95").getName());
	}
	
	@Test
	public void testMarkID(){
		CommandTest.setType(CommandType.MARK_ID);
		CommandTest.setID(666);
		
		
		assertEquals(CommandTest.getType(),
				parser.parseCommand("mark 666").getType());
		assertEquals(CommandTest.getID(), 
				parser.parseCommand("mark 666").getID());
	}
	@Test
	public void testDelete(){
		CommandTest.setType(CommandType.DELETE);
		CommandTest.setName("blah95");
		
		
		assertEquals(CommandTest.getType(),
				parser.parseCommand("del blah95").getType());
		assertEquals(CommandTest.getName(), 
				parser.parseCommand("del blah95").getName());
	}
	
	@Test
	public void testDeleteID(){
		CommandTest.setType(CommandType.DELETE_ID);
		CommandTest.setID(666);
		
		
		assertEquals(CommandTest.getType(),
				parser.parseCommand("del 666").getType());
		assertEquals(CommandTest.getID(), 
				parser.parseCommand("del 666").getID());
	}
	@Test
	public void testSearchName(){
		CommandTest.setType(CommandType.SEARCH_NAME);
		CommandTest.setName("blah95");
		
		
		assertEquals(CommandTest.getType(),
				parser.parseCommand("search blah95").getType());
		assertEquals(CommandTest.getName(), 
				parser.parseCommand("search blah95").getName());
	}
	
	@Test
	public void testSearchID(){
		CommandTest.setType(CommandType.SEARCH_ID);
		CommandTest.setID(666);
		
		
		assertEquals(CommandTest.getType(),
				parser.parseCommand("s 666").getType());
		assertEquals(CommandTest.getID(), 
				parser.parseCommand("s 666").getID());
	}
	
	@Test
	public void testSearchDate(){
		CommandTest.setType(CommandType.SEARCH_DATE);
		CommandTest.setDeadline(new GregorianCalendar(2014,8,9));
		
		
		assertEquals(CommandTest.getType(),
				parser.parseCommand("s 09082014").getType());
		assertEquals(CommandTest.getDeadline(), 
				parser.parseCommand("s 09082014").getDeadline());
	}
}
