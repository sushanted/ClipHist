package sr.cliphist.transform;

import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LatexTransformer {

    public String fraction(String str){
        String[] splits = str.split("/");

        if (splits.length==2){
            return String.format("\\frac{%s}{%s}",splits[0],splits[1]);
        }

        return str;
    }

    public String matrix(String str){
        String[] splits = str.split(",");

        if(splits.length==2){
            return String.format("\\begin{bmatrix} %s \\\\ %s \\end{bmatrix}",fraction(splits[0]),fraction(splits[1]));
        }

        if(splits.length==4){
            return String.format("\\begin{bmatrix} %s \\ %s \\\\ %s \\ %s \\end{bmatrix}",fraction(splits[0]),fraction(splits[1])
                    ,fraction(splits[2]),fraction(splits[3]));
        }

        return str;

    }

    public String matrixExpr(String str){
        return "$ "+Pattern.compile("\\s+").splitAsStream(str)
                .map(this::matrix)
                .collect(Collectors.joining(" "))+" $";

    }
}
