package icp.data;

/**
 * V�jimka vyhozen� p�i chybn� zadan�m indexu v <code>Buffer</code>u.
 * @author Ji�� Ku�era
 */
public class InvalidFrameIndexException extends Exception {
	public InvalidFrameIndexException(String message) {
		super(message);
	}
}
