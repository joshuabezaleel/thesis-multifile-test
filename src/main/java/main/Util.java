package main;

public class Util {
	
	public Util() {
		
	}
	
	public void clearScreen() {
		System.out.println(new String(new char[50]).replace("\0", "\r\n"));
	}
}
