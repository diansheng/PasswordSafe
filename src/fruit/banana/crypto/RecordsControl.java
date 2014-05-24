package fruit.banana.crypto;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RecordsControl {
	public static String SafeFile = "data/safe.xml";
	public static String ROOT="Safe";
	public static String RECORD="record";
	public static String RECORDS="records";
	public static String HEADER="header";

	public static void main(String[] args) {
	}
	
	public void init(){
		
	}

	public void initializeDocument() {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement(ROOT);
			doc.appendChild(rootElement);

			// records elements
			Element records = doc.createElement(RECORDS);
			rootElement.appendChild(records);

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(SafeFile));

			// Output to console for testing
			// StreamResult result = new StreamResult(System.out);

			transformer.transform(source, result);

			System.out.println("File created");

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}

	public void readXML() {

	}

	public Record readRecord(String keyword) {
		Record r=null;
		
		try {
			File xmlFile = new File(SafeFile);
			if(!xmlFile.exists()){
				initializeDocument();
				xmlFile=new File(SafeFile);
			}
			DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
			Document doc = documentBuilder.parse(xmlFile);

			doc.getDocumentElement().normalize();

			//root node
			System.out.println("Root element :"	+ doc.getDocumentElement().getNodeName());
			Element root=doc.getDocumentElement();
			
			//primary element: records
			Element records=(Element) root.getElementsByTagName(RECORDS).item(0);
			NodeList nodeList = records.getElementsByTagName(RECORD);

			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				System.out.println("\nElement type :" + node.getNodeName());
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element record = (Element) node;
					System.out.println("Record header : "+ record.getAttribute(HEADER));
					String header=record.getAttribute(HEADER);
					if(header.equalsIgnoreCase(keyword)){
						r=new Record();
						r.setHeader(header);
						String data=node.getTextContent();
						System.out.println("Data : "+ data);
						r.setData(data);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return r;
	}

	public void deleteRecord(String keyword) {
		try {
			File xmlFile = new File(SafeFile);
			if(!xmlFile.exists()){
				initializeDocument();
				xmlFile=new File(SafeFile);
			}
			DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
			Document doc = documentBuilder.parse(xmlFile);

//			doc.getDocumentElement().normalize();

			//root node
			System.out.println("Root element :"	+ doc.getDocumentElement().getNodeName());
			Element root=doc.getDocumentElement();
			
			//primary element: records
			Element records=(Element) root.getElementsByTagName(RECORDS).item(0);
			NodeList nodeList = records.getElementsByTagName(RECORD);

			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				System.out.println("\nElement type :" + node.getNodeName());
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element record = (Element) node;
					System.out.println("Record header : "+ record.getAttribute(HEADER));
					String header=record.getAttribute(HEADER);
					if(header.equalsIgnoreCase(keyword)){
						records.removeChild(record);
						break;
					}
				}
			}
			
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(SafeFile));
			transformer.transform(source, result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean addRecord(Record r) {
		// null check;
		if(r==null) return false;
		try {
			File xmlFile = new File(SafeFile);
			if(!xmlFile.exists()){
				initializeDocument();
				xmlFile=new File(SafeFile);
			}
			DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
			Document doc = documentBuilder.parse(xmlFile);

//			doc.getDocumentElement().normalize();

			//root node
			System.out.println("Root element :"	+ doc.getDocumentElement().getNodeName());
			Element root=doc.getDocumentElement();
			
			//primary element: records
			Element records=(Element) root.getElementsByTagName(RECORDS).item(0);
			NodeList nodeList = records.getElementsByTagName(RECORD);

			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				System.out.println("\nElement type :" + node.getNodeName());
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element record = (Element) node;
					System.out.println("Record header : "+ record.getAttribute(HEADER));
					String header=record.getAttribute(HEADER);
					if(header.equalsIgnoreCase(r.getHeader())){
						System.out.println("update existing record");
						record.setTextContent(r.getData());
						record.setAttribute(HEADER, r.getHeader());
						return true;
					}
				}
			}
			
			System.out.println("insert new record");
			// firstname elements
			Element record = doc.createElement(RECORD);
			record.setTextContent(r.getData());
			record.setAttribute(HEADER, r.getHeader());
			records.appendChild(record);
			System.out.println("new record inserted");
			
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(SafeFile));
			transformer.transform(source, result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
}
