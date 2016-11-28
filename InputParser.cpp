//
// Created by Kyle Bolton on 11/26/16.
//

#include "InputParser.h"

std::unique_ptr<InputData> InputParser::parse(std::istream& input, const ReplacementStrategy& replacement_strategy) {
  std::unique_ptr<InputData> input_data(new InputData());
  unsigned int num_processes;
  input >> input_data->num_page_frames;
  input >> input_data->max_segment_length;
  input >> input_data->page_size;
  if(replacement_strategy.getName() != "WS") {
    input >> input_data->num_page_frames_per_process;
    if(replacement_strategy.getName() == "OPT") {
      input >> input_data->window_size;
    }else{
      //input.ignore(std::numeric_limits<unsigned int>::max(), '\n');
      std::string trash;
      input >> trash;
    }
  }else{
    input >> input_data->window_size;
    //input.ignore(std::numeric_limits<unsigned int>::max(), input.widen('\n')); //doesn't work for some reason
    std::string trash;
    input >> trash;
  }
  input >> input_data->min;
  input >> input_data->max;
  input >> num_processes;
  std::string pid;
  for (int process_num = 0; process_num < num_processes; ++process_num) {
    unsigned int num_page_frames;
    input >> pid >> num_page_frames;
    input_data->address_spaces.emplace_back(pid, num_page_frames);
  }
  std::string address;
  while(input >> pid >> address){
    unsigned int temp = std::stoul(address, nullptr, 16);
    if(temp != -1) {
      unsigned int segment = temp & 0xF; //lower 4 bits
      unsigned int page = (temp >> 4) & 0xF; //upper 4 bits
      unsigned int offset; //TODO delete if not used
      input_data->memory_requests.emplace(pid, segment, page, offset);
    }
  }

  return input_data;
}
