//
// Created by Kyle Bolton on 11/26/16.
//

#ifndef OS_MEMORY_INPUTDATA_H
#define OS_MEMORY_INPUTDATA_H


#include <string>
#include <vector>
#include <queue>

struct InputData {
  struct ProcessData{
    std::string pid;
    unsigned int num_page_frames;
    ProcessData(const std::string& pid, const unsigned int& num_page_frames):pid(pid), num_page_frames(num_page_frames){}
  };
  struct MemoryRequest{
    std::string pid;
    unsigned int segment;
    unsigned int page;
    unsigned int offset;
    MemoryRequest(const std::string& pid,
                const unsigned int& segment,
                const unsigned int& page,
                const unsigned int& offset
                ):pid(pid), segment(segment), page(page), offset(offset){}
  };
  unsigned int num_page_frames;
  unsigned int max_segment_length;  // maximum segment length (in number of pages)
  unsigned int page_size; // page size (in number of bytes)
  unsigned int num_page_frames_per_process; //for FIFO, LRU, LFU and OPT,
  unsigned int window_size; //(delta) for the Working Set algorithm
  unsigned int min; //min size of pool
  unsigned int max; //max size of frames pool
  std::vector<ProcessData> address_spaces;
  std::queue<MemoryRequest> memory_requests;
};


#endif //OS_MEMORY_INPUTDATA_H
