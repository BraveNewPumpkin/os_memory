#include <boost/program_options.hpp>
#include <iostream>
#include <string>
#include "ReplacementAlgorithmFactory.h"
#include "ReplacementAlgorithm.h"


const std::string makeAlgorithmArgDescription(const ReplacementAlgorithmFactory& factory){
  std::string description("");
  for(std::pair<const std::string, const std::string> name_description: factory.getNameDescriptions()){
    description += "\n  " + name_description.first + " (" + name_description.second + ")";
  }
  return description;
}

int main(const int argc, const char* argv[]){
  std::string program_name = argv[0];
  boost::program_options::options_description desc("usage: " + program_name + " [--help] [--algorithm=<name>] [--input=</path/to/input/file.txt>] ");
  ReplacementAlgorithmFactory algorithm_factory;
  desc.add_options()
     ("help,h,H", "produce help message")
     ("algorithm", boost::program_options::value<std::string>(), std::string("set replacement algorithm. valid names are:" + makeAlgorithmArgDescription(algorithm_factory)).c_str())
     ("input", boost::program_options::value<std::string>(), "absolute path to input file")
     ;
  boost::program_options::variables_map program_options_variables;
  boost::program_options::store(boost::program_options::parse_command_line(argc, argv, desc), program_options_variables);
  boost::program_options::notify(program_options_variables);

  if (program_options_variables.count("help")) {
    std::cout << desc << std::endl;
    return EXIT_SUCCESS;
  }
  ReplacementAlgorithm replacement_algorithm;
  try {
    if (!program_options_variables.count("algorithm") || !program_options_variables.count("algorithm")) {
      throw std::runtime_error("must define algorithm and input arguments");
    }
    std::string replacement_algorithm_name = program_options_variables["algorithm"].as<std::string>();
    replacement_algorithm = algorithm_factory.getAlgorithmObject(replacement_algorithm_name);
  }catch(const std::exception& e){
    std::cerr << e.what() << std::endl << desc << std::endl;
    return EXIT_FAILURE;
  }
/*
  std::string input_filepath = argv[2];
  //check if asking for help
  boost::regex help_arg("^-{1,2}[Hh](?:elp)?$");
  if (std::regex_match(tie_breaking_strategy, help_arg) || std::regex_match(input_filepath, help_arg)) {
    printUsage();
    return EXIT_SUCCESS;
  }
  //attempt to open input file
  std::ifstream input_stream(input_filepath);
  if (!input_stream.is_open()) {
    std::string message = "failed to open input file: \"" + input_filepath + "\"";
    throw std::runtime_error(message);
  }
  //parse file
  //create data structures
  */
  return 0;
}
