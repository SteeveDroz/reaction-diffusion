package com.github.steevedroz.reactiondiffusion;

import javafx.scene.canvas.Canvas;
import javafx.scene.image.PixelWriter;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class ReactionDiffusion extends Canvas {
    private Cell[][] current;
    private Cell[][] next;

    public double dA = 1.0;
    public double dB = 0.25;
    public double f = 0.055;
    public double k = 0.062;
    public double dT = 1;

    public ReactionDiffusion(double width, double height) {
	super(width, height);
	current = new Cell[(int) width][(int) height];
	next = new Cell[(int) width][(int) height];
	for (int x = 0; x < width; x++) {
	    for (int y = 0; y < height; y++) {
		current[x][y] = new Cell(1, 0);
		next[x][y] = new Cell(1, 0);
	    }
	}

	setOnMousePressed((event) -> drawPixels(event));
	setOnMouseDragged((event) -> drawPixels(event));
    }

    public void show() {
	PixelWriter pixelWriter = getGraphicsContext2D().getPixelWriter();
	for (int x = 0; x < getWidth(); x++) {
	    for (int y = 0; y < getHeight(); y++) {
		double value = normalize(current[x][y].a - current[x][y].b);
		pixelWriter.setColor(x, y, new Color(value, value, value, 1));
	    }
	}
    }

    public void update() {
	for (int x = 0; x < getWidth(); x++) {
	    for (int y = 0; y < getHeight(); y++) {
		if (x == 0 || x == getWidth() - 1 || y == 0 || y == getHeight() - 1) {
		    next[x][y].a = 1;
		    next[x][y].b = 0;
		    continue;
		}
		double a = current[x][y].a;
		double b = current[x][y].b;
		next[x][y].a = normalize(a + (dA * laplaceA(x, y) - a * b * b + f * (1 - a)) * dT);
		next[x][y].b = normalize(b + (dB * laplaceB(x, y) + a * b * b - (k + f) * b) * dT);
	    }
	}
	switchFrames();
    }

    public void clear() {
	PixelWriter pixelWriter = getGraphicsContext2D().getPixelWriter();
	for (int x = 0; x < getWidth(); x++) {
	    for (int y = 0; y < getHeight(); y++) {
		current[x][y] = new Cell(1, 0);
		next[x][y] = new Cell(1, 0);
		pixelWriter.setColor(x, y, Color.WHITE);
	    }
	}
    }

    private void drawPixels(MouseEvent event) {
	PixelWriter pixelWriter = getGraphicsContext2D().getPixelWriter();
	double newA = 0;
	double newB = 0;
	if (event.isPrimaryButtonDown()) {
	    newA = 0;
	    newB = 1;
	} else if (event.isSecondaryButtonDown()) {
	    newA = 1;
	    newB = 0;
	}
	for (int x = 0; x < getWidth(); x++) {
	    for (int y = 0; y < getHeight(); y++) {
		if (distance(x, y, event.getX(), event.getY()) < 3) {
		    current[x][y].a = newA;
		    current[x][y].b = newB;
		    double value = normalize(current[x][y].a - current[x][y].b);
		    pixelWriter.setColor(x, y, new Color(value, value, value, 1));
		}
	    }
	}
    }

    private double laplaceA(int x, int y) {
	double laplace = 0;
	laplace += current[x][y].a * -1;
	laplace += current[x][y - 1].a * 0.2;
	laplace += current[x][y + 1].a * 0.2;
	laplace += current[x - 1][y].a * 0.2;
	laplace += current[x + 1][y].a * 0.2;
	laplace += current[x - 1][y - 1].a * 0.05;
	laplace += current[x - 1][y + 1].a * 0.05;
	laplace += current[x + 1][y - 1].a * 0.05;
	laplace += current[x + 1][y + 1].a * 0.05;
	return laplace;
    }

    private double laplaceB(int x, int y) {
	double laplace = 0;
	laplace += current[x][y].b * -1;
	laplace += current[x][y - 1].b * 0.2;
	laplace += current[x][y + 1].b * 0.2;
	laplace += current[x - 1][y].b * 0.2;
	laplace += current[x + 1][y].b * 0.2;
	laplace += current[x - 1][y - 1].b * 0.05;
	laplace += current[x - 1][y + 1].b * 0.05;
	laplace += current[x + 1][y - 1].b * 0.05;
	laplace += current[x + 1][y + 1].b * 0.05;
	return laplace;
    }

    private void switchFrames() {
	Cell[][] tmp = current;
	current = next;
	next = tmp;
    }

    private double distance(double x1, double y1, double x2, double y2) {
	return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

    private double normalize(double value) {
	return Math.min(Math.max(value, 0), 1);
    }
}
