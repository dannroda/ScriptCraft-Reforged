import java.io.*;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.graalvm.polyglot.*;

public class jscript {
    public static void main(String[] args) throws Exception {

        String filePath = args[0];

        File errFile = new File(args[args.length - 1]);
        System.setErr(new PrintStream(errFile));

        try (Context context = Context.newBuilder("js")
                .allowHostAccess(HostAccess.ALL)
                .allowHostClassLookup(className -> true)
                .allowAllAccess(true)
                .allowNativeAccess(true)
                .allowExperimentalOptions(true)
                .option("js.nashorn-compat", "true")
                .build()) {
            Value bindings = context.getBindings("js");
            bindings.putMember("engine", context);
            bindings.putMember("args", args);
            bindings.putMember("lib", new File(args[2]));
            Value script = context.eval("js", FileUtils.readFileToString(new File(filePath), StandardCharsets.UTF_8));
            // Debug generation output to file
            try (FileWriter fileWriter = new FileWriter("target/output_debug.txt", true);
                 BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
                bufferedWriter.write("------");
                bufferedWriter.newLine();

                for (String str : args) {
                    bufferedWriter.write(str);
                    bufferedWriter.newLine();

                }
            }

        }


    }
}
