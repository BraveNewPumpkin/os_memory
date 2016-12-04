import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

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
        ExecutorService executor = Executors.newCachedThreadPool();
        try (
                FileReader input_reader = new FileReader(input_file);
                BufferedReader buffered_input_reader = new BufferedReader(input_reader);
        ) {
            ReplacementStrategyFactory replacement_strategy_factory = new ReplacementStrategyFactory();
            ReplacementStrategy replacement_strategy = replacement_strategy_factory.getStrategyObject(replacement_algorithm_name);
            InputParser input_parser = new InputParser();
            InputData input_data = input_parser.parse(buffered_input_reader, replacement_strategy);
            MainMemory main_memory = new MainMemory(
                    input_data.max,
                    input_data.min,
                    input_data.max_segment_length,
                    input_data.address_spaces,
            );
            for(MemoryRequest memory_request: input_data.memory_requests) {
                if(!memory_request.address.equals("-1")) {
                    //this is for opt and ws type stratagies where they may remove addresses from main memory
                    List<MemoryRequest> memory_to_delete = replacement_strategy.update(memory_request);
                    main_memory.delete(memory_to_delete);
                    MemoryResponse memory_response = main_memory.getData(memory_request);
                    if(!memory_response.wasSuccessful()) {
                        //TODO somehow put the pid we're replacing an address for on hold. maybe make it reusable so
                        // when main memory needs to put a pid on hold and dump it's memory because it's over max it can reuse
                        executor.execute(replacement_strategy);
                    }
                }else{
                    main_memory.purgePid(memory_request.pid);
                    //TODO remove from replacement_strategy (does it store them by pid? if not then main_memory will need to do this)
                    // if not by pid then working set window is across all pids?
                }
            }
            System.out.println("I've been run!");
        } catch (IOException e) {
            System.err.print(e.toString());
        }catch (InterruptedException|ExecutionException e){
            System.err.print(e.toString());
//        } catch (UndefinedVariableException|InvalidDataException e){
//            System.err.print(e.toString());
        }finally {
            shutdownAndAwaitTermination(executor);
        }
/*
 * create free frames pool
 * if coming close to min free frames then put a process (random pick) on hold and move it's frames out of memory
 *
 */
    }

    void shutdownAndAwaitTermination(ExecutorService pool) {
        pool.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                pool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(60, TimeUnit.SECONDS))
                    System.err.println("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }
}
