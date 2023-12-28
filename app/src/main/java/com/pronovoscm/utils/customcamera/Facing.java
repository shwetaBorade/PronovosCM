package com.pronovoscm.utils.customcamera;


/**
 * Facing value indicates which camera sensor should be used for the current session.
 *
 * @see CameraView#setFacing(Facing)
 */
public enum Facing implements Control {

    /**
     * Back-facing camera sensor.
     */
    BACK(0),

    /**
     * Front-facing camera sensor.
     */
    FRONT(1);

    final static Facing DEFAULT = BACK;

    private int value;

    Facing(int value) {
        this.value = value;
    }

    public static Facing fromValue(int value) {
        Facing[] list = Facing.values();
        for (Facing action : list) {
            if (action.value() == value) {
                return action;
            }
        }
        return null;
    }

    public int value() {
        return value;
    }
}
