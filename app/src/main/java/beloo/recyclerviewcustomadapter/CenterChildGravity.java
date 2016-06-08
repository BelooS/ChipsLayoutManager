package beloo.recyclerviewcustomadapter;

import android.view.Gravity;

public class CenterChildGravity implements IChildGravityResolver {
    @Override
    public int getItemGravity(int position) {
        return Gravity.CENTER_VERTICAL;
    }
}
