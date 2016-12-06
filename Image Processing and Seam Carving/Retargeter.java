package main;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.concurrent.Semaphore;

public class Retargeter {

	private BufferedImage m_img;
	public BufferedImage result;
	private int[] helper;
	private int[][] costMatrix;
	private int height, width;
	private int[][] gray; 
	private int[][] original;
	public int[][] seamordermatrix;
	private int seamechange;


	public Retargeter(BufferedImage m_img, int pixremove) {

		//saving the absolute of the number of seams that should be multiplied or deleted
		//The absolute number helps dealing with both cases
		this.seamechange = Math.abs(pixremove);
		this.m_img = m_img;
		this.height = m_img.getHeight();
		this.width = m_img.getWidth();
		this.result = new BufferedImage(width - pixremove, height, m_img.getType());
		//An array that keeps the optimal seam that should be removed each iteration
		this.helper = new int[height];
		this.costMatrix = new int[height][width];
		this.gray = new int[height][width];
		//A matrix the saves the original spot of the pixels
		this.original =  new int[height][width];
		//A matrix that saves the seams that should be removed
		this.seamordermatrix = new int[height][width];


		calculateGray();
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++ ) { 
				original[i][j] = j;

			}
		}
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++ ) { 
				seamordermatrix[i][j] = Integer.MAX_VALUE;
			}
		}

		calculateSeamsOrderMatrix();
	}

	private void calculateGray() {
		Color rgb;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				rgb = new Color(m_img.getRGB(j, i));
				gray[i][j] =  (rgb.getRed() + rgb.getGreen() + rgb.getBlue()) / 3;
			}
		}
	}
	public int[][] getSeamsOrderMatrix() {
		return seamordermatrix;
	}
	public int[][] getOrigPosMatrix() {
		return original;
	}
	//The method that retargets the size of the image 
	public BufferedImage retarget(int newSize) {
		BufferedImage result = new BufferedImage(newSize, height, m_img.getType());
		//The case of reducing seams 
		if (newSize < width) {
			for (int i = 0; i < result.getHeight(); i++) {
				for (int j = 0; j < newSize; j++) {
					result.setRGB(j, i, m_img.getRGB(original[i][j], i));	
				}
			}
		}
		//The case of adding seams
		if (newSize > width) {
			int count = 0;
			for (int i = 0; i < height; i++) {
				count = 0;
				//
				for (int j = 0; (j < width) && (j + count) < newSize; j++) {
					if (seamordermatrix[i][j] == Integer.MAX_VALUE) {
						result.setRGB(j + count, i, m_img.getRGB(j, i));

					}
					else  {
						result.setRGB(j + count, i, m_img.getRGB(j, i));
						result.setRGB(j + 1 + count, i, m_img.getRGB(j, i));
						count++;
					}
				}
			}	
		}
		return result;
	}

	//Calculating the seams order matrix
	private void calculateSeamsOrderMatrix() {

		//Making an iteration for every seam that we need to remove or multiply
		for (int i = 0; i < seamechange; i++) {

			//Calculating the cost matrix
			calculateCostsMatrix(width - i);

			//finding the seam that needs to be remove or multiplied
			findseam();

			//Shifting the gray scale matrix and the original spots matrix according to the seam the we need to remove or multiply
			shiftpixels();

			//Calculating the seams order matrix
			//Updating the spots in the matrix according to the seam that we found
			for (int j = helper.length - 1 ; j >= 0; j--) {
				int add = 0;

				//For optimization we are trying to put the new pixel in the spots where there is Integer.MaxValue
				//making sure that we are not running on pixels that were added in previous iterations
				while ((original[j][helper[j]] + add > 0)  && (seamordermatrix[j][ original[j][helper[j]] + add ] != Integer.MAX_VALUE)) {
					add--;
				}
				if ((original[j][helper[j]] + add == 0)) {
					add = 0;
					while ((original[j][helper[j]] + add < width - 1) && (seamordermatrix[j][ original[j][helper[j]] + add] != Integer.MAX_VALUE)) {
						add++;
					}

				}
				seamordermatrix[j][ original[j][helper[j]] + add] = i;
			}
		}

	}

	//Shifting the pixel gray scale matrix and the original spots matrix
	private void shiftpixels() {
		for (int i = 0; i < height; i++) {
			for (int j = helper[i]; j < width - 1; j++) {
				gray[i][j] = gray[i][j + 1];
				original[i][j] = original[i][j + 1];
			}
		}
	}	
	//Calculating the cost matrix by the forward energy algorithm 
	private void calculateCostsMatrix(int w) {
		int CL, CV, CR, ML, MV, MR, M1, min;		

		this.costMatrix = new int[height][w];
		costMatrix[0][0] = 1000;
		costMatrix[0][w - 1] = 1000;
		for (int j = 1; j < w - 2; j++) {
			costMatrix[0][j] = -16777216;
		}
		for (int i = 1; i < height; i++) {
			costMatrix[i][0] = gray[i][0];
			costMatrix[i][w - 1] = gray[i][w - 1];
		}
		//(Forward Energy Algorithm) Calculating the lowest energy that will be added by removing a pixel
		//i.e calculating the  minor change
		for (int i = 1; i < height; i++) {
			for (int j = 1; j < w - 1; j++) {
				CL = Math.abs(gray[i][j + 1] -  gray[i][j - 1]) 
						+ Math.abs(gray[i - 1][j] - gray[i][j - 1]);


				CR = Math.abs(gray[i][j + 1] -  gray[i][j - 1]) 
						+ Math.abs(gray[i - 1][j] - gray[i][j + 1]);

				CV = Math.abs(gray[i][j + 1] - gray[i][j - 1]);

				ML = costMatrix[i - 1][j - 1]; 

				MV = costMatrix[i - 1][j]; 

				MR = costMatrix[i - 1][j + 1]; 

				M1 = Math.min(CV + MV, CR + MR);
				min = Math.min(CL + ML, M1);

				//putting the minimum in the (i,j) spot of the cost matrix
				costMatrix[i][j] = min;


			}

		}

	}

	//Finding the seam that should be removed or multiplied and saving it in the helper array
	private void findseam(){
		int min = costMatrix[height - 1][0];
		for(int j = 1; j < costMatrix[0].length; j++) {

			if (min > costMatrix[height - 1][j]) {
				min = costMatrix[height - 1][j];
				helper[height - 1] = j;
			}
		}
		//Border case
		for (int i =  height - 2; i >= 0; i--) {
			if (helper[i + 1] == 0) {
				if (costMatrix[i][0] > costMatrix[i][1]) {
					helper[i] = 1;
				}
				else {
					helper[i] = 0;
				}
			}

			//Border case
			if (helper[i + 1] == costMatrix[0].length - 1) {
				if (costMatrix[i][costMatrix[0].length - 2] > costMatrix[i][costMatrix[0].length - 1]) {
					helper[i] = costMatrix[0].length - 1;
				}
				else {
					helper[i] = costMatrix[0].length - 2;
				}
			}
			//General case
			else {
				int j = helper[i + 1] - 1;
				min = costMatrix[i][j];
				if (min > costMatrix[i][helper[i + 1]]) {
					min = costMatrix[i][helper[i + 1]];
					j = helper[i + 1];
				} 
				if (min > costMatrix[i][helper[i + 1] + 1]) {
					min = costMatrix[i][helper[i + 1] + 1];
					j = helper[i + 1] + 1;
				}
				//Updating the helper array that keeps the optimal seam
				helper[i] = j;


			}
		}

	}

}









