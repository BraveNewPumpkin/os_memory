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
import java.util.concurrent.ExecutorService;

/**
 * Created by kylebolton on 12/4/16.
 */
public class OptReplacementStrategy extends ReplacementStrategy{
    private final Map<String, List<MemoryRequest>> requests;
    private final int max_page_frames_per_process;
    private MemoryRequest most_recent_request;

    @Override
    protected MemoryRequest getMostRecentRequest() {
        return most_recent_request;
    }

    public OptReplacementStrategy(String name, InputData input_data, MemoryManager memory_manager, ExecutorService executor) {
        super(name, input_data, memory_manager, executor);
        requests = new HashMap<>(input_data.address_spaces.size());
        for(ProcessData process_data: input_data.address_spaces){
            requests.put(process_data.getPid(), new ArrayList<MemoryRequest>(input_data.num_page_frames_per_process));
        }
        max_page_frames_per_process = input_data.num_page_frames_per_process;
    }

    @Override
    public List<MemoryRequest> update(MemoryRequest memory_request) {
        List<MemoryRequest> requests_to_delete = new ArrayList<>();
        List<MemoryRequest> requests_for_pid = requests.get(memory_request.pid);
        if(!requests_for_pid.contains(memory_request)) {
            requests_for_pid.add(memory_request);
        }
        if(requests_for_pid.size() > max_page_frames_per_process) {
            MemoryRequest furthest_used = null;
            int greatest_distance_into_future = 0;
            for(MemoryRequest request: requests_for_pid){
                Iterator<MemoryRequest> request_iterator = input_data.memory_requests.iterator();
                for (int i = 0; i < input_data.window_size; i++) {
                    if(!request_iterator.hasNext()){
                        break;
                    }
                    if(request_iterator.next().equals(request) && i > greatest_distance_into_future){
                        greatest_distance_into_future = i;
                        furthest_used = request;
                    }
                }
            }
            if(furthest_used != null){
                requests_for_pid.remove(furthest_used);
                requests_to_delete.add(furthest_used);
            } else {
                requests_to_delete.add(requests_for_pid.remove(0));
            }
        }
        most_recent_request = memory_request;

        return requests_to_delete;
    }

    @Override
    public void requestsRemoved(Set<MemoryRequest> removed_requests) {
        for(MemoryRequest request: removed_requests){
            requests.get(request.pid).remove(request);
        }
    }
}
