package resources;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

import kotlin.Pair;
import main.PointOI;

public class PointsHandler extends DefaultHandler {

    private boolean in_titletag;
    private boolean in_icontag;
    private boolean in_pointdescriptiontag;
    private boolean in_imagestag;
    private boolean in_imagetag;
    private boolean in_videotag;
    private boolean in_artag;

    private ArrayList<PointOI> points;
    private PointOI currentPoint;
    private ArrayList<Pair<String, String>> currentImgs;
    private String imageName = "";
    private String imageUrl = "";
    private StringBuffer buffer;
    private String arFiles;

    public void startDocument() throws SAXException {
        in_titletag = false;
        in_icontag = false;
        in_imagestag = false;
        in_videotag = false;
        in_artag = false;
        in_imagetag = false;
        in_pointdescriptiontag = false;
        points = new ArrayList<PointOI>();
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        buffer = new StringBuffer();
        if(localName.equals("point"))
        {
            currentPoint = new PointOI();
            arFiles = "";
            currentPoint.id = Integer.parseInt(attributes.getValue("id"));
        }
        else if(localName.equals("coords"))
        {
            currentPoint.coords[0] = Float.parseFloat(attributes.getValue("x"));
            currentPoint.coords[1] = Float.parseFloat(attributes.getValue("y"));
        }
        else if (localName.equals("title"))
            in_titletag = true;
        else if (localName.equals("pointicon"))
            in_icontag = true;
        else if (localName.equals("pointdescription"))
            in_pointdescriptiontag = true;
        else if (localName.equals("images"))
            currentImgs = new ArrayList<Pair<String, String>>();
        else if (localName.equals("image"))
        {
            in_imagetag = true;
            imageUrl = attributes.getValue("url");
        }
        else if (localName.equals("video"))
            in_videotag = true;
        else if (localName.equals("ar"))
        {
            in_artag = true;
            currentPoint.arCoords[0] = Float.parseFloat(attributes.getValue("lat"));
            currentPoint.arCoords[1] = Float.parseFloat(attributes.getValue("long"));
            currentPoint.arCoords[2] = Float.parseFloat(attributes.getValue("alt"));
            this.arFiles = attributes.getValue("file");
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        if(in_titletag)
        {
            buffer.append(ch, start, length);
            currentPoint.title = buffer.toString();
        }
        else if(in_icontag)
        {
            buffer.append(ch, start, length);
            currentPoint.icon = buffer.toString();
        }
        else if(in_pointdescriptiontag)
        {
            buffer.append(ch, start, length);
            currentPoint.pointdescription = buffer.toString();
        }
        else if(in_imagestag)
            buffer.append(ch, start, length);

        else if(in_imagetag)
        {
            buffer.append(ch, start, length);
            imageName = buffer.toString();
        }
        else if(in_videotag)
        {
            buffer.append(ch, start, length);
            currentPoint.video = buffer.toString();
        }
        else if (in_artag)
        {
            buffer.append(ch, start, length);
            currentPoint.textAr = buffer.toString();
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equals("point"))
        {
            currentPoint.title = currentPoint.title.replace("\\n", "\n");
            currentPoint.pointdescription = currentPoint.pointdescription.replace("\\n", "\n").replace("\\t", "\t");
            currentPoint.textAr = currentPoint.textAr.replace("\\n", "\n").replace("\\t", "\t");
            if (!this.arFiles.equals(""))
                currentPoint.urlfilesAr = arFiles.split(",");

            points.add(currentPoint);
        }
        else if (localName.equals("title"))
            in_titletag = false;
        else if (localName.equals("pointicon"))
            in_icontag = false;
        else if (localName.equals("pointdescription"))
            in_pointdescriptiontag = false;
        else if (localName.equals("images"))
            currentPoint.images = currentImgs;
        else if (localName.equals("image"))
        {
            in_imagetag = false;
            currentImgs.add(new Pair<String, String>(imageName, imageUrl));
        }
        else if (localName.equals("video"))
            in_videotag = false;
        else if (localName.equals("ar"))
            in_artag = false;
    }

    public ArrayList<PointOI> getParsedData() {
        return points;
    }
}

