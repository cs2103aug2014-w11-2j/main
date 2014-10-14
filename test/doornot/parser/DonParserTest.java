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
		
		// test add task
		CommandTest.setType(CommandType.ADD_TASK);
		CommandTest.setNewName("hihihi");
		CommandTest.setNewDeadline(new GregorianCalendar(2014,8,9));
		
		assertEquals(CommandTest.getType(), parser.parseCommand("a \"hihihi 12345678 \" at 09092014").getType());
		assertEquals(CommandTest.getNewName(), parser.parseCommand("add \"hihihi\" at 09092014").getNewName());
		assertEquals(CommandTest.getNewDeadline(), 
				parser.parseCommand("add \"hihihi 12345678 \" @ 09092014").getNewDeadline());
		
		assertEquals(CommandType.INVALID_DATE, parser.parseCommand("a \"hihihi 12345678 \" at 12132014").getType());
		
		
		// test hours
		CommandTest.setNewDeadline(new GregorianCalendar(2014,8,9,13,24));
		assertEquals(CommandTest.getNewDeadline(), 
				parser.parseCommand("add \"hihihi\" @ 09092014_1324").getNewDeadline());
		
		// test invalid
		assertEquals(CommandType.INVALID_COMMAND, parser.parseCommand("ad \"hihihi\" at 09082014").getType());
		assertEquals(CommandType.INVALID_FORMAT, parser.parseCommand("a hihihi\" at 09082014").getType());
		assertEquals(CommandType.INVALID_FORMAT, parser.parseCommand("add \"hihihi\" 09082014").getType());
		assertEquals(CommandType.INVALID_FORMAT, parser.parseCommand("add \"hihihi\" at 090814").getType());
		assertEquals(CommandType.INVALID_FORMAT, parser.parseCommand("a \"hihihi\" a 09082014").getType());
		
		
		
	}
	
	@Test
	public void testAddEvent(){
		// test add event
		CommandTest.setType(CommandType.ADD_EVENT);
		CommandTest.setNewName("hihihi");
		CommandTest.setNewStartDate(new GregorianCalendar(2014,7,7));
		CommandTest.setNewEndDate(new GregorianCalendar(2014,7,9));
		
		
		assertEquals(CommandTest.getType(),
				parser.parseCommand("add \"hihihi\" from 07082014 to 09082014").getType());
		assertEquals(CommandTest.getNewName(), 
				parser.parseCommand("add \"hihihi\" from 07082014 to 09082014").getNewName());
		assertEquals(CommandTest.getNewStartDate(), 
				parser.parseCommand("add \"hihihi\" from 07082014 to 09082014").getNewStartDate());
		assertEquals(CommandTest.getNewEndDate(), 
				parser.parseCommand("add \"hihihi\" from 07082014 to 09082014").getNewEndDate());
		
		
		// test time
		CommandTest.setNewStartDate(new GregorianCalendar(2014,7,7,13,24));
		CommandTest.setNewEndDate(new GregorianCalendar(2014,7,9,15,54));
		assertEquals(CommandTest.getNewStartDate(), 
				parser.parseCommand("add \"hihihi\" from 07082014_1324 to 09082014_1554").getNewStartDate());
		assertEquals(CommandTest.getNewEndDate(), 
				parser.parseCommand("add \"hihihi 12345678 \" from 07082014_1324 to 09082014_1554").getNewEndDate());
		
		// test invalid 
		assertEquals(CommandType.INVALID_FORMAT, 
				parser.parseCommand("add \"hihihi\" from 07082014 to 09082014_1554").getType());
		assertEquals(CommandType.INVALID_FORMAT, 
				parser.parseCommand("add \"hihihi\" from 07082014_1324 to 09082014").getType());
		
		assertEquals(CommandType.INVALID_FORMAT,
				parser.parseCommand("add \"hihihi\" fro 07082014 to 09082014").getType());
		assertEquals(CommandType.INVALID_FORMAT,
				parser.parseCommand("add \"hihihi\" from 0702014 to 0908204").getType());
		assertEquals(CommandType.INVALID_FORMAT,
				parser.parseCommand("add \"hihihi\" from 0702014 2 0908204").getType());
		assertEquals(CommandType.INVALID_FORMAT,
				parser.parseCommand("add \"hihihi from 0702014 2 0908204").getType());
	}
	
	@Test
	public void testAddFloat(){
		CommandTest.setType(CommandType.ADD_FLOAT);
		CommandTest.setNewName("hihihi");
		
		// test event add
		assertEquals(CommandTest.getType(),
				parser.parseCommand("add \"hihihi\"").getType());
		assertEquals(CommandTest.getNewName(), 
				parser.parseCommand("a \"hihihi\"").getNewName());
		
		// test invalid
		assertEquals(CommandType.INVALID_FORMAT, 
				parser.parseCommand("add \"hihihi\" dfs").getType());
	}
	
	@Test
	public void testMark(){
		CommandTest.setType(CommandType.MARK);
		CommandTest.setName("blah95");
		
		// test mark
		assertEquals(CommandTest.getType(),
				parser.parseCommand("mark \"blah95\"").getType());
		assertEquals(CommandTest.getName(), 
				parser.parseCommand("m \"blah95\"").getName());
		
		// test invalid
		assertEquals(CommandType.INVALID_FORMAT, 
				parser.parseCommand("mark \"blah95").getType());
	}
	
	@Test
	public void testMarkID(){
		
		CommandTest.setType(CommandType.MARK_ID);
		CommandTest.setID(666);
		
		// test mark id
		assertEquals(CommandTest.getType(),
				parser.parseCommand("mark 666").getType());
		assertEquals(CommandTest.getID(), 
				parser.parseCommand("mark 666").getID());
	}
	@Test
	public void testDelete(){
		CommandTest.setType(CommandType.DELETE);
		CommandTest.setName("blah95");
		
		// test delete
		assertEquals(CommandTest.getType(),
				parser.parseCommand("del \"blah95\"").getType());
		assertEquals(CommandTest.getName(), 
				parser.parseCommand("del \"blah95\"").getName());
		// test invalid
		assertEquals(CommandType.INVALID_FORMAT, 
				parser.parseCommand("del \"blah95").getType());
	}
	
	@Test
	public void testDeleteID(){
		CommandTest.setType(CommandType.DELETE_ID);
		CommandTest.setID(666);
		
		// test delete ID
		assertEquals(CommandTest.getType(),
				parser.parseCommand("delete 666").getType());
		assertEquals(CommandTest.getID(), 
				parser.parseCommand("d 666").getID());
		
	}
	@Test
	public void testSearchName(){
		CommandTest.setType(CommandType.SEARCH_NAME);
		CommandTest.setName("blah95");
		
		// test search
		assertEquals(CommandTest.getType(),
				parser.parseCommand("search \"blah95\"").getType());
		assertEquals(CommandTest.getName(), 
				parser.parseCommand("s \"blah95\"").getName());
		// test invalid
		assertEquals(CommandType.INVALID_FORMAT, 
				parser.parseCommand("search \"blah95").getType());
	}
	
	@Test
	public void testSearchID(){
		CommandTest.setType(CommandType.SEARCH_ID);
		CommandTest.setID(666);
		
		// test search ID
		assertEquals(CommandTest.getType(),
				parser.parseCommand("s 666").getType());
		assertEquals(CommandTest.getID(), 
				parser.parseCommand("s 666").getID());
	}
	
	@Test
	public void testSearchDate(){
		CommandTest.setType(CommandType.SEARCH_DATE);
		CommandTest.setDeadline(new GregorianCalendar(2014,7,9));
		
		// test search date
		assertEquals(CommandTest.getType(),
				parser.parseCommand("s 09082014").getType());
		assertEquals(CommandTest.getDeadline(), 
				parser.parseCommand("s 09082014").getDeadline());
		
		// test search with time
		CommandTest.setDeadline(new GregorianCalendar(2014,7,9,11,23));
		assertEquals(CommandTest.getDeadline(), 
				parser.parseCommand("s 09082014_1123").getDeadline());
	}
	
	@Test
	public void testEditName(){
		
		// test edit 
		CommandTest.setType(CommandType.EDIT_NAME);
		CommandTest.setName("hihihi");
		CommandTest.setNewName("HEHEHE");
		
		assertEquals(CommandTest.getType(), 
				parser.parseCommand("ed \"hihihi\" to \"HEHEHE\"").getType());
		assertEquals(CommandTest.getNewName(), 
				parser.parseCommand("e \"hihihi\" to \"HEHEHE\"").getNewName());
		assertEquals(CommandTest.getName(), 
				parser.parseCommand("EDIT \"hihihi\" to \"HEHEHE\"").getName());
		
		// tets edit ID
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
		
		// test edit event
		CommandTest.setType(CommandType.EDIT_EVENT);
		CommandTest.setName("hihihi");
		CommandTest.setNewStartDate(new GregorianCalendar(2014,7,7));
		CommandTest.setNewEndDate(new GregorianCalendar(2014,7,9));
		
		
		assertEquals(CommandTest.getType(),
				parser.parseCommand("edit \"hihihi\" to from 07082014 to 09082014").getType());
		assertEquals(CommandTest.getName(), 
				parser.parseCommand("edit \"hihihi\" to from 07082014 to 09082014").getName());
		assertEquals(CommandTest.getNewStartDate(), 
				parser.parseCommand("edit \"hihihi\" to from 07082014 to 09082014").getNewStartDate());
		assertEquals(CommandTest.getNewEndDate(), 
				parser.parseCommand("edit \"hihihi\" to from 07082014 to 09082014").getNewEndDate());
		
		// test edit ID event
		CommandTest.setType(CommandType.EDIT_ID_EVENT);
		CommandTest.setID(666);
		CommandTest.setNewStartDate(new GregorianCalendar(2014,7,7));
		CommandTest.setNewEndDate(new GregorianCalendar(2014,7,9));
		
		
		assertEquals(CommandTest.getType(),
				parser.parseCommand("edit 666 to from 07082014 to 09082014").getType());
		assertEquals(CommandTest.getID(), 
				parser.parseCommand("edit 666 to from 07082014 to 09082014").getID());
		assertEquals(CommandTest.getNewStartDate(), 
				parser.parseCommand("edit 666 to from 07082014 to 09082014").getNewStartDate());
		assertEquals(CommandTest.getNewEndDate(), 
				parser.parseCommand("edit 666 to from 07082014 to 09082014").getNewEndDate());
		
		// tets with time
		CommandTest.setNewStartDate(new GregorianCalendar(2014,7,7,13,55));
		CommandTest.setNewEndDate(new GregorianCalendar(2014,7,9,11,44));
		
		assertEquals(CommandTest.getNewStartDate(), 
				parser.parseCommand("edit 666 to from 07082014_1355 to 09082014_1144").getNewStartDate());
		assertEquals(CommandTest.getNewEndDate(), 
				parser.parseCommand("edit 666 to from 07082014_1355 to 09082014_1144").getNewEndDate());
	}
	
	@Test
	public void testEditDate(){
		// test edit date
		CommandTest.setType(CommandType.EDIT_DATE);
		CommandTest.setName("hihihi");
		CommandTest.setNewDeadline(new GregorianCalendar(2014,7,9));
		
		
		assertEquals(CommandTest.getType(),
				parser.parseCommand("edit \"hihihi\" to 09082014").getType());
		assertEquals(CommandTest.getName(), 
				parser.parseCommand("edit \"hihihi\" to 09082014").getName());
		assertEquals(CommandTest.getNewDeadline(), 
				parser.parseCommand("edit \"hihihi\" to 09082014").getNewDeadline());
		
		// tets with time
		CommandTest.setNewDeadline(new GregorianCalendar(2014,7,9,1,23));
		
		assertEquals(CommandTest.getNewDeadline(), 
				parser.parseCommand("edit \"hihihi\" to 09082014_0123").getNewDeadline());
		
		// tets ID edit date
		CommandTest.setType(CommandType.EDIT_ID_DATE);
		CommandTest.setID(666);
		CommandTest.setNewDeadline(new GregorianCalendar(2014,7,9));
		
		
		assertEquals(CommandTest.getType(),
				parser.parseCommand("edit 666 to 09082014").getType());
		assertEquals(CommandTest.getID(), 
				parser.parseCommand("edit 666 to 09082014").getID());
		assertEquals(CommandTest.getNewDeadline(), 
				parser.parseCommand("edit 666 to 09082014").getNewDeadline());
		
	
	}
	@Test
	public void testHelp(){
		
		// test help
		CommandTest.setType(CommandType.HELP_GENERAL);
		
		assertEquals(CommandTest.getType(), parser.parseCommand("help").getType());
		
		CommandTest.setType(CommandType.HELP_ADD);
		
		assertEquals(CommandTest.getType(), parser.parseCommand("help add").getType());
		
		CommandTest.setType(CommandType.HELP_EDIT);
		
		assertEquals(CommandTest.getType(), parser.parseCommand("help edit").getType());
		
		CommandTest.setType(CommandType.HELP_SEARCH);
		
		assertEquals(CommandTest.getType(), parser.parseCommand("help search").getType());
		
		CommandTest.setType(CommandType.HELP_DELETE);
		
		assertEquals(CommandTest.getType(), parser.parseCommand("help del").getType());
		assertEquals(CommandTest.getType(), parser.parseCommand("help delete").getType());
		
		CommandTest.setType(CommandType.HELP_MARK);
		
		assertEquals(CommandTest.getType(), parser.parseCommand("help mark").getType());

		CommandTest.setType(CommandType.HELP_UNDO);
		
		assertEquals(CommandTest.getType(), parser.parseCommand("help undo").getType());
		// test invalid
		assertEquals(CommandType.INVALID_FORMAT, parser.parseCommand("help commands").getType());
		assertEquals(CommandType.INVALID_COMMAND, parser.parseCommand("helpppp add").getType());
		assertEquals(CommandType.INVALID_FORMAT, parser.parseCommand("help adds").getType());
	}


}
