/**
 * Project: Callga
 * Create At 2014-10-21.
 * @author hhool
 */
package com.cchat.common.base.data.XmlParse;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;


public abstract class XmlParseBase {

    private static final String TAG = XmlParseBase.class.getName();

    protected abstract XMLHandler getHandler();

    public XmlParseBase() {

    }

    public Object parse(InputStream in) {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = null;
        try {
            parser = factory.newSAXParser();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return null;
        } catch (SAXException e) {
            e.printStackTrace();
            return null;
        }
        XMLHandler handler = getHandler();
        try {
            parser.parse(in, handler);
        } catch (SAXException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return handler.getResult();
    }

    public abstract class XMLHandler extends DefaultHandler {
        protected StringBuilder mBuilder;

        public abstract Object getResult();

        @Override
        public void characters(char[] ch, int start, int length)
                throws SAXException {
            super.characters(ch, start, length);
            mBuilder.append(ch, start, length);
        }

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
            mBuilder = new StringBuilder();
        }

        @Override
        public void startElement(String uri, String localName, String qName,
                                 Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            mBuilder.setLength(0);
        }
    }
}
