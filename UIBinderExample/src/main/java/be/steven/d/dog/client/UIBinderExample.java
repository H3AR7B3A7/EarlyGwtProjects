package be.steven.d.dog.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Document;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class UIBinderExample implements EntryPoint {
  HelloWorld helloWorld = new HelloWorld("able", "baker", "charlie");

  @Override
  public void onModuleLoad() {
    // Don't forget, this is DOM only; will not work with GWT widgets
    Document.get().getBody().appendChild(helloWorld.getElement());
  }
}
