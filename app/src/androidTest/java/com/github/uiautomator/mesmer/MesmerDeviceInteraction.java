package com.github.uiautomator.mesmer;

import android.graphics.Rect;
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
     * @return ClickResponse
     * @throws UiObjectNotFoundException
     */
    public ClickResponse click(Selector obj, float gravityX, float gravityY) throws UiObjectNotFoundException {

        UiObject2 closestObject = obj.toClosestUiObject2();

        if (closestObject != null) {
            return click(closestObject, gravityX, gravityY);
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

    private ClickResponse click(UiObject2 uiObject2, float gravityX, float gravityY) {

        Point tapLocation = getTapLocation(uiObject2.getVisibleBounds(), gravityX, gravityY);

        ClickResponse response = new ClickResponse();

        boolean clickSuccess = false;

        Log.d("Going to tap on (" + tapLocation.getX() + ", " + tapLocation.getY() + ")");

        try {

            clickSuccess = MesmerInteractionController.getInteractionController(device).clickAndSync(tapLocation.getX(), tapLocation.getY(),
                    Configurator.getInstance().getActionAcknowledgmentTimeout());

        } catch (Exception e) {
            e.printStackTrace();
        }
        response.setSuccess(clickSuccess);
        response.setX(tapLocation.getX());
        response.setY(tapLocation.getY());

        return response;

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
