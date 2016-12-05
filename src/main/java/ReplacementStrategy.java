import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Created by kylebolton on 12/3/16.
 */
public abstract class ReplacementStrategy implements Runnable{
    protected String name;
    protected InputData input_data;
    protected MemoryManager.MainMemory main_memory;
    protected ExecutorService executor;

    protected abstract MemoryRequest getMostRecentRequest();

    public ReplacementStrategy(String name, InputData input_data, MemoryManager.MainMemory main_memory, ExecutorService executor) {
        this.name = name;
        this.input_data = input_data;
        this.main_memory = main_memory;
        this.executor = executor;
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
        Future<MemoryResponse> future = executor.submit(disk);
        try {
            //this will wait until thread returns value
            if (future.get().wasSuccessful()) {
                //put memory_request into main_memory
                try {
                    main_memory.put(memory_request);
                }catch (InterruptedException e){
                    //unrecoverable failure... TODO
                }
            }else{
                //if we weren't faking disk access, this would probably notify the OS of disk error.
            }
        }catch (InterruptedException|ExecutionException e){
            //again, would normally do something here, but since this is all for pretend we do nothing.
        }
    }

}
