package resources;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

import kotlin.Pair;
import main.PointOI;

public class PointsHandler extends DefaultHandler {

    /*Essa classe é um manipulador SAX (Simple API for XML) para processar arquivos XML que contêm
     informações sobre os pontos de interesse (POIs). O SAX é uma API de processamento de XML que
     permite analisar os documentos XML de forma sequencial.

    -> startDocument(): É chamado quando o parser SAX inicia o processamento do documento
    XML. Ele inicializa as variáveis de controle e cria uma lista para armazenar os POIs.

    -> startElement(String uri, String localName, String qName, Attributes attributes): Método para quando
    o parser SAX encontra uma tag de abertura de elemento no XML. Ele inicializa algumas
    variáveis de controle com base no nome do elemento. Por exemplo, se encontrar a tag <point>,
    cria um novo objeto PointOI para representar o ponto de interesse atual e atribui seu ID com base
    nas informações da tag.

    -> characters(char[] ch, int start, int length): Este método é chamado quando o parser SAX encontra
    texto dentro de um elemento XML. Ele captura o texto e o armazena em um buffer, que será utilizado
    posteriormente para preencher os atributos do ponto de interesse atual.

    -> endElement(String uri, String localName, String qName): Este método é chamado quando o parser
    SAX encontra uma tag de fechamento de elemento no XML. Ele finaliza o processamento do elemento
    atual e atualiza as variáveis de controle conforme necessário. Por exemplo, se encontrar a tag de
    fechamento </point>, adiciona o ponto de interesse atual à lista de pontos.

    -> getParsedData(): Este método retorna a lista de pontos de interesse após o processamento do documento XML.
    Este manipulador SAX é projetado especificamente para lidar com um formato de XML específico,
    onde cada ponto de interesse é representado por uma série de elementos aninhados, como title,
    pointdescription, images, video, etc. Ele lê esses elementos do XML e os transforma em objetos
    PointOI, que são posteriormente armazenados em uma lista para uso posterior no aplicativo.*/

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
            //currentPoint.video = buffer.toString();
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

