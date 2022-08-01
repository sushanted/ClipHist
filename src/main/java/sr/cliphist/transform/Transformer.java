package sr.cliphist.transform;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;

public class Transformer {
	
	public void transform(Transformation transformation) {
		
		final Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
		
		try {
			String data = String.valueOf(sysClip.getData(DataFlavor.stringFlavor));
			final StringSelection clipboardTarget = new StringSelection(transformation.apply(data));
			sysClip.setContents(clipboardTarget, clipboardTarget);
			
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

}
