import java.awt.EventQueue;

import javax.swing.JFrame;

import pyhton.IProgress;
import pyhton.LoadMZXML;

public class Startup {

	public static boolean DEBUG = true;

	public static LoadMZXML loadMZML(IProgress progressTracker) {
		LoadMZXML.Param param = new LoadMZXML.Param();
		param.filepath = "E:\\Enironments\\data\\abcdefgh_1.mzXML";
		param.lines = 8;
		param.widthInMM = 62;
		param.heightInMM = 10;
		param.downMotionInMM = 1.25f;
		try {
			LoadMZXML loadMZXML = new LoadMZXML(param, LoadMZXML.Type.NORMAL);
			loadMZXML.setProgressListener(progressTracker);
			return loadMZXML;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return null;
	}

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Startup window = new Startup();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * 
	 * @throws Exception
	 */
	public Startup() throws Exception {
		frame = new FrameMain();
	}
}
