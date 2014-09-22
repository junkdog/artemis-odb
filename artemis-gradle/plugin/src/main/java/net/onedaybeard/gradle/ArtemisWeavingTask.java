package net.onedaybeard.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

/**
 * @author Daan van Yperen
 */
public class ArtemisWeavingTask extends DefaultTask {

	@TaskAction
	public void weavingTask() {
		System.out.println("Weaving is cool");
	}

}