package resources;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

import main.MinRouteInfo;

public class MinRouteInfoHandler extends DefaultHandler {

    /*
    ->startDocument(): Este método é chamado quando o parser SAX inicia o processamento do documento
    XML. Ele inicializa as variáveis de controle e cria uma lista para armazenar as informações sobre as rotas mínimas.

    ->startElement(String uri, String localName, String qName, Attributes attributes): quando
    o parser SAX encontrar uma tag de abertura de elemento no XML. Ele inicializa algumas
    variáveis de controle com base no nome do elemento encontrado. Por exemplo, se encontrar a tag <route>,
    cria um novo objeto MinRouteInfo para representar a rota atual e atribui seu ID e versão com base nos atributos da tag.

    ->characters(char[] ch, int start, int length): Este método é chamado quando o parser SAX encontra
    texto dentro de um elemento XML. Ele captura o texto e o armazena em um buffer, que será utilizado
    posteriormente para preencher os atributos da rota mínima.

    ->endElement(String uri, String localName, String qName): Este método é chamado quando o parser SAX
    encontra uma tag de fechamento de elemento no XML. Ele finaliza o processamento do elemento atual e
    atualiza as variáveis de controle conforme necessário. Por exemplo, se encontrar a tag de fechamento
    </route>, adiciona a rota mínima atual à lista de rotas.

    ->getParsedData(): Este método retorna a lista de rotas mínimas após o processamento do documento XML.
    Assim como o manipulador anterior, este manipulador SAX é projetado especificamente para lidar com um
    formato de XML específico, onde cada rota mínima é representada por uma série de elementos aninhados,
    como name, url, arfiles, etc. Ele lê esses elementos do XML e os transforma em objetos MinRouteInfo,
    que são posteriormente armazenados em uma lista para uso posterior no aplicativo.*/

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
                currentRoute.urlArFiles = this.arfiles.split(",");
            else
                currentRoute.urlArFiles = null;
        }
    }

    public ArrayList<MinRouteInfo> getParsedData() { return allroutes; }
}
