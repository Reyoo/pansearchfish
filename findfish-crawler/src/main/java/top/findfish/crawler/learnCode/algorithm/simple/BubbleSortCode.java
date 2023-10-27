package top.findfish.crawler.learnCode.algorithm.simple;

/**
 * *************************************************************************
 * <p/>
 *
 * @文件名称: BubbleSortCode.java
 * @包 路 径： top.findfish.crawler.learnCode.algorithm.simple
 * @类描述:
 * @版本: V1.0
 * @Author：SunQi
 * @创建时间：2023/10/27 15:39
 */
public class BubbleSortCode {


    /**
     * 升序  冒泡
     * @param arr
     */
    public static void bubbleSortAsc(int[] arr) {
        int temp;
        for(int i = 0;i<arr.length -1 ;i++){
            for(int j = 0; j< arr.length-i-1;j++){
                if(arr[j] > arr[j+1]){
                    temp = arr[j];
                    arr[j] = arr[j+1];
                    arr[j+1] = temp;
                }
            }
        }
    }


    public static void bubbleSortDesc(int[] arr){
        int temp;
        for(int i = 0;i<arr.length -1 ;i++){
            for (int j = 0 ;j<arr.length-i-1 ;j++){
               if(arr[j] < arr[j+1]){
                   temp = arr[j];
                   arr[j] = arr[j+1];
                   arr[j+1] = temp;
               }
            }
        }
    }


    public static void main(String[] args) {
        int[] arr = new int[]{4,5,2,5,6,7,8,3,87,24,124,52};
        bubbleSortDesc(arr);

    }



}
