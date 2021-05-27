`timescale 1ns / 1ps
//////////////////////////////////////////////////////////////////////////////////
// Company: 
// Engineer: 
// 
// Create Date: 01.04.2021 18:36:37
// Design Name: 
// Module Name: mult_tb
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


module mult1_tb();
  reg clock;
  reg reset;
  reg enable;
  
  reg [15:0] a;
  reg [7:0] b;
  
  wire finish;
  wire [23:0] result;
  
  mult mult1(
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
    
    a = 16'd123;
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
    
    $display("Result: %d * %d = %d, with %d ticks to calculate", a, b, result, counter);
        
    a = 16'd321;
    b = 8'd44;
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
    
    $display("Result: %d * %d = %d, with %d ticks to calculate", a, b, result, counter);

  end
  
endmodule
