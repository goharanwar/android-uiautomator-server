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


    public boolean fastSwipe(int startX, int startY, int endX, int endY) {

        boolean ret = false;

        int totalDistanceX = endX - startX;
        int totalDistanceY = endY - startY;
        int currentPositionX = startX;
        int currentPositionY = startY;
        int remainingDistanceX = totalDistanceX;
        int remainingDistanceY = totalDistanceY;

        try {

            MesmerInteractionController interactionController = MesmerInteractionController.getInteractionController(device);

            // first touch starts exactly at the point requested
            ret = interactionController.touchDown(startX, startY);

            boolean toExecuteTouchMove = true;
            int step = 0;

            do {

                int xPosition;
                int yPosition;
                step++;

                int maxDistance = Math.max(Math.abs(remainingDistanceX), Math.abs(remainingDistanceY));

                int factor = (maxDistance / 300) + 2;

                if(factor < 3) {
                    factor = 3;
                }

                Log.d("calculatedFactor = " + factor);

                int xDistance = (remainingDistanceX / factor);
                int yDistance = (remainingDistanceY / factor);

                remainingDistanceX = remainingDistanceX - xDistance;
                remainingDistanceY = remainingDistanceY - yDistance;

                maxDistance = Math.max(Math.abs(xDistance), Math.abs(yDistance));

                if (maxDistance < 2) {
                    Log.d("Max distance is less than 2");
                    break;
                }

                if(maxDistance < 3) {

                    Log.d("Max distance is less than 5");
                    toExecuteTouchMove = false;
                    xPosition = endX;
                    yPosition = endY;

                } else {

                    xPosition = currentPositionX + xDistance;
                    yPosition = currentPositionY + yDistance;
                }


                Log.d("Step = " + step + ", Touch Move x = " + xPosition + ", y = " + yPosition);

                ret &= interactionController.touchMove(xPosition, yPosition);

                currentPositionX = xPosition;
                currentPositionY = yPosition;

                if (ret == false)
                    break;
                // set some known constant delay between steps as without it this
                // become completely dependent on the speed of the system and results
                // may vary on different devices. This guarantees at minimum we have
                // a preset delay.

                SystemClock.sleep(5);

                if (step > 100) {
                    Log.e("Step count increased from 100");
                    toExecuteTouchMove = false;
                }

            } while (toExecuteTouchMove);

            SystemClock.sleep(50);

            ret &= interactionController.touchUp(endX, endY);


        } catch (Exception e) {
            Log.e(e.getMessage());
            e.printStackTrace();
        }

        return(ret);

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
