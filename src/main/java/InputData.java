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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * Created by kylebolton on 12/3/16.
 */
public class InputData {
    private class ProcessData{
        public String pid;
        public int num_page_frames;
        ProcessData(String pid, int num_page_frames){
            this.pid = pid;
            this.num_page_frames = num_page_frames;
        }
    };
    private class MemoryRequest{
        String pid;
        int segment;
        int page;
        int offset;
        MemoryRequest(String pid, int segment, int page, int offset){
            this.pid = pid;
            this.segment = segment;
            this.page = page;
            this.offset = offset;
        }
    };
    public int num_page_frames;
    public int max_segment_length;  // maximum segment length (in number of pages)
    public int page_size; // page size (in number of bytes)
    public int num_page_frames_per_process; //for FIFO, LRU, LFU and OPT,
    public int window_size; //(delta) for the Working Set algorithm
    public int min; //min size of pool
    public int max; //max size of frames pool
    public List<ProcessData> address_spaces;
    public Queue<MemoryRequest> memory_requests;

    public InputData() {
        address_spaces = new ArrayList<>();
        memory_requests = new ArrayDeque<>();
    }

    public void addAddressSpace(String pid, int num_page_frames){
        address_spaces.add(new ProcessData(pid, num_page_frames));
    }
    public void addMemoryRequest(String pid, int segment, int page, int offset){
        memory_requests.add(new MemoryRequest(pid, segment, page, offset));
    }

}
