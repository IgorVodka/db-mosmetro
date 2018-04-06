package vodka.igor.mosmetro.logic;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class AccessGroup {
    String name;
    String password;
    AccessGroupPermissions permissions;

    public AccessGroup(String groupName)
            throws SAXException, ParserConfigurationException,
                        XPathExpressionException, IOException, GroupNotFoundException {
        Element group = findGroupElement(groupName);

        if(group == null)
            throw new GroupNotFoundException();

        NodeList permissionNodes = group.getElementsByTagName("permissions").item(0).getChildNodes();
        Set<String> permissionsSet = new HashSet<>();
        for(int i = 0; i < permissionNodes.getLength(); i++) {
            if(permissionNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                permissionsSet.add(permissionNodes.item(i).getTextContent());
            }
        }
        this.name = groupName;
        this.password = group.getElementsByTagName("password").item(0).getTextContent();
        this.permissions = new AccessGroupPermissions(permissionsSet);
    }

    private Document loadGroupsConfiguration() throws SAXException, IOException, ParserConfigurationException {
        File xml = new File("src/resources/access_groups.xml");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(xml);
        doc.getDocumentElement().normalize();
        return doc;
    }

    private Element findGroupElement(String groupName)
        throws SAXException, IOException, XPathExpressionException, ParserConfigurationException {

        Document doc = loadGroupsConfiguration();
        XPath xpath = XPathFactory.newInstance().newXPath();
        NodeList nodes = (NodeList) xpath.evaluate(
                String.format("/groups/group[@login='%s']", groupName),
                doc.getDocumentElement(),
                XPathConstants.NODESET
        );

        if(nodes.getLength() == 0)
            return null;

        Element group = (Element) nodes.item(0);
        return group;
    }

    public String getName() {
        return name;
    }

    public boolean isCorrectPassword(String passedPassword) {
        return password.equals(passedPassword);
    }

    public AccessGroupPermissions getPermissions() {
        return permissions;
    }

    public boolean can(String permission) {
        return permissions.can(permission);
    }

    public boolean canAny(String permission1, String permission2) {
        return can(permission1) || can(permission2);
    }
}
