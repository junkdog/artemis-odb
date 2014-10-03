package com.artemis;

import javax.lang.model.element.Element;

public final class FluentUtil {
	public enum Match { ONE_OF, ALL_OF };
	
	public static ReflectedElement element(Element element) {
		return new ReflectedElement(element);
	}
	
	public static class ReflectedElement {
		private final Element element;

		private ReflectedElement(Element element) {
			this.element = element;
		}
		
		public boolean hasAnnotation(String annotation) {
			return MirrorUtil.hasMirror(annotation, element);
		}
		
		public boolean hasAnnotation(Match strategy, String... annotations) {
			for (String annotation : annotations) {
				switch (strategy) {
					case ALL_OF:
						if (!hasAnnotation(annotation)) return false;
						break;
					case ONE_OF:
						if (hasAnnotation(annotation)) return true;
						break;
					default:
						throw new RuntimeException("missing case: " + strategy);
				}
			}
			
			return strategy == Match.ALL_OF;
		}
	}
}
