package sr.cliphist.transform;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonTransformer {
	
	public String prettyJson(String string) {
		ObjectMapper om = new ObjectMapper();
		try {
			Object parsed = om.readValue(string, Object.class);
			return om.writerWithDefaultPrettyPrinter().writeValueAsString(parsed);
		} catch (Exception e) {
			e.printStackTrace();
			return string;
		} 
	}

}
