package com.artemis.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

public class Utils {

	public static float cubicInterpolation(float v0, float v1, float v2, float v3, float t) {
		float t2 = t * t;
		float a0 = v3 - v2 - v0 + v1;
		float a1 = v0 - v1 - a0;
		float a2 = v2 - v0;
		float a3 = v1;

		return (a0 * (t * t2)) + (a1 * t2) + (a2 * t) + a3;
	}

	public static float quadraticBezierInterpolation(float a, float b, float c, float t) {
		return (((1f - t) * (1f - t)) * a) + (((2f * t) * (1f - t)) * b) + ((t * t) * c);
	}

	public static float lengthOfQuadraticBezierCurve(float x0, float y0, float x1, float y1, float x2, float y2) {
		if ((x0 == x1 && y0 == y1) || (x1 == x2 && y1 == y2)) {
			return distance(x0, y0, x2, y2);
		}

		float ax, ay, bx, by;
		ax = x0 - 2 * x1 + x2;
		ay = y0 - 2 * y1 + y2;
		bx = 2 * x1 - 2 * x0;
		by = 2 * y1 - 2 * y0;
		float A = 4 * (ax * ax + ay * ay);
		float B = 4 * (ax * bx + ay * by);
		float C = bx * bx + by * by;

		float Sabc = 2f * (float) Math.sqrt(A + B + C);
		float A_2 = (float) Math.sqrt(A);
		float A_32 = 2f * A * A_2;
		float C_2 = 2f * (float) Math.sqrt(C);
		float BA = B / A_2;

		return (A_32 * Sabc + A_2 * B * (Sabc - C_2) + (4f * C * A - B * B) * (float) Math.log((2 * A_2 + BA + Sabc) / (BA + C_2))) / (4 * A_32);
	}

	public static float lerp(float a, float b, float t) {
		if (t < 0)
			return a;
		return a + t * (b - a);
	}

	public static float distance(float x1, float y1, float x2, float y2) {
		return euclideanDistance(x1, y1, x2, y2);
	}

	public static boolean doCirclesCollide(float x1, float y1, float radius1, float x2, float y2, float radius2) {
		float dx = x2 - x1;
		float dy = y2 - y1;
		float d = radius1 + radius2;
		return (dx * dx + dy * dy) < (d * d);
	}

	public static float euclideanDistanceSq2D(float x1, float y1, float x2, float y2) {
		float dx = x1 - x2;
		float dy = y1 - y2;
		return dx * dx + dy * dy;
	}

	public static float manhattanDistance(float x1, float y1, float x2, float y2) {
		return Math.abs(x1 - x2) + Math.abs(y1 - y2);
	}

	public static float euclideanDistance(float x1, float y1, float x2, float y2) {
		float a = x1 - x2;
		float b = y1 - y2;

		return (float) FastMath.sqrt(a * a + b * b);
	}

	public static float angleInDegrees(float ownerRotation, float x1, float y1, float x2, float y2) {
		return Math.abs(ownerRotation - angleInDegrees(x1, y1, x2, y2)) % 360;
	}

	public static float angleInDegrees(float originX, float originY, float targetX, float targetY) {
		return (float) Math.toDegrees(Math.atan2(targetY - originY, targetX - originX));
	}

	public static float angleInRadians(float originX, float originY, float targetX, float targetY) {
		return (float) Math.atan2(targetY - originY, targetX - originX);
	}

	public static boolean shouldRotateCounterClockwise(float angleFrom, float angleTo) {
		float diff = (angleFrom - angleTo) % 360;
		return diff > 0 ? diff < 180 : diff < -180;
	}

	public static float getRotatedX(float currentX, float currentY, float pivotX, float pivotY, float angleDegrees) {
		float x = currentX - pivotX;
		float y = currentY - pivotY;
		float xr = (x * TrigLUT.cosDeg(angleDegrees)) - (y * TrigLUT.sinDeg(angleDegrees));
		return xr + pivotX;
	}

	public static float getRotatedY(float currentX, float currentY, float pivotX, float pivotY, float angleDegrees) {
		float x = currentX - pivotX;
		float y = currentY - pivotY;
		float yr = (x * TrigLUT.sinDeg(angleDegrees)) + (y * TrigLUT.cosDeg(angleDegrees));
		return yr + pivotY;
	}

	public static float getXAtEndOfRotatedLineByOrigin(float x, float lineLength, float angleDegrees) {
		return x + TrigLUT.cosDeg(angleDegrees) * lineLength;
	}

	public static float getYAtEndOfRotatedLineByOrigin(float y, float lineLength, float angleDegrees) {
		return y + TrigLUT.sinDeg(angleDegrees) * lineLength;
	}

	public static boolean collides(float x1, float y1, float radius1, float x2, float y2, float radius2) {
		float d = Utils.distance(x1, y1, x2, y2);

		d -= radius1 + radius2;

		return d < 0;
	}

	public static String readFileContents(String file) {
		InputStream is = Utils.class.getClassLoader().getResourceAsStream(file);
		String contents = "";
		try {
			if (is != null) {
				Writer writer = new StringWriter();

				char[] buffer = new char[1024];
				Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}

				contents = writer.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return contents;
	}

}
