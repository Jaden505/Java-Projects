package nl.hva.ict.ads.utils.xml;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.util.StreamReaderDelegate;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class XMLParser extends StreamReaderDelegate {
    private static final Logger LOG = Logger.getLogger(XMLParser.class.getName());
//    private String resourceName = "";

    static {
        try {
            LogManager.getLogManager().readConfiguration(XMLParser.class.getResourceAsStream("/logging.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public XMLParser(String resourceName) {
//        this(XMLParser.class.getClassLoader().getResourceAsStream(resourceName));
//        this.resourceName = resourceName;
//    }
//
    public XMLParser(InputStream inputStream) {
        super(createXMLStreamReader(inputStream));
    }

    public static XMLStreamReader createXMLStreamReader(InputStream input) {
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLStreamReader xmlStreamReader = null;
        try {
            xmlStreamReader = inputFactory.createXMLStreamReader(input);
        } catch (XMLStreamException e) {
            LOG.severe("Cannot attach XMLStreamReader to file stream content:\n" + e);
        }
        return xmlStreamReader;
    }

    public boolean tryNext() throws XMLStreamException {
        if (hasNext()) {
            next();
            return true;
        }
        return false;
    }

    public boolean nextBeginTag(String tag) throws XMLStreamException {

        int fromEvent = getEventType();
        int skipCount = 0;
        while (!isStartElement() && !isEndElement() && hasNext()) {
            if (!isWhiteSpace()) {
                skipCount++;
            }
            next();
        }
        int toEvent = getEventType();
        String hit = (isStartElement() ? getLocalName() : "/");

        if (skipCount > 0 && !hit.equals(tag)) {
            LOG.fine("nextBeginTag(" + tag + "): skipped " + skipCount +
                    " from event" + fromEvent + " to event" + toEvent + " hit <" + hit + ">");
        }
        return hit.equals(tag);
    }

    public String nextEndTag() throws XMLStreamException {
        while (!isEndElement() && hasNext()) {
            next();
        }
        return (isEndElement() ? getLocalName() : "/");
    }

    public boolean nextEndTag(String tag) throws XMLStreamException {
        int fromEvent = getEventType();
        int skipCount = 0;
        while (!isEndElement() && hasNext()) {
            if (!isWhiteSpace()) {
                skipCount++;
            }
            next();

        }
        int toEvent = getEventType();
        String hit = (isEndElement() ? getLocalName() : "/");
        if (skipCount > 0 && !tag.equals(hit)) {
            LOG.fine("nextEndTag(" + tag + "): skipped " + skipCount +
                    " from event" + fromEvent + " to event" + toEvent + " hit </" + hit + ">");
        }
        return tag.equals(hit);
    }

    public boolean findBeginTag(String tag) throws XMLStreamException {
        boolean hit = nextBeginTag(tag);
        while (!hit && hasNext()) {
            next();
            hit = nextBeginTag(tag);
        }
        return hit;
    }

    public boolean findAndAcceptEndTag(String tag) throws XMLStreamException {
        boolean hit = nextEndTag(tag);
        while (!hit && hasNext()) {
            next();
            hit = nextEndTag(tag);
        }
        if (hit) {
            next();
        }
        return hit;
    }

    public boolean skipMandatoryElement(String tag) throws XMLStreamException {
        if (findBeginTag(tag)) {
            findAndAcceptEndTag(tag);
            return true;
        }
        return false;
    }

    public double getDoubleAttributeValue(String ns, String name, double defaultValue) {
        String value = getAttributeValue(ns, name);
        return (value != null ? Double.parseDouble(value) : defaultValue);
    }

    public int getIntegerAttributeValue(String ns, String name, int defaultValue) {
        String value = getAttributeValue(ns, name);
        return (value != null ? Integer.parseInt(value) : defaultValue);
    }

    public void logStatus() {
        LOG.config("start=" + isStartElement() +
                " end=" + isEndElement() +
                " chars=" + isCharacters() +
                " whitespace=" + isWhiteSpace() +
                " name=" + getLocalName());
    }
}
