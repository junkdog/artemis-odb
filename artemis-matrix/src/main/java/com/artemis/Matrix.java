package com.artemis;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class Matrix {
	
	public static void main(String[] args) throws MalformedURLException {
		URL path = new File(args[0]).toURI().toURL();
		List<String> packages = new ArrayList<String>(Arrays.asList(args));
		packages.remove(0);
		
		Set<Class<? extends Component>> components =
			ComponentFinder.getComponents(path, packages.toArray(new String[0]));
		
		for (Class<? extends Component> c : components) {
			System.out.println(c);
		}
	}
}
