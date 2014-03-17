package com.artemis.util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.artemis.model.AgroteraMapping;

public final class MatrixStringUtil {
	private MatrixStringUtil() {}
	
	public static String findLongestClassName(Map<String, List<AgroteraMapping>> mappings) {
		return findLongestString(mappings, new LongestClassName());
	}
	
	public static String findLongestManagerList(Map<String, List<AgroteraMapping>> mappings) {
		return findLongestString(mappings, new LongestManagers());
	}
	
	public static String findLongestSystemList(Map<String, List<AgroteraMapping>> mappings) {
		return findLongestString(mappings, new LongestSystems());
	}
	
	private static String findLongestString(Map<String, List<AgroteraMapping>> mappings, LongestMapper longestStrategy) {
		String longest = "";
		for (Entry<String, List<AgroteraMapping>> entry : mappings.entrySet()) {
			if (entry.getKey().length() > longest.length()) longest = entry.getKey();
			for (AgroteraMapping mapping : entry.getValue()) {
				longest = longestStrategy.getMaxLength(mapping, longest);
			}
		}
		return longest;
	}
	
	private static interface LongestMapper {
		String getMaxLength(AgroteraMapping mapping, String previousLongest);
	}
	
	private static class LongestClassName implements LongestMapper {
		@Override
		public String getMaxLength(AgroteraMapping mapping, String longest) {
			return (mapping.name.length() > longest.length())
				? mapping.name
				: longest;
		}
	}
	
	private static class LongestManagers implements LongestMapper {
		@Override
		public String getMaxLength(AgroteraMapping mapping, String longest) {
			return (Arrays.toString(mapping.refManagers).length() > longest.length())
				? Arrays.toString(mapping.refManagers)
				: longest;
		}
	}
	
	private static class LongestSystems implements LongestMapper {
		@Override
		public String getMaxLength(AgroteraMapping mapping, String longest) {
			return (Arrays.toString(mapping.refSystems).length() > longest.length())
				? Arrays.toString(mapping.refSystems)
				: longest;
		}
	}
}
