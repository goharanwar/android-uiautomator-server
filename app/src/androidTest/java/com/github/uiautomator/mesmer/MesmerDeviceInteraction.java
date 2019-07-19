package com.github.uiautomator.mesmer;

import android.graphics.Rect;
import android.os.SystemClock;
import android.support.test.uiautomator.Configurator;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;

import com.github.uiautomator.stub.Log;
import com.github.uiautomator.stub.Point;
import com.github.uiautomator.stub.Selector;

public class MesmerDeviceInteraction {

    private static MesmerDeviceInteraction instance;

    public static MesmerDeviceInteraction getInstance(UiDevice device) {
        if(instance == null) {
            instance = new MesmerDeviceInteraction(device);
        }
        return instance;
    }

    private UiDevice device;

    private MesmerDeviceInteraction(UiDevice device) {
        this.device = device;
    }

    /**
     * Performs a click at the center of the visible bounds of the UI element represented
     * by this UiObject.
     *
     * @return boolean
     */
    public boolean longClick(int x, int y, int duration) {
        try {
            Log.d("Going to Long click on (" + x + ", " + y + ") with duration = " + duration);
            if(MesmerInteractionController.getInteractionController(device).touchDown(x, y)) {
                SystemClock.sleep(duration);
                Log.d("Going to invoke touchUp at (" + x + ", " + y + ")");
                if(MesmerInteractionController.getInteractionController(device).touchUp(x, y)) {
                    Log.d("Long Click at (" + x + ", " + y + ") done");
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Performs a click at the center of the visible bounds of the UI element represented
     * by this UiObject.
     *
     * @return ClickResponse
     * @throws UiObjectNotFoundException
     */
    public ClickResponse click(Selector obj, float gravityX, float gravityY) throws UiObjectNotFoundException {

        UiObject2 closestObject = obj.toClosestUiObject2();

        if (closestObject != null) {
            Point tapLocation = getTapLocation(closestObject.getVisibleBounds(), gravityX, gravityY);
            boolean result = clickAndSync(tapLocation);
            ClickResponse response = new ClickResponse();
            response.setSuccess(result);
            response.setX(tapLocation.getX());
            response.setY(tapLocation.getY());
            return response;

        }

        throw new UiObjectNotFoundException(obj.toBySelector().toString());

    }


    /**
     * Performs a long click at given gravity of the visible bounds of the UI Element represented
     * by this UiObject.
     *
     * @return ClickResponse
     * @throws UiObjectNotFoundException
     */
    public ClickResponse longClick(Selector obj, int duration, float gravityX, float gravityY) throws UiObjectNotFoundException {

        UiObject2 closestObject = obj.toClosestUiObject2();

        if (closestObject != null) {
            Point tapLocation = getTapLocation(closestObject.getVisibleBounds(), gravityX, gravityY);
            boolean result = longClick(tapLocation.getX(), tapLocation.getY(), duration);
            ClickResponse response = new ClickResponse();
            response.setSuccess(result);
            response.setX(tapLocation.getX());
            response.setY(tapLocation.getY());
            return response;
        }

        throw new UiObjectNotFoundException(obj.toBySelector().toString());
    }

    public boolean sendText(String text) {

        boolean result = false;

        try {
            result = MesmerInteractionController.getInteractionController(device).sendText(text);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private boolean clickAndSync(Point tapLocation) {

        boolean clickSuccess = false;
        Log.d("Going to tap on (" + tapLocation.getX() + ", " + tapLocation.getY() + ")");

        try {

            clickSuccess = MesmerInteractionController.getInteractionController(device).clickAndSync(tapLocation.getX(), tapLocation.getY(),
                    Configurator.getInstance().getActionAcknowledgmentTimeout());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return clickSuccess;

    }

    private Point getTapLocation(Rect rect, float gravityX, float gravityY) {

        Point point = new Point();

        int y = rect.top + (int)Math.floor(rect.height() * gravityY);
        int x = rect.left + (int)Math.floor(rect.width() * gravityX);
        point.setX(x);
        point.setY(y);
        return point;

    }
}
