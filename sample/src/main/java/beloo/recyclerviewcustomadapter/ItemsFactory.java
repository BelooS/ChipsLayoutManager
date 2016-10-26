package beloo.recyclerviewcustomadapter;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class ItemsFactory {
    public List<String> getItems() {
        List<String> list = new LinkedList<>();
        list.add("Item0.0");
        list.add("Item1.1");
        list.add("Item2.2");
        list.add("Item3.3");
        list.add("Item4.4");
        list.add("!Item5.5");
        list.add("Item6.6");
        list.add("Item7.7");
        list.add("Item8.8");
        list.add("Item9.9");
        list.add("Item10.10");
        list.add("!Item11.11");
        list.add("Item12.12");
        list.add("Item1001.13");
        list.add("Item1002.14");
        list.add("Item1003.15");
        list.add("!Item1004.16");
        list.add("!Item10001.17");
        list.add("Item10002.18");
        return list;
    }

    public List<String> getFewItems() {
        List<String> list = new LinkedList<>();
        list.add("Item0.0");
        list.add("Item1.1");
        list.add("Item2.2");
        list.add("Item3.3");
        list.add("4");
        list.add("5");
        list.add("Item5");
        return list;
    }

    public List<String> getALotOfItems() {
        List<String> list = new LinkedList<>();
        list.add("START item.0");
        list.add("!tall item here. 1");
        for (int i = 2; i< 1000; i++) {
            if (i%3 == 0) {
                list.add("!tall item here." + i);
            } else if (i % 5 == 0) {
                list.add("S." + i);
            } else {
                list.add("a span." + i);
            }
        }
        return list;
    }

    public List<String> getALotOfRandomItems() {
        List<String> list = new LinkedList<>();
        list.add("START item.0");
        for (int i =0; i< 1000; i++) {

            Random random = new Random();

            int rand = random.nextInt(3);
            switch (rand) {
                case 1:
                    list.add("a span." + (i+1));
                    break;
                case 2:
                    list.add("!tall item here." + (i+1));
                    break;
                default:
                    list.add("S." + (i+1));
                    break;
            }

        }
        return list;
    }
}
