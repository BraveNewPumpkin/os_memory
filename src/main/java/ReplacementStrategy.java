import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by kylebolton on 12/3/16.
 */
public class ReplacementStrategy implements Runnable{
    private String name;

    private MemoryRequest getMostRecentRequest(){
        //TODO implement in each child. some will likely get head of list
    }

    public ReplacementStrategy(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<MemoryRequest> update(MemoryRequest memory_request) {
        //TODO return list of addresses to delete from main_memory. empty if none
    }


    public void handlePageFault(MemoryRequest memory_request){
        Disk disk = new Disk(memory_request);
        Future<MemoryResponse> future = executor.submit(disk);
        //this will wait until thread returns value
        future.get()
        //TODO implement. need ability to add to main memory (at least). get as arg?
    }

    @Override
    public void run() {
        handlePageFault(getMostRecentRequest());
    }
}
