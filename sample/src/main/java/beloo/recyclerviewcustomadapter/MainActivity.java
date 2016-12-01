package beloo.recyclerviewcustomadapter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.beloo.widget.chipslayoutmanager.SpacingItemDecoration;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String EXTRA = "data";
    private RecyclerView rvTest;
    private RecyclerView.Adapter adapter;
    private Spinner spinnerPosition;
    private Spinner spinnerMoveTo;
    private List<String> positions;
    private List items;

    /** replace here different data sets */
    private IItemsFactory itemsFactory = new ShortChipsFactory();

    private OnRemoveListener onRemoveListener = new OnRemoveListener() {
        @Override
        public void onItemRemoved(int position) {
            items.remove(position);
            Log.i("activity", "delete at " + position);
            adapter.notifyItemRemoved(position);
            updateSpinners();
        }
    };

    @SuppressWarnings("unchecked")
    private RecyclerView.Adapter createAdapter(Bundle savedInstanceState) {

        List<String> items;
        if (savedInstanceState == null) {
//            items = itemsFactory.getFewItems();
//            items = itemsFactory.getALotOfItems();/
            items = itemsFactory.getItems();
        } else {
            items = savedInstanceState.getStringArrayList(EXTRA);
        }

        adapter = itemsFactory.createAdapter(items, onRemoveListener);
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

        adapter = createAdapter(savedInstanceState);

        ChipsLayoutManager spanLayoutManager = ChipsLayoutManager.newBuilder(this)
                .setOrientation(ChipsLayoutManager.HORIZONTAL)
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
//        rvTest.setLayoutManager(new LinearLayoutManager(this));
        rvTest.getRecycledViewPool().setMaxRecycledViews(0, 10);
        rvTest.getRecycledViewPool().setMaxRecycledViews(1, 10);
        rvTest.setAdapter(adapter);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(EXTRA, new ArrayList<>(items));
    }

    private void updateSpinners() {
        positions = new LinkedList<>();
        for (int i = 0; i< items.size(); i++) {
            positions.add(String.valueOf(i));
        }

        int selectedPosition = Math.min(spinnerPosition.getSelectedItemPosition(), positions.size() - 1);
        int selectedMoveToPosition = Math.min(spinnerMoveTo.getSelectedItemPosition(), positions.size() -1);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, positions);
        spinnerPosition.setAdapter(spinnerAdapter);
        selectedPosition = Math.min(spinnerAdapter.getCount() -1 , selectedPosition);
        spinnerPosition.setSelection(selectedPosition);

        ArrayAdapter<String> spinnerAdapterMoveTo = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, positions);
        spinnerMoveTo.setAdapter(spinnerAdapterMoveTo);
        spinnerMoveTo.setSelection(selectedMoveToPosition);
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
        updateSpinners();
    }

    public void onMoveClicked(View view) {
        int position = spinnerPosition.getSelectedItemPosition();
        if (position == Spinner.INVALID_POSITION)
            return;

        int positionMoveTo = spinnerMoveTo.getSelectedItemPosition();
        if (positionMoveTo == Spinner.INVALID_POSITION)
            return;

        if (position == positionMoveTo) return;

        Object item = items.remove(position);
        items.add(positionMoveTo, item);

        adapter.notifyItemMoved(position, positionMoveTo);
    }

    public void onInsertClicked(View view) {
        int position = spinnerPosition.getSelectedItemPosition();
        if (position == Spinner.INVALID_POSITION)
            position = 0;
        items.add(position, itemsFactory.createOneItemForPosition(position));
        Log.i("activity", "insert at " + position);
        adapter.notifyItemInserted(position);
        updateSpinners();
    }
}
