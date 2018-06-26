package com.example.user.dipl1.custom_ui;

public interface DrawableClickListener {

    enum DrawablePosition {
        TOP,
        BOTTOM,
        LEFT,
        RIGHT
    }

    void onClick(DrawablePosition target);
}