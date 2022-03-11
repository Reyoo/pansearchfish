package top.findfish.crawler.learnCode;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.concurrent.*;

/**
 * TODO
 *
 * @author sun71
 * @version 1.0
 * @date 2021/10/21 22:40
 * @Description: 二分查找
 * 二分查找算法
 * 每次查找取数组中位数的值进行比较，
 * 如果目标值值大于中位数的值，则截取中位数右侧的数组再次进行二分查找
 * 如果目标值小于中位数的值，则截取中位数左侧的数组再次进行二分查找
 * 直到找到相对应的中位数才终止查找算法。
 * 即每经过一次比较,查找范围就缩小一半。
 */
@Data
public class BinSearchLearn {

    String abc;

    private static int binSearch(int array[], int start, int end, int value) {
        int middle = (start + end) >> 1;
        if (array[middle] == value) {
            return middle;
        }

        if (start >= end) {
            return -1;
        } else if (array[middle] > value) {
            return binSearch(array, start, middle - 1, value);
        } else {
            return binSearch(array, middle + 1, end, value);
        }
    }

    public static void main(String[] args) {
        Hashtable<Object, Object> objectObjectHashtable = new Hashtable<>();
        HashSet<Object> objects = new HashSet<>();
        HashMap<Object, Object> objectObjectHashMap = new HashMap<>();



        Apple appleA = new Apple("张三",10);
        Apple apple = new Apple("李四", 20);
        Apple apple1 = new Apple("张三", 30);

        objects.add(apple);
        objects.add(apple1);
        objects.add(appleA);

        System.out.println(objects.size());
        objects.stream().forEach( o -> {
            System.out.println(o);
        });


        objectObjectHashMap.put(appleA,"1");
        objectObjectHashMap.put(apple,"2");
        objectObjectHashMap.put(apple1,"3");


        System.out.println(objectObjectHashMap.get(apple1));

//        BinSearchLearn binSearchLearn = new BinSearchLearn();
//        System.out.println(binSearchLearn.hashCode());





    }

}
