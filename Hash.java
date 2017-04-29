import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {
	public static int md5(String str, int hostNumber) {
		int hostId = 0;
		try {
			MessageDigest md =  MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			byte[] bArr= md.digest();
			BigInteger number = new BigInteger(1, bArr);
			hostId = (int) ((int) (Math.abs(number.intValue()) % 65536) * hostNumber / 65536);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return hostId;
	}
}
