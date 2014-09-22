package doornot.parser;
import static org.junit.Assert.*;
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
		assertEquals(parser.parseCommand("add hihihi at 09082014"), addCommandTest);
	}
	
	

}
