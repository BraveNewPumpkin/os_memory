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

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * Created by kylebolton on 12/4/16.
 */
public class OptReplacementStrategy extends ReplacementStrategy{

    public OptReplacementStrategy(String name, InputData input_data, MemoryManager.MainMemory main_memory, ExecutorService executor) {
        super(name, input_data, main_memory, executor);
    }

    @Override
    public List<MemoryRequest> update(MemoryRequest memory_request) {
        //TODO implement
        return null;
    }

    @Override
    public void requestsRemoved(Set<MemoryRequest> removed_requests) {
        //TODO implement

    }
}