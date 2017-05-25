package com.artemis.cli;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;

import com.artemis.cli.converter.EclipseProjectFolder;
import com.artemis.cli.converter.FolderConverter;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.Strings;

@Parameters(
	commandDescription="Configures EntityFactory Annotation Processor with Eclipse project")
public class EclipseProcessorCommand {
	static final String COMMAND = "eclipse-ap";
	
	@Parameter(
		names = {"-p", "--project-folder"},
		description = "Root project folder",
		converter = EclipseProjectFolder.class,
		required = true)
	private File projectFolder;
	
	@Parameter(
		names = {"-o", "--jar-folder"},
		description = "Save location for annotation processor jar",
		converter = FolderConverter.class,
		required = false)
	private File apSaveFolder = new File(".");
	
	@Parameter(
		names = {"-s", "--ap-src-folder"},
		description = "Source folder for generated files",
		converter = FolderConverter.class,
		required = false)
	private File apSourceFolder = new File("src-generated");
	
	void execute() {
		configureSavePaths();
		downloadProcessor();
		writeProjectConfiguration();
	}

	private void configureSavePaths() {
		String basePath = projectFolder.getAbsolutePath();

		if ("src-generated".equals(apSourceFolder.toString()))
			apSourceFolder = new File(projectFolder, "src-generated");

		if (".".equals(apSaveFolder.toString()))
			apSaveFolder = projectFolder;


		if (!apSaveFolder.getAbsolutePath().startsWith(basePath)) {
			throw new RuntimeException("Save location for annotation " +
				"processor must be a subpath of project.");
		}
		
		if (!apSourceFolder.getAbsolutePath().startsWith(basePath)) {
			throw new RuntimeException("Generated source folder " +
				"must be a subpath of project.");
		}
		
		apSaveFolder.mkdirs();
		apSourceFolder.mkdirs();
		new File(projectFolder, ".settings").mkdirs();
	}

	private void downloadProcessor() {
		String baseUrl = "http://repo1.maven.org/maven2/net/onedaybeard/artemis/artemis-odb-processor";
		try {
			String version = getArtemisVersion();
			String url = String.format("%s/%s/artemis-odb-processor-%s.jar", baseUrl, version, version);
			URL downloadUrl = new URL(url);
			File saveFile = new File(apSaveFolder, String.format("artemis-odb-processor-%s.jar", version));
			
			System.out.print("downloading processor: " + url + " ... ");
			FileUtils.copyURLToFile(downloadUrl, saveFile);
			System.out.println("done");
		} catch (IOException e) {
			System.out.println("failed");
			throw new RuntimeException(e);
		}
	}

	private void writeProjectConfiguration() {
		writeFactoryPath();
		writeCorePrefs();
		writeJdtAptPrefs();
	}
	
	private void writeJdtAptPrefs() {
		File prefs = new File(projectFolder, ".settings/org.eclipse.jdt.apt.core.prefs");
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("eclipse.preferences.version=1").append("\n");
			sb.append("org.eclipse.jdt.apt.aptEnabled=true").append("\n");
			sb.append("org.eclipse.jdt.apt.genSrcDir=" + genSrcDir()).append("\n");
			sb.append("org.eclipse.jdt.apt.reconcileEnabled=true");
			
			FileUtils.write(prefs, sb.toString());
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private String genSrcDir() {
		String basePath = projectFolder.getAbsolutePath();
		return apSourceFolder.getAbsolutePath().substring(basePath.length());
	}

	private void writeCorePrefs() {
		File prefs = new File(projectFolder, ".settings/org.eclipse.jdt.core.prefs");
		try {
			
			if (!prefs.exists())
				prefs.createNewFile();
			
			Properties properties = new Properties();
			properties.load(FileUtils.openInputStream(prefs));
			properties.setProperty("org.eclipse.jdt.core.compiler.processAnnotations", "enabled");
			properties.store(FileUtils.openOutputStream(prefs), null);
		
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}

	private void writeFactoryPath() {
		File factoryPath = new File(projectFolder, ".factorypath");
		
		try {
			List<String> lines = IOUtils.readLines(EclipseProcessorCommand.class.getResourceAsStream("/factorypath"));
			StringBuilder sb = new StringBuilder();
			
			String projectName = getProjectName();
			String artemisVersion = getArtemisVersion();
			for (String s : lines) {
				s = s.replaceAll("PROJECT", projectName);
				s = s.replaceAll("VERSION", artemisVersion);
				sb.append(s).append("\n");
			}
			FileUtils.write(factoryPath, sb.toString());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private String getProjectName() {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new File(projectFolder, ".project"));
			doc.getDocumentElement().normalize();
			
			XPathFactory xFactory = XPathFactory.newInstance();
			XPath xpath = xFactory.newXPath();
			XPathExpression expr = xpath.compile("/projectDescription/name/text()");
			
			return (String) expr.evaluate(doc, XPathConstants.STRING);
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private static String getArtemisVersion() throws IOException {
		InputStream is = EclipseProcessorCommand.class.getResourceAsStream("/artemis-version.txt");
		return IOUtils.readLines(is).get(0);
	}
}
