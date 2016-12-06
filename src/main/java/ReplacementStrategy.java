import java.util.List;
import java.util.Set;

/**
 * Created by kylebolton on 12/3/16.
 */
public abstract class ReplacementStrategy implements Runnable{
    protected final String name;
    protected final InputData input_data;
    protected final MemoryManager memory_manager;
    protected final MemoryManager.MainMemory main_memory;
    protected MemoryRequest most_recent_request;

    protected abstract MemoryRequest getMostRecentRequest();

    public ReplacementStrategy(String name, InputData input_data, MemoryManager memory_manager) {
        this.name = name;
        this.input_data = input_data;
        this.memory_manager = memory_manager;
        this.main_memory = memory_manager.getMainMemory();
    }

    public String getName() {
        return name;
    }

    @Override
    public void run() {
        handlePageFault(getMostRecentRequest());
    }

    /**
     * @param memory_request next memory request that will be tried against main memory
     * @return list of addresses to removeAll from main_memory. empty if none.
     */
    public abstract List<MemoryRequest> update(MemoryRequest memory_request);

    public abstract void requestsRemoved(Set<MemoryRequest> removed_requests);

    protected void handlePageFault(MemoryRequest memory_request){
        Disk disk = new Disk(memory_request);

        try {
            if (disk.call().wasSuccessful()) {
                //put memory_request into main_memory
                    main_memory.put(memory_request);
                    memory_manager.activateProcess(memory_request.pid);
            }else{
                //if we weren't faking disk access, this would probably notify the OS of disk error.
            }
        }catch (InterruptedException e){
            //again, would normally do something here, but since this is all for pretend we do nothing.
        }
    }

}
