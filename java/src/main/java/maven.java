import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;

public class maven {

	public static void main(String[] args) throws IOException {
		File libs = new File("./libs");
		addJarsToRepository(libs);
	}

	private static void addJarsToRepository(File dir) throws IOException {

		File[] files = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".jar");
			}
		});

		for (File file : files) {
			String name = file.getName().trim();
			name = name.substring(0, name.length() - 4);
			// System.out.println(name);
			String cmd = String.format("mvn install:install-file -Dfile=%s -DgroupId=com.local -DartifactId=%s -Dversion=1.0 -Dpackaging=jar", file.getAbsolutePath(), name);

			// Execute command
			Process child = Runtime.getRuntime().exec(new String[] { "cmd.exe", "/c", cmd });

			BufferedReader in = new BufferedReader(new InputStreamReader(child.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				// System.out.println(line);
			}
			in.close();

			System.out.println(String.format("<dependency><groupId>com.local</groupId><artifactId>%s</artifactId><version>1.0</version></dependency>", name));
		}

		for (File newdir : dir.listFiles()) {
			if (newdir.isDirectory()) {
				addJarsToRepository(newdir);
			}
		}
	}

}
