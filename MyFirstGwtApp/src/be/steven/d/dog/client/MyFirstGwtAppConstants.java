package be.steven.d.dog.client;

import com.google.gwt.i18n.client.Constants;

public interface MyFirstGwtAppConstants extends Constants {
    @DefaultStringValue("StonkWatcher")
    String stonkWatcher();

    @DefaultStringValue("Symbol")
    String symbol();

    @DefaultStringValue("Price")
    String price();

    @DefaultStringValue("Change")
    String change();

    @DefaultStringValue("Remove")
    String remove();

    @DefaultStringValue("Add")
    String add();
}
