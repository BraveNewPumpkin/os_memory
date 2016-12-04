import java.util.Objects;

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

    public ReplacementStrategy getStrategyObject(String name) {
        ReplacementStrategy replacement_strategy;
        if (name.equalsIgnoreCase("fifo")) {
            replacement_strategy = new ReplacementStrategy(name);
            //TODO construct and return fifo ReplacementStrategy object
        } else if (name.equalsIgnoreCase("lru")) {
            replacement_strategy = new ReplacementStrategy(name);
            //TODO construct and return lru ReplacementStrategy object
        } else if (name.equalsIgnoreCase("lfu")) {
            replacement_strategy = new ReplacementStrategy(name);
            //TODO construct and return lfu ReplacementStrategy object
        } else if (name.equalsIgnoreCase("opt")) {
            replacement_strategy = new ReplacementStrategy(name);
            //TODO construct and return opt ReplacementStrategy object
        } else if (name.equalsIgnoreCase("ws")) {
            replacement_strategy = new ReplacementStrategy(name);
            //TODO construct and return ws ReplacementStrategy object
        } else {
            throw new IllegalArgumentException("invalid replacement algorithm name: \"" + name + '"');
        }
        return replacement_strategy;
    }
}
