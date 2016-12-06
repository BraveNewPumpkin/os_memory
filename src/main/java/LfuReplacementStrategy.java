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

import com.google.common.collect.Multiset;
import com.google.common.collect.HashMultiset;

/**
 * Created by kylebolton on 12/4/16.
 */
public class LfuReplacementStrategy extends ReplacementStrategy{
    private final Map<String, Queue<MemoryRequest>> requests;
    private final Map<String, Multiset<MemoryRequest>> request_frequency;
    private final int max_page_frames_per_process;
    private MemoryRequest most_recent_request;

    private class RequestFrequencyComparator implements Comparator<MemoryRequest>{
        @Override
        public int compare(MemoryRequest o1, MemoryRequest o2) {
            int request_frequency_1 = request_frequency.get(o1.pid).count(o1);
            int request_frequency_2 = request_frequency.get(o2.pid).count(o2);
            if(request_frequency_1 == request_frequency_2){
                return 0;
            }else if(request_frequency_1 > request_frequency_2){
                return 1;
            }else{
                return -1;
            }
        }
    }

    @Override
    protected MemoryRequest getMostRecentRequest() {
        return most_recent_request;
    }

    public LfuReplacementStrategy(String name, InputData input_data, MemoryManager memory_manager, ExecutorService executor) {
        super(name, input_data, memory_manager, executor);
        max_page_frames_per_process = input_data.num_page_frames_per_process;
        requests = new HashMap<>(input_data.address_spaces.size());
        request_frequency = new HashMap<>(input_data.address_spaces.size());
        for(ProcessData process_data: input_data.address_spaces){
            requests.put(process_data.getPid(), new PriorityQueue<MemoryRequest>(max_page_frames_per_process, new RequestFrequencyComparator()));
            //have to do this on a separate line for the create static method to infer the template parameter
            Multiset<MemoryRequest> multiset = HashMultiset.create(max_page_frames_per_process);
            request_frequency.put(process_data.getPid(), multiset);
        }
    }

    @Override
    public List<MemoryRequest> update(MemoryRequest memory_request) {
        List<MemoryRequest> requests_to_delete = new ArrayList<>();
        Queue<MemoryRequest> requests_for_pid = requests.get(memory_request.pid);
        Multiset<MemoryRequest> frequencies_for_pid = request_frequency.get(memory_request.pid);

        frequencies_for_pid.add(memory_request);
        requests_for_pid.add(memory_request);
        if(requests_for_pid.size() > max_page_frames_per_process) {
            MemoryRequest removed_request = requests_for_pid.remove();
            requests_to_delete.add(removed_request);
            //limits window size. remove this line for infinite window
            frequencies_for_pid.remove(removed_request, frequencies_for_pid.count(removed_request));
        }
        most_recent_request = memory_request;

        return requests_to_delete;
    }

    @Override
    public void requestsRemoved(Set<MemoryRequest> removed_requests) {
        for(MemoryRequest request: removed_requests){
            requests.get(request.pid).remove(request);
            Multiset<MemoryRequest> frequencies_for_pid = request_frequency.get(request.pid);
            frequencies_for_pid.remove(request, frequencies_for_pid.count(request));
        }
    }
}
