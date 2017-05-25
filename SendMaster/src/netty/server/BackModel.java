package netty.server;

import com.circle.core.elastic.Json;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Created by fomky on 15-7-13.
 */
public class BackModel {
	private Map<String, Object> map;

	public BackModel() {
		this.map = new HashMap<>();
	}

	public BackModel(int n) {
		this.map = new HashMap<>(n);
	}

	public Map<String, Object> getMap() {
		return map;
	}

	public void add(String key, Object value) {
		map.put(key, value);
	}



	public String buildJson() {
		return Json.json(map);
	}
}
