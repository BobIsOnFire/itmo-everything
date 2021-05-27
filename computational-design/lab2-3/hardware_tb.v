`timescale 1ns / 1ps
//////////////////////////////////////////////////////////////////////////////////
// Company: 
// Engineer: 
// 
// Create Date: 15.04.2021 10:05:19
// Design Name: 
// Module Name: hardware_tb
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


module hardware_tb(

  );
  
  reg clock;
  reg reset;
  reg enable;
  
  reg [15:0] sw;
  
  wire [7:0] an;
  wire ca;
  wire cb;
  wire cc;
  wire cd;
  wire ce;
  wire cf;
  wire cg;
  wire [15:0] led;
  
  hardware hrdw (
    .CLK100MHZ(clock),
    .SW(sw),
    .BTNC(enable),
    .BTNR(reset),
    
    .AN(an),
    .CA(ca),
    .CB(cb),
    .CC(cc),
    .CD(cd),
    .CE(ce),
    .CF(cf),
    .CG(cg),
    
    .LED(led)
  );
  
  initial begin
    clock = 1;
    #1
    clock = 0;
    #1
  
    clock = 1; reset = 1;
    #1
    clock = 0; reset = 0;
    #1
    
    sw = {8'd123, 8'd33};
    
    clock = 1; enable = 1;
    #1
    clock = 0;
    #1
    clock = 1; enable = 0;
    
    while (1) begin
      #1
      clock = 0;
      #1
      clock = 1;
    end
  end
endmodule
