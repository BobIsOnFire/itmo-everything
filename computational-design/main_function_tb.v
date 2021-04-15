`timescale 1ns / 1ps
//////////////////////////////////////////////////////////////////////////////////
// Company: 
// Engineer: 
// 
// Create Date: 03/17/2021 08:52:41 PM
// Design Name: 
// Module Name: main_function
// Project Name: 
// Target Devices: 
// Tool Versions: 
// Description: 
// 
// Dependencies: 
// 
// Revision:
// Revision 0.01 - File Created
// Additional Comments:
// 
//////////////////////////////////////////////////////////////////////////////////

module main_function_tb();
  reg clock;
  reg reset;
  reg enable;
  
  reg [7:0] a;
  reg [7:0] b;
  
  wire finish;
  wire [23:0] result;
  
  main_function main(
    .clock(clock),
    .reset(reset),
    .enable(enable),
    
    .a(a),
    .b(b),
    
    .finish(finish),
    .result(result)
  );
  
  integer counter;
    
  initial begin
    clock = 1; reset = 1;
    #1
    clock = 0; reset = 0;
    #1
    
    a = 8'd123;
    b = 8'd33;
    counter = 0;
    
    clock = 1; enable = 1;
    #1
    while (!finish) begin
      clock = 0;
      #1
      clock = 1; enable = 0;
      #1
      counter = counter + 1;
    end
    
    $display("a=%d, b=%d, result=%d, with %d ticks to calculate", a, b, result, counter);
    
    clock = 0;
    #1
        
    a = 8'd255;
    b = 8'd255;
    counter = 0;
    
    clock = 1; enable = 1;
    #1
    while (!finish) begin
      clock = 0;
      #1
      clock = 1; enable = 0;
      #1
      counter = counter + 1;
    end
    
    $display("a=%d, b=%d, result=%d, with %d ticks to calculate", a, b, result, counter);

  end
  
endmodule
