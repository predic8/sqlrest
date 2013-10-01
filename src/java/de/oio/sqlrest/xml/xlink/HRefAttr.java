package de.oio.sqlrest.xml.xlink;

import org.xml.sax.helpers.AttributesImpl;

/**
 * @author tbayer
 *
 */
public class HRefAttr extends AttributesImpl {

	public HRefAttr( String link) {

		addAttribute(
			"http://www.w3.org/1999/xlink",
			"href",
			"xlink:href",
			null,
			link);
	}
}

