package main;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


/*
	유의사항
	1. txt 파일에 단어가 없으면 에러가 남(단어가 없을 시 띄어쓰기를 넣어줘야함)

 */


public class Main {
	public static void main(String[] args) {
		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
			try {
				// 파일 열기
				List<String> lines = Files.readAllLines(Paths.get("test_2015_08_21.txt"),StandardCharsets.UTF_8);
				
				// 라인별로 읽어서 데이터로 저장하기
				for (int i = 0; i < lines.size(); i++) {
					// ','를 통해 스플릿
					String[] itemArray = lines.get(i).toString().split(",");
					ArrayList<String> itemList = new ArrayList<String>();
					for (int j = 0; j < itemArray.length; j++) {
						// 첫번째 엘리먼트와 두번째 엘리먼트는 태그로 써야하기때문에 공백이 있다면 제거하여 저장
						if(j==0 || j==1) {
							itemList.add(itemArray[j].replace(" ", ""));
						} else {
							// 한국어, 영어, 중국어 중에 첫 시작에 띄어쓰기가 있다면 제거
							if(itemArray[j].charAt(0) == ' ') {
								itemList.add(itemArray[j].substring(1));
							} else {
								itemList.add(itemArray[j]);
							}
							
						}
					}

					list.add(itemList);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("Language");
			doc.appendChild(rootElement);
			
			Element prevEle;
			Element curEle;
			Element firstElement = null;
			for(int i = 1; i < list.size(); i++) {
				
				if(i == 1) {
					// 첫번째 데이터를 저장 시 스트링값에 ' '의 값이 들어가는 문제가 있어서 데이터를 삽입해줌
					prevEle = doc.createElement("start");
					curEle = doc.createElement(new String(list.get(i).get(0).toString().replace(" ", "")));
				} else {
					prevEle = doc.createElement(new String(list.get(i-1).get(0).toString().replace(" ", "")));
					curEle = doc.createElement(new String(list.get(i).get(0).toString().replace(" ", "")));
				}

				
				if(prevEle.getNodeName().equals(curEle.getNodeName())) {
					System.out.println(i);
					Element secondElement = doc.createElement(new String(list.get(i).get(1).toString().replace(" ", "")));
					Element englishElement = doc.createElement("English");
					Element koreanElement = doc.createElement("Korean");
					Element chineseElement = doc.createElement("Chinese");
					englishElement.appendChild(doc.createTextNode(new String(list.get(i).get(2).toString())));
					koreanElement.appendChild(doc.createTextNode(new String(list.get(i).get(3).toString())));
					chineseElement.appendChild(doc.createTextNode(new String(list.get(i).get(4).toString())));
					secondElement.appendChild(englishElement);
					secondElement.appendChild(koreanElement);
					secondElement.appendChild(chineseElement);
					firstElement.appendChild(secondElement);
				} else {
					if(i > 1) {
						rootElement.appendChild(firstElement);
					}
					
					firstElement = doc.createElement(new String(list.get(i).get(0).toString().replace(" ", "")));
					Element secondElement = doc.createElement(new String(list.get(i).get(1).toString().replace(" ", "")));
					Element englishElement = doc.createElement("English");
					Element koreanElement = doc.createElement("Korean");
					Element chineseElement = doc.createElement("Chinese");
					englishElement.appendChild(doc.createTextNode(new String(list.get(i).get(2).toString())));
					koreanElement.appendChild(doc.createTextNode(new String(list.get(i).get(3).toString())));
					chineseElement.appendChild(doc.createTextNode(new String(list.get(i).get(4).toString())));
					secondElement.appendChild(englishElement);
					secondElement.appendChild(koreanElement);
					secondElement.appendChild(chineseElement);
					firstElement.appendChild(secondElement);
				}
			}

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File("Language.xml"));

			transformer.transform(source, result);

			System.out.println("File saved!");

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}
}
