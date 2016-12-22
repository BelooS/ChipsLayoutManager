package com.beloo.chipslayoutmanager.sample.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import com.beloo.chipslayoutmanager.sample.R;
import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private Drawer drawer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainer, ItemsFragment.newInstance())
                    .commit();
        }

        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawer = new DrawerBuilder(this)
                .withToolbar(toolbar)
                .addDrawerItems(new PrimaryDrawerItem().withName(R.string.main).withIdentifier(1))
                .addDrawerItems(new PrimaryDrawerItem().withName(R.string.bottom_sheet).withIdentifier(2))
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        int id = (int) drawerItem.getIdentifier();
                        switch (id) {
                            case 1:
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.fragmentContainer, ItemsFragment.newInstance())
                                        .commit();
                                drawer.closeDrawer();
                                break;
                            case 2:
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.fragmentContainer, BottomSheetFragment.newInstance())
                                        .commit();
                                drawer.closeDrawer();
                                break;
                        }
                        return true;
                    }
                })
                .build();
    }
}
