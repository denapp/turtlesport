package fr.turtlesport.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import fr.turtlesport.log.TurtleLogger;

/**
 * @author Denis Apparicio
 * 
 */
public final class SchemaValidator {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(SchemaValidator.class);
  }

  // static {
  // System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
  // "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
  // }

  private SchemaValidator() {
  }

  /**
   * Validation XML Schema.
   * 
   * @param schemaUrl
   * @param xmlFile
   * 
   * @throws IOException
   */
  public static boolean validateSchema(Schema schema, InputStream xml) throws IOException,
                                                                      ParserConfigurationException,
                                                                      SAXException {

    Validator validator = schema.newValidator();

    
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    factory.setValidating(true);
    // factory
    // .setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage",
    // "http://www.w3.org/2001/XMLSchema");
    factory.setSchema(schema);

    DocumentBuilder builder = factory.newDocumentBuilder();
    MyValidator handler = new MyValidator();
    builder.setErrorHandler(handler);

    Document docXml = builder.parse(xml);
    try {
      validator.validate(new DOMSource(docXml));
    }
    catch (SAXException e) {
      log.error("", e);
      return false;
    }

    return true;
  }

  /**
   * Validation XML Schema.
   * 
   * @param xml
   * @param xmlDocumentUrl
   * @throws ParserConfigurationException
   * @throws ParserConfigurationException
   * @throws IOException
   * @throws SAXException
   */
  public static boolean validateSchema(String xml) throws IOException,
                                                  ParserConfigurationException,
                                                  SAXException {
    return validateSchema((Schema) null, new FileInputStream(xml));
  }

  /**
   * Validation XML Schema.
   * 
   * @param schemaUrl
   * @param xmlFile
   * 
   * @throws IOException
   */
  public static boolean validateSchema(InputStream schema, InputStream xml) throws IOException,
                                                                           ParserConfigurationException,
                                                                           SAXException {
    SchemaFactory factory = SchemaFactory
        .newInstance("http://www.w3.org/2001/XMLSchema");
    return validateSchema(factory.newSchema(new StreamSource(schema)), xml);
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private static class MyValidator extends DefaultHandler {
    private boolean           isValid = true;

    private SAXParseException saxParseException;

    public boolean isValid() {
      return isValid;
    }

    public SAXParseException getSaxParseException() {
      return saxParseException;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.helpers.DefaultHandler#error(org.xml.sax.SAXParseException)
     */
    public void error(SAXParseException exception) throws SAXException {
      isValid = false;
      saxParseException = exception;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.helpers.DefaultHandler#fatalError(org.xml.sax.SAXParseException)
     */
    public void fatalError(SAXParseException exception) throws SAXException {
      isValid = false;
      saxParseException = exception;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.helpers.DefaultHandler#warning(org.xml.sax.SAXParseException)
     */
    public void warning(SAXParseException exception) throws SAXException {
    }
  }
}
