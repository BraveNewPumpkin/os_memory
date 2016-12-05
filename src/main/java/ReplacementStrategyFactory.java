import java.util.Objects;
import java.util.concurrent.ExecutorService;

/**
 * Created by kylebolton on 12/3/16.
 */
public class ReplacementStrategyFactory {
//    ReplacementStrategyFactory():name_description_map{
//        {"fifo", "first-in-first-out"},
//        {"lru", "least recently used"},
//        {"lfu", "least frequently used"},
//        {"opt", "optimum MUST ALSO SET --lookahead"},
//        {"ws", "Working Set"}
    private InputData input_data;
    private MemoryManager memory_manager;
    private ExecutorService executor;

    public ReplacementStrategyFactory(InputData input_data, MemoryManager memory_manager, ExecutorService executor) {
        this.input_data = input_data;
        this.memory_manager = memory_manager;
        this.executor = executor;
    }

    public ReplacementStrategy getStrategyObject(String name) {
        ReplacementStrategy replacement_strategy;
        if (name.equalsIgnoreCase("fifo")) {
            replacement_strategy = new FifoReplacementStrategy(name, input_data, memory_manager, executor);
        } else if (name.equalsIgnoreCase("lru")) {
            replacement_strategy = new LruReplacementStrategy(name, input_data, memory_manager, executor);
        } else if (name.equalsIgnoreCase("lfu")) {
            replacement_strategy = new LfuReplacementStrategy(name, input_data, memory_manager, executor);
        } else if (name.equalsIgnoreCase("opt")) {
            replacement_strategy = new OptReplacementStrategy(name, input_data, memory_manager, executor);
        } else if (name.equalsIgnoreCase("ws")) {
            replacement_strategy = new WsReplacementStrategy(name, input_data, memory_manager, executor);
        } else {
            throw new IllegalArgumentException("invalid replacement algorithm name: \"" + name + '"');
        }
        return replacement_strategy;
    }
}
