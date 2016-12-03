import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.beust.jcommander.*;


/**
 * Created by kylebolton on 12/2/16.
 */
public class Runner {
    @Parameter(names={"--algorithm", "-a"}, description = "algorithm to use", required = true)
    private String replacement_algorithm_name;

    @Parameter(names={"--input", "-i"}, description = "path to input file", required = true, converter = FileConverter.class)
    private File input_file;

    @Parameter(names = "--help", help = true, description = "display this help message")
    private boolean help = false;

    public class FileConverter implements IStringConverter<File> {
        @Override
        public File convert(String value) {
            return new File(value);
        }
    }

    public static void main(String args[]) {
        Runner runner = new Runner();
        JCommander jc = null;
        try {
            jc = new JCommander(runner, args);
            jc.setProgramName("os_memory");
            if(runner.help){
                jc.usage();
            }else {
                runner.run();
            }
        }catch (ParameterException e){
            System.err.println(e.toString());
            if(jc != null) {
                jc.usage();
            }
        }
    }

    public void run() {
        try (
                FileReader input_reader = new FileReader(input_file);
                BufferedReader buffered_input_reader = new BufferedReader(input_reader);
        ) {
            ReplacementStrategyFactory replacement_strategy_factory = new ReplacementStrategyFactory();
            ReplacementStrategy replacement_strategy = replacement_strategy_factory.getStrategyObject(replacement_algorithm_name);
            InputParser input_parser = new InputParser();
            InputData input_data = input_parser.parse(buffered_input_reader, replacement_strategy);
            System.out.println("I've been run!");
        } catch (IOException e) {
            System.err.print(e.toString());
//        } catch (UndefinedVariableException|InvalidDataException e){
//            System.err.print(e.toString());
        }
    }
}
