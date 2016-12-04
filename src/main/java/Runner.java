#include <boost/program_options.hpp>
#include <iostream>
#include <string>
#include <fstream>
#include "ReplacementStrategyFactory.h"
#include "ReplacementStrategy.h"
#include "InputParser.h"


const std::string makeAlgorithmArgDescription(const ReplacementStrategyFactory& factory){
  std::string description("");
  for(std::pair<const std::string, const std::string> name_description: factory.getNameDescriptions()){
    description += "\n  " + name_description.first + " (" + name_description.second + ")";
  }
  return description;
}

int main(const int argc, const char* argv[]){
  try {
    std::string program_name = argv[0];
    boost::program_options::options_description desc(
       "usage: " + program_name + " [--help] [--algorithm=<name>] [--input=</path/to/input/file.txt>] ");
    ReplacementStrategyFactory replacement_strategy_factory;
    desc.add_options()
       ("help,h,H", "produce help message")
       ("algorithm", boost::program_options::value<std::string>(), std::string(
          "set replacement algorithm. valid names are:" + makeAlgorithmArgDescription(replacement_strategy_factory)).c_str())
       ("input", boost::program_options::value<std::string>(), "absolute path to input file");
    boost::program_options::variables_map program_options_variables;
    boost::program_options::store(boost::program_options::parse_command_line(argc, argv, desc),
                                  program_options_variables);
    boost::program_options::notify(program_options_variables);

    if (program_options_variables.count("help")) {
      std::cout << desc << std::endl;
      return EXIT_SUCCESS;
    }

    //check required arguments and set replacement algorithm
    std::unique_ptr<ReplacementStrategy> replacement_strategy;
    try {
      if (!program_options_variables.count("algorithm") || !program_options_variables.count("input")) {
        throw std::runtime_error("must define algorithm and input arguments");
      }
      std::string replacement_strategy_name = program_options_variables["algorithm"].as<std::string>();
      replacement_strategy = replacement_strategy_factory.getAlgorithmObject(replacement_strategy_name);
    } catch (const std::exception &e) {
      std::cerr << e.what() << std::endl << desc << std::endl;
      return EXIT_FAILURE;
    }

    //open input file
    std::string input_filepath;
    std::ifstream input_stream;
    try {
      input_filepath = program_options_variables["input"].as<std::string>();
      //attempt to open input file
      input_stream.open(input_filepath);
      if (!input_stream.is_open()) {
        throw std::runtime_error(std::string(strerror(errno)));
      }
    } catch (const std::exception &e) {
      std::cerr << "failed to open \"" + input_filepath + "\": " + e.what() << std::endl;
      return EXIT_FAILURE;
    }

    //parse file
    InputParser input_parser;
    std::unique_ptr<InputData> input_data = input_parser.parse(input_stream, *replacement_strategy); //TODO make sure can pass unique_ptr


    //create data structures
  }catch (const std::exception& e){
    std::cerr << "unhandled std::exception caught in main: " << e.what() << std::endl;
    return EXIT_FAILURE;
  }catch (...){
    std::cerr << "unhandled exception of unknown type caught in main: " << std::endl;
    return EXIT_FAILURE;
  }
  return EXIT_SUCCESS;
}
/*
 * create free frames pool
 * if coming close to min free frames then put a process (random pick) on hold and move it's frames out of memory
 *
 */