package icp.online.tcpip;

import java.util.LinkedList;

/**
 * N�zev �lohy: Jednoduch� BCI
 * T��da: SynchronizedLinkedListObject
 * @author Michal Pato�ka
 * Prvn� verze vytvo�ena: 3.3.2010
 * @version 1.0
 * 
 * Thread-safe linked list, pou��van� jako buffer objekt� typu RDA.
 * P�et�eny jsou pouze pou��van� metody.
 */

public class SynchronizedLinkedListObject extends LinkedList<Object> {
	private static final long serialVersionUID = 1L;

        @Override
	public synchronized void addLast(Object o){
		super.add(o);
	}
	
        @Override
	public synchronized Object removeFirst(){
		return super.removeFirst();
	}
	
        @Override
	public synchronized boolean isEmpty(){
		return super.isEmpty();
	}

}
