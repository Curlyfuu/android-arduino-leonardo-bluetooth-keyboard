package com.example.danyal.bluetoothhc05;

public interface TouchListener {

    void onTouchDown(int x, int y);     //手指按下滑动事件

    void onTouchMove(int x, int y);     //手指滑动事件

    void onTouchUp(int x, int y);       //手指弹起事件
}
