package icp;

import icp.application.SessionManager;

/**
 * Hlavn� spou�t�c� t��da aplikace.
 * @author Ji�� Ku�era
 */
public class Main {

	public static void main(String[] args) {
		new SessionManager().startGui();
	}
}
