package org.iirds.dita.ot.plugin;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.iirds.rdf.IirdsConstants;
import org.iirds.rdf.RDFConstants;
import org.iirds.rdf.facade.Factory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class IirRDSTaxonomyExtractor {

	Set<String> ignoreIRIs = Set.of(IirdsConstants.RENDITION_CLASS_URI, IirdsConstants.DIRECTORYNODE_CLASS_URI,
			IirdsConstants.DIRECTORYNODETYPE_CLASS_URI, IirdsConstants.NIL_RESOURCE_URI);

	Document createDocument() throws ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.newDocument();
	}

	String render(Document doc) throws TransformerException {
		Transformer tf = TransformerFactory.newInstance().newTransformer();
		tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		tf.setOutputProperty(OutputKeys.INDENT, "yes");
		tf.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "subjectScheme.dtd");
		tf.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "-//OASIS//DTD DITA Subject Scheme Map//EN");
		Writer out = new StringWriter();
		tf.transform(new DOMSource(doc), new StreamResult(out));
		return out.toString();
	}

	List<Resource> getSubClassesOf(Model model, Resource r) {
		List<Resource> result = new ArrayList<>();
		for (ResIterator iter = model.listSubjectsWithProperty(RDFS.subClassOf, r); iter.hasNext();) {
			result.add(iter.next());
		}
		return result;
	}

	List<Resource> getInstancesOf(Model model, Resource r) {
		List<Resource> result = new ArrayList<>();
		for (ResIterator iter = model.listSubjectsWithProperty(RDF.type, r); iter.hasNext();) {
			result.add(iter.next());
		}
		return result;
	}

	Element createSubject(Element parent, Model model, Resource r, boolean isClass) {
		if (!ignoreIRIs.contains(r.getURI())) {
			String label = Factory.getLabel(r, "en");
			Statement cr = r.getProperty(RDFS.comment, "en");
			String comment = "";
			if (cr != null) {
				comment = cr.getString();
			}
			Element subject = parent.getOwnerDocument().createElement("subjectdef");
			parent.appendChild(subject);
			Element topicmeta = parent.getOwnerDocument().createElement("topicmeta");
			subject.appendChild(topicmeta);
			Element navtitle = parent.getOwnerDocument().createElement("navtitle");
			topicmeta.appendChild(navtitle);
			if (label != null) {
				navtitle.setTextContent(label);
			}
			Element shortdesc = parent.getOwnerDocument().createElement("shortdesc");
			topicmeta.appendChild(shortdesc);
			shortdesc.setTextContent(comment);
			Element resourceid = parent.getOwnerDocument().createElement("resourceid");
			topicmeta.appendChild(resourceid);
			resourceid.setAttribute("appid", r.getURI());
			resourceid.setAttribute("appname", isClass ? "class-iri" : "instance-iri");

			String fragment = StringUtils.substringAfter(r.getURI(), "#");
			if (fragment != null) {
				subject.setAttribute("keys", "iirds." + fragment);
			}
			return subject;
		}
		return null;
	}

	void extractClass(Element parent, Model model, Resource clazz) {
		Element p = createSubject(parent, model, clazz, true);
		if (p != null) {
			parent = p;
			List<Resource> instances = getInstancesOf(model, clazz);
			for (Resource instance : instances) {
				createSubject(parent, model, instance, false);
			}
			List<Resource> subclasses = getSubClassesOf(model, clazz);
			for (Resource subclass : subclasses) {
				extractClass(parent, model, subclass);
			}
		}
	}

	void extractTaxonomies() throws TransformerException, ParserConfigurationException {
		Model model = RDFConstants.getRDFSModel();
		Document doc = createDocument();
		Element root = doc.createElement("subjectScheme");
		root.setAttribute("xml:lang", "en");
		doc.appendChild(root);
		Resource baseEntity = model.createResource("http://iirds.tekom.de/iirds#iirdsDomainEntity");
		extractClass(root, model, baseEntity);
		String xml = render(doc);
		System.out.println(xml);
	}

	public static void main(String[] args) throws Exception {
		new IirRDSTaxonomyExtractor().extractTaxonomies();
	}
}