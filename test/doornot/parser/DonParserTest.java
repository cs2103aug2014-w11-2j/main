package doornot.parser;
import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.BeforeClass;
import org.junit.Test;

import doornot.parser.IDonCommand.CommandType;
/**
 * Test the DonParser and check if it parses the user commands correctly and creates the right
 * DonCommand
 */
//@author A0115503W
public class DonParserTest {

	private static DonParser parser;
	private DonCommand CommandTest = new DonCommand();
	@BeforeClass
	public static void init(){
		parser = new DonParser();
	}
	
	@Test
	public void testAddTask(){
		
		Calendar cal = new GregorianCalendar(0,0,0);
		System.out.println("blah: "+ cal.getTime());
		
		
		CommandTest.setType(CommandType.ADD_TASK);
		CommandTest.setNewName("hihihi");
		CommandTest.setNewDeadline(new GregorianCalendar(2014,8,9));
		
		assertEquals(CommandTest.getType(), parser.parseCommand("a \"hihihi 12345678 \" at 09092014").getType());
		assertEquals(CommandTest.getNewName(), parser.parseCommand("add \"hihihi\" at 09092014").getNewName());
		assertEquals(CommandTest.getNewDeadline(), 
				parser.parseCommand("add \"hihihi 12345678 \" @ 09092014").getNewDeadline());
		assertEquals(CommandType.INVALID_DATE, parser.parseCommand("a \"hihihi 12345678 \" at 12132014").getType());
		
		CommandTest.setNewDeadline(new GregorianCalendar(2014,8,9,13,24));
		assertEquals(CommandTest.getNewDeadline(), 
				parser.parseCommand("add \"hihihi\" @ 09092014_1324").getNewDeadline());
		
//		assertEquals(CommandType.INVALID, parser.parseCommand("ad \"hihihi\" at 09082014").getType());
//		assertEquals(CommandType.INVALID, parser.parseCommand("a hihihi\" at 09082014").getType());
//		assertEquals(CommandType.INVALID, parser.parseCommand("add \"hihihi\" 09082014").getType());
//		assertEquals(CommandType.INVALID, parser.parseCommand("add \"hihihi\" at 090814").getType());
//		assertEquals(CommandType.INVALID, parser.parseCommand("a \"hihihi\" a 09082014").getType());
		
		
		
	}
	
	@Test
	public void testAddEvent(){
		CommandTest.setType(CommandType.ADD_EVENT);
		CommandTest.setNewName("hihihi");
		CommandTest.setNewStartDate(new GregorianCalendar(2014,8,7));
		CommandTest.setNewEndDate(new GregorianCalendar(2014,8,9));
		
		
		assertEquals(CommandTest.getType(),
				parser.parseCommand("add \"hihihi\" from 07082014 to 09082014").getType());
		assertEquals(CommandTest.getNewName(), 
				parser.parseCommand("add \"hihihi\" from 07082014 to 09082014").getNewName());
		assertEquals(CommandTest.getNewStartDate(), 
				parser.parseCommand("add \"hihihi\" from 07082014 to 09082014").getNewStartDate());
		assertEquals(CommandTest.getNewEndDate(), 
				parser.parseCommand("add \"hihihi\" from 07082014 to 09082014").getNewEndDate());
		
		CommandTest.setNewStartDate(new GregorianCalendar(2014,8,7,13,24));
		CommandTest.setNewEndDate(new GregorianCalendar(2014,8,9,15,54));
		assertEquals(CommandTest.getNewStartDate(), 
				parser.parseCommand("add \"hihihi\" from 07082014_1324 to 09082014_1554").getNewStartDate());
		assertEquals(CommandTest.getNewEndDate(), 
				parser.parseCommand("add \"hihihi 12345678 \" from 07082014_1324 to 09082014_1554").getNewEndDate());
//		assertEquals(CommandType.INVALID, 
//				parser.parseCommand("add \"hihihi\" from 07082014 to 09082014_1554").getType());
//		assertEquals(CommandType.INVALID, 
//				parser.parseCommand("add \"hihihi\" from 07082014_1324 to 09082014").getType());
//		
//		assertEquals(CommandType.INVALID,
//				parser.parseCommand("add \"hihihi\" fro 07082014 to 09082014").getType());
//		assertEquals(CommandType.INVALID,
//				parser.parseCommand("add \"hihihi\" from 0702014 to 0908204").getType());
//		assertEquals(CommandType.INVALID,
//				parser.parseCommand("add \"hihihi\" from 0702014 2 0908204").getType());
//		assertEquals(CommandType.INVALID,
//				parser.parseCommand("add \"hihihi from 0702014 2 0908204").getType());
	}
	
