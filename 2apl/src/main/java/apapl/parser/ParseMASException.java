package apapl.parser;

import java.io.*;

/**
 * Signals that an exception has occurred while parsing the module specification.
 * A ParseMASException is raised by the {@link apapl.Parser} in case of a syntax 
 * error. It is also raised in case of an I/O error while reading the specification 
 * file. This exception extends the original {@link apapl.parser.ParseException} by
 * providing extra information about the file that caused the error.
 */
public class ParseMASException extends ParseException
{
  private File file;

  /**
   * Constructs a ParseMASException with the same information as
   * the ParseException.
   * 
   * @param file the file that was parsed
   * @param e the parse exception as raised by the javaCC parser
   */
	public ParseMASException(File file, ParseException e)
	{
	  // 'Cast' this class to a ParseException
	  super(e.currentToken, e.expectedTokenSequences, e.tokenImage);
	  this.file = file;
	}

	/**
	 * Constructs a ParseMASException with information about the exception.
	 * 
	 * @param file the file that was parsed
	 * @param message the information about the exception
	 */
	public ParseMASException(File file, String message)
	{
	  super(message);
	  this.file = file;
	}
  
	/**
	 * Returns the file that caused the error.
	 * 
	 * @return the file that caused the error
	 */
	public File getFile()
	{
	  return file;
	}
}
