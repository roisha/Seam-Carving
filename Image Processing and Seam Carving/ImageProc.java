/*
 * This class defines some static methods of image processing.
 */

package main;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.nio.Buffer;

public class ImageProc {

	public static BufferedImage scaleDown(BufferedImage img, int factor) {
		if (factor <= 0)
			throw new IllegalArgumentException();
		int newHeight = img.getHeight()/factor;
		int newWidth = img.getWidth()/factor;
		BufferedImage out = new BufferedImage(newWidth, newHeight, img.getType());
		for (int x = 0; x < newWidth; x++)
			for (int y = 0; y < newHeight; y++)
				out.setRGB(x, y, img.getRGB(x*factor, y*factor));
		return out;
	}
	//A method that returns a BufferedImage gray scaled
	public static BufferedImage grayScale(BufferedImage img) {
		BufferedImage result = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
		for (int i = 0; i < img.getWidth(); i++) {
			for (int j = 0; j < img.getHeight(); j++) {
				Color recent = new Color(img.getRGB(i, j));
				//Measuring every gray pixel in a loop 
				int gray = (int) (recent.getBlue() * 0.1140 + recent.getGreen() * 0.587 + recent.getRed() * 0.2989);
				result.setRGB(i, j, (new Color(gray,gray,gray)).getRGB());
			}
		}

		return result;
	}
	//A method that returns a BufferedImage that is the horizontal derivative of a colored image
	public static BufferedImage horizontalDerivative(BufferedImage img) {
		//Using the gray scale to measure the derivative
		BufferedImage imgGray = grayScale(img);
		BufferedImage result = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
		for (int i = 0; i < img.getWidth(); i++) {
			for (int j = 0; j < img.getHeight(); j++) {
				if ((i == 0) || (i == img.getWidth() -1)) {
					result.setRGB(i, j, (new Color(127, 127, 127).getRGB()));
				}
				else {
					//calculating the derivative
					int gray = ((imgGray.getRGB(i - 1, j) & 0xFF) - (imgGray.getRGB(i + 1, j) & 0xFF) + 255) / 2;
					result.setRGB(i, j, (new Color(gray, gray, gray).getRGB()));
				}
			}
		}
		return result;
	}
	//A method that returns a BufferedImage that is the vertical derivative of a colored image
	public static BufferedImage verticalDerivative(BufferedImage img) {
		//Using the gray scale to measure the derivative
		BufferedImage imgGray = grayScale(img);
		BufferedImage result = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
		for (int i = 0; i < img.getWidth(); i++) {
			for (int j = 0; j < img.getHeight(); j++) {
				if ((j == 0) || (j == img.getHeight() -1)) {
					result.setRGB(i, j, (new Color(127, 127, 127).getRGB()));
				}
				else {
					//calculating the derivative
					int gray = ((imgGray.getRGB(i, j - 1) & 0xFF) - (imgGray.getRGB(i, j + 1) & 0xFF) + 255) / 2;
					result.setRGB(i, j, (new Color(gray, gray, gray).getRGB()));
				}
			}
		}
		return result;
	}
	//A method that calculates the magnitude of gradient at each pixel of the image and returns a buffered image
	//that represent the magnitude of the gradient at each pixel
	public static BufferedImage gradientMagnitude(BufferedImage img) {
		//Using the gray scale to measure the derivative
		BufferedImage imgGray = grayScale(img);
		BufferedImage result = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
		for (int i = 0; i < img.getWidth(); i++) {
			for (int j = 0; j < img.getHeight(); j++) {
				if ((j == 0) || (j == img.getHeight() -1) || (i == 0) || (i == img.getWidth() -1)) {
					result.setRGB(i, j, (new Color(127, 127, 127).getRGB()));
				}
				else {
					//The derivative of dx 
					int dx = (imgGray.getRGB(i - 1, j) & 0xFF) - (imgGray.getRGB(i + 1, j) & 0xFF);
					//The derivative of dy 
					int dy = (imgGray.getRGB(i, j - 1) & 0xFF) - (imgGray.getRGB(i, j + 1) & 0xFF);
					int sum = (int) (Math.pow(dx, 2) + Math.pow(dy, 2));
					int gradient = (int) Math.sqrt(sum);
					if (gradient > 255) {
						gradient = 255;
					}
					result.setRGB(i, j, (new Color(gradient,gradient,gradient).getRGB()));
				}
			}
		}

		return result;
	}
	//A method that changes the size of a given image
	public static BufferedImage retargetSize(BufferedImage img, int width, int height) {
		//initializing a buffered image that will be returned as a result
		BufferedImage result = new BufferedImage(img.getWidth(), img.getWidth(), img.getType());
		//Changing only the width of the picture
		if (width != img.getWidth()) {
			Retargeter imgn = new Retargeter(img, img.getWidth() - width);
			result = imgn.retarget(width);
		}
		//Changing the width and the height of the picture
		if ((img.getWidth() != width) && (img.getHeight() != height)) {
			Retargeter imgn2 = new Retargeter(transpose(result), img.getHeight() - height);
			result = imgn2.retarget(height);
			return transpose(result);

		}
		//Changing only the height of the picture
		if ((height != img.getHeight()) && (img.getWidth() == width)) {
			Retargeter imgn3 = new Retargeter(transpose(img), img.getHeight() - height);
			result = imgn3.retarget(height);
			return transpose(result);
		}
		//If there was no change so returning the regular image
		if ((height == img.getHeight()) && (img.getWidth() == width)) {
			return img;
		}

		return result;
	}

