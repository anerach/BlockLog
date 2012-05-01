package me.arno.blocklog.schedules;

import java.net.URL;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;

import me.arno.blocklog.BlockLog;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Updates implements Runnable {

	@Override
	public void run() {
		Logger log = BlockLog.plugin.log;
		try {
        	URL url = new URL("http://dev.bukkit.org/server-mods/block-log/files.rss");
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(url.openConnection().getInputStream());
            doc.getDocumentElement().normalize();
            NodeList nodes = doc.getElementsByTagName("item");
            Node firstNode = nodes.item(0);
            if (firstNode.getNodeType() == 1) {
                Element firstElement = (Element) firstNode;
                NodeList firstElementTagName = firstElement.getElementsByTagName("title");
                Element firstNameElement = (Element) firstElementTagName.item(0);
                NodeList firstNodes = firstNameElement.getChildNodes();
                BlockLog.plugin.newVersion = firstNodes.item(0).getNodeValue().replace("BlockLog", "").trim();
            }
			
            BlockLog.plugin.doubleCurrentVersion = Double.valueOf(BlockLog.plugin.currentVersion.replaceFirst("\\.", ""));
            BlockLog.plugin.doubleNewVersion = Double.valueOf(BlockLog.plugin.newVersion.replaceFirst("\\.", ""));
			
			if(BlockLog.plugin.doubleNewVersion > BlockLog.plugin.doubleCurrentVersion) {
				log.warning("BlockLog v" + BlockLog.plugin.newVersion + " is released! You're using BlockLog v" + BlockLog.plugin.currentVersion);
				log.warning("Update BlockLog at http://dev.bukkit.org/server-mods/block-log/");
			}
        } catch (Exception e) {
        	// Nothing
        }
	}
}
