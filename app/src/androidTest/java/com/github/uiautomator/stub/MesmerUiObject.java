package com.github.uiautomator.stub;

import android.graphics.Rect;
import android.support.test.uiautomator.Configurator;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.view.accessibility.AccessibilityNodeInfo;

import static com.mesmer.util.ReflectionUtils.invoke;
import static com.mesmer.util.ReflectionUtils.method;

public class MesmerUiObject {

    private static final String CLASS_UI_OBJECT = "android.support.test.uiautomator.UiObject";

    private static final String METHOD_FIND_ACCESSIBILITY_NODE_INFO = "findAccessibilityNodeInfo";
    private static final String METHOD_GET_VISIBLE_BOUNDS = "getVisibleBounds";


    private UiObject uiObject;
    private UiDevice uiDevice;

    public MesmerUiObject(UiObject uiObject, UiDevice uiDevice) {
        this.uiObject = uiObject;
        this.uiDevice = uiDevice;
    }

    /**
     * Performs a click at the center of the visible bounds of the UI element represented
     * by this UiObject.
     *
     * @return true id successful else false
     * @throws UiObjectNotFoundException
     * @since API Level 16
     */
    public ClickResponse click(float gravityX, float gravityY) throws UiObjectNotFoundException {

        ClickResponse result = new ClickResponse();
        AccessibilityNodeInfo node = null;
        try {
            node = findAccessibilityNodeInfo(Configurator.getInstance().getWaitForSelectorTimeout());

            if(node == null) {
                throw new UiObjectNotFoundException(uiObject.getSelector().toString());
            }
            Rect rect = getVisibleBounds(node);

            int y = rect.top + (int)Math.floor(rect.height() * gravityY);
            int x = rect.left + (int)Math.floor(rect.width() * gravityX);
            result.setX(x);
            result.setY(y);

            Log.d("Going to tap on (" + x + ", " + y + ")");

            boolean clickSuccess = MesmerInteractionController.getInteractionController(uiDevice).clickAndSync(x, y,
                    Configurator.getInstance().getActionAcknowledgmentTimeout());
            result.setSuccess(clickSuccess);

        }  catch (UiObjectNotFoundException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private AccessibilityNodeInfo findAccessibilityNodeInfo(long timeout) throws Exception {
        AccessibilityNodeInfo result = (AccessibilityNodeInfo) invoke(method(CLASS_UI_OBJECT,
                METHOD_FIND_ACCESSIBILITY_NODE_INFO, long.class), uiObject, timeout);
        return result;
    }


    private Rect getVisibleBounds(AccessibilityNodeInfo node) throws Exception {
        Rect result = (Rect) invoke(method(CLASS_UI_OBJECT,
                METHOD_GET_VISIBLE_BOUNDS, AccessibilityNodeInfo.class), uiObject, node);
        return result;
    }
}
