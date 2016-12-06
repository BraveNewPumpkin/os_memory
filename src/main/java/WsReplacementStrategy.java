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

/**
 * Created by kylebolton on 12/4/16.
 */
public class WsReplacementStrategy extends ReplacementStrategy{
    private final Map<String, List<MemoryRequest>> requests;
    private final Map<String, List<Integer>> ages;

    private final int max_age;


    @Override
    protected MemoryRequest getMostRecentRequest() {
        return most_recent_request;
    }

    public WsReplacementStrategy(String name, InputData input_data, MemoryManager memory_manager) {
        super(name, input_data, memory_manager);
        requests = new HashMap<>(input_data.address_spaces.size());
        ages = new HashMap<>(input_data.address_spaces.size());
        for(ProcessData process_data: input_data.address_spaces){
            requests.put(process_data.getPid(), new ArrayList<MemoryRequest>(input_data.num_page_frames_per_process));
            ages.put(process_data.getPid(), new ArrayList<Integer>(input_data.num_page_frames_per_process));
        }
        max_age = input_data.num_page_frames_per_process;
    }

    @Override
    public List<MemoryRequest> update(MemoryRequest memory_request) {
        List<MemoryRequest> requests_to_delete = new ArrayList<>();
        List<MemoryRequest> requests_for_pid = requests.get(memory_request.pid);
        List<Integer> ages_for_pid = ages.get(memory_request.pid);
        if(!requests_for_pid.contains(memory_request)) {
            requests_for_pid.add(memory_request);
            ages_for_pid.add(0);
        }
        Iterator<MemoryRequest> requests_iterator = requests_for_pid.iterator();
        Iterator<Integer> ages_iterator = ages_for_pid.iterator();
        while(requests_iterator.hasNext()){
            MemoryRequest request = requests_iterator.next();
            int age = ages_iterator.next();
            if(age > max_age){
                requests_to_delete.add(request);
                requests_iterator.remove();
                ages_iterator.remove();
            }
        }
        most_recent_request = memory_request;

        return requests_to_delete;
    }

    @Override
    public void requestsRemoved(Set<MemoryRequest> removed_requests) {
        for(MemoryRequest request: removed_requests){
            int index = requests.get(request.pid).indexOf(request);
            requests.get(request.pid).remove(index);
            ages.get(request.pid).remove(index);
        }
    }
}
