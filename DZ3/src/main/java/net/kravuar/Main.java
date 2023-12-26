package net.kravuar;

import net.kravuar.model.Child;
import net.kravuar.model.Parent;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        System.out.println("CountMap");
        System.out.println();

        var map = new CountMapImpl<Integer>();

        map.add(10);
        map.add(10);
        map.add(5);
        map.add(6);
        map.add(5);
        map.add(10);

        System.out.println("5: " + map.getCount(5)); // 2
        System.out.println("6: " + map.getCount(6)); // 1
        System.out.println("10: " + map.getCount(10)); // 3
        assert (map.getCount(5) == 2);
        assert (map.getCount(6) == 1);
        assert (map.getCount(10) == 3);

        System.out.println("==============================");

        var destination = new HashMap<Number, Integer>();
        map.toMap(destination);
        var asMap = map.toMap();

        System.out.println("To dest: " + destination); // { 10: 3, 5: 2, 6: 1 }
        System.out.println("As map: " + asMap); // { 10: 3, 5: 2, 6: 1 }
        assert (destination.equals(Map.of(10, 3, 5, 2, 6, 1)));
        assert (asMap.equals(Map.of(10, 3, 5, 2, 6, 1)));

        System.out.println("==============================");

        var old5th = map.remove(5);
        var new5th = map.getCount(5);
        System.out.println("5th before remove: " + old5th);
        System.out.println("5th after remove: " + new5th);
        assert (old5th == 2);
        assert (new5th == 1);

        System.out.println("==============================");

        var old6th = map.remove(6);
        var new6th = map.getCount(6);
        var countMapView = map.toMap();
        System.out.println("6th before remove: " + old6th);
        System.out.println("6th after remove: " + new6th);
        System.out.println("Count map after completely removing 6 (and decreasing 5): " + countMapView); // { 10: 3, 5: 1 }
        assert (old6th == 1);
        assert (new6th == 0);
        assert (countMapView.equals(Map.of(10, 3, 5, 1)));

        System.out.println("==============================");

        var size = map.size();
        System.out.println("Size after manipulations: " + size); // 4
        assert (size == 4);

        System.out.println("==============================");

        System.out.println();
        System.out.println();
        System.out.println();

        System.out.println("CollectionUtils");
        System.out.println();

        List<Integer> arrayList = CollectionUtils.newArrayList();
        System.out.println("New integer array list: " + arrayList);
        assert (arrayList.equals(new ArrayList<Integer>()));

        System.out.println("==============================");

        Child toSearch = new Child("toSearch");
        List<Child> toSearchIn = List.of(new Child("bebe"), toSearch, new Child("baba"));
        var toSearchIndex = CollectionUtils.<Parent>indexOf(
                toSearchIn,
                toSearch
        );
        System.out.println("toSearch: " + toSearch);
        System.out.println("To search in: " + toSearchIn);
        System.out.println("toSearch idx: " + toSearchIndex);
        assert (toSearchIndex == 1);

        System.out.println("==============================");

        List<Child> toLimit = List.of(new Child("bebe"), new Child("baba"), new Child("byby"), new Child("bobo"));
        var limitedList = CollectionUtils.<Parent>limit(
                toLimit,
                3
        );
        System.out.println("To limit: " + toLimit);
        System.out.println("Limited list (3): " + limitedList);
        assert (limitedList.size() == 3);

        System.out.println("==============================");

        Child toAdd = new Child("toAdd");
        List<Parent> toAddDestination = new ArrayList<>();
        CollectionUtils.<Parent>add(toAddDestination, toAdd);
        System.out.println("To add: " + toAdd);
        System.out.println("After add: " + toAddDestination);
        assert (toAddDestination.contains(toAdd));

        System.out.println("==============================");

        List<Child> common = new ArrayList<>(List.of(new Child("bebe"), new Child("baba")));
        List<Object> commonWith1Different = Stream.concat(
                Stream.of(new Child("different")), // 1 different element
                common.stream()
        ).map(Object.class::cast).collect(Collectors.toCollection(ArrayList::new));
        var containsAny = CollectionUtils.<Parent>containsAny(commonWith1Different, common);
        var containsAll = CollectionUtils.<Parent>containsAll(commonWith1Different, common);
        System.out.println("Common: " + common);
        System.out.println("Common with 1 different: " + commonWith1Different);
        System.out.println("Common with 1 different contains ANY from common: " + containsAny);
        System.out.println("Common with 1 different contains ALL from common: " + containsAll);
        assert (containsAny);
        assert (containsAll);

        System.out.println("==============================");

        System.out.println("Common with 1 different before removing common: " + commonWith1Different);
        CollectionUtils.<Parent>removeAll(commonWith1Different, common);
        System.out.println("Common with 1 different after removing common: " + commonWith1Different);
        assert (commonWith1Different.size() == 1);

        System.out.println("==============================");

        List<Child> toFilter = new ArrayList<>(List.of(new Child("be"), new Child("bebebe"), new Child("bebebebebebebebe")));
        var min = new Child("bebe");
        var max = new Child("bebebebeb");
        List<Child> filtered = CollectionUtils.range(toFilter, min, max); // The middle one will remain
        System.out.println("Before range: " + toFilter);
        System.out.println("After range within: [" + min + "; " + max + "]: " + filtered);
        assert (filtered.size() == 1);

        System.out.println("==============================");

        filtered = CollectionUtils.range(toFilter, min, max, Comparator.comparingInt(a -> a.getName().length())); // The middle one will remain
        System.out.println("Before range (with comparator): " + toFilter);
        System.out.println("After range within: [" + min + "; " + max + "]: " + filtered);
        assert (filtered.size() == 1);
    }
}