package com.artemis;

import com.artemis.meta.ClassMetadata;
import com.artemis.meta.ClassMetadata.WeaverType;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WeaverLog {
	public static final int RELATIVE_WIDTH = 72;
	public static final String LINE = horizontalLine(); 
	
	public int timeComponents;
	public int timeComponentSystems;
	public int timeSystems;
	public List<ClassMetadata> components = new ArrayList<ClassMetadata>();
	public List<ClassMetadata> componentSystems = new ArrayList<ClassMetadata>();
	public List<ClassMetadata> systems = new ArrayList<ClassMetadata>();
	
	private static String format(String key, Object value, char delim) {
		int length = key.length() + value.toString().length() + 2; // margin
		length = Math.max(length, 3);
		
		char[] padding = new char[Math.max(RELATIVE_WIDTH - length, 0)];
		Arrays.fill(padding, delim);
		
		return new StringBuilder(RELATIVE_WIDTH)
			.append(key)
			.append(" ").append(String.valueOf(padding)).append(" ")
			.append(value)
			.toString();
	}
	
	public String getFormattedLog() {
		StringBuilder sb = new StringBuilder();
		
		if (timeComponents > 0) {
			sb.append("").append('\n');
			sb.append(format("WOVEN COMPONENTS", timeComponents + "ms", ' ')).append('\n');
			sb.append(LINE);
			for (String detail : getComponentSummary(components).split("\n"))
				sb.append(detail).append('\n');
			sb.append(LINE);
		}
		
		if (timeComponentSystems > 0) {
			sb.append("").append('\n');
			sb.append(format("COMPONENT ACCESS REWRITTEN", timeComponentSystems + "ms", ' ')).append('\n');
			sb.append(LINE);
			for (String detail : getRewrittenAccessSummary(componentSystems).split("\n"))
				sb.append(detail).append('\n');
			sb.append(LINE);
		}
		
		
		if (timeSystems > 0) {
			sb.append("").append('\n');
			sb.append(format("OPTIMIZED ENTITY SYSTEMS", timeSystems + "ms", ' ')).append('\n');
			sb.append(LINE).append('\n');
			for (String detail : getSystemSummary(systems).split("\n"))
				sb.append(detail).append('\n');
			sb.append(LINE);
		}
		
		return sb.toString();
	}
	
	public static String format(String key, Object value) {
		return format(key, value, '.');
	}
	
	private static String horizontalLine() {
		char[] raw = new char[RELATIVE_WIDTH];
		Arrays.fill(raw, '-');
		return String.valueOf(raw) + "\n";
	}

	private static String getComponentSummary(List<ClassMetadata> processed) {
		StringBuilder sb = new StringBuilder();
		
		for (ClassMetadata meta : processed) {
			if (meta.annotation == WeaverType.NONE)
				continue;
			
			String klazz = shortenClass(meta.type);
			sb.append(format(klazz, meta.annotation.name())).append("\n");
		}
		
		return sb.toString();
	}
	
	private static String shortenClass(Type type) {
		return shortenClass(type.getClassName());
	}
	
	private static String shortenClass(String className) {
		StringBuilder sb = new StringBuilder();
		
		String[] split = className.split("\\.");
		for (int i = 0; (split.length - 1) > i; i++) {
			sb.append(split[i].charAt(0)).append('.');
		}
		sb.append(split[split.length - 1]);
		return sb.toString();
	}

	private static String getSystemSummary(List<ClassMetadata> processed) {
		StringBuilder sb= new StringBuilder();
		
		for (ClassMetadata meta : processed) {
			String klazz = shortenClass(meta.type);
			sb.append(format(klazz, meta.sysetemOptimizable.name())).append("\n");
		}
		
		return sb.toString();
	}

	private static String getRewrittenAccessSummary(List<ClassMetadata> processed) {
		StringBuilder sb= new StringBuilder();
		
		for (ClassMetadata meta : processed) {
			String klazz = shortenClass(meta.type);
			sb.append(format(klazz, "SUCCESS")).append("\n");
		}
		
		return sb.toString();
	}
}