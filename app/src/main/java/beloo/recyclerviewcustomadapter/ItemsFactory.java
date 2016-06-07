package beloo.recyclerviewcustomadapter;

import java.util.LinkedList;
import java.util.List;

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
        list.add("Item10003.19");
        list.add("Item10004.20");
        list.add("Item10005.21");
        list.add("Item10006.22");
        list.add("Item10007.23");
        list.add("Item10008.24");
        list.add("Item10009.25");
        list.add("!Item10010.26");
        list.add("Item10011.27");
        list.add("Item10012.28");
        list.add("Item10013.29");
        list.add("I");
        list.add("A");
        list.add("C");
        list.add("long item here");
        list.add("long item here 2");
        return list;
    }

    public List<String> getALotOfItems() {
        List<String> list = new LinkedList<>();
        list.add("START item");
        for (int i =0; i< 200; i++) {
            if (i%2 == 0) {
                list.add("a span");
            } else if (i%3 == 0) {
                list.add("!long item here");
            } else if (i % 5 == 0) {
                list.add("S");
            }
        }
        return list;
    }
}
