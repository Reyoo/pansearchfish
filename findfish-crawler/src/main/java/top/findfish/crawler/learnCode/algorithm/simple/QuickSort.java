package top.findfish.crawler.learnCode.algorithm.simple;

import java.util.Arrays;

/**
 * *************************************************************************
 * <p/>
 *
 * @文件名称: QuickSort.java
 * @包 路 径： top.findfish.crawler.learnCode.algorithm.simple
 * @类描述:
 * @版本: V1.0
 * @Author：SunQi
 * @创建时间：2023/10/25 15:54
 */
public class QuickSort {
    public static void quickSort(int[] array, int low, int high) {
        if (low < high) {
            int pivot = partition(array, low, high);
            quickSort(array, low, pivot - 1);
            quickSort(array, pivot + 1, high);
        }
    }

    private static int partition(int[] array, int low, int high) {
        int pivot = array[high];
        int i = (low - 1);
        for (int j = low; j < high; j++) {
            if (array[j] < pivot) {
                i++;
                int temp = array[i];
                array[i] = array[j];
                array[j] = temp;
            }
        }
        int temp = array[i + 1];
        array[i + 1] = array[high];
        array[high] = temp;
        return i + 1;
    }

    public static void main(String[] args) {
        int[] array = {12, 4, 5, 6, 7, 3, 1, 15};
        int n = array.length;
        quickSort(array, 0, n - 1);
        System.out.println("Sorted array: " + Arrays.toString(array));
    }
}