	@Test
	public void testAddFloat(){
		CommandTest.setType(CommandType.ADD_FLOAT);
		CommandTest.setNewName("hihihi");
		
		
		assertEquals(CommandTest.getType(),
				parser.parseCommand("add \"hihihi fh\"").getType());
		assertEquals(CommandTest.getNewName(), 
				parser.parseCommand("a \"hihihi\"").getNewName());
//		assertEquals(CommandType.INVALID, 
//				parser.parseCommand("add \"hihihi\" dfs").getType());
	}
	
	@Test
	public void testMark(){
		CommandTest.setType(CommandType.MARK);
		CommandTest.setName("blah95");
		
		
		assertEquals(CommandTest.getType(),
				parser.parseCommand("mark \"blah95\"").getType());
		assertEquals(CommandTest.getName(), 
				parser.parseCommand("m \"blah95\"").getName());
//		assertEquals(CommandType.INVALID, 
//				parser.parseCommand("mark \"blah95").getType());
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
				parser.parseCommand("del \"blah95\"").getType());
		assertEquals(CommandTest.getName(), 
				parser.parseCommand("del \"blah95\"").getName());
//		assertEquals(CommandType.INVALID, 
//				parser.parseCommand("del \"blah95").getType());
	}
	
	@Test
	public void testDeleteID(){
		CommandTest.setType(CommandType.DELETE_ID);
		CommandTest.setID(666);
		
		
		assertEquals(CommandTest.getType(),
				parser.parseCommand("delete 666").getType());
		assertEquals(CommandTest.getID(), 
				parser.parseCommand("d 666").getID());
		
	}
	@Test
	public void testSearchName(){
		CommandTest.setType(CommandType.SEARCH_NAME);
		CommandTest.setName("blah95");
		
		
		assertEquals(CommandTest.getType(),
				parser.parseCommand("search \"blah95\"").getType());
		assertEquals(CommandTest.getName(), 
				parser.parseCommand("s \"blah95\"").getName());
//		assertEquals(CommandType.INVALID, 
//				parser.parseCommand("search \"blah95").getType());
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
		
		CommandTest.setDeadline(new GregorianCalendar(2014,8,9,11,23));
		assertEquals(CommandTest.getDeadline(), 
				parser.parseCommand("s 09082014_1123").getDeadline());
	}
	
	@Test
	public void testEditName(){
		
		CommandTest.setType(CommandType.EDIT_NAME);
		CommandTest.setName("hihihi");
		CommandTest.setNewName("HEHEHE");
		
		assertEquals(CommandTest.getType(), 
				parser.parseCommand("ed \"hihihi\" to \"HEHEHE\"").getType());
		assertEquals(CommandTest.getNewName(), 
				parser.parseCommand("e \"hihihi\" to \"HEHEHE\"").getNewName());
		assertEquals(CommandTest.getName(), 
				parser.parseCommand("EDIT \"hihihi\" to \"HEHEHE\"").getName());
		
		CommandTest.setType(CommandType.EDIT_ID_NAME);
		CommandTest.setID(666);
		CommandTest.setNewName("hehehe");
		
		assertEquals(CommandTest.getType(), 
				parser.parseCommand("edit 666 to \"hehehe\"").getType());
		assertEquals(CommandTest.getNewName(), 
				parser.parseCommand("edit 666 to \"hehehe\"").getNewName());
		assertEquals(CommandTest.getID(), 
				parser.parseCommand("edit 666 to \"hehehe\"").getID());
		
	}
	
	@Test
	public void testEditEvent(){
		CommandTest.setType(CommandType.EDIT_EVENT);
		CommandTest.setName("hihihi");
		CommandTest.setNewStartDate(new GregorianCalendar(2014,8,7));
		CommandTest.setNewEndDate(new GregorianCalendar(2014,8,9));
		
		
		assertEquals(CommandTest.getType(),
				parser.parseCommand("edit \"hihihi\" to from 07082014 to 09082014").getType());
		assertEquals(CommandTest.getName(), 
				parser.parseCommand("edit \"hihihi\" to from 07082014 to 09082014").getName());
		assertEquals(CommandTest.getNewStartDate(), 
				parser.parseCommand("edit \"hihihi\" to from 07082014 to 09082014").getNewStartDate());
		assertEquals(CommandTest.getNewEndDate(), 
				parser.parseCommand("edit \"hihihi\" to from 07082014 to 09082014").getNewEndDate());
		
		CommandTest.setType(CommandType.EDIT_ID_EVENT);
		CommandTest.setID(666);
		CommandTest.setNewStartDate(new GregorianCalendar(2014,8,7));
		CommandTest.setNewEndDate(new GregorianCalendar(2014,8,9));
		
		
		assertEquals(CommandTest.getType(),
				parser.parseCommand("edit 666 to from 07082014 to 09082014").getType());
		assertEquals(CommandTest.getID(), 
				parser.parseCommand("edit 666 to from 07082014 to 09082014").getID());
		assertEquals(CommandTest.getNewStartDate(), 
				parser.parseCommand("edit 666 to from 07082014 to 09082014").getNewStartDate());
		assertEquals(CommandTest.getNewEndDate(), 
				parser.parseCommand("edit 666 to from 07082014 to 09082014").getNewEndDate());
		
		CommandTest.setNewStartDate(new GregorianCalendar(2014,8,7,13,55));
		CommandTest.setNewEndDate(new GregorianCalendar(2014,8,9,11,44));
		
		assertEquals(CommandTest.getNewStartDate(), 
				parser.parseCommand("edit 666 to from 07082014_1355 to 09082014_1144").getNewStartDate());
		assertEquals(CommandTest.getNewEndDate(), 
				parser.parseCommand("edit 666 to from 07082014_1355 to 09082014_1144").getNewEndDate());
	}
	
	@Test
	public void testEditDate(){
		CommandTest.setType(CommandType.EDIT_DATE);
		CommandTest.setName("hihihi");
		CommandTest.setNewDeadline(new GregorianCalendar(2014,8,9));
		
		
		assertEquals(CommandTest.getType(),
				parser.parseCommand("edit \"hihihi\" to 09082014").getType());
		assertEquals(CommandTest.getName(), 
				parser.parseCommand("edit \"hihihi\" to 09082014").getName());
		assertEquals(CommandTest.getNewDeadline(), 
				parser.parseCommand("edit \"hihihi\" to 09082014").getNewDeadline());
		
		CommandTest.setNewDeadline(new GregorianCalendar(2014,8,9,1,23));
		
		assertEquals(CommandTest.getNewDeadline(), 
				parser.parseCommand("edit \"hihihi\" to 09082014_0123").getNewDeadline());
		
		CommandTest.setType(CommandType.EDIT_ID_DATE);
		CommandTest.setID(666);
		CommandTest.setNewDeadline(new GregorianCalendar(2014,8,9));
		
		
		assertEquals(CommandTest.getType(),
				parser.parseCommand("edit 666 to 09082014").getType());
		assertEquals(CommandTest.getID(), 
				parser.parseCommand("edit 666 to 09082014").getID());
		assertEquals(CommandTest.getNewDeadline(), 
				parser.parseCommand("edit 666 to 09082014").getNewDeadline());
		
	
	}
	



}
