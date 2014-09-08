package adm;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.escidoc.core.adm.business.admin.ReindexStatus;
import de.escidoc.core.common.business.fedora.resources.ResourceType;

public class TestReindexStatus {

    ReindexStatus status = ReindexStatus.getInstance();

    @Before
    public void setUp() {
        System.out.println("*********************************");
        status.clear();
        status.startMethod();
    }

    @Test
    @Ignore
    public void test1() {
        status.setTotalNumberOfObjects(10000, ResourceType.ITEM);
        status.put(ResourceType.ITEM, 9900);

        try {
            Thread.currentThread().sleep(10000);
        }
        catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        assertTrue(status.toString().contains("36000"));
    }

    @Test
    @Ignore
    public void test2() {

        status.setTotalNumberOfObjects(10000, ResourceType.ITEM);
        status.put(ResourceType.ITEM, 9999);

        try {
            Thread.currentThread().sleep(10000);
        }
        catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        assertTrue(status.toString().contains("360"));
    }

    @Test
    @Ignore
    public void test3() {

        status.setTotalNumberOfObjects(1000, ResourceType.ITEM);
        status.setTotalNumberOfObjects(100, ResourceType.OU);
        status.setTotalNumberOfObjects(10, ResourceType.CONTEXT);
        status.setTotalNumberOfObjects(2, ResourceType.CONTENT_MODEL);
        status.put(ResourceType.ITEM, 9999);
        status.put(ResourceType.OU, 100);
        status.put(ResourceType.CONTEXT, 10);
        status.put(ResourceType.CONTENT_MODEL, 2);

        try {
            Thread.currentThread().sleep(10000);
        }
        catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() {
        status.setFillingComplete();
        System.out.println(status.toString());

        status.finishMethod();
    }

}
