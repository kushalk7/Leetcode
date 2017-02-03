package programming.questions.solutions;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiThreadedPalindrome {
	String s ;
	MultiThreadedPalindrome(String str){
		s = str;
	}
	public static void main(String[] args) {
//		String phrase = "A man,a plan,a canal,Panama!";
		String phrase = "A man, a plan, a cameo, Zena, Bird, Mocha, Prowel, a rave, Uganda, Wait,"
				+ " a lobola, Argo, Goto, Koserre, Sokoto, Gogra, a lobo, Lati, a wadna, Guevara,"
				+ " Lew, Orpah, Comdr, Ibanez, OEM, a canal, Panama!";
		MultiThreadedPalindrome p = new MultiThreadedPalindrome(phrase);
		int k = 7; // No. of Threads
		
		if(!p.isPalindrome(k)){
			System.out.println("Phrase : "+phrase+"\n not a Palindrome");
		}
		else{
			System.out.println("Phrase : "+phrase+"\n is a Palindrome");			
		}
		
	}
	
	public boolean isPalindrome(int k){
		WorkerThread[] workers = new WorkerThread[k];
		WorkerThread.setNumberofThreads(k);
		Thread[] t = new Thread[k];
		PalindromeThread[] pThreads = new PalindromeThread[k];
		int len = s.length();
		int taskSize = len/k;
		for(int i=0; i < k ; i++){
			workers[i] = new WorkerThread(s, i*taskSize, (i+1)*taskSize-1);
			t[i] = new Thread(workers[i],String.valueOf(i));
			t[i].start();
		}
		
		try {
			for (int i=0 ; i<k ; i++){
				t[i].join();				
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(Arrays.toString(WorkerThread.getParsedStr())); //workers[k-1].getParsedStr()
		
		len = WorkerThread.getValidCharLength();
		int leftStart = 0,rightStart;
		int interval = (len/(k*2));
		for(int i=0 ; i< k ; i++){
			leftStart = interval*i; 
			rightStart = len - leftStart;
			pThreads[i] = new PalindromeThread(s, leftStart, rightStart, interval);
			pThreads[i].start();
		}
		try {
			pThreads[k-1].join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return PalindromeThread.getIsPalindrome();
	}
	
	
}

class WorkerThread implements Runnable{
	int start;
	int end;
	String str;
	int threadNumber;
	private static int k;
	private static Integer len = 0;
	private static String monitor = new String("");
	private static int[] validchar;
	private static final AtomicInteger threadCnt = new AtomicInteger(-1);
	private static int tcnt = 0;
	private static char[] parsedStr;
	public WorkerThread(String s,int startIndex, int endIndex){
		str = s.toLowerCase();
		start = startIndex;
		end = (endIndex > s.length())? s.length()-1 : endIndex;
		System.out.println("Thread "+threadCnt.incrementAndGet()+" created start:"+start+" end:"+end);
	}

	@Override
	public void run() {
//		validchar = new int[k];
		threadNumber = Integer.parseInt(Thread.currentThread().getName());
		System.out.println("Thread "+threadNumber+" running");
		/**
		 * Phase 1
		 * get valid char count
		 */
		int cnt = 0;
		for(int i = start ; i<= end ; i++){
			if (Character.isLetter(str.charAt(i))){
				cnt++;
			}
		}
		
		synchronized(monitor){
			validchar[threadNumber] = cnt;
			len += cnt;
			System.out.println("Thread "+threadNumber+" Added Length: "+len);
			
			if(tcnt == (threadCnt.get())){
				System.out.println("Thread "+threadNumber+" invokesAll with "+cnt+" validChar:"+Arrays.toString(validchar));
				/*
				 * Create char array of length of valid chars
				 */
				if(parsedStr == null){
					parsedStr = new char[len];
					System.out.println("array of Length:"+len+" created");
				}
				monitor.notifyAll();
				tcnt++;
			}
			else{
				tcnt++;
				try {
					System.out.println("Thread "+threadNumber+" waiting with "+cnt+" validChar:"+Arrays.toString(validchar));
					monitor.wait();					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}			
		}		
	
		/*
		 * Fill char array with valid chars
		 */
		int curr=0,stop=0;
		for(int i=0;i<threadNumber;i++){
			curr += validchar[i]; 
		}
		stop = curr + cnt - 1;
		int temp = curr;
		for(int i = start ; i<= end ; i++){
			if (Character.isLetter(str.charAt(i))){
				synchronized(parsedStr){
					if(curr < len){
						parsedStr[temp++] = str.charAt(i);
					}
				}
			}
		}
		
//		temp = stop;
//		Boolean flag = false;
//		for(int i=curr; i<stop ; i++){
//			System.out.println("Thread "+threadNumber+" i:"+i+" stop:"+stop);
//			if(parsedStr[curr] != parsedStr[temp]){
//				flag = false;
//				break;
//			}
//			flag = true;
//		}
//		
//		if(!flag){
//			System.out.println("Not Palindrome");
//		}
//		else{
//			System.out.println("Palindrome");			
//		}
		System.out.println("Thread "+threadNumber+" done running");
		
	}

	public static void setNumberofThreads(int k){
		k = k;
		validchar = new int[k];
	}
	public static char[] getParsedStr() {
		return parsedStr;
	}
	public static int getValidCharLength(){
		return len.intValue();
	}
	
}

class PalindromeThread extends Thread {
	int leftStart,rtStart,interval = 0;
	String S;
	private static boolean isPalindrome = false;
	
	public PalindromeThread(String S,int ls, int rs, int interval){
		this.S = S;
		this.leftStart = ls;
		this.rtStart = rs;
		this.interval = interval;
	}
	
	public void run(){
		int end = leftStart+interval;
		boolean flag = false;
		for (int i =leftStart; i< end; i++){
			if(S.charAt(i) != S.charAt(rtStart)){
				flag = false;
			}
			flag = true;
			rtStart--;
		}
		isPalindrome = flag;
//		if(!flag){
//			System.out.println("Not Palindrome");
//		}
//		else{
//			System.out.println("Palindrome");			
//		}
	}
	
	public static boolean getIsPalindrome(){
		return isPalindrome;
	}
}
