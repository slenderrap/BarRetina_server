package com.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class ProductLoader {

    public static final String PRODUCTS_FILE = "Productes.xml";

    public static ArrayList<Product> loadProducts() {
        File inputFile = null;
        try {
            inputFile = new File(ProductLoader.class.getClassLoader().getResource(PRODUCTS_FILE).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Error al cargar el fichero XML");
        }
        Document doc = parseXML(inputFile);
        ArrayList<Product> products = new ArrayList<>();
        if (doc == null) {
            throw new RuntimeException("Error al cargar el fichero XML");
        }
        NodeList productes = doc.getElementsByTagName("producte");
        for (int i = 0; i < productes.getLength(); i++) {
            Element element = (Element) productes.item(i);
            products.add(loadProduct(element));
        }
        return products;
    }

    private static Product loadProduct(Element element) {
        int id = Integer.parseInt(element.getAttribute("id"));
        String nom = element.getElementsByTagName("nom").item(0).getTextContent();
        double preu = Double.parseDouble(element.getElementsByTagName("preu").item(0).getTextContent());
        String descripcio = element.getElementsByTagName("descripcio").item(0).getTextContent();
        ArrayList<String> tags = new ArrayList<>();
        NodeList tagsNodeList = element.getElementsByTagName("tag");
        for (int j = 0; j < tagsNodeList.getLength(); j++) {
            Element tagElement = (Element) tagsNodeList.item(j);
            tags.add(tagElement.getTextContent());
        }
        URL imatgeURL = ProductLoader.class.getClassLoader().getResource("images/" + element.getElementsByTagName("imatge").item(0).getTextContent());
        if (imatgeURL == null) {
            throw new RuntimeException("Error al cargar la imagen: " + element.getElementsByTagName("imatge").item(0).getTextContent());
        }
        try {
            String imatgePath = Paths.get(imatgeURL.toURI()).toString();
            String imatgeBase64 = encodeImageToBase64(imatgePath);
            return new Product(id, nom, preu, descripcio, tags, imatgePath, imatgeBase64);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Error al cargar la imagen: " + element.getElementsByTagName("imatge").item(0).getTextContent());
        }
    }

    public static String encodeImageToBase64(String imagePath)  {
        try (FileInputStream imageStream = new FileInputStream(new File(imagePath))) {
            byte[] imageBytes = new byte[imageStream.available()];
            imageStream.read(imageBytes);
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            throw new RuntimeException("Error al codificar la imagen a base64: " + imagePath);
        }
    }

    private static Document parseXML(File inputFile) {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            return doc;
        } catch (Exception e) {
            throw new RuntimeException("Error al parsear el fichero XML");
        }
    }
}
