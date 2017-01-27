/**

2. Add Two Numbers  
You are given two non-empty linked lists representing two non-negative integers. The digits are stored in reverse order and each of their nodes contain a single digit. Add the two numbers and return it as a linked list.

You may assume the two numbers do not contain any leading zero, except the number 0 itself.

Input: (2 -> 4 -> 3) + (5 -> 6 -> 4)
Output: 7 -> 0 -> 8


 * Definition for singly-linked list.
 * public class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode(int x) { val = x; }
 * }
 */
public class Solution {
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode l = l1;
        ListNode prev = l1;
        int t = 0;
        while(l1 != null && l2 != null){
            l1.val =l1.val + l2.val +t;
            t = l1.val / 10 ;
            l1.val %= 10;
            
            // if (l1.next == null && t > 0) {
            //     l1.next = new ListNode(t);
            // }
            prev = l1;
            l1 = l1.next;
            l2 = l2.next;
            
        }
        
        while (l2 != null) {
            prev.next = l2;
            l2.val = l2.val + t;
            t = l2.val / 10 ;
            l2.val %= 10;
            prev = l2;
            l2 = l2.next;
        }
        
        if ( t > 0 ){
            while (l1 != null){
                l1.val = l1.val + t;
                t = l1.val / 10;
                l1.val = l1.val % 10;
                prev = l1;
                l1 = l1.next;
            }
            if (t > 0)
                prev.next = new ListNode(t);
        }
       
        return l;
    }
}