	public static BufferedImage showSeams(BufferedImage img, int width, int height) {


		BufferedImage result = new BufferedImage(img.getWidth(),img.getHeight(), img.getType());


		//Showing only the vertical seams
		if ((width != img.getWidth()) && (height == img.getHeight())) {

			Retargeter retarget = new Retargeter(img, img.getWidth() - width);
			//getting the seam order matrix
			int[][] RedSeams = retarget.getSeamsOrderMatrix();
			//creating the picture with the seams on it
			for (int i = 0; i < img.getHeight(); i++) {
				for (int j = 0; j < img.getWidth(); j++) {
					//filling the red seams
					if (RedSeams[i][j] != Integer.MAX_VALUE) {
						result.setRGB(j, i, new Color (255, 0 ,0).getRGB());
					}
					else {
						result.setRGB(j, i, img.getRGB(j, i));
					}
				}
			}
			return result;
		}

		//Showing only the horizontal seams
		if (height != img.getHeight() && (width == img.getWidth())) {

			Retargeter imagetransposed = new Retargeter(transpose(img),img.getHeight() - height);
			//getting the seam order matrix
			int[][] GreenSeams = imagetransposed.getSeamsOrderMatrix();
			//creating the picture with the seams on it
			for (int i = 0; i < img.getHeight(); i++) {
				for (int j = 0; j < img.getWidth(); j++) {
					//filling the green seams
					if (GreenSeams[j][i] != Integer.MAX_VALUE) {
						result.setRGB(j, i, new Color (0 , 255, 0).getRGB());
					}
					else {
						result.setRGB(j, i, img.getRGB(j, i));
					}
				}
			}
			return result;
		}
		//Showing both vertical and horizontal seams
		if (height != img.getHeight() && (width != img.getWidth())) {

			Retargeter retarget = new Retargeter(img,img.getWidth() - width);
			//getting the seam order matrix
			int[][] RedSeams = retarget.getSeamsOrderMatrix();

			Retargeter imagetransposed = new Retargeter(transpose(img),img.getHeight() - height);
			//getting the seam order matrix
			int[][] GreenSeams = imagetransposed.getSeamsOrderMatrix();
			//creating the picture with the seams on it
			for (int i = 0; i < img.getHeight(); i++) {
				for (int j = 0; j < img.getWidth(); j++) {
					//filling the red seams
					if (RedSeams[i][j] != Integer.MAX_VALUE) {
						result.setRGB(j, i, new Color (255 , 0, 0).getRGB());
					}
					//drawing the green seams
					else if (GreenSeams[j][i] != Integer.MAX_VALUE) {
						result.setRGB(j, i, new Color (0 , 255, 0).getRGB());
					}
					else {
						result.setRGB(j, i, img.getRGB(j, i));
					}
				}
			}

			return result;
		}

		return img;

	}
	//A method that returns the transpose of an image
	private static BufferedImage transpose(BufferedImage img) {
		BufferedImage transposed = new BufferedImage(img.getHeight(), img.getWidth(), img.getType());
		for (int i = 0; i < img.getHeight(); i++) {
			for (int j = 0; j < img.getWidth(); j++) {
				transposed.setRGB(i, j, img.getRGB(j, i));
			}
		}

		return transposed;
	}

}





