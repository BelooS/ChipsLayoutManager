package beloo.recyclerviewcustomadapter;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import beloo.recyclerviewcustomadapter.entity.ChipsEntity;

public class ChipsFactory {

    List<ChipsEntity> getChips(Context context) {

        List<ChipsEntity> chipsList = new ArrayList<>();
        chipsList.add(ChipsEntity.newBuilder()
                .drawableResId(R.drawable.batman)
                .name("Batman")
                .build());

        chipsList.add(ChipsEntity.newBuilder()
                .drawableResId(R.drawable.girl1)
                .name("Veronic Cloyd")
                .build());

//        chipsList.add(ChipsEntity.newBuilder()
//                .drawableResId(R.drawable.girl2)
//                .name("Jayne")
//                .description("Everyone want to meet Jayne")
//                .build());

        chipsList.add(ChipsEntity.newBuilder()
                .drawableResId(R.drawable.girl3)
                .name("Cat")
                .build());

        chipsList.add(ChipsEntity.newBuilder()
                .drawableResId(R.drawable.girl2)
                .name("Jess")
                .build());

        chipsList.add(ChipsEntity.newBuilder()
                .drawableResId(R.drawable.girl1)
                .name("Ann Ackerman")
                .build());

//        chipsList.add(ChipsEntity.newBuilder()
//                .drawableResId(R.drawable.batman)
//                .name("Second Batman")
//                .description("Batman is our friend")
//                .build());

        chipsList.add(ChipsEntity.newBuilder()
                .drawableResId(R.drawable.girl2)
                .name("Claudette")
                .build());

        chipsList.add(ChipsEntity.newBuilder()
                .drawableResId(R.drawable.karl)
                .name("Karl")
                .build());

        chipsList.add(ChipsEntity.newBuilder()
                .drawableResId(R.drawable.anonymous)
                .name("Very Long Name Anonymous")
                .build());



        return chipsList;

    }
}
