package com.example.removebackgrdemo.removebg;

import android.graphics.Path;

public class FingerPath {
    public int color;

    public int strokeWidth;
    public Path path;

    public FingerPath(int color, boolean emboss, boolean blur, int strokeWidth, Path path) {
        this.color = color;
       // this.emboss = emboss;
       // this.blur = blur;
        this.strokeWidth = strokeWidth;
        this.path = path;
    }
}
