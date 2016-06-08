package beloo.recyclerviewcustomadapter.gravityModifier;

import android.graphics.Rect;

public interface IGravityModifier {
    void modifyChildRect(int minTop, int maxBottom, Rect childRect);
}
