package netty.server;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 解析Urls
 *
 * @author Created by cxx on 15-7-21.
 */
public class Urls {
    private Logger logger = LoggerFactory.getLogger(Urls.class);
    private Map<String, URL> uris;
    private static Urls urls;
    private Document document;
    private Urls(String uri_xmls) throws DocumentException {
        uris = new HashMap<>();
        SAXReader reader = new SAXReader();
        //read(new File(uri_xmls))
        document = reader.read(Urls.class.getClassLoader().getResourceAsStream(uri_xmls));
        List<Element> rooturls = document.getRootElement().elements();
        for (Element element : rooturls) {
            for (Object ele : element.elements()) {
                URL url = create((Element) ele);
                uris.put(url.url, url);
            }
        }
    }

    public static final Urls create(String path) throws DocumentException {
        if (urls == null) {
            urls = new Urls(path);
        }
        return urls;
    }

    public URL get(Object key) {
        return uris.get(key);
    }

    public static Urls getUrls() {
        return urls;
    }

    public Map<String, URL> uris() {
        return uris;
    }

    public URL create(Element element) {
        URL url = new URL();
        url.desc = element.attribute("desc").getValue();
        url.parent = element.getParent().attribute("name").getValue();
        url.url = url.parent + element.attribute("name").getValue();
        url.methodName = element.attribute("method").getValue();
        try {
            Class cla = Class.forName(element.getParent().attributeValue("class"));
            url.method = cla.getMethod(url.methodName);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            logger.error("url=" + url + ",methodName=" + url.methodName ,e);
            System.exit(1);
        }
        logger.info(url.toString());
        return url;
    }

    public class URL {
        public String url;
        public String parent;
        public String methodName;
        public String desc;
        public Method method;

        @Override
        public String toString() {
            return "url=" + url + ", methodName=" + methodName + ", desc=" + desc;
        }
    }
}
