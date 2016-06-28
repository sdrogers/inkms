import java.awt.Color;

import pyhton.LoadMZXML;

public class Main {

	public static void main(String[] args) throws Exception {
		new Main().run();
	}

	public void run() throws Exception {

		LoadMZXML.Param param = new LoadMZXML.Param();
		param.filepath = "E:\\Enironments\\data\\abcdefgh_1.mzXML";
		param.lines = 8;
		param.widthInMM = 62;
		param.heightInMM = 10;
		param.downMotionInMM = 1.25f;

		LoadMZXML loadMZML = new LoadMZXML(param, LoadMZXML.Type.NORMAL);
		loadMZML.getReduceSpec(374, 376);
	}

}
