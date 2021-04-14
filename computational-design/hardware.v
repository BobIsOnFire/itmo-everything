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
  
  localparam BOUNCE_LATENCY = 20 * 1000 * 100; // 20 ms
  localparam SEGMENT_LATENCY = 20 * 1000 * 100; // 20 ms (50 FPS)
  
  wire [7:0] a;
  wire [7:0] b;
  assign a = SW[15:8];
  assign b = SW[7:0];

  reg enable = 0;
  wire enable_listen;
  integer enable_bounce = BOUNCE_LATENCY;
  assign enable_listen = enable_bounce >= BOUNCE_LATENCY;
  
  reg reset = 0;
  wire reset_listen;
  integer reset_bounce = BOUNCE_LATENCY;
  assign reset_listen = reset_bounce >= BOUNCE_LATENCY;

  wire busy;
  wire finish;
  wire [23:0] result;
  
  assign LED = result[15:0];
  
  integer segment_clock = 0;
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
  
  wire [3:0] num;
  wire print;
  assign num = result_bcd >> (segment_num * 4);
  assign print = segment_num == 0 || (result_bcd >> (segment_num * 4)) != 0;

  assign CA = ~print & (num == 1 || num == 4);
  assign CB = ~print & (num == 5 || num == 6);
  assign CC = ~print & (num == 2);
  assign CD = ~print & (num == 1 || num == 4 || num == 7);
  assign CE = ~print & (num == 1 || num == 3 || num == 4 || num == 5 || num == 7 || num == 9);
  assign CF = ~print & (num == 1 || num == 2 || num == 3 || num == 7);
  assign CG = ~print & (num == 0 || num == 1 || num == 7);

  main_function main1 (
    .clock(CLK100MHZ),
    .reset(reset),
    .enable(enable),
    
    .a(a),
    .b(b),
    
    .busy(busy),
    .finish(finish),
    .result(result)  
  );
  
  // debouncing: do not change signal for 20 ms = 20 * 1000 * 100 ticks
  always @(posedge BTNC)
    if (enable_listen) begin
      enable <= 1;
      enable_bounce <= 0;
    end
  
  always @(negedge BTNC)
    if (enable_listen) begin
      enable <= 0;
      enable_bounce <= 0;
    end
  
  always @(posedge BTNR)
    if (reset_listen) begin
      reset <= 1;
      reset_bounce <= 0;
    end
  
  always @(negedge BTNR)
    if (reset_listen) begin
      reset <= 0;
      reset_bounce <= 0;
    end
  
  always @(posedge CLK100MHZ) begin
    if (!enable_listen) enable_bounce <= enable_bounce + 1;
    if (!reset_listen) reset_bounce <= reset_bounce + 1;
    segment_clock <= segment_clock + 1;
    if (segment_clock >= SEGMENT_LATENCY) segment_clock <= 0;
  end
endmodule
