package org.escidoc.core.util.xml.internal;

import net.sf.oval.constraint.NotEmpty;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.guard.Guarded;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.*;

/**
 * @author Marko Voss (marko.voss@fiz-karlsruhe.de)
 */
@Guarded
public class JAXBContextProvider {

    private static final Logger LOG = LoggerFactory.getLogger(JAXBContextProvider.class);

    private JAXBContext jaxbContext;

    private String packageFile;

    protected JAXBContextProvider() {}

    @NotNull
    public final JAXBContext getJAXBContext() throws JAXBException {
        if (this.jaxbContext == null) {
            this.jaxbContext = initJAXBContext();
            if (this.jaxbContext == null) {
                this.jaxbContext = JAXBContext.newInstance();
                LOG.warn("Returning default JAXBContext instance.");
            }
        }
        return this.jaxbContext;
    }

    public final void setPackageFile(@NotNull @NotEmpty final String packageFile) {
        this.packageFile = packageFile;
    }

    private JAXBContext initJAXBContext() throws JAXBException {
        if (this.packageFile == null) return null;
        
        final InputStream stream = getClass().getClassLoader().getResourceAsStream(this.packageFile);
        try {
            if (stream != null) {
                final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                final StringBuilder packages = new StringBuilder();

                String line;
                while ((line = reader.readLine()) != null) {
                    packages.append(line);
                    packages.append(':');
                }
                packages.deleteCharAt(packages.length() - 1);
                return JAXBContext.newInstance(packages.toString());
            } else {
                LOG.warn("Unable to find JAXB package list: " + this.packageFile);
            }
        } catch (IOException e) {
            LOG.error("Error reading JAXB package list: " + this.packageFile, e);
        }
        return null;
    }

    public static void main(String[] args) throws JAXBException {
        JAXBContextProvider provider = new JAXBContextProvider();
        provider.setPackageFile("org/escidoc/core/domain/jaxb.packages");
        System.out.println(provider.getJAXBContext());
    }
}
