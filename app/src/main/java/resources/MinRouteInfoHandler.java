package resources;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

public class MinRouteInfoHandler extends DefaultHandler {
    private boolean in_nametag;
    private boolean in_urltag;
    private boolean in_arfilestag;
    private MinRouteInfo currentRoute;
    private String arfiles;
    private ArrayList<MinRouteInfo> allroutes;

    public void startDocument() throws SAXException {
        in_nametag = false;
        in_urltag = false;
        in_arfilestag = false;
        allroutes = new ArrayList<MinRouteInfo>();
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException{
        StringBuffer buffer = new StringBuffer();
        switch (localName) {
            case "route":
                currentRoute = new MinRouteInfo();
                currentRoute.id = Integer.parseInt(attributes.getValue("id"));
                currentRoute.version = Float.parseFloat(attributes.getValue("version"));
                break;
            case "name":
                in_nametag = true;
                break;
            case "url":
                in_nametag = true;
                break;
            case "arfiles":
                arfiles = "";
                in_arfilestag = true;
                break;
        }
    }

    public void enElement(String uri, String localName, String qName) throws SAXException{
        if(localName.equals("routes"))
        {
            currentRoute.name = currentRoute.name.replace("\\n", "\n");
            allroutes.add(currentRoute);
        }
        else if(localName.equals("name"))
            in_nametag = false;
        else if(localName.equals("url"))
            in_urltag = false;
        else if(localName.equals("arfiles"))
        {
            in_arfilestag = false;
            if(!arfiles.equals(""))
                currentRoute.urlArfiles = this.arfiles.split(",");
            else
                currentRoute.urlArFiles = null;
        }
    }

    public ArrayList<MinRouteInfo> getParsedData() { return allroutes; }
}
