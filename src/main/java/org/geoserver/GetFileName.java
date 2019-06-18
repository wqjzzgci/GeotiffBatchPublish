package org.geoserver;

import java.io.File;


public class GetFileName {

	private String path;
   
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
    
	public static void getFileName(String path) {
        System.out.println(path);
		File f = new File(path);
		if (!f.isDirectory()) {
			System.out.println("it is not directory");
		}
		File fs[] = f.listFiles();
		for (int i = 0; i < fs.length; i++) {
			File fn = fs[i];
			int size = fs[1].toString().indexOf("/");
			System.out.println(size);
			if (fn.isFile()) {
				System.out.println(fn.getName());
			} else {
				System.out.println("it is not file");
			}
		}
	}

	public File[] getFileUrl(String path) {

		File f = new File(path);
		if (!f.isDirectory()) {
			System.out.println("it is not directory");
		}
		File fileUrl[] = f.listFiles();

		return fileUrl;
	}

	/*public static void main(String[] args) {
		GetFileName a = new GetFileName();
		a.setPath("F:/Landsat8/LC80551202015365LGN00");// Â·¾¶
		getFileName(a.getPath());

	}*/
}




