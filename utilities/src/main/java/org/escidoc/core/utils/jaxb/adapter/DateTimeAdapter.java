package org.escidoc.core.utils.jaxb.adapter;

import org.joda.time.DateTime;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author Marko Voss (marko.voss@fiz-karlsruhe.de)
 */
public class DateTimeAdapter extends XmlAdapter<String, DateTime>
{
    public DateTime unmarshal(String value) {
        return (org.escidoc.core.utils.xml.DateTimeJaxbConverter.parseDate(value));
    }

    public String marshal(DateTime value) {
        return (org.escidoc.core.utils.xml.DateTimeJaxbConverter.printDate(value));
    }

}