package beloo.recyclerviewcustomadapter.gravityModifier;

import android.view.Gravity;

import java.util.HashMap;
import java.util.Map;

import beloo.recyclerviewcustomadapter.SpanLayoutChildGravity;

public class GravityModifiersFactory {

    public Map<Integer, IGravityModifier> gravityModifierMap;

    public GravityModifiersFactory() {
        gravityModifierMap = new HashMap<>();

        CenterGravityModifier centerGravityModifier = new CenterGravityModifier();
        TopGravityModifier topGravityModifier = new TopGravityModifier();
        BottomGravityModifier bottomGravityModifier = new BottomGravityModifier();

        gravityModifierMap.put(Gravity.TOP, topGravityModifier);
        gravityModifierMap.put(Gravity.BOTTOM, bottomGravityModifier);
        gravityModifierMap.put(Gravity.CENTER, centerGravityModifier);
        gravityModifierMap.put(Gravity.CENTER_VERTICAL, centerGravityModifier);
    }

    public IGravityModifier getGravityModifier(@SpanLayoutChildGravity int gravity) {
        IGravityModifier gravityModifier = gravityModifierMap.get(gravity);
        if (gravityModifier == null) {
            gravityModifier = gravityModifierMap.get(Gravity.CENTER_VERTICAL);
        }
        return gravityModifier;
    }

}
