package org.escidoc.core.utils.jaxb.adapter;

import org.escidoc.core.utils.xml.DateJaxbConverter;
import org.joda.time.DateTime;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author Marko Voss (marko.voss@fiz-karlsruhe.de)
 */
public class DateAdapter extends XmlAdapter<String, DateTime>
{
    public DateTime unmarshal(String value) {
        return DateJaxbConverter.parseDate(value);
    }

    public String marshal(DateTime value) {
        return DateJaxbConverter.printDate(value);
    }

}