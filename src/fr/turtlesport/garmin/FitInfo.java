package fr.turtlesport.garmin;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

public class FitInfo {
  private String id;

  private String softwareVersion;

  private String displayName;

  private File dir;

  protected FitInfo(File dir) throws SAXException,
                             IOException,
                             ParserConfigurationException {
    this.dir = dir;
    
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();

    Document doc = builder.parse(new File(dir, "GarminDevice.XML"));

    id = getText(doc.getDocumentElement(), "Id");
    displayName = getText(doc.getDocumentElement(), "DisplayName");
    softwareVersion = getText(doc.getDocumentElement(), "SoftwareVersion");
  }

  public String getId() {
    return id;
  }

  public String getSoftwareVersion() {
    return softwareVersion;
  }

  public String getDisplayName() {
    return displayName;
  }

  public File getDir() {
    return dir;
  }

  @Override
  public String toString() {
    return String.format("id=%s displayName=%s softwareVersion=%s",
                         id,
                         displayName,
                         softwareVersion);
  }

  private String getText(Element parentEl, String nodeName) {
    NodeList nl = parentEl.getElementsByTagName(nodeName);
    if (nl.getLength() > 0) {
      Element el = (Element) nl.item(0);
      NodeList nlChild = el.getChildNodes();
      for (int i = 0; i < nlChild.getLength(); i++) {
        if (nlChild.item(i) instanceof Text) {
          return nlChild.item(i).getNodeValue();
        }
      }
    }
    return null;
  }
}
