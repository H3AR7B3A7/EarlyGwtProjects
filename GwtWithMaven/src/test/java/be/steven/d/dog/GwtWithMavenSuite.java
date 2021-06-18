package be.steven.d.dog;

import be.steven.d.dog.client.GwtWithMavenTest;
import com.google.gwt.junit.tools.GWTTestSuite;
import junit.framework.Test;
import junit.framework.TestSuite;

public class GwtWithMavenSuite extends GWTTestSuite {
  public static Test suite() {
    TestSuite suite = new TestSuite("Tests for GwtWithMaven");
    suite.addTestSuite(GwtWithMavenTest.class);
    return suite;
  }
}
