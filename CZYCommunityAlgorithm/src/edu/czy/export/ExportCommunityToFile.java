package edu.czy.export;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

public class ExportCommunityToFile {

	public static void exportCommunityWithCidPerline(Collection<Collection<Integer>> coms,String filename,String delim) {
		if(null == coms||null == filename || "".equals(filename)) {
			return ;
		}
		if(null == delim) {
			delim ="\t";
		}
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
			for(Collection<Integer> com :coms){
				for(Integer nodeid:com){
					bw.write(nodeid);
					bw.write(delim);
				}
				bw.newLine();
			}
			bw.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
