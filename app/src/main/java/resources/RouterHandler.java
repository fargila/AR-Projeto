package resources;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import main.Route;

public class RouterHandler extends DefaultHandler {

    private boolean in_nametag;
    private boolean in_descriptiontag;
    private boolean in_icontag;

    private Route currentRoute;
    private Route route;
    private StringBuffer buffer;

    public void endDocument() throws SAXException {
    }

    public void startDocument() throws SAXException {
        in_nametag = false;
        in_descriptiontag = false;
        in_icontag = false;
        currentRoute = new Route();
        route = new Route();
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        if (in_nametag)
        {
            buffer.append(ch, start, length);
            currentRoute.name = buffer.toString();
        }
        else if (in_descriptiontag)
        {
            buffer.append(ch, start, length);
            currentRoute.description = buffer.toString();
        }
        else if (in_icontag)
        {
            buffer.append(ch, start, length);
            //currentRoute.icon = buffer.toString();
        }
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        buffer = new StringBuffer();
        if (localName.equals("route"))
        {
            currentRoute.id = Integer.parseInt(attributes.getValue("id"));
            currentRoute.version = Float.parseFloat(attributes.getValue("version"));
        }
        else if (localName.equals("name"))
            in_nametag = true;
        else if (localName.equals("description"))
            in_descriptiontag = true;
        else if (localName.equals("icon"))
            in_icontag = true;
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equals("route"))
        {
            route = currentRoute;
            route.name = route.name.replace("\\n", "\n");
            route.description = route.description.replace("\\n", "\n").replace("\\t", "\t");
        }
        else if (localName.equals("name"))
            in_nametag = false;
        else if (localName.equals("description"))
            in_descriptiontag = false;
        else if (localName.equals("icon"))
            in_icontag = false;

    }

    public Route getParsedData() {
        return route;
    }
}