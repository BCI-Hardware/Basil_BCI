package icp.online.tcpip.objects;

import java.util.LinkedList;

/**
 * N�zev �lohy: Jednoduch� BCI
 * T��da: SynchronizedLinkedListByte
 * @author Michal Pato�ka
 * Prvn� verze vytvo�ena: 3.3.2010
 * @version 1.0
 * 
 * Thread-safe linked list, pou��van� jako buffer bajt� pro tcp/ip clienta.
 * P�et�eny jsou pouze pou��van� metody.
 */


public class SynchronizedLinkedListByte extends LinkedList<Byte> {
	private static final long serialVersionUID = 1L;

	public synchronized void addLast(Byte b){
		super.add(b);
	}
	
	public synchronized Byte removeFirst(){
		return super.removeFirst();
	}
	
	public synchronized boolean isEmpty(){
		return super.isEmpty();
	}

}
