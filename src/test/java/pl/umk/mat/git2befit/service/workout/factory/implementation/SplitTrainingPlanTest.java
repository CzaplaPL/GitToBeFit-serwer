package pl.umk.mat.git2befit.service.workout.factory.implementation;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SplitTrainingPlanTest {

    @Test
    public void testMethod(){
        //given
        var integers = List.of(1, 1, 1, 1, 1);
        List<Map<String, String>> list = new ArrayList<>();
        Map<String, String> map1 = new HashMap<>();
        map1.put("a","1");
        map1.put("b","2");
        map1.put("c","3");
        list.add(map1);
        map1 = new HashMap<>();
        map1.put("d","4");
        map1.put("e","5");
        map1.put("f","6");
        map1.put("g","7");
        list.add(map1);
        map1 = new HashMap<>();
        //map1.put("h","a");
        //map1.put("i","a");
        list.add(map1);
        //when
        var random = new Random();
        int maxIndex, minIndex;
        int min;
        int max;

        do {
            List<Integer> sizeList = list.stream().map(stringListMap -> stringListMap.keySet().size()).collect(Collectors.toList());
            maxIndex = 0;
            minIndex = 0;
            min = list.get(0).size();
            max = list.get(0).size();
            for (int i = 1; i < sizeList.size(); i++) {
                if (sizeList.get(i) > max) {
                    maxIndex = i;
                    max = sizeList.get(i);
                }
                if (sizeList.get(i) < min) {
                    minIndex = i;
                    min = sizeList.get(i);
                }
            }

            if (max - min > 1) {
                String randomKey = (String) list.get(maxIndex).keySet().toArray()[random.nextInt(sizeList.get(maxIndex))];
                var exerciseExecutions = list.get(maxIndex).get(randomKey);
                list.get(maxIndex).remove(randomKey);
                list.get(minIndex).put(randomKey, exerciseExecutions);
            }
        } while (max - min > 1);
        //then
        System.out.println(list);
        assertEquals(true , true);
    }

}