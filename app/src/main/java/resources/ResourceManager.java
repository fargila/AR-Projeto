package resources;

import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.nio.file.Files;
import java.util.ArrayList;

import main.PointOI;
import main.ProjectAR;

public class ResourceManager {

    /*Parte responsável por gerenciaar os recursos em um aplicativo Android, com o foco
    principal na criação e uso dos arquivos XML que contêm as informações sobre rotas e
    pontos de interesse (POIs).

    -> getInstance(): Método usado para obter a instância singleton da classe ResourceManager.

    -> initialize(String root): Método usado para inicializar o ResourceManager. Ele verifica se
    tem armazenamento externo disponível e, se tiver, cria os diretórios necessários e configura o XMLManager.

    -> createXMLTemplate(...): Este método cria um arquivo XML com base no número especificado de
    POIs, criando tags XML para cada ponto de interesse com coordenadas, título, descrição,
    imagens, vídeo e etc.

    -> createXMLFromPoints(...): Este método cria um arquivo XML a partir da lista de POIs
    fornecidos. Parecido com o método 'createXMLTemplate', a diferença é que ele recebe uma
    lista de pontos de interesse em vez de um número.

    -> toTempFile(): Este método salva os POIs atuais em um arquivo temporário.

    -> fromTmpFile(String path, String routeName): Este método lê os POIs de um arquivo momentânio e
    carrega eles no estado mais recente do aplicativo. A lógica é struturada para lidar com exceções que
    possam ocorrer durante a criação e manipulação de arquivos, além de exibir mensagens informativas ao
    usuário por meio de 'Toasts'. Ele também faz uso de recursos da API do Android, como
    Environment.getExternalStorageDirectory() para obter o diretório de armazenamento externo e
    XmlSerializer para criar arquivos XML.*/

    // Declaração de variáveis e objetos
    private static ResourceManager instance; // Instância única da classe ResourceManager
    private boolean initialized; // Indica se o recurso foi inicializado com sucesso
    private String rootPath; // Caminho aonde os arquivos serão armazenados

    private final XMLManager xmlManager; // Gerenciador XML para operações XML

    // Construtor privado para garantir que a classe seja singleton
    private ResourceManager() {
        initialized = false;
        xmlManager = new XMLManager();
    }

    // Método para obter a instância única da classe ResourceManager
    public static ResourceManager getInstance() {
        if (instance == null)
            instance = new ResourceManager();
        return instance;
    }

    // Método para criar os diretórios necessários no armazenamento externo
    private void createDirs(File rootSD) {
        // Criação dos diretórios necessários
        rootSD.mkdir();
        new File(rootSD.getAbsolutePath() + "/ar").mkdir();
        new File(rootSD.getAbsolutePath() + "/config").mkdir();
        new File(rootSD.getAbsolutePath() + "/user").mkdir();
        new File(rootSD.getAbsolutePath() + "/routes").mkdir();
        new File(rootSD.getAbsolutePath() + "/tmp").mkdir();
    }

    // Método para inicializar o recurso ResourceManager
    public void initialize(final String root) {
        try {
            // Verifica se o armazenamento externo está disponível
            String state = Environment.getExternalStorageState();
            if (!Environment.MEDIA_MOUNTED.equals(state))
                throw new Exception("SD Card not available");

            // Obtém o diretório do armazenamento externo
            File sd = Environment.getExternalStorageDirectory();
            // Define o caminho raiz
            rootPath = sd.getAbsolutePath() + "/" + root;

            // Verifica permissões de leitura e escrita no armazenamento externo
            if ((!sd.canRead()) || (!sd.canWrite()))
                throw new Exception("SD Card not available");

            // Log indica que o cartão SD está pronto
            Log.d("FRAGUEL", "SD Card ready");

            // Cria diretórios se não existirem
            File rootSD = new File(rootPath);
            if (!rootSD.exists())
                createDirs(rootSD);

            // Define o caminho raiz para o XMLManager
            xmlManager.setRoot(root);
            // Indica que a inicialização foi bem sucedida
            initialized = true;
        } catch (Exception e) {
            // Log de erros durante a inicialização
            Log.d("FRAGUEL", "Error: " + e);
        }
    }

    // Método para verificar se o ResourceManager foi inicializado com sucesso
    public boolean isInitialized() {
        return initialized;
    }

    // Método para obter o caminho raiz
    public String getRootPath() {
        return rootPath;
    }

    // Método para obter o XMLManager
    public XMLManager getXmlManager() {
        return xmlManager;
    }

