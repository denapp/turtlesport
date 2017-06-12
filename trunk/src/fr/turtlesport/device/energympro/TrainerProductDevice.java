package fr.turtlesport.device.energympro;

import fr.turtlesport.device.IProductDevice;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

/**
 * Copyright (c) 2008-2016, Turtle Sport
 * <p/>
 * This file is part of Turtle Sport.
 * <p/>
 * Turtle Sport is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 2.1 of the License, or
 * (at your option) any later version.
 * <p/>
 * Turtle Sport is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with Turtle Sport.  If not, see <http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html>.
 */
public class TrainerProductDevice implements IProductDevice {
    protected static String FILENAME = "Device.xml";

    private String product;

    private String version;

    protected TrainerProductDevice(File file) throws IOException {
        parse(file);
    }

    public String getProduct() {
        return product;
    }

    public String getVersion() {
        return version;
    }

    private void parse(File file) throws IOException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);
            doc.normalize();

            NodeList nodeList;
            Element el;

            // Description
            nodeList = doc.getElementsByTagName("Model");
            if (nodeList.getLength() > 0) {
                el = (Element) nodeList.item(0);
                nodeList = el.getElementsByTagName("Description");
                if (nodeList.getLength() > 0) {
                    Element elVersion = (Element) nodeList.item(0);
                    product = elVersion.getTextContent();
                }
            }
            // Version
            nodeList = doc.getElementsByTagName("SystemInfo");
            if (nodeList.getLength() > 0) {
                el = (Element) nodeList.item(0);
                nodeList = el.getElementsByTagName("Version");
                if (nodeList.getLength() > 0) {
                    el = (Element) nodeList.item(0);
                    version = el.getTextContent();
                }
            }

        } catch (ParserConfigurationException e) {
            throw new IOException(e);
        }
        catch(SAXException e) {
            throw new IOException(e);
        }
    }

    @Override
    public String displayName() {
        return product + " v" + version;
    }

    @Override
    public String id() {
        return product;
    }

    @Override
    public String softwareVersion() {
        return version;
    }
}

 

