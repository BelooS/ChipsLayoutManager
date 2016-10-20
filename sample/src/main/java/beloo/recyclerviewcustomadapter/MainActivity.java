package beloo.recyclerviewcustomadapter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.beloo.widget.spanlayoutmanager.ChipsLayoutManager;
import com.beloo.widget.spanlayoutmanager.gravity.IChildGravityResolver;

import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvTest;
    private RecyclerViewAdapter adapter;
    private Spinner spinnerPosition;
    private Spinner spinnerMoveTo;
    private List<String> positions;
    private List<String> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvTest = (RecyclerView) findViewById(R.id.rvTest);
        spinnerPosition = (Spinner) findViewById(R.id.spinnerPosition);
        spinnerMoveTo = (Spinner) findViewById(R.id.spinnerMoveTo);

//        items = new ItemsFactory().getFewItems();
        items = new ItemsFactory().getALotOfItems();

        positions = new LinkedList<>();
        for (int i = 0; i< items.size(); i++) {
            positions.add(String.valueOf(i));
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, positions);
        ArrayAdapter<String> spinnerAdapterMoveTo = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, positions);
        spinnerPosition.setAdapter(spinnerAdapter);
        spinnerMoveTo.setAdapter(spinnerAdapterMoveTo);

        adapter = new RecyclerViewAdapter(items);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2) {
            @Override
            public boolean canScrollVertically() {
                return true;
            }
        };
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return true;
            }
        };

        FixedGridLayoutManager fixedGridLayoutManager = new FixedGridLayoutManager();
        fixedGridLayoutManager.setTotalColumnCount(10);

        ChipsLayoutManager spanLayoutManager = ChipsLayoutManager.newBuilder(this)
                //set vertical gravity for all items in a row. Default = Gravity.CENTER_VERTICAL
                .setChildGravity(Gravity.TOP)
                //whether RecyclerView can scroll
                .setScrollingEnabled(true)
                //set gravity resolver where you can determine gravity for item in position. This method have priority over previous one
                .setGravityResolver(new IChildGravityResolver() {
                    @Override
                    public int getItemGravity(int position) {
                        return Gravity.CENTER;
                    }
                })
                .build();

        rvTest.addItemDecoration(new SpacingItemDecoration(getResources().getDimensionPixelOffset(R.dimen.item_space),
                getResources().getDimensionPixelOffset(R.dimen.item_space)));

        rvTest.setLayoutManager(spanLayoutManager);
        rvTest.getRecycledViewPool().setMaxRecycledViews(0, 10);
        rvTest.getRecycledViewPool().setMaxRecycledViews(1, 10);
        rvTest.setAdapter(adapter);

    }

    private void updateSpinner() {
        positions = new LinkedList<>();
        for (int i = 0; i< items.size(); i++) {
            positions.add(String.valueOf(i));
        }

        int selectedPosition = spinnerPosition.getSelectedItemPosition();

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, positions);
        spinnerPosition.setAdapter(spinnerAdapter);
        selectedPosition = Math.min(spinnerAdapter.getCount() -1 , selectedPosition);
        spinnerPosition.setSelection(selectedPosition);
    }

    public void onRevertClicked(View view) {
        int position = spinnerPosition.getSelectedItemPosition();
        if (position == Spinner.INVALID_POSITION)
            return;

        int positionMoveTo = spinnerMoveTo.getSelectedItemPosition();
        if (positionMoveTo == Spinner.INVALID_POSITION)
            return;

        if (position == positionMoveTo) return;

        spinnerPosition.setSelection(positionMoveTo);
        spinnerMoveTo.setSelection(position);
    }

    public void onDeleteClicked(View view) {
        int position = spinnerPosition.getSelectedItemPosition();
        if (position == Spinner.INVALID_POSITION)
            return;
        items.remove(position);
        Log.i("activity", "delete at " + position);
        adapter.notifyItemRemoved(position);
        updateSpinner();
    }

    public void onMoveClicked(View view) {
        int position = spinnerPosition.getSelectedItemPosition();
        if (position == Spinner.INVALID_POSITION)
            return;

        int positionMoveTo = spinnerMoveTo.getSelectedItemPosition();
        if (positionMoveTo == Spinner.INVALID_POSITION)
            return;

        if (position == positionMoveTo) return;

        String item = items.remove(position);
        items.add(positionMoveTo, item);

        adapter.notifyItemMoved(position, positionMoveTo);
    }

    public void onInsertClicked(View view) {
        int position = spinnerPosition.getSelectedItemPosition();
        if (position == Spinner.INVALID_POSITION)
            position = 0;
        items.add(position, "inserted item." + position);
        Log.i("activity", "insert at " + position);
        adapter.notifyItemInserted(position);
        updateSpinner();
    }
}
