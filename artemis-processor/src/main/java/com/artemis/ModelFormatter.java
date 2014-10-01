package com.artemis;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import freemarker.log.Logger;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;

public class ModelFormatter {
	private final Configuration config;
	
	static {
		try {
			Logger.selectLoggerLibrary(Logger.LIBRARY_JAVA);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	ModelFormatter() {
		config = new Configuration();
		config.setClassForTemplateLoading(getClass(), "");
		config.setIncompatibleImprovements(new Version(2, 3, 20));
		config.setDefaultEncoding("UTF-8");
		config.setLocale(Locale.US);
		config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
	}
	
	CharSequence generate(FactoryModel model) throws IOException {
		Template template = config.getTemplate("class.ftl");
		
		StringWriter out = new StringWriter();
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("model", model);
			template.process(data, out);
			return out.toString();
		} catch (TemplateException e) {
			throw new IOException(e);
		}
	}
}
