package apapl.tests.parser;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import apapl.Parser;
import apapl.parser.ParseMASException;

public class BigTest {

	@Test
	  public void testMultiplication() {
		
		Parser parser = new Parser();
		
		try {
			parser.parseMas(new File("resources/tests/Good/Harry and Sally/harrysally.mas"));
		} catch (ParseMASException e) {
			fail("Parser failed.");
			e.printStackTrace();
		}
		
	  }

}
