import java.io.BufferedReader;

import static java.lang.Integer.decode;
import static java.lang.Integer.parseInt;

/**
 * Created by kylebolton on 12/3/16.
 */
public class InputParser {
    public InputData parse(BufferedReader input, ReplacementStrategy replacement_strategy) throws java.io.IOException{
        InputData input_data = new InputData();
        int num_processes;
        input_data.num_page_frames = parseInt(input.readLine());
        input_data.max_segment_length = parseInt(input.readLine());
        input_data.page_size = parseInt(input.readLine());
        if(replacement_strategy.getName().equalsIgnoreCase("ws")) {
            input_data.num_page_frames_per_process = parseInt(input.readLine());
            if(replacement_strategy.getName().equalsIgnoreCase("OPT")) {
                input_data.window_size = parseInt(input.readLine());
            }else{
                input.readLine(); //throw away line
            }
        }else{
            input_data.window_size = parseInt(input.readLine());
            input.readLine(); //throw away line
        }
        input_data.min = parseInt(input.readLine());
        input_data.max = parseInt(input.readLine());
        num_processes = parseInt(input.readLine());
        for (int process_num = 0; process_num < num_processes; ++process_num) {
            String[] tokens = input.readLine().split("\\s");
            String pid = tokens[0];
            int num_page_frames = parseInt(tokens[1]);
            input_data.addAddressSpace(pid, num_page_frames);
        }
        String line;
        while((line = input.readLine()) != null){
            String[] tokens = line.split("\\s");
            String pid = tokens[0];
            int address = decode(tokens[1]);
            int segment = 0;
            int page = 0;
            int offset = 0;
            if(address != -1) {
                segment = address & 0x3; //lower 2 bits
                page = (address >> 4) & 0xF; //middle 4 bits
                offset = (address >> 2) & 0x3; //highest 2 bits
            }
            input_data.addMemoryRequest(pid, tokens[1], segment, page, offset);
        }
        return input_data;
    }
}
