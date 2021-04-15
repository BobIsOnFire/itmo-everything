`timescale 1ns / 1ps
//////////////////////////////////////////////////////////////////////////////////
// Company: 
// Engineer: 
// 
// Create Date: 14.04.2021 18:19:13
// Design Name: 
// Module Name: hardware
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


module hardware(
  input CLK100MHZ,
  input [15:0] SW,
  input BTNC,
  input BTNR,
  
  output [7:0] AN,
  output CA,
  output CB,
  output CC,
  output CD,
  output CE,
  output CF,
  output CG,
  
  output [15:0] LED
  );
  
  localparam SEGMENT_LATENCY = 20 * 1000 * 100; // 20 ms (50 FPS)
  
  wire [7:0] a;
  wire [7:0] b;
  assign a = SW[15:8];
  assign b = SW[7:0];

  // Printing result to LEDs
  wire busy;
  wire finish;
  wire [23:0] result;
  
  assign LED[7] = BTNC;
  assign LED[8] = BTNR;
  
  // Converting into BCD format
  reg [31:0] segment_clock = 0;
  wire [2:0] segment_num;
  assign segment_num = segment_clock / (SEGMENT_LATENCY / 8);
  assign AN = ~({8{~busy}} & (8'b0000_0001 << segment_num));
  
  wire [31:0] result_bcd;
  assign result_bcd[ 3: 0] = result              % 10;
  assign result_bcd[ 7: 4] = result /         10 % 10;
  assign result_bcd[11: 8] = result /        100 % 10;
  assign result_bcd[15:12] = result /      1_000 % 10;
  assign result_bcd[19:16] = result /     10_000 % 10;
  assign result_bcd[23:20] = result /    100_000 % 10;
  assign result_bcd[27:24] = result /  1_000_000 % 10;
  assign result_bcd[31:28] = result / 10_000_000 % 10;
  
  // Printing BCD on 7-segment display
  wire [3:0] num;
  wire print;
  assign num = result_bcd >> (segment_num * 4);
  assign print = segment_num == 0 || (result_bcd >> (segment_num * 4)) != 0;

  assign CA = ~print | (num == 1 || num == 4);
  assign CB = ~print | (num == 5 || num == 6);
  assign CC = ~print | (num == 2);
  assign CD = ~print | (num == 1 || num == 4 || num == 7);
  assign CE = ~print | (num == 1 || num == 3 || num == 4 || num == 5 || num == 7 || num == 9);
  assign CF = ~print | (num == 1 || num == 2 || num == 3 || num == 7);
  assign CG = ~print | (num == 0 || num == 1 || num == 7);

  assign LED[15:12] = num;

  main_function main1 (
    .clock(CLK100MHZ),
    .reset(BTNR),
    .enable(BTNC),
    
    .a(a),
    .b(b),
    
    .busy(busy),
    .finish(finish),
    .result(result)  
  );
  
  always @(posedge CLK100MHZ) begin
    if (segment_clock >= SEGMENT_LATENCY - 1) segment_clock <= 0;
    else segment_clock <= segment_clock + 1;
  end
endmodule