    // Método para criar um arquivo XML de template com pontos de interesse
    public void createXMLTemplate(String fileName, String routeName,
                                  int routeId, int numPoints) {

        // Criação do arquivo XML no diretório do usuário
        File file = new File(ResourceManager.getInstance().getRootPath() + "/user/" + fileName + ".xml");

        try {
            // Cria o arquivo se ele não existir
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream fileos = null;

        try {
            // Abre um fluxo de saída para o arquivo
            fileos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        XmlSerializer serializer = Xml.newSerializer();

        try {
            // Configura o serializer para escrever no fluxo de saída
            serializer.setOutput(fileos, "UTF-8");
            serializer.startDocument(null, null);
            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

            // Inicia a tag raiz 'route' com atributos de identificação
            serializer.startTag(null, "route");
            serializer.attribute(null, "id", Integer.toString(routeId));

            // Adiciona tags de informações básicas sobre a rota
            serializer.startTag(null, "name");
            serializer.text(routeName);
            serializer.endTag(null, "name");
            serializer.startTag(null, "description");
            serializer.endTag(null, "description");
            serializer.startTag(null, "icon");
            serializer.endTag(null, "icon");

            // Inicia a tag 'points' que conterá os pontos de interesse
            serializer.startTag(null, "points");

            // Loop para criar tags para cada ponto de interesse
            for (int i = 0; i < numPoints; i++) {
                serializer.startTag(null, "point");
                serializer.attribute(null, "id", Integer.toString(i));
                serializer.attribute(null, "version", "1.0");

                // Adiciona informações básicas sobre o ponto de interesse
                serializer.startTag(null, "coords");
                serializer.attribute(null, "x", "0");
                serializer.attribute(null, "y", "0");
                serializer.endTag(null, "coords");

                serializer.startTag(null, "title");
                serializer.endTag(null, "title");

                serializer.startTag(null, "pointdescription");
                serializer.endTag(null, "pointdescription");

                serializer.startTag(null, "pointicon");
                serializer.endTag(null, "pointicon");

                // Adiciona informações sobre imagens associadas ao ponto
                serializer.startTag(null, "images");
                serializer.startTag(null, "image");
                serializer.attribute(null, "url", "");
                serializer.endTag(null, "image");
                serializer.endTag(null, "images");

                // Adiciona informações sobre vídeo associado ao ponto
                serializer.startTag(null, "video");
                serializer.endTag(null, "video");

                // Adiciona informações sobre realidade aumentada associada ao ponto
                serializer.startTag(null, "ar");
                serializer.attribute(null, "lat", "0.0");
                serializer.attribute(null, "long", "0.0");
                serializer.attribute(null, "alt", "0.0");
                serializer.attribute(null, "file", "");
                serializer.endTag(null, "ar");

                // Fecha a tag do ponto
                serializer.endTag(null, "point");
            }

            // Fecha a tag 'points'
            serializer.endTag(null, "points");

            // Fecha a tag raiz 'route'
            serializer.endTag(null, "route");

            // Finaliza o documento XML
            serializer.endDocument();
            serializer.flush();
            // Fecha o fluxo de saída
            assert fileos != null;
            fileos.close();
            // Exibe uma mensagem de sucesso
            Toast.makeText(ProjectAR.getInstance().getApplicationContext(), "Template Criado", Toast.LENGTH_SHORT).show();

        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            e.printStackTrace();
        }
    }

    // Método para criar um arquivo XML a partir de uma lista de pontos de interesse
    public void createXMLFromPoints(String fileName, String routeName, int routeId, ArrayList<PointOI> points) {

        // Criação do arquivo XML no diretório do usuário
        File file = new File(ResourceManager.getInstance().getRootPath() + "/user/" + fileName + ".xml");

        try {
            // Cria o arquivo se ele não existir
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream fileos = null;

        try {
            // Abre um fluxo de saída para o arquivo
            fileos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        XmlSerializer serializer = Xml.newSerializer();

        try {
            // Configura o serializer para escrever no fluxo de saída
            serializer.setOutput(fileos, "UTF-8");
            serializer.startDocument(null, null);
            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

            // Inicia a tag raiz 'route' com atributos de identificação
            serializer.startTag(null, "route");
            serializer.attribute(null, "id", Integer.toString(routeId));

            // Adiciona tags de informações básicas sobre a rota
            serializer.startTag(null, "name");
            serializer.text(routeName);
            serializer.endTag(null, "name");
            serializer.startTag(null, "description");
            serializer.endTag(null, "description");
            serializer.startTag(null, "icon");
            serializer.endTag(null, "icon");

            // Inicia a tag 'points' que conterá os pontos de interesse
            serializer.startTag(null, "points");

            // Loop para criar tags para cada ponto de interesse
            for (PointOI p : points) {
                serializer.startTag(null, "point");
                serializer.attribute(null, "id", Integer.toString(p.id));
                serializer.attribute(null, "version", "1.0");

                // Adiciona informações básicas sobre o ponto de interesse
                serializer.startTag(null, "coords");
                serializer.attribute(null, "x", Float.toString(p.coords[0]));
                serializer.attribute(null, "y", Float.toString(p.coords[1]));
                serializer.endTag(null, "coords");

                serializer.startTag(null, "title");
                serializer.text(p.title);
                serializer.endTag(null, "title");

                serializer.startTag(null, "pointdescription");
                serializer.endTag(null, "pointdescription");

                serializer.startTag(null, "pointicon");
                serializer.endTag(null, "pointicon");

                // Adiciona informações sobre imagens associadas ao ponto
                serializer.startTag(null, "images");
                serializer.startTag(null, "image");
                serializer.attribute(null, "url", "");
                serializer.endTag(null, "image");
                serializer.endTag(null, "images");

                // Adiciona informações sobre vídeo associado ao ponto
                serializer.startTag(null, "video");
                serializer.endTag(null, "video");

                // Adiciona informações sobre realidade aumentada associada ao ponto
                serializer.startTag(null, "ar");
                serializer.attribute(null, "lat", "0.0");
                serializer.attribute(null, "long", "0.0");
                serializer.attribute(null, "alt", "0.0");
                serializer.attribute(null, "file", "");
                serializer.endTag(null, "ar");

                // Fecha a tag do ponto
                serializer.endTag(null, "point");
            }

            // Fecha a tag 'points'
            serializer.endTag(null, "points");

            // Fecha a tag raiz 'route'
            serializer.endTag(null, "route");

            // Finaliza o documento XML
            serializer.endDocument();
            serializer.flush();
            // Fecha o fluxo de saída
            assert fileos != null;
            fileos.close();
            // Exibe uma mensagem de sucesso
            Toast.makeText(ProjectAR.getInstance().getApplicationContext(), "XML Criado", Toast.LENGTH_SHORT).show();

        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            e.printStackTrace();
        }
    }

    // Método para salvar pontos de interesse em um arquivo temporário
    public void toTempFile() {
        if (ProjectAR.getInstance().getCurrentState().getId() == MainMenuState.STATE_ID) {
            MainMenuState state = (MainMenuState) ProjectAR.getInstance().getCurrentState();
            File file = new File(ResourceManager.getInstance().getRootPath() + "/user/" + state.getRouteName() + ".tmp");
            if (file.exists()) {
                file.delete();
                Toast.makeText(ProjectAR.getInstance().getApplicationContext(), "O arquivo já existe e foi sobrescrito", Toast.LENGTH_SHORT).show();
            }
            try {
                ObjectOutputStream oos = new ObjectOutputStream(
                        new FileOutputStream(file));
                for (PointOI p : state.getGeoTaggingPoints())
                    oos.writeObject(p);
                oos.close();
            } catch (FileNotFoundException e) {
                Toast.makeText(ProjectAR.getInstance().getApplicationContext(),
                                "Erro ao gravar o arquivo", Toast.LENGTH_SHORT)
                        .show();
                e.printStackTrace();
            } catch (IOException e) {
                Toast.makeText(ProjectAR.getInstance().getApplicationContext(),
                                "Erro ao gravar o arquivo", Toast.LENGTH_SHORT)
                        .show();
                e.printStackTrace();
            }
            Toast.makeText(ProjectAR.getInstance().getApplicationContext(),
                    "Dados salvos com sucesso", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para ler pontos de interesse de um arquivo temporário
    public void fromTmpFile(String path, String routeName) {
        if (ProjectAR.getInstance().getCurrentState().getId() == MainMenuState.STATE_ID) {
            MainMenuState state = (MainMenuState) ProjectAR.getInstance().getCurrentState();
            ArrayList<PointOI> points = new ArrayList<PointOI>();
            state.setRouteName(routeName);
            File f = new File(path);
            ObjectInputStream ois = null;
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    ois = new ObjectInputStream(Files.newInputStream(f.toPath()));
                }

                // Lê o primeiro objeto
                Object aux = ois.readObject();

                // Enquanto houver objetos
                while (aux != null) {
                    if (aux instanceof PointOI) {
                        points.add((PointOI) aux);
                        aux = ois.readObject();
                    }
                }
                ois.close();
                state.setGeoTaggingPoints(points);

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            try {
                assert ois != null;
                ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            state.setGeoTaggingPoints(points);
        }
    }
}