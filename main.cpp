#include <iostream>

int main(std::int argc, std::char* argv[]){
  std::string program_name = argv[0];
  //define closure for printing out usage
  auto printUsage = [&program_name]() {
    std::cout << "usage: " << program_name << " /path/to/input/file.txt " << endl;
  };
  //check number of parameters
  if (argc < 3) {
    std::cerr << "must pass path to input file as 2nd argument" << std::endl;
    printUsage();
    return EXIT_FAILURE;
  }
  std::string tie_breaking_strategy = argv[1];
  std::string input_filepath = argv[2];
  //check if asking for help
  std::regex help_arg("^-{1,2}[Hh](?:elp)?$");
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
  return 0;
}