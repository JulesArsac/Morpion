package ai;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class ConfigFileLoader {

	/**
	 * Config File format: level:hiddenLayerSize:learningRate:numberOfhiddenLayers
	 * @param f
	 */
	public void loadConfigFile(String name) {
		try {
			File f = new File(name) ;
			this.mapConfig = new HashMap<String, Config>();
			if(f.exists() && f.isFile()) {
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
				String s = "";
				while( (s = br.readLine()) != null ) {
					String[] t = s.split(":");
					if ( t.length == 4 ) {
						String level = t[0];
						int hiddenLayerSize = Integer.parseInt(t[1]);
						double learningRate = Double.parseDouble(t[2]);
						int numberOfhiddenLayers = Integer.parseInt(t[3]);
						//
						Config c = new Config(level, hiddenLayerSize, numberOfhiddenLayers, learningRate);
						mapConfig.put(level, c);
					}
				}
				br.close();
			}
		}
		catch (Exception e) {
			System.out.println("Config.loadConfigFile()");
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public Config get(String level) {
		try {
			if (mapConfig != null && mapConfig.containsKey(level) )
				return mapConfig.get(level);
		} 
		catch (Exception e) {
			System.out.println("ConfigFileLoader.get()");
			e.printStackTrace();
			System.exit(-1);
		}
		return null ;
	}
	
	@Override
	public String toString() {
		return "ConfigFileLoader [mapConfig=" + mapConfig + "]";
	}

	//FIELDS ...
	private HashMap<String, Config> mapConfig ;
}
