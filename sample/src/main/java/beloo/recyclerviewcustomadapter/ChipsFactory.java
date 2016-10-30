package beloo.recyclerviewcustomadapter;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import beloo.recyclerviewcustomadapter.entity.ChipsEntity;

public class ChipsFactory {

    List<ChipsEntity> getFewChips() {

        List<ChipsEntity> chipsList = new ArrayList<>();
        chipsList.add(ChipsEntity.newBuilder()
                .drawableResId(R.drawable.batman)
                .name("Batman")
                .build());

        chipsList.add(ChipsEntity.newBuilder()
                .drawableResId(R.drawable.girl1)
                .name("Veronic Cloyd")
                .build());

        chipsList.add(ChipsEntity.newBuilder()
                .drawableResId(R.drawable.girl2)
                .name("Jayne")
                .description("Everyone want to meet Jayne")
                .build());

        chipsList.add(ChipsEntity.newBuilder()
                .drawableResId(R.drawable.girl3)
                .name("Cat")
                .build());

        chipsList.add(ChipsEntity.newBuilder()
                .drawableResId(R.drawable.girl2)
                .name("Jess")
                .build());

        chipsList.add(ChipsEntity.newBuilder()
                .drawableResId(R.drawable.girl4)
                .name("Ann Ackerman")
                .build());

        chipsList.add(ChipsEntity.newBuilder()
                .drawableResId(R.drawable.batman)
                .name("Second Batman")
                .description("Batman is our friend")
                .build());

        chipsList.add(ChipsEntity.newBuilder()
                .drawableResId(R.drawable.girl5)
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

    List<ChipsEntity> getChips() {
        List<ChipsEntity> chipsEntities = getFewChips();

        List<ChipsEntity> secondPortion = getFewChips();
        Collections.reverse(secondPortion);
        chipsEntities.addAll(secondPortion);
        chipsEntities.addAll(getFewChips());
        chipsEntities.addAll(getFewChips());

        for (int i=0; i< chipsEntities.size(); i++) {
            ChipsEntity chipsEntity = chipsEntities.get(i);
            chipsEntity.setName(chipsEntity.getName() + " " + i);
        }

        return chipsEntities;
    }

    List<ChipsEntity> getDoubleChips() {
        List<ChipsEntity> chipsEntities = getFewChips();

        List<ChipsEntity> secondPortion = getFewChips();
        Collections.reverse(secondPortion);
        chipsEntities.addAll(secondPortion);
        return chipsEntities;
    }
}
