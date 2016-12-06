/*
 * The MIT License (MIT)
 * Copyright (c) 2016 Kyle Bolton
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;

/**
 * Created by kylebolton on 12/4/16.
 */
public class MemoryManager {
    private final MainMemory main_memory;
    private final ConcurrentMap<String, List<MemoryRequest>> deactivated_processes_memory_requests;
    private final ConcurrentMap<String, Set<MemoryRequest>> deactivated_processes_address_spaces;
    private final Queue<MemoryRequest> requests_queue;
    private final ConcurrentMap<String, Semaphore> pid_mutexs;
    private final Semaphore activateDeactivate_mutex;

    public MemoryManager(
            int max_pages,
            int min_pages,
            int max_pages_per_segment,
            List<ProcessData> process_data_list,
            Queue<MemoryRequest> requests_queue){
        main_memory = new MainMemory(max_pages, min_pages, max_pages_per_segment, process_data_list);
        this.requests_queue = requests_queue;
        deactivated_processes_memory_requests = new ConcurrentHashMap<>();
        deactivated_processes_address_spaces = new ConcurrentHashMap<>();
        pid_mutexs = new ConcurrentHashMap<>(process_data_list.size());
        for(ProcessData process_data: process_data_list){
            pid_mutexs.put(process_data.getPid(), new Semaphore(1));
        }
        activateDeactivate_mutex = new Semaphore(1);
    }

    public void deactivateProcess(String pid) throws InterruptedException{
        activateDeactivate_mutex.acquire();
        pid_mutexs.get(pid).acquire();
        deactivated_processes_memory_requests.put(pid, Collections.synchronizedList(new ArrayList<MemoryRequest>()));
        deactivated_processes_address_spaces.put(pid, main_memory.removeAddressSpace(pid));
        activateDeactivate_mutex.release();
    }

    public void activateProcess(String pid) throws InterruptedException{
        activateDeactivate_mutex.acquire();
        requests_queue.addAll(deactivated_processes_memory_requests.remove(pid));
        main_memory.addAddressSpace(pid, deactivated_processes_address_spaces.remove(pid));
        pid_mutexs.get(pid).release();
        activateDeactivate_mutex.release();
    }

    public void activateProcess(int max_size) throws InterruptedException{
        activateDeactivate_mutex.acquire();
        for(Map.Entry<String, Set<MemoryRequest>> entry: deactivated_processes_address_spaces.entrySet()){
            String pid = entry.getKey();
            Set<MemoryRequest> address_space = entry.getValue();
            if(address_space.size() <= max_size){
                main_memory.addAddressSpace(pid, address_space);
                deactivated_processes_address_spaces.remove(pid);
                List<MemoryRequest> memory_requests_to_restore_to_work_queue = deactivated_processes_memory_requests.remove(pid);
                requests_queue.addAll(memory_requests_to_restore_to_work_queue);
                pid_mutexs.get(pid).release();
                break;
            }
        }
        activateDeactivate_mutex.release();
    }

    public boolean shouldProcess(MemoryRequest unprocessed_request){
        boolean should_process_request;
        if(deactivated_processes_address_spaces.containsKey(unprocessed_request.pid)){
            deactivated_processes_memory_requests.get(unprocessed_request.pid).add(unprocessed_request);
            should_process_request = false;
        }else{
            should_process_request = true;
        }
        return should_process_request;
    }

    public MainMemory getMainMemory() {
        return main_memory;
    }

    public class MainMemory {
        private int max_pages;
        private int min_pages;
        private int used_pages;
        private final Set<MemoryRequest> frames;
        private final ConcurrentMap<String, Set<MemoryRequest>> address_spaces;
        private final Semaphore put_mutex;
        private final Semaphore removeAddressSpace_mutex;

        public MainMemory(
                int max_pages,
                int min_pages,
                int max_pages_per_segment,
                List<ProcessData> process_data_list){
            this.max_pages = max_pages;
            this.min_pages = min_pages;
            used_pages = 0;
            frames = new HashSet<>();
            address_spaces = new ConcurrentHashMap<>();
            for(ProcessData process_data: process_data_list){
                address_spaces.put(process_data.getPid(), new HashSet<MemoryRequest>(process_data.getNumPageFrames()));
            }
            put_mutex = new Semaphore(1);
            removeAddressSpace_mutex = new Semaphore(1);
        }

        public void put(MemoryRequest memory_request) throws InterruptedException{
            //if we will be over the desired max pages
            if(used_pages + 1 > max_pages){
                //find suitable address space to deactivate
                for(Map.Entry<String, Set<MemoryRequest>> entry: address_spaces.entrySet()){
                    if(used_pages - entry.getValue().size() > min_pages){
                        deactivateProcess(entry.getKey());
                        break;
                    }
                }
            }
            Set<MemoryRequest> deactivated_address_space = deactivated_processes_address_spaces.get(memory_request.pid);
            if(deactivated_address_space != null){
                deactivated_address_space.add(memory_request);
            }else{
                frames.add(memory_request);
                address_spaces.get(memory_request.pid).add(memory_request);
            }
        }

        public MemoryResponse get(MemoryRequest memory_request) {
            MemoryResponse memory_response;
            if(frames.contains(memory_request)) {
                memory_response = new MemoryResponseBuilder().success(memory_request.pid, memory_request.address).build();
            }else{
                memory_response = new MemoryResponseBuilder().failure(memory_request.pid).build();
            }
            return memory_response;
        }

        /**
         * deletes all MemoryRequests corresponding to given pid. DOES NOT CHECK min page frames as this is used to mitigate
         *  used frames > max frames
         * @param pid
         * @return set of removed MemoryRequests
         */
        public Set<MemoryRequest> removeAddressSpace(String pid) throws InterruptedException{
            Set<MemoryRequest> address_space = address_spaces.remove(pid);
            frames.removeAll(address_space);
            used_pages -= address_space.size();
            return address_space;
        }

        /**
         * used to add a list of MemoryRequests associated with given pid. DOES NOT CHECK max page frames as this is used to
         *  mitigate used frames < min frames
         * @param pid pid of address space
         * @param address_space set of MemoryRequest objects
         */
        public void addAddressSpace(String pid, Set<MemoryRequest> address_space) throws InterruptedException{
            address_spaces.put(pid, address_space);
            frames.addAll(address_space);
            used_pages += address_space.size();
        }

        /**
         * @param memory_to_delete_list
         * @return list of successfully deleted MemoryRequests
         */
        public List<MemoryRequest> removeAll(List<MemoryRequest> memory_to_delete_list) throws InterruptedException{
            List<MemoryRequest> deleted = new ArrayList<>();
            for(MemoryRequest memory_to_delete: memory_to_delete_list){
                if(frames.remove(memory_to_delete)){
                    //only attempt to remove if we had it in memory
                    address_spaces.get(memory_to_delete.pid).remove(memory_to_delete);
                    used_pages--;
                    deleted.add(memory_to_delete);
                }
            }
            if(used_pages < min_pages){
                activateProcess(max_pages - used_pages);
            }
            return deleted;
        }
    }
}
