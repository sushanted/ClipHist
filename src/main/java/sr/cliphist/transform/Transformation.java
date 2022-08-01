package sr.cliphist.transform;

import java.util.Base64;
import java.util.function.UnaryOperator;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.common.base.CaseFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum Transformation {
	
	UpperCase ("\u2B06",String::toUpperCase),
	LowerCase ("\u2B07",String::toLowerCase),
	UpperCamelCase ("^_",str -> CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, str)), 
	LowerCamelCase ("_^",str -> CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, str)), 
	SnakeCase ("~",str -> CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, str)), 
	
	Base64Encode ("B64E",str -> Base64.getEncoder().encodeToString(str.getBytes())),
	Base64Decode ("B64D",str -> new String(Base64.getDecoder().decode(str))),
	
	PrettyJson ("PJson",new JsonTransformer()::prettyJson),
	Sha256 ("Sha256", (DigestUtils::sha256Hex)),
	Sha256_10 ("Sha256/10", str -> DigestUtils.sha256Hex(str).substring(0,10)),
	
	;
	
	private String caption;
	private UnaryOperator<String> transformation;
	
	public String apply(String string){
		return this.transformation.apply(string);
	}


}
