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
import java.util.concurrent.Semaphore;

/**
 * Created by kylebolton on 12/4/16.
 */
public class MemoryManager {
    private MainMemory main_memory;
    private Map<String, List<MemoryRequest>> deactivated_processes_memory_requests;
    private Map<String, Set<MemoryRequest>> deactivated_processes_address_spaces;

    public MemoryManager(
            int max_pages,
            int min_pages,
            int max_pages_per_segment,
            List<ProcessData> process_data_list){
        main_memory = new MainMemory(max_pages, min_pages, max_pages_per_segment, process_data_list);
        deactivated_processes_memory_requests = new HashMap<>();
        deactivated_processes_address_spaces = new HashMap<>();
    }

    public void deactivateProcess(String pid){
        deactivated_processes_memory_requests.put(pid, new ArrayList<MemoryRequest>());
        deactivated_processes_address_spaces.put(pid, main_memory.removeAddressSpace(pid));
    }

    public void activateProcess(String pid){
        //TODO handle deactivated_processes_memory_requests
        main_memory.addAddressSpace(pid, deactivated_processes_address_spaces.get(pid));
    }

    public void activateProcess(int max_size){
        for(Map.Entry<String, Set<MemoryRequest>> entry: deactivated_processes_address_spaces.entrySet()){
            String pid = entry.getKey();
            Set<MemoryRequest> address_space = entry.getValue();
            if(address_space.size() <= max_size){
                main_memory.addAddressSpace(pid, address_space);
                deactivated_processes_address_spaces.remove(pid);
                //TODO handle deactivated_processes_memory_requests
                break;
            }
        }
    }

    public MainMemory getMainMemory() {
        return main_memory;
    }

    public class MainMemory {
        private int max_pages;
        private int min_pages;
        private int used_pages;
        private final Semaphore putProtection;
        private Set<MemoryRequest> frames;
        private Map<String, Set<MemoryRequest>> address_spaces;
        public MainMemory(
                int max_pages,
                int min_pages,
                int max_pages_per_segment,
                List<ProcessData> process_data_list){
            this.max_pages = max_pages;
            this.min_pages = min_pages;
            used_pages = 0;
            frames = new HashSet<>();
            address_spaces = new HashMap<>();
            for(ProcessData proces_data: process_data_list){
                address_spaces.put(proces_data.getPid(), new HashSet<MemoryRequest>(proces_data.getNumPageFrames()));
            }
            putProtection = new Semaphore(1);
        }

        public void put(MemoryRequest memory_request) throws InterruptedException{
            putProtection.acquire();
            //if we will be over the desired max pages and we can do something about it (memory_manager has been set)
            if(used_pages + 1 > max_pages){
                //find suitable address space to deactivate
                for(Map.Entry<String, Set<MemoryRequest>> entry: address_spaces.entrySet()){
                    if(used_pages - entry.getValue().size() > min_pages){
                        deactivateProcess(entry.getKey());
                        break;
                    }
                }
            }
            frames.add(memory_request);
            address_spaces.get(memory_request.pid).add(memory_request);
            putProtection.release();
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
        public Set<MemoryRequest> removeAddressSpace(String pid){
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
        public void addAddressSpace(String pid, Set<MemoryRequest> address_space){
            address_spaces.put(pid, address_space);
            frames.addAll(address_space);
            used_pages += address_space.size();
        }

        /**
         * @param memory_to_delete_list
         * @return list of successfully deleted MemoryRequests
         */
        public List<MemoryRequest> removeAll(List<MemoryRequest> memory_to_delete_list){
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
