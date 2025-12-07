package application;

public class JNI {

	static {
		System.loadLibrary("JNI");
	}
	
	public JNI() {
	}

	public native String execute(String txt);
	
	public static void main(String[] args) {
		JNI test = new JNI();
		System.out.println(test.execute("da li RADI?"));
	}
}
