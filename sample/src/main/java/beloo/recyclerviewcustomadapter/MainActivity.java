package beloo.recyclerviewcustomadapter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.beloo.widget.spanlayoutmanager.ChipsLayoutManager;
import com.beloo.widget.spanlayoutmanager.gravity.IChildGravityResolver;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import beloo.recyclerviewcustomadapter.adapter.ChipsAdapter;
import beloo.recyclerviewcustomadapter.adapter.RecyclerViewAdapter;
import beloo.recyclerviewcustomadapter.entity.ChipsEntity;

public class MainActivity extends AppCompatActivity {
    private static final String EXTRA = "data";
    private RecyclerView rvTest;
    private RecyclerView.Adapter adapter;
    private Spinner spinnerPosition;
    private Spinner spinnerMoveTo;
    private List<String> positions;
    private List items;

    private RecyclerView.Adapter createChipsAdapter() {
        List<ChipsEntity> items = new ChipsFactory().getChips();
        this.items = items;
        return new ChipsAdapter(items, onRemoveListener);
    }

    private OnRemoveListener onRemoveListener = new OnRemoveListener() {
        @Override
        public void onItemRemoved(int position) {
            items.remove(position);
            Log.i("activity", "delete at " + position);
            adapter.notifyItemRemoved(position);
            updateSpinner();
        }
    };

    private RecyclerView.Adapter createItemsAdapter(Bundle savedInstanceState) {

        List<String> items;
        if (savedInstanceState == null) {
//            items = new ItemsFactory().getFewItems();
            items = new ItemsFactory().getALotOfItems();
//            items = new ItemsFactory().getItems();
        } else {
            items = savedInstanceState.getStringArrayList(EXTRA);
        }

        adapter = new RecyclerViewAdapter(items, onRemoveListener);
        this.items = items;

        return adapter;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvTest = (RecyclerView) findViewById(R.id.rvTest);
        spinnerPosition = (Spinner) findViewById(R.id.spinnerPosition);
        spinnerMoveTo = (Spinner) findViewById(R.id.spinnerMoveTo);

//        adapter = createChipsAdapter();
        adapter = createItemsAdapter(savedInstanceState);

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

        positions = new LinkedList<>();
        for (int i = 0; i< items.size(); i++) {
            positions.add(String.valueOf(i));
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, positions);
        ArrayAdapter<String> spinnerAdapterMoveTo = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, positions);
        spinnerPosition.setAdapter(spinnerAdapter);
        spinnerMoveTo.setAdapter(spinnerAdapterMoveTo);

        rvTest.setLayoutManager(spanLayoutManager);
        rvTest.getRecycledViewPool().setMaxRecycledViews(0, 10);
        rvTest.getRecycledViewPool().setMaxRecycledViews(1, 10);
        rvTest.setAdapter(adapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(EXTRA, new ArrayList<>(items));
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
//        int position = spinnerPosition.getSelectedItemPosition();
//        if (position == Spinner.INVALID_POSITION)
//            return;
//
//        int positionMoveTo = spinnerMoveTo.getSelectedItemPosition();
//        if (positionMoveTo == Spinner.INVALID_POSITION)
//            return;
//
//        if (position == positionMoveTo) return;
//
//        Object item = items.remove(position);
//        items.add(positionMoveTo, item);
//
//        adapter.notifyItemMoved(position, positionMoveTo);
        rvTest.scrollToPosition(126);
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
