package com.github.uiautomator.mesmer;

import android.support.test.uiautomator.UiDevice;

import static com.mesmer.util.ReflectionUtils.invoke;
import static com.mesmer.util.ReflectionUtils.method;

public class MesmerInteractionController {

    private static final String METHOD_TOUCH_DOWN = "touchDown";
    private static final String METHOD_TOUCH_UP = "touchUp";
    private static final String METHOD_TOUCH_MOVE = "touchMove";
    private static final String SEND_TEXT = "sendText";
    private static final String CLICK_AND_SYNC = "clickAndSync";
    private static final String INJECT_EVENT = "injectEventSync";

    private static final String CLASS_INTERACTION_CONTROLLER = "android.support.test.uiautomator.InteractionController";

    private final Object interactionController;

    public static MesmerInteractionController getInteractionController(UiDevice device) throws Exception {
        return new MesmerInteractionController(invoke(method(UiDevice.class, "getInteractionController"),
                device));
    }

    private MesmerInteractionController(Object interactionController) {
        this.interactionController = interactionController;
    }

    public boolean touchDown(final int x, final int y) throws Exception {
                Boolean result = (Boolean) invoke(method(CLASS_INTERACTION_CONTROLLER,
                        METHOD_TOUCH_DOWN, int.class, int.class), interactionController, x, y);
                return result;
    }

    public boolean touchUp(final int x, final int y) throws Exception {
        Boolean result = (Boolean) invoke(method(CLASS_INTERACTION_CONTROLLER,
                METHOD_TOUCH_UP, int.class, int.class), interactionController, x, y);
        return result;
    }

    public boolean touchMove(final int x, final int y) throws Exception {
        Boolean result = (Boolean) invoke(method(CLASS_INTERACTION_CONTROLLER,
                METHOD_TOUCH_MOVE, int.class, int.class), interactionController, x, y);
        return result;
    }

    public boolean sendText(String text) throws Exception {
        Boolean result = (Boolean) invoke(method(CLASS_INTERACTION_CONTROLLER,
                SEND_TEXT, String.class), interactionController, text);
        return result;

    }

    public boolean clickAndSync(final int x, final int y, long timeout) throws Exception {
        Boolean result = (Boolean) invoke(method(CLASS_INTERACTION_CONTROLLER,
                CLICK_AND_SYNC, int.class, int.class, long.class), interactionController, x, y, timeout);
        return result;

    }


//    public boolean touchCancel(int x, int y) throws Exception {
//
//
//        Long mDownTime = (Long) getField("mDownTime", interactionController);
//
//        final long eventTime = SystemClock.uptimeMillis();
//        MotionEvent event = getMotionEvent(mDownTime, eventTime, MotionEvent.ACTION_CANCEL, x, y);
//
//        Boolean result = (Boolean) invoke(method(CLASS_INTERACTION_CONTROLLER,
//                INJECT_EVENT, InputEvent.class), interactionController, event);
//
//        return result;
//    }
//
//    private MotionEvent getMotionEvent(long downTime, long eventTime, int action, float x, float y) {
//
//        MotionEvent.PointerProperties properties = new MotionEvent.PointerProperties();
//        properties.id = 0;
//        properties.toolType = Configurator.getInstance().getToolType();
//
//        MotionEvent.PointerCoords coords = new MotionEvent.PointerCoords();
//        coords.pressure = 1;
//        coords.size = 1;
//        coords.x = x;
//        coords.y = y;
//
//        return MotionEvent.obtain(downTime, eventTime, action, 1,
//                new MotionEvent.PointerProperties[] { properties }, new MotionEvent.PointerCoords[] { coords },
//                0, 0, 1.0f, 1.0f, 0, 0, InputDevice.SOURCE_TOUCHSCREEN, 0);
//    }


}
