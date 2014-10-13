package com.artemis;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Wrapper for GWT tests of the contrib framework.
 *
 * @author Daan van Yperen
 */
public class ArtemisTest implements EntryPoint {

	@Override
	public void onModuleLoad() {

		final VerticalPanel panel = new VerticalPanel();

		final Label myLabel = new Label();
		myLabel.setText("Test 1");
		final Label myLabel2 = new Label();
		myLabel2.setText("Test 2");

		panel.add(myLabel);
		panel.add(myLabel2);

		RootPanel.get("gwtContainer").add(panel);
	}
}
