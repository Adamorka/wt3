package by.bsuir.adam.wt.tasks.third.server.dao;

import by.bsuir.adam.wt.tasks.third.server.model.Case;
import by.bsuir.adam.wt.tasks.third.server.service.ServiceFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CaseDAO {
    private static final CaseDAO INSTANCE = new CaseDAO();
    private static final String CASES_PATH = "./src/resources/cases.xml";

    private final ReadWriteLock lock;
    private final Map<Integer, Case> cases;

    private CaseDAO() {
        lock = new ReentrantReadWriteLock();
        cases = new HashMap<>();
        init();
    }

    private void init() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File(CASES_PATH));
            doc.getDocumentElement().normalize();
            NodeList nodes = doc.getDocumentElement().getChildNodes();
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Case box = ServiceFactory.getInstance().getCaseService().createCase(node.getChildNodes());
                    cases.put(box.getId(), box);
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException ignored) {
        }
    }

    public static CaseDAO getInstance() {
        return INSTANCE;
    }

    public boolean contains(int id) {
        return cases.containsKey(id);
    }

    public List<Case> getAll() {
        try {
            lock.readLock().lock();
            return new ArrayList<>(cases.values());
        } finally {
            lock.readLock().unlock();
        }
    }

    public void add(Case box) {
        try {
            lock.writeLock().lock();
            box.setId(cases.keySet().stream().max(Comparator.comparingInt(a -> a)).get() + 1);
            cases.put(box.getId(), box);
            update();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void setById(int id, Case box) {
        try {
            lock.writeLock().lock();
            box.setId(id);
            cases.put(id, box);
            update();
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void update() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.newDocument();
            Element rootEle = doc.createElement("cases");
            for(var _case : getAll()) {
                Element caseEle = ServiceFactory.getInstance().getCaseService().createNode(doc, _case);
                rootEle.appendChild(caseEle);
            }

            doc.appendChild(rootEle);

            try {
                Transformer tr = TransformerFactory.newInstance().newTransformer();
                tr.setOutputProperty(OutputKeys.INDENT, "yes");
                tr.transform(new DOMSource(doc), new StreamResult(new FileOutputStream(CASES_PATH)));

            } catch (IOException | TransformerException e) {
                e.printStackTrace();
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }
}
