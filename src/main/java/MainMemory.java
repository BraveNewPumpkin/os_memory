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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainMemory {
    private int max_pages;
    private int min_pages;
    private int used_pages;
    private Map<String, SegmentTable> segment_tables;
    public MainMemory(
            ExecutorService executor,
            int max_pages,
            int min_pages,
            int max_pages_per_segment,
            List<ProcessData> process_data_list,
            ReplacementStrategy replacement_strategy){
        this.max_pages = max_pages;
        this.min_pages = min_pages;
        used_pages = 0;
        segment_tables = new HashMap<>();
        for(ProcessData process_data: process_data_list){
            SegmentTable segment_table = new SegmentTable(max_pages_per_segment);
            segment_tables.put(process_data.getPid(), segment_table);
        }
    }

    public MemoryResponse getData(MemoryRequest memory_request) throws MemoryNotFoundException {
        MemoryResponse memory_response;
        //TODO implement
        memory_response = new MemoryResponseBuilder().success(memory_request.pid, memory_request.address).build();
        return memory_response;
    }

    public void purgePid(String pid){
        //TODO clear all used memory for given pid
    }
}
