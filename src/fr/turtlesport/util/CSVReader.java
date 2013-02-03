package fr.turtlesport.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import fr.turtlesport.log.TurtleLogger;

/**
 * @author Denis Apparicio
 * 
 */
public class CSVReader {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(CSVReader.class);
  }

  private BufferedReader      br;

  private String[]            headers;

  private List<String[]>      datas     = new ArrayList<String[]>();

  private static final String DELIMITER = ",";

  public CSVReader(Reader in) {
    br = new BufferedReader(in);
  }

  public CSVReader(BufferedReader in) {
    br = in;
  }

  /**
   * Lecture des headers
   * 
   * @throws IOException
   */
  public String[] readHeader() throws IOException {
    br.readLine();
    headers = readLine(br.readLine());
    return headers;
  }

  /**
   * Lecture d'une ligne
   * 
   * @throws IOException
   */
  public String[] readLine() throws IOException {
    String line = br.readLine();
    String[] res = readLine(line);
    if (res != null && res.length == headers.length) {
      return res;
    }
    return null;
  }

  /**
   * Lecture des donn&eacute;s
   * 
   * @throws IOException
   */
  public List<String[]> readDatas() throws IOException {
    String line;
    while ((line = br.readLine()) != null) {
      String[] tab = readLine(line);
      if (tab.length != headers.length) {
        datas.add(tab);
      }
    }
    return datas;
  }

  public void close() throws IOException {
    br.close();
  }

  private String[] readLine(String line) {
    if (line ==null) {
      return new String[0]; 
    }
    log.info("before : " +line);
    while (line.contains(",,")) {
      line = line.replaceAll(",,", ", ,");
    }
    if (line.endsWith("<br />")) {
      line = line.substring(0, line.length() - 6);
    }
    log.info("after  : " +line);
    
    List<String> tokens = new ArrayList<String>();
    StringTokenizer st = new StringTokenizer(line, DELIMITER);
    while (st.hasMoreTokens()) {
      String t = st.nextToken();
      tokens.add(t);
    }
    return tokens.toArray(new String[tokens.size()]);
  }

}
