package top.findfish.crawler.learnCode.algorithm.simple;

/**
 * 反转字符串
 * 写出一个程序，接受一个字符串，然后输出该字符串反转后的字符串。（字符串长度不超过1000）
 * 数据范围： 0 \le n \le 10000≤n≤1000
 * 要求：空间复杂度 O(n)O(n)，时间复杂度 O(n)O(n)
 * 输入： "abcd"
 * 返回值："dcba"
 */
public class ReverseStringCode {

    public static void main(String[] args) {
        String normalStr = "abcd";
        /**
         * 第一种方法  新建一个和待反转字符串相同大小的新字符串数组
         */
        char[] reverseStr = new char[normalStr.length()];
        int len = normalStr.length();
        for(int i = 0 ; i < len ; i++){
            reverseStr[i] = normalStr.charAt(len -1-i);
        }
        System.out.println(new String(reverseStr));

        /**
         * 第二种方法 字符串内部交换
         */

        char[] normalStrChar = normalStr.toCharArray();
        char tempChar ;
        for(int i = 0 ; i < len/2 ; i++ ){
            tempChar = normalStrChar[i];
            normalStrChar[i] = normalStrChar[len-1-i];
            normalStrChar[len-1-i] = tempChar;
        }
        System.out.println(new String(normalStrChar));

        /**
         *  第三种方式 直接调用 api
         */
        StringBuffer stringBuffer = new StringBuffer(normalStr);
        StringBuffer reverse = stringBuffer.reverse();
        System.out.println(reverse);
    }

}
