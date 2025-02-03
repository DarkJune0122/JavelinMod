package javelin;

import arc.KeyBinds;
import arc.input.InputDevice;
import arc.input.KeyCode;

public enum Binding implements KeyBinds.KeyBind {
    discharge(KeyCode.altLeft, "Javelin inputs"),
    /** Key, which will discharge energy on its double-clicking. */
    discharge_double_click(KeyCode.mouseLeft)
    ;

    //  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==
    //
    //                   Variables
    //
    //  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==
    private final KeyBinds.KeybindValue defaultValue;
    private final String category;

    //  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==
    //
    //                 Constructors
    //
    //  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==
    Binding(KeyBinds.KeybindValue defaultValue, String category){
        this.defaultValue = defaultValue;
        this.category = category;
    }

    Binding(KeyBinds.KeybindValue defaultValue){
        this(defaultValue, null);
    }

    //  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==
    //
    //               Implementations
    //
    //  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==
    @Override
    public KeyBinds.KeybindValue defaultValue(InputDevice.DeviceType type) {
        return defaultValue;
    }
    @Override
    public String category() {
        return category;
    }
}
