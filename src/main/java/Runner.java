import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
            Map<String, Integer> page_faults = new HashMap<>();
            InputParser input_parser = new InputParser();
            InputData input_data = input_parser.parse(buffered_input_reader);
            for(ProcessData process_data: input_data.address_spaces){
                page_faults.put(process_data.getPid(), 0);
            }
            MemoryManager memory_manager = new MemoryManager(
                    input_data.max,
                    input_data.min,
                    input_data.max_segment_length,
                    input_data.address_spaces,
                    input_data.memory_requests
            );
            MemoryManager.MainMemory main_memory = memory_manager.getMainMemory();
            ReplacementStrategyFactory replacement_strategy_factory = new ReplacementStrategyFactory(input_data, memory_manager);
            ReplacementStrategy replacement_strategy = replacement_strategy_factory.getStrategyObject(replacement_algorithm_name);
            while(!input_data.memory_requests.isEmpty()) {
                MemoryRequest memory_request = input_data.memory_requests.remove();
                if(memory_manager.shouldProcess(memory_request)) {
                    if (!memory_request.address.equals("-1")) {
                        //this is for opt and ws type strategies where they may remove addresses from main memory
                        List<MemoryRequest> memory_to_delete = replacement_strategy.update(memory_request);
                        main_memory.removeAll(memory_to_delete);
                        MemoryResponse memory_response = main_memory.get(memory_request);
                        if (!memory_response.wasSuccessful()) {
                            //page fault
                            page_faults.put(memory_request.pid, page_faults.get(memory_request.pid) + 1);
                            //deactivate on page fault so that further requests from this pid will get queued until
                            // reactivated (after page fault handling is done)
                            memory_manager.deactivateProcess(memory_response.getPid());
                            //create page fault handler thread
                            //NOTE: uncomment to make async, but there's bugs in my semaphores :(
//                            executor.execute(replacement_strategy);
                            replacement_strategy.run();
                        }
                    } else {
                        Set<MemoryRequest> deleted_memory = main_memory.removeAddressSpace(memory_request.pid);
                        replacement_strategy.requestsRemoved(deleted_memory);
                    }
                }
            }

            System.out.println("page faults per pid:");
            int total_page_faults = 0;
            for(Map.Entry<String, Integer> entry: page_faults.entrySet()){
                System.out.println("pid: " + entry.getKey() + " page faults: " + entry.getValue());
                total_page_faults += entry.getValue();
            }
            System.out.println("total page faults: " + total_page_faults);
        } catch (IOException e) {
            System.err.print(e.toString());
        }catch (InterruptedException e){
            System.err.print(e.toString());
        }finally {
            shutdownAndAwaitTermination(executor);
        }
    }

    void shutdownAndAwaitTermination(ExecutorService pool) {
        pool.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(5, TimeUnit.SECONDS)) {
                pool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(5, TimeUnit.SECONDS))
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
